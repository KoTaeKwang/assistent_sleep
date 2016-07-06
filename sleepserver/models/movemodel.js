
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var moveSchema = Schema({
	move : Number,
	id : String,
	date : {type:Date,default : Date.now}
});

module.exports = moveSchema;
