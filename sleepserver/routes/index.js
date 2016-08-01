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
	console.log("canclesleep");
	var id =req.body.cancel;
	console.log("cancel id : ",id);
	mongodb.cancelsleep(id,function(success){
		if(success==1)
		{
			member.cancelsleep(id,function(success){
				res.json(success);
			});
		}
		else{
			res.json(success);
		}
	});
});

router.post('/pushSleep',function(req,res,next){  //자고있을때
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

router.post('/wakeupSleep',function(req,res,next){
	var heartRate = req.body.heartRate;
	var id = req.body.id;
	var datas={heartRate:heartRate,id:id};

	member.wakeupSleep(datas,function(success){
		res.json(success);
	});
});


router.get('/pusharduino',function(req,res,next){
	res.json(1);
});


router.post('/visualdata',function(req,res,next){ //자는동안 심박수, 움직임
	var id=req.body.id;
  var obj=[];

 	for(var i=0;i<31;i++){
 		var obj2={};
 		obj2.x=i+1;
 		obj2.y=i%5+1;
 		obj.push(obj2);
 	}

  res.json(obj);
/*	mongodb.visualdata(id,function(success){
		res.json(success);
	})*/
});



router.post('/sleepTimeData',function(req,res,next){ //일별로 잔 총시간
	var id= req.body.id;

	member.sleepTimeData(id,function(success){
		res.json(success);
	});
});


router.get('/ageData',function(req,res,next){ //나이별 수면시간
	member.ageData(function(success){
		res.json(success);
	})
})

module.exports = router;
