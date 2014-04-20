var http = require("http");

var SOCIALBUS_ENDPOINT = "http://reaction.fe.up.pt:9191/api/sysinfo";

function getData(){
    http.get(SOCIALBUS_ENDPOINT, function(res) {
        var body = '';

        res.on('data', function(chunk) {
            body += chunk;
        });

        res.on('end', function() {
			body = body.replace("jsonCallback(","");
			body = body.replace('SNAPSHOT"})','SNAPSHOT"}');

			var jsonResponse = JSON.parse(body)

			console.log("Got response: ", jsonResponse.stats);
			console.log("Total of messages: ", jsonResponse.stats._global_.count);
			
			for (var topic in jsonResponse.stats.topics) {
			 	console.log("topic : " + topic);
				console.log("count : " + jsonResponse.stats.topics[topic].count);
			}
        });
		
    }).on('error', function(e) {
			console.log("Got error: ", e);
    });	
}
 
getData();


  