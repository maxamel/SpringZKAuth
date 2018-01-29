var http = require("http");
var crypto = require('crypto');
var bigInt = require("big-integer");
var Stack = require('stack-lifo');
var stack = new Stack();

var g = "AC4032EF4F2D9AE39DF30B5C8FFDAC506CDEBE7B89998CAF74866A08CFE4FFE3A6824A4E10B9A6F0DD921F01A70C4AFAAB739D7700C29F52C57DB17C620A8652BE5E9001A8D66AD7C17669101999024AF4D027275AC1348BB8A762D0521BC98AE247150422EA1ED409939D54DA7460CDB5F6C6B250717CBEF180EB34118E98D119529A45D6F834566E3025E316A330EFBB77A86F0C1AB15B051AE3D428C8F8ACB70A8137150B8EEB10E183EDD19963DDD9E263E4770589EF6AA21E7F5F2FF381B539CCE3409D13CD566AFBB48D6C019181E1BCFE94B30269EDFE72FE9B6AA4BD7B5A0F1C71CFFF4C19C418E1F6EC017981BC087F2A7065B384B890D3191F2BFA";
var N = "AD107E1E9123A9D0D660FAA79559C51FA20D64E5683B9FD1B54B1597B61D0A75E6FA141DF95A56DBAF9A3C407BA1DF15EB3D688A309C180E1DE6B85A1274A0A66D3F8152AD6AC2129037C9EDEFDA4DF8D91E8FEF55B7394B7AD5B7D0B6C12207C9F98D11ED34DBF6C6BA0B2C8BBC27BE6A00E0A0B9C49708B3BF8A317091883681286130BC8985DB1602E714415D9330278273C7DE31EFDC7310F7121FD5A07415987D9ADC0A486DCDF93ACC44328387315D75E198C641A480CD86A1B9E587E8BE60E69CC928B2B9C52172E413042E9B23F10B0E16E79763C9B53DCF4BA80A29E3FB73C16B8E75B97EF363E2FFA31F71CF9DE5384E71B81C0AC4DFFE0C10E64F";

var cache = {
        name: "",
        password: ""
};

var registerpass = "";

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
    if (!stack.isEmpty())
    {
        action = stack.pop();
        console.log("Got password " + string);
        var object = {};
        gen = bigInt(g,16);
        mod = bigInt(N,16);
        hash = crypto.createHash('sha256');
        if (action.includes("REGISTER")) registerpass = bigInt(hash.update(string).digest("hex"), 16);
        else cache.password = bigInt(hash.update(string).digest("hex"), 16);
        
        return processCommand(action);
    }	
    
    arr = string.split(' '),
    result = arr.splice(0,2);
    result.push(arr.join(' ')); 
    
	if ((result[0] != "REGISTER" && cache.password == "") || (result[0] == "REGISTER" && registerpass == "")) 
	{
	    stack.push(string);
	    console.log("Please enter password for new user " + result[2]);
	    return;
	}  
    
    if (result.length < 2) return;
    
    meth = "DEFAULT";
    url = "/zkauth/users/";
    object = {};
    body = "";
    heads = {}
    heads["content-type"] = "application/json";
    switch(result[0])
    {
		case "FETCH":
			meth = 'GET';
			url += result[2];
		    break;
		case "REMOVE":
			meth = 'DELETE';
			url += result[2];
		    break;
		case "REGISTER":
			meth = 'POST';
			object.name = result[2];
			object.passwordless = registerpass;
			registerpass = "";
			body = JSON.stringify(object);
			heads["content-length"] = body.length;
			//heads["transfer-encoding"] = '';
		    break;
		default:
		    console.log("Unknown command");
		    return;
	}
    x = result[1].split(':');
    
    console.log(meth + " " + url + " " + body);
    
    var options = {
		  host: x[0],
		  port: x[1],
		  path: url,
		  method: meth,
		  headers : heads
	};
	console.log(JSON.stringify(options));
    sendRequestOptions(options,body);
    body = "";
}

function sendRequestOptions(options, body)
{
	var req = http.request(options, function(r1){
	console.log(`STATUS: ${r1.statusCode}`);
    console.log(`HEADERS: ${JSON.stringify(r1.headers)}`);
      	  r1.on('data', function(chunk){
      	    response = JSON.parse(chunk);
      	    console.log("Got chunk " + chunk + " message: " + response["message"]);
    	    
      	    if (response["message"] == "Unauthorized") 
      	    {
      	        challenge = bigInt(response["challenge"], 10);
      	    	mod = bigInt(N,16);
      	    	answer = challenge.modPow(cache.password, mod);
      	    	options.headers["ZKAuth-Token"] = answer.toString();
      	    	console.log("token " + answer.toString())
      	    	var ret = http.request(options, function(r2){
      	      	  r2.on('data', function(chunk){
      	      	    console.log("Got chunk " + chunk);
      	      	  });
      	      	}).on("error", function(e){
      	      	  console.log("Got error: " + e.message);
      	      	});
      	      	ret.end();
      	   	}
      	  });
      	}).on("error", function(e){
      	  console.log("Got error: " + e.message);
      	});
      	
	if (body != "") req.write(body);
  	req.end();
}

