#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This script apply a model to classify users as 'humans' or 'bots'
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

import pymongo
from pymongo import Connection

from datasources import *

import classify_user

def process(tweet,model):
    print "processing tweet ", tweet["id"]
    
    user_type = classify_user.classify([tweet["user"]["id"]], model,5)
    if user_type == []:
        user_type = "undefined"
    
    print "result ", user_type
    
    mongo_conn.get_collection("twitter").update(
                        {'_id':tweet['_id']},
                        {'$set':{'metadata.user.type': user_type}}, 
                        upsert=False, multi=False)
    
def apply_model(model):
    tweets_info_col = mongo_conn.get_collection("twitter")
    tweets_info_col.ensure_index("metadata.user.type")
    
    # metadata.user.type,1
    tweets = tweets_info_col.find({"$or":[
                                    {"metadata.user.type":{"$exists": False}},
                                    {"metadata.user.type":"undefined"}
                                    ]
                                  }).sort("created_at", -1)
    for tweet in tweets :
        try:
            process(tweet,model)
        except TypeError, e:
            print e

if __name__ == "__main__":
    print "##################################################################"
    print " [INFO] Running Bot classifier "
    print "------------------------------------------------------------------"
        
    print "Creating MongoDB connection"
    mongo_conn = MongoDataSource()
    mongo_conn.open_connection({"host":"192.168.102.195","port":int(27017),"database":"socialecho_v05"})

    bot_detection_model = classify_user.initialize_model()
    apply_model(bot_detection_model)
        
    print "------------------------------------------------------------------"
    print " [INFO] Done "


