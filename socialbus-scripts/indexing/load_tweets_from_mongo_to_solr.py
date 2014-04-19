#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This program loads tweets from MongoDB collection to Solr
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

from datasources import *


def index_into_solr(tweet):
    print "processing tweet ", tweet["id"]
    solr_conn.index(tweet)

def load_tweets_from_mongo_to_solr():
    tweets_info_col = mongo_conn.get_collection("tweets")

    solr_conn.resetIndex()

    tweets = tweets_info_col.find({"metadata.topic":cmd_options.topic}).limit(1000).sort("created_at", -1)
    for tweet in tweets :
        try:
            index_into_solr(tweet)
        except TypeError, e:
            print e

if __name__ == "__main__":
    # Command Line Arguments Parser
    cmd_parser = OptionParser(version="%prog 0.1")
    cmd_parser.add_option("-H", "--host", type="string", action="store", dest="mongo_host", help="Mongo host")
    cmd_parser.add_option("-D", "--database", type="string", action="store", dest="mongo_database", help="Mongo database")
    cmd_parser.add_option("-P", "--port", type="string", action="store", dest="mongo_port", help="Mongo port")
    cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="solr_address", help="Solr address")
    cmd_parser.add_option("-T", "--topic", type="string", action="store", dest="topic", help="Topic")

    (cmd_options, cmd_args) = cmd_parser.parse_args()

    if not (cmd_options.mongo_host or cmd_options.solr_address or cmd_options.mongo_database):
        cmd_parser.print_help()
        sys.exit(3)

    print "##################################################################"
    print " [INFO] Loading tweets from MongoDB to Solr "
    print "------------------------------------------------------------------"
        
    print "Creating MongoDB connection"
    mongo_conn = MongoDataSource()
    mongo_conn.open_connection({"host":cmd_options.mongo_host,"port":int(cmd_options.mongo_port),"database":cmd_options.mongo_database})

    print "Creating Solr connection"
    solr_conn = SolrDataSource()
    solr_conn.open_connection({"address":cmd_options.solr_address})
    
    load_tweets_from_mongo_to_solr()
        
    print "------------------------------------------------------------------"
    print " [INFO] Done "


