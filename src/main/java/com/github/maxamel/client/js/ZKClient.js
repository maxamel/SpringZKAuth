var http = require("http")
var rl = require("readline-sync")
var crypto = require('crypto');
var hash = crypto.createHash('sha256')

var isPassword = false;
var cache = {}

stdin = process.openStdin();

stdin.addListener("data", function(d) {
    string = d.toString()
    console.log("got input: [" + 
        d.toString().trim() + "]");
        
    command = d.toString().trim();
    processCommand(command);
});

function processCommand(string)
{
	if (isPassword) 
	{
		console.log("Got password " + string);
		var object = {};
		object.name = cache.name;
		object.passwordless = hash.update(string);
		x = cache.destination.split(':');
	    
	    var options = {
			  host: x[0],
			  port: x[1],
			  path: "/zauth/users",
			  body: JSON.stringify(object),
			  method: 'POST'
		};
	    sendRequestOptions(options);
		isPassword = false;
		return;
	}
    arr = string.split(' '),
    result = arr.splice(0,2);
    result.push(arr.join(' ')); 
    //console.log(result.toString());
    
    if (result.length < 2) return;
    
    meth = "DEFAULT"
    switch(result[0])
    {
		case "FETCH":
			command = 'GET';
		    break;
		case "REMOVE":
			command = 'DELETE';
		    break;
		case "REGISTER":
			command = 'POST';
			console.log("Please enter password for user");
			isPassword = true;
			cache.destination = result[1];
			cache.name = result[2];
			return;  
		    break;
		default:
		    condole.log("Unknown command");
		    break;
	}
    
    console.log(command + " " + result[2])

    x = result[1].split(':');
    
    
    var options = {
		  host: x[0],
		  port: x[1],
		  path: "/zauth/users",
		  method: meth
	};
    sendRequestOptions(options);
}

function sendRequestOptions(options)
{
	var req = http.request(options, function(resp){
  	  resp.on('data', function(chunk){
  	    console.log("Got chunk " + chunk);
  	  });
  	}).on("error", function(e){
  	  console.log("Got error: " + e.message);
  	});
  
  	req.end();
}