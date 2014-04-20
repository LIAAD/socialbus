var services = {};
var services_counts = {};
var http = require("http");

var SOCIALBUS_ENDPOINT = "http://reaction.fe.up.pt:9191/api/sysinfo";

setInterval(function() {
	http.get(SOCIALBUS_ENDPOINT, function(res) {
	    var body = '';

	    res.on('data', function(chunk) {
	        body += chunk;
	    });

	    res.on('end', function() {
			body = body.replace("jsonCallback(","");
			body = body.replace('SNAPSHOT"})','SNAPSHOT"}');

			var jsonResponse = JSON.parse(body)

			for (var service in jsonResponse.stats.services) {
				var service_count =  jsonResponse.stats.services[service].count;
				
				var service_name = service.split("_")[0];
				
			    services_counts[service_name] = {
			      label: service_name,
			      value: service_count
			    };
			    var data = [];
			    for (var i in services_counts) {
			      data.push(services_counts[i]);
			    }
			    
				console.log({ items: data });
				send_event('services', { items: data });		
			}
			
	    });
	
	}).on('error', function(e) {
			console.log("Got error: ", e);
	});	
	
}, 5 * 1000);
