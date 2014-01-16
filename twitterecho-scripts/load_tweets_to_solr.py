#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This program loads tweets from MongoDB collection to Solr
'''

from multiprocessing import Pool
import csv,os, sys, urllib,urllib2, datetime,requests,json,time
import sys
import re
from datetime import date
from json import loads
from urllib2 import urlopen
from optparse import OptionParser
from pprint import pprint
from datetime import datetime
import traceback
from datasources import *

PAGE_SIZE = 10000

def index_into_solr(tweet):
    print tweet
    tweet["id"] = tweet["_id"]
    print "processing tweet ", tweet["id"], tweet["created_at"]

    try:
        solr_conn.index(tweet)
    except :
        print "###Error###:" , sys.exc_info()[0]
        tb = traceback.format_exc()
        print tb

def load_tweets_from_page(current_page):
    offset = current_page * PAGE_SIZE

    collection = mongo_conn.get_collection("tweets_popstar")
    start_date = datetime.strptime('Jul 03 2013', '%b %d %Y')
    end_date = datetime.strptime('Jul 08 2013', '%b %d %Y')
    date_range_query = {"created_at": {"$gte": start_date, "$lte": end_date}}

    tweets = collection.find(date_range_query).skip(offset).limit(PAGE_SIZE).sort("created_at", 1)

    return tweets

def count_total_rows():
    tweets_info_col = mongo_conn.get_collection("tweets_popstar")
    
    start_date = datetime.strptime('Jul 03 2013', '%b %d %Y')
    end_date = datetime.strptime('Jul 08 2013', '%b %d %Y')
    date_range_query = {"created_at": {"$gte": start_date, "$lte": end_date}}
    total = tweets_info_col.find(date_range_query).count()

    return total

def load_tweets():
    total = count_total_rows()
    print total
    total_pages = total / PAGE_SIZE + 1

    for current_page in range(total_pages):
        print "processing_page ", str(current_page)
        tweets = load_tweets_from_page(current_page);

        # for tweet in tweets:
        #     index_into_solr(tweet)
        pool.map(index_into_solr,tweets)

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-H", "--host", type="string", action="store", dest="mongo_host", help="Mongo host")
cmd_parser.add_option("-D", "--database", type="string", action="store", dest="mongo_database", help="Mongo database")
cmd_parser.add_option("-P", "--port", type="string", action="store", dest="mongo_port", help="Mongo port")
cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="solr_address", help="Solr address")
cmd_parser.add_option("-O", "--order", type="string", action="store", dest="order", help="Sql where statement order direction", default="ASC")
# cmd_parser.add_option("-T", "--topic", type="string", action="store", dest="topic", help="Topic")

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

pool = Pool(processes=8)    
if __name__ == "__main__":
    load_tweets()
        
    print "------------------------------------------------------------------"
    print " [INFO] Done "


