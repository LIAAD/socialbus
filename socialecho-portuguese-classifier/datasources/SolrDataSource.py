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
		self.connection.delete_query('*:*')
		self.connection.commit()

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
		print "Indexing into Solr"
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

			hashtags = tweet["metadata"]["hashtags"]
			mentions = tweet["metadata"]["mentions"]
			emoticons = tweet["metadata"]["emoticons"]
			urls = tweet["metadata"]["urls"]
			

			# media

			_created_at = self.__from_float_to_isodate(tweet["created_at"])
			_user_created_at = self.__from_float_to_isodate(tweet["user"]["created_at"])
	        
			_text = ""

			if(tweet.has_key("text")):
				_text = tweet["text"]


			_retweet_count = tweet["retweet_count"]
			# if(tweet.has_key["retweeted_status"]):
			# 	if(tweet["retweeted_status"].has_key["retweet_count"]):

			_id = tweet["id"]
			_text = tweet["text"]
			_retweet_count = tweet["retweet_count"]
			_id = tweet["id"]
			_text = tweet["text"]
			_profile_image_url_https = tweet["user"]["profile_image_url_https"]
			_profile_image_url = tweet["user"]["profile_image_url"]
			_screen_name=tweet["user"]["screen_name"]
			_description=tweet["user"]["description"]
			_user_id=tweet["user"]["id"]
			_name=tweet["user"]["name"]
			_location = tweet["user"]["location"]
			_time_zone = tweet["user"]["time_zone"]
			_lang = tweet["user"]["lang"]
			_favourites_count = tweet["user"]["favourites_count"]
			_followers_count = tweet["user"]["followers_count"]
			_friends_count = tweet["user"]["friends_count"]
			_statuses_count = tweet["user"]["statuses_count"]
			_source = tweet["source"]

			_media = self.list_media(tweet)
			_is_rt = False
			
			try :
				_media = self.list_media(tweet["retweeted_status"])
				_retweet_count = tweet["retweeted_status"]["retweet_count"]
				_is_rt = True
				_id = tweet["retweeted_status"]["id"]
				_text = tweet["retweeted_status"]["text"]
				_profile_image_url_https = tweet["retweeted_status"]["user"]["profile_image_url_https"]
				_profile_image_url = tweet["retweeted_status"]["user"]["profile_image_url"]
				_screen_name=tweet["retweeted_status"]["user"]["screen_name"]
				_description=tweet["retweeted_status"]["user"]["description"]
				_user_id=tweet["retweeted_status"]["user"]["id"]
				_name=tweet["retweeted_status"]["user"]["name"]
				_text = tweet["retweeted_status"]["text"]

				_location = tweet["retweeted_status"]["user"]["location"]
				_time_zone = tweet["retweeted_status"]["user"]["time_zone"]
				_lang = tweet["retweeted_status"]["user"]["lang"]
				_favourites_count = tweet["retweeted_status"]["user"]["favourites_count"]
				_followers_count = tweet["retweeted_status"]["user"]["followers_count"]
				_friends_count = tweet["retweeted_status"]["user"]["friends_count"]
				_statuses_count = tweet["retweeted_status"]["user"]["statuses_count"]
				_source = tweet["retweeted_status"]["source"]
				_created_at = self.__from_float_to_isodate(tweet["created_at"])
				_user_created_at = self.__from_float_to_isodate(tweet["retweeted_status"]["user"]["created_at"])

				
			except :
				print "erro"
			pass
			
			# _polarity = tweet["metadata"]["polarity"] if tweet.has_key("metadata") else None
			# _topic = tweet["metadata"]["topic"] if tweet.has_key("metadata") else None
			
			self.connection.add(
				medias=_media,
				is_rt_b=_is_rt,
				id=_id,
		    	language=tweet["metadata"]["language"],
		    	topic=tweet["metadata"]["topic"],
				client=tweet["metadata"]["client"],
				tokenized=tweet["metadata"]["tokenized"],

				text=_text,
				created_at=_created_at,
				user_created_at=_user_created_at,
				retweet_count=_retweet_count,
				profile_image_url_https=_profile_image_url_https,
				profile_image_url=_profile_image_url,
				source=_source,

				statuses_count=_statuses_count,
				friends_count=_friends_count,
				followers_count=_followers_count,
				favourites_count=_favourites_count,
				lang=_lang,
				time_zone=_time_zone,
				location=_location,
				screen_name=_screen_name,
				description=_description,
				name=_name,
				
				user_id=_user_id,

				hashtags=hashtags,
				mentions=mentions,
				emoticons=emoticons,
				urls=urls
		    )

		except solr.core.SolrException:
			print "Oops! Solr Request Error :" , sys.exc_info()[0]

	def close_connection(self):
		self.connection.close()