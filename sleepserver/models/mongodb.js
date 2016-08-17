var mongoose = require('mongoose')
var async = require('async');
var moment = require('moment');
var url = 'mongodb://localhost/test';
var dateutils = require('date-utils');
var pool = require('../models/mysqldb');
var log =require('simple-node-logger').createSimpleFileLogger('kimnam.log');
var options = {
	server : {poolSize:100}
}
var plotly = require('plotly')("KoTaeKwang","oawu6tmili");

var mongodb = mongoose.createConnection(url,options);
var flag = false;
mongodb.on('error',function(err){
	if(err) console.error('db error',err);
});

mongodb.on('open',function callback(){
	console.info('mongo db connected successfully');
});

var moveSchema = require('../models/movemodel');
var move = mongodb.model('move',moveSchema);
var heartRateSchema = require('../models/heartRatemodel');
var heartRate = mongodb.model('heartRate',heartRateSchema);



exports.pushSleep = function(data,callback){

	var date = new Date(data.date);
	var dates = new Date(data.date);

	dates.setHours(date.getHours()+9);

	console.log(dates);
	var moves= new move({
		move : data.move,
		id : data.id,
		date : dates
	});

	var heartRates = new heartRate({
		heartRate : data.heartRate,
		id: data.id,
		date : dates
	});

	async.waterfall([
		function(callback){
			moves.save(function(err,conn){
				if(err){console.log('err',err); return;}
				callback(null);
			})
		},function(callback){
			heartRates.save(function(err,conn){
				if(err){console.log('err',err); return;}
				log.info("heartrate : ",data.heartRate," date -> ",dates);
				callback(null);
			})
		},function(callback){
			pool.getConnection(function(err,conn){
				if(err){callback(err);return;}
				conn.query("select endtime from sleeptime where id=? order by endtime desc",data.id,function(err,row){
					if(err){conn.release();console.log('err',err);return;}

					var dates= new Date();

					console.log('row[0]enddate',row[0].endtime);
					console.log('dates',dates);
					var subdate = row[0].endtime-dates;
					subdate=Math.floor(subdate/(60*1000));
					console.log(subdate);
					conn.release();
					callback(null);
				});
			});
		},function(callback){
			pool.getConnection(function(err,conn){
				if(err){callback(err);return;}
				conn.query("select id,heartrate,count from heartrate where id=?",data.id,function(err,rows){
					console.log("rows.length",rows.length);
					if(rows==null){ //처음
						conn.release();
						callback(null,0,rows.length);
					}else{
						if(rows.length==10){
							var heartrateAdd=0;
							rows.forEach(function(value){
								heartrateAdd+=value.heartrate;
							})
							heartrateAdd=Math.floor(heartrateAdd/10); //10회 평균값
							conn.release();
							callback(null,heartrateAdd,10);
						}else{
							conn.release();
							callback(null,0,rows.length);
						}
					}
				})
			});
		},function(heartrate,count,callback){
			if(count!=10){ //추가
				pool.getConnection(function(err,conn){
					if(err){console.log(err);callback(err);return;}
					conn.query("insert into heartrate(id,heartrate,count) values(?,?,?)",[data.id,data.heartRate,count+1],function(err,row){
						if(err){console.log(err);conn.release();callback(err);return;}
						conn.release();
						log.info("비수면");
						callback(null,1);
						})
				});
			}else{ //비교
				console.log("heartrate",heartrate," data.heartrates - > ",data.heartRate);
				console.log(heartrate*0.85); //잠
				if((heartrate*0.85)>=data.heartRate){ //여기서 램과,비램 처리해야하나
					//console.log("잔다");
					log.info("수면");
						if((heartrate*0.85)>=data.heartRate&&data.heartRate>=(heartrate*0.85*0.9))
						{
							//램수면   //이때 enddate 30분전이면 7
							log.info("램수면");
							callback(null,3);
							flag=true;
						}
						else{//비램수면
							if(flag)
							{
								log.info("비램수면");
								callback(null,4);
								flag=false;
							}
							else
							callback(null,1);
						}
				}
				else{ //안잠
					log.info("비수면");
				callback(null,1);
				}
			}
		}
		],function(err,results){
			callback(results);
	})
}

