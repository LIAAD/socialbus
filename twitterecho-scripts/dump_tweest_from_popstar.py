from pymongo import Connection
from bson.code import Code
from datetime import datetime

mongo_popstar = Connection('192.168.102.194', 27017)
mongo_popstar.popstar.authenticate('backend', 'starback_PASS')
db_popstar = mongo_popstar.popstar
#popstar_tweets_collection = db_popstar["twitterecho"]
popstar_tweets_collection = db_popstar["tweets"]
# 2011-08-21
# 2011-12-12
# 2012-12-17
start_date = datetime.strptime('Jul 03 2013', '%b %d %Y')
end_date = datetime.strptime('Jul 08 2013', '%b %d %Y')
date_range_query = {"tweet_date": {"$gte": start_date,"$lte": end_date}}
popstart_tweets = popstar_tweets_collection.find(date_range_query).sort("tweet_date", 1)

mongo_twitterecho = Connection('192.168.102.195', 27017)
db_twitterecho = mongo_twitterecho.twitterecho
twitterecho_tweets_collection = db_twitterecho["tweets_popstar"]

# Log every 1000 lines.
# LOG_EVERY_N = 10000
# i = 0
for tweet in popstart_tweets:
	# if (i % LOG_EVERY_N) == 0:
	print "insert tweet from ", tweet["tweet_date"]
	# i = i + 1
	
	tweet["created_at"] = tweet["tweet_date"]
	tweet["datasource_s"] = "popstar_mention_tweets"
	twitterecho_tweets_collection.save(tweet)	

print "Done"
