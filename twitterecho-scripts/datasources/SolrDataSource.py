#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This class defines Solr interface
'''

from DataSource import *

import json,solr,time
import solr
import sys
import re
import datetime
from datetime import date
from datetime import datetime
from json import loads
import traceback
from sylvester.tokenizer import Tokenizer
from TwitterFeatureExtractor import *

print "- Loading TwitterFeatureExtractor"
twitter_feature_extractor = TwitterFeatureExtractor()

tokenizer = Tokenizer()

class SolrDataSource(DataSource):        # define parent class

	connection = None;

	def __from_float_to_isodate(self,value):
		# _date = datetime.fromtimestamp(long(value))
		_date_isoformat = value.strftime('%Y-%m-%dT%H:%M:%SZ')
		return _date_isoformat

	def __from_str_to_list(self,full_string):
		_result = []
		if(full_string != ""):
			split_string = full_string.split(",")
			for value in split_string:
				_result.append(value)

		return _result


	def resetIndex(self):
		print "are you really sure??"
		# self.connection.delete_query('*:*')
		# self.connection.commit()

	def open_connection(self,options):
		print 'Opening solr connection'

		if not options["address"]:
			return False
		
		self.connection = solr.SolrConnection(options["address"],http_user="fcul_labs", http_pass="fcul_labs_pass")


	def list_media(self,tweet):
		# "media" : [{ 	

		# "media_url" : "http://pbs.twimg.com/media/BM7EoYeCEAAB8i0.jpg",
		# "type" : "photo", 	
		# "url" : "http://t.co/F3qtTcmYes" 
		# } ],

		result = []
		try:
			medias = tweet["entities"]["media"]
			for media in medias:
				media_url = media["media_url"]
				url = media["url"]
				result.append (media_url + "#_####_#" + url)
		except :
			print "erro"
		pass

		# if(tweet.has_key["entities"]):
		# 	if(tweet.has_key["media"]):
				

		return result

	def index(self,object):
		# print "Indexing into Solr"
		tweet = object
		try:
	  #      <field name="client" type="string" indexed="true" stored="true"/> 
			# <field name="topic" type="string" indexed="true" stored="true"/> 
			
			# <field name="user_id" type="string" indexed="true" stored="true" required="true" /> 
			# <field name="id" type="string" indexed="true" stored="true" required="true" /> 
			# <field name="unique_id" type="string" indexed="true" stored="true"/> 

			# <field name="text" type="text_general" indexed="true" stored="true"
			# 	termVectors="true" termPositions="true" termOffsets="true"/>
				
			# <field name="tokenized" type="laboreiro_tokenized_text" indexed="true" stored="true"
			# 	termVectors="true" termPositions="true" termOffsets="true"/>

			# <field name="description" type="text_general" indexed="true" stored="true"/>
			# <field name="name" type="string" indexed="true" stored="true"/>
			# <field name="screen_name" type="string" indexed="true" stored="true"/>
			# <field name="location" type="string" indexed="true" stored="true"/>
			# <field name="user_created_at" type="date" indexed="true" stored="true"/>
			# <field name="lang" type="string" indexed="true" stored="true"/>
			
			# <field name="language" type="string" indexed="true" stored="true"/>
			
			# <field name="hashtags" type="string" indexed="true" stored="true" multiValued="true"/>           
			# <field name="mentions" type="string" indexed="true" stored="true" multiValued="true"/>
			# <field name="urls" type="string" indexed="true" stored="true" multiValued="true"/>
			# <field name="emoticons" type="string" indexed="true" stored="true" multiValued="true"/>
			
			# <field name="time_zone" type="string" indexed="true" stored="true"/>
			# <field name="followers_count" type="int" indexed="true" stored="true"/>
			# <field name="favourites_count" type="int" indexed="true" stored="true"/>
			# <field name="friends_count" type="int" indexed="true" stored="true"/>
			# <field name="listed_count" type="int" indexed="true" stored="true"/>
			# <field name="profile_background_color" type="string" indexed="true" stored="true"/>
			# <field name="profile_background_image_url" type="string" indexed="true" stored="true"/>
			# <field name="profile_background_image_url_https" type="string" indexed="true" stored="true"/>
			# <field name="profile_image_url_https" type="string" indexed="true" stored="true"/>
			# <field name="profile_image_url" type="string" indexed="true" stored="true"/>
			# <field name="profile_link_color" type="string" indexed="true" stored="true"/>
			# <field name="profile_sidebar_border_color" type="string" indexed="true" stored="true"/>
			# <field name="profile_sidebar_fill_color" type="string" indexed="true" stored="true"/>
			# <field name="profile_text_color" type="string" indexed="true" stored="true"/>
			# <field name="created_at" type="date" indexed="true" stored="true"/>
			# <field name="source" type="string" indexed="true" stored="true"/>
			# <field name="status_in_reply_to_screen_name" type="string" indexed="true" stored="true"/>
			# <field name="status_in_reply_to_user_id" type="string" indexed="true" stored="true"/>
			# <field name="status_in_reply_to_status_id" type="string" indexed="true" stored="true"/>
			# <field name="retweet_count" type="int" indexed="true" stored="true"/>
			# <field name="statuses_count" type="int" indexed="true" stored="true"/>
			# <field name="geo_coordinates" type="location" indexed="true" stored="true"/>

			# user_website_s
			# profile_link_color_s

			
			hashtags = []
			urls = []
			mentions = []
			_retweet_count = 0
			_is_rt = False
			_description = ""
			_time_zone = ""
			_favourites_count=tweet["favourites_count"]
			_statuses_count = 0
			_followers_count = 0
			_friends_count = 0
			_profile_background_image_url = ""
			_listed_count = 0
			_location = ""
			_lang = ""

			if(tweet.has_key("lang")):
				_lang = tweet["lang"]


			if(tweet.has_key("is_retweet")):
				_is_rt = tweet["is_retweet"]

			if(tweet.has_key("retweet_count")):
				_retweet_count = tweet["retweet_count"]	

			if(tweet.has_key("hashtag_entities")):
				hashtags = tweet["hashtag_entities"]	

			if(tweet.has_key("url_entities")):
				urls = tweet["url_entities"]

			if(tweet.has_key("user_mention_entities")):
				mentions = tweet["user_mention_entities"]

			if(tweet.has_key("description")):
				_description=tweet["description"]	

			if(tweet.has_key("time_zone")):
				_time_zone=tweet["time_zone"]

			if(tweet.has_key("favourites_count")):
				_favourites_count=tweet["favourites_count"]	

			if(tweet.has_key("statuses_count")):
				_statuses_count=tweet["statuses_count"]	

			if(tweet.has_key("friends_count")):
				_friends_count=tweet["friends_count"]	

			if(tweet.has_key("followers_count")):
				_followers_count=tweet["followers_count"]	

			if(tweet.has_key("listed_count")):
				_listed_count=tweet["listed_count"]	
			
			if(tweet.has_key("profile_background_image_url")):
				_profile_background_image_url=tweet["profile_background_image_url"]	

			if(tweet.has_key("location")):
				_location = tweet["location"]

			# media
			# _created_at = self.__from_float_to_isodate(tweet["created_at"])
			# _user_created_at = self.__from_float_to_isodate(tweet["user"]["created_at"])
			# _user_created_at = self.__from_float_to_isodate(tweet["user_created_at"])
	        	       
			print tweet['user_created_at'], type(tweet['user_created_at'])
			print tweet['created_at'], type(tweet['created_at'])

			if(isinstance(tweet['created_at'],unicode)):
				_user_created_at = tweet['user_created_at']
				_created_at = tweet['created_at']
			else:	
				if(isinstance(tweet['created_at'],datetime)):
					_user_created_at = tweet['user_created_at'].strftime('%Y-%m-%dT%H:%M:%SZ')
					_created_at = tweet['created_at'].strftime('%Y-%m-%dT%H:%M:%SZ')
			
			text = ""

			if(tweet.has_key("text")):
				_text = tweet["text"]
				_tokenized_text = tokenizer.tokenize(tweet["text"])

			if(len(hashtags) == 0):
				hashtags = twitter_feature_extractor.getHashtags(_tokenized_text)

			if(len(mentions) == 0):
				mentions = twitter_feature_extractor.getUserMentions(_tokenized_text)

			if(len(urls) == 0):
				urls = twitter_feature_extractor.getUrls(_tokenized_text)
			
			# if(tweet.has_key["retweeted_status"]):
			# 	if(tweet["retweeted_status"].has_key["retweet_count"]):

			_id = tweet["id"]
			_text = tweet["text"]
			# _retweet_count = tweet["retweet_count"]
			_id = tweet["id"]
			_text = tweet["text"]
			# _profile_image_url_https = tweet["profile_image_url_https"]
			_profile_image_url = tweet["profile_image_url"]
			_screen_name=tweet["screen_name"]
			_user_id=tweet["user_id"]
			_name=tweet["name"]
			# _location = tweet["location"]
			# _lang = _
			
			
			_source = tweet["source"]

			
			# _profile_background_image_url_https = tweet["profile_background_image_url_https"]

			# _media = self.list_media(tweet)
			# _is_rt = False
			
			# _polarity = tweet["metadata"]["polarity"] if tweet.has_key("metadata") else None
			# _topic = tweet["metadata"]["topic"] if tweet.has_key("metadata") else None
			
			self.connection.add(
				# medias=_media,
				is_rt_b=_is_rt,
				id=_id,
				_datasource_s=tweet["_source_index"],
				# language=tweet["metadata"]["language"],
		    	# topic="portugal",
				# client="legacy_solr_pattie",
				tokenized_text=_tokenized_text,

				text=_text,
				created_at=_created_at,
				user_created_at=_user_created_at,
				retweet_count=_retweet_count,
				# profile_image_url_https=_profile_image_url_https,
				profile_image_url=_profile_image_url,
				source=_source,

				statuses_count=_statuses_count,
				friends_count=_friends_count,
				followers_count=_followers_count,
				favourites_count=_favourites_count,
				listed_count=_listed_count,
				lang=_lang,
				time_zone=_time_zone,
				location=_location,
				screen_name=_screen_name,
				description=_description,
				name=_name,
				
				user_id=_user_id,

				profile_background_image_url=_profile_background_image_url,
				# profile_background_image_url_https=_profile_background_image_url_https,

				hashtags=hashtags,
				mentions=mentions,
				# emoticons=emoticons,
				urls=urls
		    )

		except solr.core.SolrException:
			print "Oops! Solr Request Error :" , sys.exc_info()[0]
	        tb = traceback.format_exc()
	        print tb

	def close_connection(self):
		self.connection.close()