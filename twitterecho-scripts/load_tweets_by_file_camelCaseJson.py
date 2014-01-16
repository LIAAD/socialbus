#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This program loads tweets from file
'''

from multiprocessing import Pool
import csv,os, sys, urllib,urllib2, datetime,requests,json,solr,time
from datetime import date
from json import loads
from urllib2 import urlopen
from optparse import OptionParser
from pprint import pprint

import traceback
from sylvester.tokenizer import Tokenizer
import sys
import re

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="server_address", help="Solr address")
cmd_parser.add_option("-F", "--file", type="string", action="store", dest="file_content", help="File with tweets")
# cmd_parser.add_option("-U", "--support_user_index", action="store_false", dest="support_user_index", help="Suport user index", default=False)
cmd_parser.add_option("-A", "--use_auth", action="store_true", dest="use_auth", help="Suport authentication", default=False)
(cmd_options, cmd_args) = cmd_parser.parse_args()

if not (cmd_options.file_content or cmd_options.server_address):
    cmd_parser.print_help()
    sys.exit(3)

httpuser = "reaction_solr"
httppass = "r34ct10n@2013!@#$$#@!"

# create a connection to a solr server
#if cmd_options.support_user_index:
# s_tweets = solr.SolrConnection(cmd_options.server_address + "/tweets",http_user=httpuser,http_pass=httppass)
s_tweets = solr.SolrConnection(cmd_options.server_address + "/tweets")
#s_users = solr.SolrConnection(cmd_options.server_address + "/users",http_user=httpuser,http_pass=httppass)
#else:    
#    s_tweets = solr.SolrConnection(cmd_options.server_address)

# create instance for Laboreiro's tokenizer
# print "loading tokenizer..."
tokenizer = Tokenizer()

def parseToJson(json_string):
   # print json_string
    json_tweet = json.loads(json_string.decode("utf-8"))
    
    indexTweetIntoSolr(json_tweet)    

def indexTweetIntoSolr(json):
    user_data = json['user']
                  
    #Sat Nov 10 01:59:54 +0000 2012              
    statusCreatedAt = datetime.datetime.fromtimestamp(long(json['createdAt'])/1000.0).strftime('%Y-%m-%dT%H:%M:%SZ')
    userCreatedAt = datetime.datetime.fromtimestamp(long(user_data['createdAt'])/1000.0).strftime('%Y-%m-%dT%H:%M:%SZ')
    
    print statusCreatedAt + " : " +  str(json['id']) + " : " +  json['text'].encode("utf-8");
    
    _tokenized_text = tokenizer.tokenize(json["text"])  

    _user_mentions = [] 
    _url_mentions = [] 
    _hashtag_mentions = [] 
    
    for user_mention in json['userMentionEntities']:
        _user_mentions.append(user_mention["screenName"])
        
    for url_mention in json['urlentities']:
        _url_mentions.append(url_mention["url"])
            
    for hashtag_mention in json['hashtagEntities']:
        _hashtag_mentions.append(hashtag_mention["text"])
    
    try:
        s_tweets.add(
        
        user_id=json['user']['id'],
        name=json['user']['name'],
        screen_name=json['user']['screenName'],
        location=json['user']['location'],
        description=json['user']['description'],
        lang=json['user']['lang'],      
        time_zone=json['user']['timeZone'],
        followers_count=json['user']['followersCount'], 
        favourites_count=json['user']['favouritesCount'],
        statuses_count=json['user']['statusesCount'],
        friends_count=json['user']['friendsCount'],
        listed_count=json['user']['listedCount'],   
        profile_background_color=json['user']['profileBackgroundColor'],    
        profile_background_image_url=json['user']['profileBackgroundImageUrl'], 
        profile_background_image_url_https=json['user']['profileBackgroundImageUrlHttps'],
        profile_image_url_https=json['user']['profileImageUrlHttps'],
        profile_image_url=json['user']['profileImageURL'],
        profile_link_color=json['user']['profileLinkColor'],    
        profile_sidebar_border_color=json['user']['profileSidebarBorderColor'], 
        profile_sidebar_fill_color=json['user']['profileSidebarFillColor'], 
        profile_text_color=json['user']['profileTextColor'],
        id=json['id'],
        text=json['text'],
        created_at=statusCreatedAt,
        source=json['source'],
        status_in_reply_to_screen_name=json['inReplyToScreenName'],
        status_in_reply_to_user_id=json['inReplyToUserId'],
        status_in_reply_to_status_id=json['inReplyToStatusId'],
        retweet_count=json['retweetCount'],
        
        tokenized_text=_tokenized_text,
        hashtags=_hashtag_mentions,
        urls=_url_mentions,
        mentions=_user_mentions,

        user_created_at=userCreatedAt,  

        is_rt_b=json["retweet"],
        
        version='10',
        datasource_s="json_camel_case"
        )
    except solr.core.SolrException:
        print "Oops! Solr Request Error :" , sys.exc_info()[0]
        tb = traceback.format_exc()
        print tb
    

print "##################################################################"
print " [INFO] Loading last hour tweets file : " + cmd_options.file_content
print "------------------------------------------------------------------"
    
f = open(cmd_options.file_content, 'rb')
lines = f.readlines()
urls = []

pool = Pool(processes=10)
pool.map(parseToJson,lines)
# for row in lines:
    # parseToJson(row)
    
print "##################################################################"
print " [INFO] Finished the indexing for " + cmd_options.file_content
print "##################################################################"  
