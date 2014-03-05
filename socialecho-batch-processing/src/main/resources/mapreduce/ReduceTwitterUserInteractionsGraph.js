function reduce(key,values) {
	
	var result = { outlinks : [], retweet : [], mention : [], reply : [], indegree : 0, outdegree : 0  };
	var arr_outlinks = [];
	var arr_retweet = [];
	var arr_mention = [];
	var arr_reply = [];
	
	values.forEach(function(value) {
		if (value.outlinks.length > 0) {
			arr_outlinks = arr_outlinks.concat(value.outlinks);
		}
		if (value.retweet.length > 0) {
			arr_retweet = arr_retweet.concat(value.retweet);
		}
		if (value.mention.length > 0) {
			arr_mention = arr_mention.concat(value.mention);
		}
		if (value.reply.length > 0) {
			arr_reply = arr_reply.concat(value.reply);
		}
		
		result.indegree += value.indegree;
      	result.outdegree += value.outdegree;
	});
	
	result.outlinks = arr_outlinks;
	result.retweet = arr_retweet;
	result.mention = arr_mention;
	result.reply = arr_reply;
	
	return result;
}