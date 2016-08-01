var mongoose = require('mongoose');
var async = require('async');
var moment = require('moment');
var url = 'mongodb://localhost/test';
var dateutils = require('date-utils');
var pool = require('../models/mysqldb');
var options = {
	server : {poolSize:100}
}

var mongodb = mongoose.createConnection(url,options);

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
				callback(null);
			})
		}
		],function(err,results){
			callback(3);
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
						console.log('row',row)
						console.log('id',data)
						console.log('row.startime',row[0].starttime)
						console.log('row.endtime',row[0].endtime)
						callback(null,row[0].starttime,row[0].endtime);
				})
			});
		},function(starttime,endtime,callback){
			move.remove({date:{$gte:starttime}},function(err,conn){
			if(err){callback(2); console.log('err',err); return;}
				callback(null,starttime,endtime);
			})
		},function(starttime,endtime,callback){

			heartRate.remove({date:{$gte:starttime}},function(err,conn){
				if(err){ callback(2); console.log('err',err); return;}
				callback(null);
			})
		}
		],function(err,results){
			callback(1);
	});
}


exports.wakeupSleep = function(data,callback){

	var heartRate=data.heartRate;

	if(heartRate>=123)
		callback(5);

	else
		callback(1);
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
				callback(null,row[0].starttime,row[0].endtime);
				})
			});
		},
		function(starttime,endtime,callback){
			heartRate.find({id:data,date:{$gte:starttime,$lte:endtime}},{id:1,date:1,heartRate:1,_id:0},function(err,results){
				callback(null,starttime,endtime,results);
			})
		},
		function(starttime,endtime,heartRates,callback){
			move.find({id:data,date:{$gte:starttime,$lte:endtime}},{id:1,date:1,move:1,_id:0},function(err,moves){
				console.log("results",moves[0]);
				var obj = {};
				obj.move=moves;
				obj.heartRate=heartRates;
				console.log("results",obj);

				callback(null,obj);
			})
		}
		],function(err,results){
			callback(results);
		})
}