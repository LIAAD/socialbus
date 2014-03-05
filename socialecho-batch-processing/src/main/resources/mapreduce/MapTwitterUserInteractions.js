function map() {
	var isRT = this.hasOwnProperty('retweeted_status');
	var isReply = this.hasOwnProperty('in_reply_to_screen_name') && this.in_reply_to_screen_name !== null;
	var user = this.user.screen_name;
	var topic = this.metadata.topic;
	
	var created_date_str_key = this.created_at.getFullYear() + "_" + 
							   (this.created_at.getMonth() + 1) + "_" +
								this.created_at.getDate();
		
	if (isRT){
		var rtUser = this.retweeted_status.user.screen_name;
		emit({"topic":topic,"user":user,"date":created_date_str_key}, { rt : 0, rtDone : 1, replies : 0, repliesReceived :0 , mentions : 0, mentionsReceived : 0, tweets : 0 , indegree : 0, outdegree: 1  });
		emit({"topic":topic,"user":rtUser,"date":created_date_str_key}, { rt : 1, rtDone : 0, replies : 0, repliesReceived :0 , mentions : 0, mentionsReceived : 0, tweets : 0 , indegree : 1, outdegree: 0  });
		
	} else if (isReply){
		var repliedUser = this.in_reply_to_screen_name;
		emit({"topic":topic,"user":user,"date":created_date_str_key}, { rt : 0, rtDone : 0, replies : 1, repliesReceived :0 , mentions : 0, mentionsReceived : 0, tweets : 0 ,indegree : 0, outdegree: 1  });
		emit({"topic":topic,"user":repliedUser,"date":created_date_str_key}, { rt : 0, rtDone : 0, replies : 0, repliesReceived :1 , mentions : 0, mentionsReceived : 0, tweets : 0 ,indegree : 1, outdegree: 0  });
	}
	
	if (!isRT){
		var mentions = this.entities.user_mentions;
		for (var i = 0; i < mentions.length;i++){
			var mention = mentions[i];
			var mentionUser = mention.screen_name;
			if (mentionUser !== repliedUser){
				emit({"topic":topic,"user":user,"date":created_date_str_key}, { rt : 0, rtDone : 0, replies : 0, repliesReceived :0 , mentions : 1, mentionsReceived : 0, tweets : 0 ,indegree : 0, outdegree: 1  });
				emit({"topic":topic,"user":mentionUser,"date":created_date_str_key}, { rt : 0, rtDone : 0, replies : 0, repliesReceived :0 , mentions : 0, mentionsReceived : 1, tweets : 0,indegree : 1, outdegree: 0  });
			}
		}
	}
	
	emit({"topic":topic,"user":user,"date":created_date_str_key}, { rt : 0, rtDone : 0, replies : 0, repliesReceived :0 , mentions : 0, mentionsReceived : 0, tweets : 1 , indegree : 0, outdegree: 0 });
}