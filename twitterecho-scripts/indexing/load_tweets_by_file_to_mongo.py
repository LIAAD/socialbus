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

from datasources import *

# from sylvester.tokenizer import Tokenizer
import sys
import re

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-F", "--file", type="string", action="store", dest="file_content", help="File with tweets")
cmd_parser.add_option("-H", "--host", type="string", action="store", dest="mongo_host", help="Mongo host")
cmd_parser.add_option("-D", "--database", type="string", action="store", dest="mongo_database", help="Mongo database")
cmd_parser.add_option("-P", "--port", type="string", action="store", dest="mongo_port", help="Mongo port")

(cmd_options, cmd_args) = cmd_parser.parse_args()

if not (cmd_options.file_content or cmd_options.mongo_host or cmd_options.solr_address or cmd_options.mongo_database):
        cmd_parser.print_help()
        sys.exit(3)

def parseToJson(json_string):
   # print json_string
    json_tweet = json.loads(json_string.decode("utf-8"))
    
    indexTweetIntoMongo(json_tweet)

def indexTweetIntoMongo(_object):
    mongo_conn.index(_object,"tweets_search_api")

print "##################################################################"
print " [INFO] Loading last hour tweets file : " + cmd_options.file_content
print "------------------------------------------------------------------"

print "Creating MongoDB connection"
mongo_conn = MongoDataSource()
mongo_conn.open_connection({"host":cmd_options.mongo_host,"port":int(cmd_options.mongo_port),"database":cmd_options.mongo_database})

f = open(cmd_options.file_content, 'rb')
lines = f.readlines()
urls = []
for row in lines:
	parseToJson(row)
	
print "##################################################################"
print " [INFO] Finished the indexing for " + cmd_options.file_content
print "##################################################################"  
