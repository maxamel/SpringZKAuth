var TopicConsumer = require('kafka-node-topic-consumer');

var http = require("http");
var crypto = require('crypto');
var bigInt = require("big-integer");
var Stack = require('stack-lifo');
var stack = new Stack();
var kafka = require('no-kafka-slim');

var g = "AC4032EF4F2D9AE39DF30B5C8FFDAC506CDEBE7B89998CAF74866A08CFE4FFE3A6824A4E10B9A6F0DD921F01A70C4AFAAB739D7700C29F52C57DB17C620A8652BE5E9001A8D66AD7C17669101999024AF4D027275AC1348BB8A762D0521BC98AE247150422EA1ED409939D54DA7460CDB5F6C6B250717CBEF180EB34118E98D119529A45D6F834566E3025E316A330EFBB77A86F0C1AB15B051AE3D428C8F8ACB70A8137150B8EEB10E183EDD19963DDD9E263E4770589EF6AA21E7F5F2FF381B539CCE3409D13CD566AFBB48D6C019181E1BCFE94B30269EDFE72FE9B6AA4BD7B5A0F1C71CFFF4C19C418E1F6EC017981BC087F2A7065B384B890D3191F2BFA";
var N = "AD107E1E9123A9D0D660FAA79559C51FA20D64E5683B9FD1B54B1597B61D0A75E6FA141DF95A56DBAF9A3C407BA1DF15EB3D688A309C180E1DE6B85A1274A0A66D3F8152AD6AC2129037C9EDEFDA4DF8D91E8FEF55B7394B7AD5B7D0B6C12207C9F98D11ED34DBF6C6BA0B2C8BBC27BE6A00E0A0B9C49708B3BF8A317091883681286130BC8985DB1602E714415D9330278273C7DE31EFDC7310F7121FD5A07415987D9ADC0A486DCDF93ACC44328387315D75E198C641A480CD86A1B9E587E8BE60E69CC928B2B9C52172E413042E9B23F10B0E16E79763C9B53DCF4BA80A29E3FB73C16B8E75B97EF363E2FFA31F71CF9DE5384E71B81C0AC4DFFE0C10E64F";

var cache = {
        name: "",
        password: ""
};

var registerpass = "";
var solution = "";
var globalcommand = "";

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
    globalcommand = string;
    if (!stack.isEmpty())
    {
        action = stack.pop();
        console.log("Got password " + string);
        var object = {};
        gen = bigInt(g,16);
        mod = bigInt(N,16);
        hash = crypto.createHash('sha256');
        pass = bigInt(hash.update(string).digest("hex"), 16);
        if (action.includes("REGISTER")) 
        {
            temp = gen.modPow(pass, mod);
            registerpass = dec2hex(temp.toString());
        }
        else cache.password = pass;
        
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
    if (solution != "") options.headers["ZKAuth-Token"] = solution.toString();
	var req = http.request(options, function(r1){
	      console.log(`STATUS #1: ${r1.statusCode}`);
	      status1 = r1.statusCode;
      	  r1.on('data', function(chunk){
      	    response = JSON.parse(chunk);
      	    console.log("Got chunk " + chunk + " message: " + response["message"]);
    	    
      	    if (status1 == 401) 
      	    {
      	        challenge = bigInt(response["challenge"], 10);
      	    	mod = bigInt(N,16);
      	    	answer = challenge.modPow(cache.password, mod);
      	    	solution = answer.toString(); 
      	    	options.headers["ZKAuth-Token"] = solution;
      	    	console.log("token " + answer.toString())
      	    	var ret = http.request(options, function(r2){
      	    	status2 = r2.statusCode;
      	    	console.log(`STATUS #2: ${r2.statusCode}`);
      	      	  r2.on('data', function(chunk){
      	      	    console.log("Got chunk " + chunk);
      	      	    if (status2 == 200 || status2 == 201) spinUpKafkaConsumer();
      	      	    else 
      	      	    {
      	      	        stack.push(globalcommand);
                        console.log("Please enter password for new user " + cache.name);
                        return;
      	      	    }
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

function dec2hex(str){ 
    var dec = str.toString().split(''), sum = [], hex = [], i, s
    while(dec.length){
        s = 1 * dec.shift()
        for(i = 0; s || i < sum.length; i++){
            s += (sum[i] || 0) * 10
            sum[i] = s % 16
            s = (s - sum[i]) / 16
        }
    }
    while(sum.length){
        hex.push(sum.pop().toString(16))
    }
    return hex.join('')
}

function spinUpKafkaConsumer()
{
    var consumer = new kafka.SimpleConsumer({
    connectionString: '172.30.141.117:9092'
    });

    // data handler function can return a Promise
    var dataHandler = function (messageSet, topic, partition) {
        messageSet.forEach(function (m) {
            json = JSON.parse(m.message.value);
            chal = bigInt(json["challenge"],16);
            mod = bigInt(N,16);
            console.log(m.message.value.toString('utf8'));
            console.log("challenge " + chal);
            solution = chal.modPow(cache.password, mod).toString();
            console.log("challenge " + chal);
        });
    };

    return consumer.init().then(function () {
        // Subscribe partitons 0 and 1 in a topic:
        return consumer.subscribe('challenge', 0, dataHandler);
    });
 
    consumer.connect()
}