exports.cancelsleep = function(data,callback){
	async.waterfall([
		function(callback){
			pool.getConnection(function(err,conn){
				if(err){callback(err);return;}
				var date = new Date()
				var dates=moment(date).format('YYYY-MM-DD HH:mm:ss')
				var sdate = dates.toString()

				console.log('sdate',sdate)
				conn.query("select starttime,endtime from sleeptime where id =? and endtime>=?",[data,sdate],function(err,row){
						if(err){conn.release(); callback(2); console.log('err',err); return;}
						console.log('id',data);
						if(row.length==0){
							conn.release();
							callback(null,0,0);
						}
					else{
						console.log('row.startime',row[0].starttime);
						console.log('row.endtime',row[0].endtime);
						conn.release();
						callback(null,row[0].starttime,row[0].endtime);
						}
				})
			});
		},function(starttime,endtime,callback){
			if(starttime==0){
				callback(null,0,0);
			}
			else{
				move.remove({date:{$gte:starttime},id:data},function(err,conn){
				if(err){callback(2); console.log('err',err); return;}
					callback(null,starttime,endtime);
				})
			}
		},function(starttime,endtime,callback){
			if(starttime==0){
				callback(null);
			}
			else{
				heartRate.remove({date:{$gte:starttime},id:data},function(err,conn){
					if(err){ callback(2); console.log('err',err); return;}
					callback(null);
				})
			}
		}
		],function(err,results){
			callback(1);
	});
}




exports.visualdata = function(data,callback){
	async.waterfall([
		function(callback){
			pool.getConnection(function(err,conn){
				if(err){callback(err);return;}
				conn.query("select starttime, endtime from sleeptime where id=? order by endtime desc",data,function(err,row){
					if(err){conn.release(); callback(2); console.log('err',err); return;}
					console.log(row[0].starttime);
					console.log(row[0].endtime);
					conn.release();
				callback(null,row[0].starttime,row[0].endtime);
				})
			});
		},
		function(starttime,endtime,callback){
			heartRate.find({id:data,date:{$gte:starttime}},{id:1,date:1,heartRate:1,_id:0},function(err,results){
				console.log(data);
				console.log(results.length);

				var heartrate=[];

				results.map(function(value){
					var obj={};
					obj.heartrate=value.heartRate;
					obj.date=moment(value.date).format('YYYY-MM-DD HH:mm:ss');
					heartrate.push(obj);
				})

				callback(null,starttime,endtime,heartrate);
			});
		},
		function(starttime,endtime,heartRates,callback){
			move.find({id:data,date:{$gte:starttime}},{id:1,date:1,move:1,_id:0},function(err,moves){

				var move=[];
				moves.map(function(value){
					var obj={};
					obj.move=value.move;
					obj.date=moment(value.date).format('YYYY-MM-DD HH:mm:ss');
					move.push(obj);
				});

				var obj = {};
				obj.move=move;
				obj.heartRate=heartRates;
				callback(null,obj);
			})
		}
		],function(err,results){
			callback(results);
		})
}


exports.visdata = function(callback){

	async.waterfall([
		function(callback){
			pool.getConnection(function(err,conn){
				conn.query("select starttime from sleeptime where id='test' order by endtime desc",function(err,row){
					console.log(row[0].starttime);
					callback(null,row[0].starttime);
				})
			})
		},function(starttime,callback){

			heartRate.find({id:'test',date:{$gte:starttime}},{id:1,date:1,heartRate:1,_id:0},function(err,results){

						var heartratedate=[];
						var heartrate=[];
						var count=0;

						results.map(function(value){
							count++;

							heartrate.push(value.heartRate);
							heartratedate.push(moment(value.date).format('YYYY-MM-DD HH:mm:ss'));
							if(count<10)
							{
							console.log(moment(value.date).format('YYYY-MM-DD HH:mm:ss'));
							}
						});

						console.log(heartrate.length);
						console.log(heartratedate.length);
						callback(null,heartrate,heartratedate);
					});
		}
		],function(err,heartrate,heartratedate){

			var data=[{x:heartratedate,y:heartrate,type:'bar'}];
			//var layout={fileopt:"overwrite",filename:"simple-node-example"};
			var layout={format:'png',width:1500,height:1000};
			plotly.plot(data,layout,function(err,msg){
				if(err) return console.log(err);
				console.log(msg.url+".jpeg");
				callback(msg.url+".jpeg");
			})
		})



}