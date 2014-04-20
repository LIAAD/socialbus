var http = require("http");

var SOCIALBUS_ENDPOINT = "http://reaction.fe.up.pt:9191/api/sysinfo";

// Populate the graph with zeros
var points = [];
for (var i = 1; i <= 10; i++) {
  points.push({x: i, y: 0});
}

var last_x = points[points.length - 1].x;
var last_total = 0;
var current_total = 0;
setInterval(function() {
	
	var last_total = current_total;
	
	http.get(SOCIALBUS_ENDPOINT, function(res) {
	    var body = '';

	    res.on('data', function(chunk) {
	        body += chunk;
	    });

	    res.on('end', function() {
			body = body.replace("jsonCallback(","");
			body = body.replace('SNAPSHOT"})','SNAPSHOT"}');

			var jsonResponse = JSON.parse(body)

			current_total = jsonResponse.stats._global_.count;
			
			
			var diff_total = current_total - last_total;
			
			console.log("last_total : " + last_total);
			console.log("current_total : " + current_total);
			console.log("diff_total : " + diff_total);
			
			points.shift();
			points.push({x: ++last_x, y:diff_total });

		    console.log(points);
		    send_event('convergence', {points: points});
	    });
	
	}).on('error', function(e) {
			console.log("Got error: ", e);
	});	
	
}, 5 * 1000);