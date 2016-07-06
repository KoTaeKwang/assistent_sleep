var mysql = require('mysql');

var pool = mysql.createPool({
	connectionLimit : 100,
	host : 'localhost',
	user : 'wellsleep',
	password : '1234',
	database : 'wellsleep'
});

module.exports=pool;