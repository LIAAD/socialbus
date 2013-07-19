#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This program loads tweets from file
'''

import csv,os, sys, urllib,urllib2, datetime,requests,json,solr,time
from datetime import date
from json import loads
from urllib2 import urlopen
from optparse import OptionParser
from pprint import pprint

# from sylvester.tokenizer import Tokenizer
import sys
import re

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="server_address", help="Solr address")
cmd_parser.add_option("-F", "--file", type="string", action="store", dest="file_content", help="File with tweets")
cmd_parser.add_option("-U", "--support_user_index", action="store_false", dest="support_user_index", help="Suport user index", default=False)
(cmd_options, cmd_args) = cmd_parser.parse_args()

if not (cmd_options.file_content or cmd_options.server_address):
    cmd_parser.print_help()
    sys.exit(3)

httpuser = "reaction_solr"
httppass = "r34ct10n@2013!@#$$#@!"

# create a connection to a solr server
#if cmd_options.support_user_index:
s_tweets = solr.SolrConnection(cmd_options.server_address + "/tweets",http_user=httpuser,http_pass=httppass)
#s_users = solr.SolrConnection(cmd_options.server_address + "/users",http_user=httpuser,http_pass=httppass)
#else:    
#    s_tweets = solr.SolrConnection(cmd_options.server_address)

# create instance for Laboreiro's tokenizer
# print "loading tokenizer..."
# tokenizer = Tokenizer()

def parseToJson(json_string):
   # print json_string
    json_tweet = json.loads(json_string.decode("utf-8"))
    
    indexTweetIntoSolr(json_tweet)
    #indexUserIntoSolr(json_tweet["user"])

def indexUserIntoSolr(userJson):

    #Sat Nov 10 01:59:54 +0000 2012              
    userCreatedAt = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.strptime(userJson['created_at'],'%a %b %d %H:%M:%S +0000 %Y'))

    # print str(userJson['id']) + " : " +  userJson['name'].encode("utf-8")

    try:
        s_users.add(
        id=userJson['id'],
        name=userJson['name'],
        screen_name=userJson['screen_name'],
        location=userJson['location'],
        description=userJson['description'],
        lang=userJson['lang'],		
        time_zone=userJson['time_zone'],
        followers_count=userJson['followers_count'],	
        favourites_count=userJson['favourites_count'],
        statuses_count=userJson['statuses_count'],
        friends_count=userJson['friends_count'],
        listed_count=userJson['listed_count'],	
        profile_background_color=userJson['profile_background_color'],	
        profile_background_image_url=userJson['profile_background_image_url'],	
        profile_background_image_url_https=userJson['profile_background_image_url_https'],
        profile_image_url_https=userJson['profile_image_url_https'],
        profile_image_url=userJson['profile_image_url'],
        profile_link_color=userJson['profile_link_color'],	
        profile_sidebar_border_color=userJson['profile_sidebar_border_color'],	
        profile_sidebar_fill_color=userJson['profile_sidebar_fill_color'],	
        profile_text_color=userJson['profile_text_color'],
        url=userJson['url'],
        created_at=userCreatedAt,

        version="12",
        datasource_s="search_api"
        )
    except solr.core.SolrException,e:
        print "Oops! Solr Request Error :" , sys.exc_info()[0]

def indexTweetIntoSolr(json):
    user_data = json['user']
		          
    #Sat Nov 10 01:59:54 +0000 2012              
    userCreatedAt = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.strptime(json['user']['created_at'],'%a %b %d %H:%M:%S +0000 %Y'))
    statusCreatedAt = time.strftime('%Y-%m-%dT%H:%M:%SZ', time.strptime(json['created_at'],'%a %b %d %H:%M:%S +0000 %Y'))

    print statusCreatedAt + " : " +  str(json['id']) + " : " +  json['text'].encode("utf-8");
    
    # _tokenized_text = tokenizer.tokenize(json["text"])	

    _user_mentions = [] 
    _url_mentions = [] 
    _hashtag_mentions = [] 
    
    entities = json['entities']
    
    for user_mention in entities['user_mentions']:
        _user_mentions.append(user_mention["screen_name"])
        
    for url_mention in entities['urls']:
        _url_mentions.append(url_mention["url"])
            
    for hashtag_mention in entities['hashtags']:
        _hashtag_mentions.append(hashtag_mention["text"])
    
    try:
        s_tweets.add(
	    # tokenized_text=_tokenized_text,
        hashtags=_hashtag_mentions,
        urls=_url_mentions,
        mentions=_user_mentions,

        user_id=json['user']['id'],
        name=json['user']['name'],
        screen_name=json['user']['screen_name'],
        location=json['user']['location'],
        description=json['user']['description'],
        user_created_at=userCreatedAt,	
        lang=json['user']['lang'],		
        time_zone=json['user']['time_zone'],
        followers_count=json['user']['followers_count'],	
        favourites_count=json['user']['favourites_count'],
        statuses_count=json['user']['statuses_count'],
        friends_count=json['user']['friends_count'],
        listed_count=json['user']['listed_count'],	
        profile_background_color=json['user']['profile_background_color'],	
        profile_background_image_url=json['user']['profile_background_image_url'],	
        profile_background_image_url_https=json['user']['profile_background_image_url_https'],
        profile_image_url_https=json['user']['profile_image_url_https'],
        profile_image_url=json['user']['profile_image_url'],
        profile_link_color=json['user']['profile_link_color'],	
        profile_sidebar_border_color=json['user']['profile_sidebar_border_color'],	
        profile_sidebar_fill_color=json['user']['profile_sidebar_fill_color'],	
        profile_text_color=json['user']['profile_text_color'],
        id=json['id'],
        text=json['text'],
        created_at=statusCreatedAt,
        source=json['source'],
        status_in_reply_to_screen_name=json['in_reply_to_screen_name'],
        status_in_reply_to_user_id=json['in_reply_to_user_id'],
        status_in_reply_to_status_id=json['in_reply_to_status_id'],
	    
        is_rt_b=json["retweeted"],
        retweet_count=int(json['retweet_count']),
        version='10',
        datasource_s="json"
        )
    except solr.core.SolrException:
        print "Oops! Solr Request Error :" , sys.exc_info()[0]


print "##################################################################"
print " [INFO] Loading last hour tweets file : " + cmd_options.file_content
print "------------------------------------------------------------------"
	
f = open(cmd_options.file_content, 'rb')
lines = f.readlines()
urls = []
for row in lines:
	parseToJson(row)
	
print "##################################################################"
print " [INFO] Finished the indexing for " + cmd_options.file_content
print "##################################################################"  
