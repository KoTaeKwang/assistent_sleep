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
	async.waterfall([
		function(callback){
			pool.getConnection(function(err,conn){
				if(err){callback(err);return;}
				var date = new Date();
				var dates=moment(date).format('YYYY-MM-DD HH:mm:ss');
				var sdate = dates.toString();

				console.log(sdate);
				conn.query("select starttime,endtime from sleeptime where id =? and starttime>='"+sdate+"'",[data],function(err,row){
						if(err){conn.release();callback(0); console.log('err',err);return;}
						console.log('row',row);
						console.log('id',data)
						console.log('row.startime',row.starttime);
						callback(null);
				})
			});
		}


		],function(err,results){
			callback(1);
	});
/*	pool.getConnection(function(err,conn){
		if(err){callback(err);return;}
		conn.query("delete from sleeptime where endtime>current_date() and id=?",data,function(err,row){
			if(err){conn.release();callback(0); console.log('err',err);return;}
			if(row)
				callback(1);
			else
				callback(0);
		})
	})*/
}