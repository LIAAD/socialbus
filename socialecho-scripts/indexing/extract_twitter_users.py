#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This program loads tweets collection and extract its users
'''

import csv,os, sys, urllib,urllib2, datetime,requests,json,time
import sys
import re
from datetime import date
from json import loads
from urllib2 import urlopen
from optparse import OptionParser
from pprint import pprint
from datetime import datetime

from datasources import MongoDataSource

def compute_user_data(tweet):
    user_collection = mongo_conn.database["twitter_users"]
    
    print "processing tweet ", tweet["id"]
    
    # metadata = tweet["metadata"]
    user = tweet["user"]
    user["_id"] = user["id"]
    metadata = {"type":"undefined", "portuguese_origin":"undefined"}
    user["metadata"] = metadata
    
    user_collection.insert(user)
    # print user

def load_tweets():
    tweets_info_col = mongo_conn.get_collection("twitter")

    tweets = tweets_info_col.find().sort("created_at", -1)
    for tweet in tweets :
        try:
            compute_user_data(tweet)
        except TypeError, e:
            print e

if __name__ == "__main__":
    # Command Line Arguments Parser
    cmd_parser = OptionParser(version="%prog 0.1")
    cmd_parser.add_option("-H", "--host", type="string", action="store", dest="mongo_host", help="Mongo host")
    cmd_parser.add_option("-D", "--database", type="string", action="store", dest="mongo_database", help="Mongo database")
    cmd_parser.add_option("-P", "--port", type="string", action="store", dest="mongo_port", help="Mongo port", default="27017")

    (cmd_options, cmd_args) = cmd_parser.parse_args()

    if not (cmd_options.mongo_host or cmd_options.mongo_database or cmd_options.topic):
        cmd_parser.print_help()
        sys.exit(3)

    print "##################################################################"
    print " [INFO] Loading tweets from MongoDB "
    print "------------------------------------------------------------------"
        
    print "Creating MongoDB connection"
    mongo_conn = MongoDataSource()
    mongo_conn.open_connection({"host":cmd_options.mongo_host,"port":int(cmd_options.mongo_port),"database":cmd_options.mongo_database})

    user_collection = mongo_conn.database["twitter_users"]
    user_collection.ensure_index('metadata.type')
    user_collection.ensure_index('metadata.portuguese_origin')
    
    load_tweets()
        
    print "------------------------------------------------------------------"
    print " [INFO] Done "


