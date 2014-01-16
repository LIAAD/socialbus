"""
=========================
load documents from solr 
=========================
"""

from multiprocessing import Pool
from os.path import dirname, join
from urllib import quote_plus
import sys
from httplib import HTTPConnection
import string
import time
import optparse
import simplejson as json
from bson.code import Code
import pymongo
from pymongo import Connection
from datetime import datetime
import base64
import string
import urllib
import csv

# Timeout on the HTTP connection
TIMEOUT = 60.0   
HTTP_USERNAME = "socialecho"
HTTP_PASSWORD = "socialecho_pass"

#pagination
QUERY = "*:*"
PAGE_SIZE = 40000
URL_TEMPLATE = "http://$host:$port/$context/select?wt=json&q=$query&sort=created_at%20desc&rows=$rows&start=$offset"

# pool = Pool(processes=8)

def convert_string_to_date(dt_string):
    dt_format = "%Y-%m-%dT%H:%M:%SZ"
    date_object = datetime.strptime(dt_string, dt_format)
    return date_object

def handle_document(candidato,document):
    print "processing ", document["id"],document["created_at"]

    document["created_at"] = convert_string_to_date(document["created_at"])
    document["user_created_at"] = convert_string_to_date(document["user_created_at"])
    # document["timestamp"] = convert_string_to_date(document["timestamp"])
    
    document["_id"] = document["id"]
    document["_source_index"] = "reaction.fe.up.pt"
    document["_candidato"] = candidato
    
    sendToMongo("tweets",document)

def loop_paginate_results(candidato, _query,page):

    offset = page * PAGE_SIZE

    url = build_url(query=_query,rows=PAGE_SIZE,offset=offset)
    result = handle_request(url)

    rows = result["response"]["docs"]
    
    # pool.map(handle_document,rows)
    for doc in result["response"]["docs"]:
        handle_document(candidato,doc)

def handle_request(url):
    print "GET ",url

    params = None
    headers = {}

    auth = base64.encodestring('%s:%s' % (HTTP_USERNAME, 
                                          HTTP_PASSWORD)).replace('\n', '')
    headers = {"Authorization": "Basic %s" % auth}

    con = HTTPConnection("reaction.fe.up.pt", 80, timeout=float(TIMEOUT))
    try:
        con.request('GET', url,params,headers)
        response = con.getresponse()

        content = response.read()
        assert 'HTTP ERROR: 500' not in content, 'Server error %s' % content

        # print content
        jsonresponse = json.loads(content)
        return jsonresponse

    except KeyboardInterrupt:
        print "\n"

def find_total_docs(_query): 
    url = build_url(query=_query,rows=0,offset=0)
    result = handle_request(url)

    numFound = result["response"]["numFound"]
    print "number of found documents : ", numFound

    return numFound

def build_url(query,rows,offset):    
    url = string.Template(URL_TEMPLATE).substitute(
        host="reaction.fe.up.pt",
        port=80,
        context="portugal/tweets",
        rows=rows,
        offset=offset,
        query=quote_plus(query))

    return url

def sendToMongo(collection,jsonObj):
    print "indexing into mongodb", collection
    
    # db[collection].update(key,jsonObj,upsert=True)
    db[collection].save(jsonObj)

def run():
    candidatos = []
    with open("candidatos.csv", 'rb') as csvfile:
        reader = csv.reader(csvfile, delimiter=',')
        for row in reader:
            candidatos.append(row)

    for candidato in candidatos:
        load_tweets_by_candidate(candidato)

def build_query(candidato):
    query = "("
    query = query + " " +candidato[0]
    query = query + " OR " + candidato[1] 
    query = query + " OR screen_name:" + candidato[0] 
    query = query + " OR mentions:" + candidato[0]
    query = query + ")"
    query = query + " AND created_at:[NOW-2MONTH TO NOW]"
    # query = quote_plus(query)
    # query = """ricardorio OR "Ricardo Rio" OR screen_name:ricardorio AND created_at:[NOW-2MONTH TO NOW]"""
    return query

def load_tweets_by_candidate(candidato):
    query = build_query(candidato)
    total_docs = find_total_docs(query)

    num_pages = total_docs/PAGE_SIZE + 1

    for page in range(num_pages):
        print "loading page ",page
        loop_paginate_results(candidato[1],query,page)

if __name__ == '__main__':
    # Create a connection to mongodb
    connection = Connection("192.168.102.195")
    db = connection["socialecho"]

    run()
