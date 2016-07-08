var mongoose = require('mongoose');
var async = require('async');
var moment = require('moment');
var url = 'mongodb://localhost/test';
var dateutils = require('date-utils');

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
			callback(1);
	})
}