var express = require('express');
var router = express.Router();
var mongodb = require('../models/mongodb');
var member = require('../models/memberdb');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.post('/login',function(req,res,next){
	var id = req.body.id;
	var pwd = req.body.pwd;
	var datas={'id':id,'pwd':pwd};
	console.log('id : ',id);
	console.log('pwd : ',pwd);
/*	member.login(datas,function(success){
		res.json(success);
	})*/
	res.json(1);
});

router.post('/startSleep',function(req,res,next){

	var starttime = req.body.starttime;
	var endtime = req.body.endtime;
	var id = req.body.id;

	console.log('starttime',starttime);
	console.log('endtime',endtime);


	var datas ={starttime : starttime, endtime : endtime, id: id};
	console.log('startsleep',datas);
	member.startsleep(datas,function(success){
		res.json(success);
	});
});

router.post('/cancelSleep',function(req,res,next){
	var id =req.body.cancel;

	member.cancelsleep(id,function(success){
		res.json(success);
	});
});

router.post('/pushSleep',function(req,res,next){
	var heartRate = req.body.heartRate;
	var move = req.body.move;
	var id = req.body.id;
	var date = req.body.currenttime;
	var datas={heartRate:heartRate,move:move,id:id,date:date};
	console.log('datas',datas);
	mongodb.pushSleep(datas,function(success){
		res.json(success);
	})
});

module.exports = router;
