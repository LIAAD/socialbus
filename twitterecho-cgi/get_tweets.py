#!/usr/bin/python
# -*- coding: utf8 -*-
"""
Authors: Bruno Tavares

Creation data: 03/10/2012

    This script is a python SERVER-SIDE rest webservice.
        - Inputs: beginDate, minFreqEdge, minFreqNode
        - Outputs: json structure with 'edges' and 'nodes' [beginDate; endDate]
                    and section (Educação, Política, etc...)

How to use it:
    /cgi-bin/Verbetes/getNet.py?beginDate=2010-12-01&minFreqEdge=3&minFreqNode=5
"""

import sys
reload( sys )
sys.setdefaultencoding( 'utf8' )

import os
import cgi
import json
import time
import re
import memcache
import datetime
import urllib2
import httplib2
from urllib import urlencode
from urllib2 import ProxyHandler, build_opener, install_opener, Request, urlopen

t0 = time.time( )

SORT_BY_RECENTS = "created_at desc"
SORT_BY_HOT = "retweet_count desc"

#arrancar o cache
mc = memcache.Client(['127.0.0.1:11211'], debug=0)

#read get params
form = cgi.FieldStorage( )
_sort = form.getvalue("sort") if 'sort' in form else 'recent'
_start = form.getvalue("start") if 'start' in form else '0'
_cache_timeout = int(form.getvalue("cache_timeout")) if 'cache_timeout' in form else 180

print 'Content-Type: application/json'
# print "Content-type: text/html"
print

_sort_value = SORT_BY_RECENTS
if(_sort == "hot"):
    _sort_value = SORT_BY_HOT

#cacular data atual
now = time.time()
# cache_key = datetime.datetime.fromtimestamp(now).strftime('%Y-%m-%d_%H:%M')
cache_key = "_"
cache_key = cache_key + "_sort=" + str(_sort) + "_start=" + _start

cache_status = True
cache_value = mc.get(cache_key)

if(cache_value == None):
    cache_status = False

    #urlAuth = "http://reaction.fe.up.pt/brazil/tweets/select?q=*:*&rows=25"
    urlAuth = "http://192.168.102.190:8080/brazil/tweets/select?q=*:*&rows=25&topic=protestos_brasil"

    params = urlencode( { 'sort' : _sort_value , 'start' : _start  } )

    req = Request( urlAuth , params )
    
    response = urlopen( req ).read( )
    
    print response
    #set cache
    mc.set(cache_key, response,_cache_timeout)
else:
    print cache_value

sys.stderr.write( '[TwitterEcho::get_tweets::cache_timeout=' +str(_cache_timeout)+ '::sort' + _sort + '::start' + _start + '::cache_key=' +cache_key +'::]cache_status=' +str(cache_status) + '(' + str( time.time( ) - t0 ) + 's)\n' )
# sys.stderr.write( '[TwitterEcho::get_tweets::use_cache_key=' +cache_key +']\n')
# sys.stderr.write( '[TwitterEcho::get_tweets::cache_status=' +str(cache_status) +']\n')






