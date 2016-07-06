var pool = require('../models/mysqldb');

exports.addmember = function(data,callback){

	pool.getConnection(function(err,conn){
		if(err){callback(err); return;}

		conn.query("insert into member(id,pwd,name,token,age) values('123','123','123','123',111)", function(err,rows){
			if(err){conn.release(); callback(err);return;}
			conn.release();
			callback('ok');
		})
	})
}