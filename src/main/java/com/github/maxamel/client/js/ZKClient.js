http = require("request")

stdin = process.openStdin();

stdin.addListener("data", function(d) {
    string = d.toString()
    console.log("got command: [" + 
        d.toString().trim() + "]");
        
    arr = string.split(' '),
    result = arr.splice(0,2);
    result.push(arr.join(' ')); 
    //console.log(result.toString());
    
    if (result.length < 2) return;
    
    url = "http://"+result[1]+"/zauth/users"
    console.log(url + " " + result[2])
    
    switch(result[0]) {
    case "GET":
          
        break;
    case "DELETE":
        break;
    case "POST":
        break;
    default:
        condole.log("Unknown command");
        break;
        
        var request = require('request');
            // this works
            request({
              method: result[0],
              url: url+result[2]
            }, function(error, response, body) {
              if (error) console.log(error);
              console.log(body);
            });
}
 });