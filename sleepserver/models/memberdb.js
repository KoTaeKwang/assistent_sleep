var pool = require('../models/mysqldb');
var mongodb = require('../models/mongodb');
var async = require('async');
var moment = require('moment');



exports.login = function(data,callback){

	pool.getConnection(function(err,conn){
		if(err){callback(err); return;}
		conn.query("select id from member where id=? and pwd=? ",[data.id,data.pwd],function(err,rows){
			if(err){conn.release(); callback(err);return;}
			conn.release();

			if(rows!=null)
				callback(1);
			else
				callback(0);
		})
	})
}

exports.startsleep = function(data,callback){
	pool.getConnection(function(err,conn){
		if(err){callback(err); return;}
		conn.query("insert into sleeptime(id,starttime,endtime) values(?,?,?)",[data.id,data.starttime,data.endtime],function(err,rows){
			if(err){conn.release();callback(0);return;}
			if(rows)
			callback(1);
			else
			callback(0);
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
						if(err){conn.release(); callback(0); console.log('err',err); return;}
						callback(1);
				})
			});
}