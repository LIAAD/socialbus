function map() {
	var isRT = this.hasOwnProperty('retweeted_status');
	var isReply = this.hasOwnProperty('in_reply_to_screen_name') && this.in_reply_to_screen_name !== null;
	var user = this.user.screen_name;
	var repliedUser = '';
	
	var topic = this.metadata.topic;
	
	var created_date_str_key = this.created_at.getFullYear() + "_" + 
							   (this.created_at.getMonth() + 1) + "_" +
								this.created_at.getDate();
								
	if (isRT){
		var rtUser = this.retweeted_status.user.screen_name;
		
		emit({"topic":topic,"user":user,"date":created_date_str_key}, { outlinks : [rtUser] , retweet : [rtUser], mention : [], reply : [], indegree : 0, outdegree: 1 });
		emit({"topic":topic,"user":rtUser,"date":created_date_str_key}, { outlinks : [], retweet : [], mention : [], reply : [] , indegree : 1, outdegree : 0 });
		
	} else if (isReply){
		var repliedUser = this.in_reply_to_screen_name;
		emit({"topic":topic,"user":user,"date":created_date_str_key}, { outlinks : [repliedUser], retweet : [], mention : [], reply : [repliedUser] , indegree : 0, outdegree : 1 });
		emit({"topic":topic,"user":repliedUser,"date":created_date_str_key}, { outlinks : [], retweet : [], mention : [], reply : [] , indegree : 1, outdegree : 0 });
	}
	
	if (!isRT){
		var mentions = this.entities.user_mentions;
		for (var i = 0; i < mentions.length;i++){
			var mention = mentions[i];
			var mentionUser = mention.screen_name;
			if (mentionUser !== repliedUser){
				emit({"topic":topic,"user":user,"date":created_date_str_key}, { outlinks : [mentionUser] , retweet : [], mention : [mentionUser], reply : [], indegree : 0, outdegree : 1 });
				emit({"topic":topic,"user":mentionUser,"date":created_date_str_key}, { outlinks : [], retweet : [], mention : [], reply : [] , indegree : 1, outdegree : 0 });
			}
		}
	}
	
}