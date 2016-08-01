var pool = require('../models/mysqldb');
var mongodb = require('../models/mongodb');
var async = require('async');
var moment = require('moment');
var plotly = require('plotly')("KoTaeKwang","oawu6tmili");


exports.login = function(data,callback){

	pool.getConnection(function(err,conn){
		if(err){callback(err); return;}
		conn.query("select id from member where id=? and pwd=? ",[data.id,data.pwd],function(err,rows){
			if(err){conn.release(); callback(err);return;}
			conn.release();

			if(rows!=null)
				callback(1);
			else
				callback(2);
		})
	})
}

exports.startsleep = function(data,callback){
	pool.getConnection(function(err,conn){
		if(err){callback(err); return;}
		conn.query("insert into sleeptime(id,starttime,endtime) values(?,?,?)",[data.id,data.starttime,data.endtime],function(err,rows){
			if(err){conn.release();callback(0);return;}
			conn.release();
			if(rows)
			callback(1);
			else
			callback(2);
		})
	})
}

exports.cancelsleep = function(data,callback){
			pool.getConnection(function(err,conn){
				if(err){callback(err);return;}
				var date = new Date()
				var dates=moment(date).format('YYYY-MM-DD HH:mm:ss')
				var sdate = dates.toString()
				console.log('sdate',sdate)
				conn.query("delete from sleeptime where id=? and endtime>=?",[data,sdate],function(err,row){
						if(err){conn.release(); callback(2); console.log('err',err); return;}
						conn.release();
						callback(1);
				})
			});
}


exports.sleepTimeData = function(data,callback){

		pool.getConnection(function(err,conn){
			if(err){callback(err);return;}
			conn.query("select id,starttime,endtime from sleeptime where id=?",data,function(err,rows){
				var obj=[];

				rows.forEach(function(value){
					console.log(value);
					var date=value.endtime-value.starttime;
					date=date/(60*1000);
					var hour =Math.floor(date/60);
					var minute=Math.floor(date%60);

					console.log(date);
					var obj2={};
					obj2.date=value.starttime;
					obj2.hour=hour;
					obj2.minute=minute;
					obj.push(obj2);
				})
				conn.release();
				callback(obj);
			})
		});
}

exports.ageData = function(callback){
	pool.getConnection(function(err,conn){
		if(err){console.log(err);callback(err);}
		var time = new Array(10);
		var count = new Array(10);
		var obj =[];
		var age;
		var date;
		async.waterfall([
			function(callback){
				for(var i=0;i<time.length;i++){
					time[i]=0;
				}
				for(var i=0;i<count.length;i++){
					count[i]=0;
				}
				callback(null);
			},function(callback){
				conn.query("select m.id, age, starttime, endtime from member m join sleeptime s on m.id=s.id",function(err,rows){
					rows.forEach(function(value){
						date = value.endtime-value.starttime;
						date=date/(60*1000);
						age = Math.floor(value.age/10);
						time[age]+=date;
						count[age]+=1;
					});
					callback(null);
				});
			},function(callback){
				time.forEach(function(value,index){
					console.log("index",index," value",value);
					var obj2={};
					if(value!=0){
						obj2.age=index*10;
						obj2.hour=Math.floor(value/count[index]/60);
						obj2.minute=Math.floor((value/count[index])%60);
						obj.push(obj2);
					}
				});
				callback(null,obj);
			}
			],function(err,results){
				callback(results);
			});
	})
}