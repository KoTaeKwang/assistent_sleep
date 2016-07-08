
var mongoose = require('mongoose');
var Schema = mongoose.Schema;

var heartRateSchema = Schema({
	heartRate : Number,
	id : String,
	date : {type:Date,default:Date.now}
});

module.exports = heartRateSchema;