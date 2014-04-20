var topics = {};
var topics_counts = {};
var current_total_global = 0;

var http = require("http");

var SOCIALBUS_ENDPOINT = "http://reaction.fe.up.pt:9191/api/sysinfo";

setInterval(function() {
	var last_value_for_total_global = current_total_global;
	  
	http.get(SOCIALBUS_ENDPOINT, function(res) {
	    var body = '';

	    res.on('data', function(chunk) {
	        body += chunk;
	    });

	    res.on('end', function() {
			body = body.replace("jsonCallback(","");
			body = body.replace('SNAPSHOT"})','SNAPSHOT"}');

			var jsonResponse = JSON.parse(body)

			for (var topic in jsonResponse.stats.topics) {
				var topic_count =  jsonResponse.stats.topics[topic].count;
				
			 	// console.log("topic : " + topic);
				// console.log("count : " + topic_count);
				
			    topics_counts[topic] = {
			      label: topic,
			      value: topic_count
			    };
			    var data = [];
			    for (var i in topics_counts) {
			      data.push(topics_counts[i]);
			    }
			    
				// console.log({ items: data });
				send_event('topics', { items: data });		
			}
			
			var current_total_global = jsonResponse.stats._global_.count;
			send_event('valuation', {current: current_total_global, last: last_value_for_total_global});		
			
	    });
	
	}).on('error', function(e) {
			console.log("Got error: ", e);
	});	
	
}, 5 * 1000);
