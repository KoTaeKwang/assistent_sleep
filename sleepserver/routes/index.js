var express = require('express');
var router = express.Router();
var mongodb = require('../models/mongodb');
var member = require('../models/memberdb');

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/test',function(req,res,next){
	mongodb.test('1',function(success){
		res.json(success);
	})
});

router.get('/addmember',function(req,res,next){
	member.addmember('1',function(success){
		res.json(success);
	})
})

module.exports = router;
