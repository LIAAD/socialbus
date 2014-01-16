from pymongo import Connection
from bson.code import Code
from datetime import datetime

mongo = Connection('192.168.102.195', 27017)
db_socialecho = mongo.socialecho
fb_posts = db_socialecho["fb_posts"]
fb_comments = db_socialecho["fb_comments"]

for post in fb_posts:
	print "insert tweet from ", tweet["created_at"]
	twitterecho_tweets_collection.save(tweet)	

print "Done"


mongoexport --host 192.168.102.195 --db socialecho --collection fb_posts --csv --out fb_posts.csv --fields publish_time,from_name,from_id,page_id,page_url,topic,post_id,post_url,_source,type,shares_count,likes_count,message
mongoexport --host 192.168.102.195 --db socialecho --collection fb_comments --csv --out fb_comments.csv --fields publish_time,from_name,from_id,page_id,page_url,topic,post_id,post_url,_source,like_count,message
mongoexport --host 192.168.102.195 --db socialecho --collection fb_pages --csv --out fb_pages.csv --fields page_id,page_url,username,name,likes_count,talking_about_count

shares_count,likes_count,comments_count,from_id,_source,_id,page_id,page_url,topic,post_url,type,message

_id" : NumberLong("560013724042990"),
	"tags" : "Autarquicas",
	"page_id" : NumberLong("560013724042990"),
	"page_url" : "http://www.facebook.com/RicardoRio2013",
	"name" : "Ricardo Rio",
	"username" : "RicardoRio2013",
	"category" : null,
	"likes_count" : 12217,
	"talking_about_count" : 1722,
	"were_here_count" : 0,
	"is_verified" : false,
	"load_comments" : true,
	"last_processing" : null,
	"dateCreated" : "2013-09-10T14:42:24+0000",
	"lastUpdated" : "2013-09-10T14:42:24+0000",
	"active" : true

"_id" : "677854382258923_22286374",
	"message" : "sem dúvida. Abr",
	"page_id" : "560013724042990",
	"page_url" : "http://www.facebook.com/RicardoRio2013",
	"from_name" : "Tio Nando Nando",
	"from_id" : "100003036641017",
	"publish_time" : "2013-09-10T14:34:44+0000",
	"like_count" : 0,
	"topic" : "Autarquicas",
	"post_id" : "560013724042990_677854422258919",
	"post_url" : "http://www.facebook.com/photo.php?fbid=677854382258923&set=a.564044510306578.123284.560013724042990&type=1&relevant_count=1",
	"collected_at" : "2013-09-10T14:43:26+0000",
	"dateCreated" : null,
	"lastUpdated" : null,
	"_source" : "page_polling"

	"_id" : "560013724042990_677854422258919",
	"page_id" : "560013724042990",
	"page_url" : "http://www.facebook.com/RicardoRio2013",
	"topic" : "Autarquicas",
	"post_url" : "http://www.facebook.com/photo.php?fbid=677854382258923&set=a.564044510306578.123284.560013724042990&type=1&relevant_count=1",
	"type" : "photo",
	"message" : "Ambiente incrível na Malafaia, onde Ricardo Rio discursa perante mais de 3000 pessoas. Não há dúvidas, este é um Tempo Novo Para Braga! Tudo e Todos Por Braga, com Ricardo Rio!",
	"story" : null,
	"publish_time" : "2013-09-10T14:11:47+0000",
	"updated_time" : "2013-09-10T14:34:44+0000",
	"shares_count" : 11,
	"likes_count" : 62,
	"comments_count" : 0,
	"from_id" : "560013724042990",
	"from_name" : "Ricardo Rio",
	"collected_at" : "2013-09-10T14:43:26+0000",
	"_source" : "page_polling"