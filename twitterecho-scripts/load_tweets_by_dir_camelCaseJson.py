#! /usr/bin/python
#

from multiprocessing import Pool
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
cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="server_address", help="Solr address")
cmd_parser.add_option("-D", "--dir", type="string", action="store", dest="directory", help="Base dir with json files")
cmd_parser.add_option("-U", "--support_user_index", action="store_false", dest="support_user_index", help="Suport user index", default=False)

(cmd_options, cmd_args) = cmd_parser.parse_args()

#homeDir = "/home/reaction/workspace/twitterecho/solr_indexing/"
homeDir = ""

if not (cmd_options.directory or cmd_options.server_address):
    cmd_parser.print_help()
    sys.exit(3)

def readTweetsFromFile(fileName):
    print " [INFO] Loading file ... "
    if cmd_options.support_user_index:
        os.system("python "+ homeDir +"load_tweets_by_file_camelCaseJson.py -U -F " + fileName + " -S " + cmd_options.server_address)
    else:
        os.system("python "+ homeDir +"load_tweets_by_file_camelCaseJson.py -F " + fileName + " -S " + cmd_options.server_address)

startTime = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')

print "##################################################################"
print " [INFO] Loading files from dir : " + cmd_options.directory
print " [INFO] Start at  " + startTime
print "------------------------------------------------------------------"
    
matches = []
for root, dirnames, filenames in os.walk(cmd_options.directory):
  for filename in fnmatch.filter(filenames, '*'):
      matches.append(os.path.join(root, filename))

pool = Pool(processes=8)
pool.map(readTweetsFromFile,matches)

# for fileName in matches:
	# print readTweetsFromFile(fileName)
    
endTime = datetime.now().strftime('%Y-%m-%dT%H:%M:%SZ')    
print "##################################################################"
print " [INFO] Finished the indexing for " + cmd_options.directory
print " [INFO] Done " + endTime
print "##################################################################"  
    

