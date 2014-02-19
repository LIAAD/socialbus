#! /usr/bin/python
#

import fnmatch
import os
import csv,os, sys, urllib,urllib2, datetime,requests,json,solr,time
from datetime import date
from datetime import datetime
from json import loads
from urllib2 import urlopen
from optparse import OptionParser
from pprint import pprint

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-D", "--dir", type="string", action="store", dest="directory", help="Base dir with json files")
cmd_parser.add_option("-H", "--host", type="string", action="store", dest="mongo_host", help="Mongo host")
cmd_parser.add_option("-B", "--database", type="string", action="store", dest="mongo_database", help="Mongo database")
cmd_parser.add_option("-P", "--port", type="string", action="store", dest="mongo_port", help="Mongo port")

(cmd_options, cmd_args) = cmd_parser.parse_args()

#homeDir = "/home/reaction/workspace/twitterecho/solr_indexing/"
homeDir = ""

if not (cmd_options.directory or cmd_options.mongo_host or cmd_options.mongo_database):
    cmd_parser.print_help()
    sys.exit(3)

def readTweetsFromFile(fileName):
    print " [INFO] Loading file ... "
    os.system("python "+ homeDir +"load_tweets_by_file_to_mongo.py -F " + fileName + " -H " + cmd_options.mongo_host + " -P " + cmd_options.mongo_port + " -D " + cmd_options.mongo_database)

startTime = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')

print "##################################################################"
print " [INFO] Loading files from dir : " + cmd_options.directory
print " [INFO] Start at  " + startTime
print "------------------------------------------------------------------"
    
matches = []
for root, dirnames, filenames in os.walk(cmd_options.directory):
  for filename in fnmatch.filter(filenames, '*'):
      matches.append(os.path.join(root, filename))

for fileName in matches:
	print readTweetsFromFile(fileName)
    
endTime = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')    
print "##################################################################"
print " [INFO] Finished the indexing for " + cmd_options.directory
print " [INFO] Done " + endTime
print "##################################################################"  
    

