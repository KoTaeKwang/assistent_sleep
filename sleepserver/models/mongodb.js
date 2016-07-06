var mongoose = require('mongoose');
var url = 'mongodb://localhost/test';
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

exports.test = function(data,callback){
	var moves= new move({
		move : 2,
		id : 'hell'
	});

	moves.save(function(err,conn){
		if(err){console.log('err', err); callback(err);}
		console.log('저장성공');
	});
	callback('ok');
}