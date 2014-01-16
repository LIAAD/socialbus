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

# Timeout on the HTTP connection
TIMEOUT = 30.0   
# HTTP_USERNAME = "socialecho"
# HTTP_PASSWORD = "socialecho_pass"
# USER_HTTP_AUTH = False

#pagination
QUERY = "*:*"
PAGE_SIZE = 10000
URL_TEMPLATE = "http://$host:$port/$context/select?wt=json&q=*:*&sort=created_at%20desc&rows=$rows&start=$offset"

pool = Pool(processes=8)

parser = optparse.OptionParser()
parser.add_option('-H', '--solr-host', dest='host', default='localhost',
    help='The hostname Solr is running on, default localhost')
parser.add_option('-C', '--solr-context', dest='context', default='portugal/tweets',
    help='Solr context')
parser.add_option('-P', '--solr-port', dest='port', default='80',
    help='The port the the server is running on, default 80')
parser.add_option("-M", "--mongo-host", type="string", action="store", 
    dest="mongo_host", help="Mongodb host ")
parser.add_option("-D", "--mongo-db", type="string", action="store", 
    dest="mongo_db", help="Mongo database name")
parser.add_option("-S", "--use-http-auth", action="store_true", 
    dest="use_http_auth", help="use http authentication")
parser.add_option("-U", "--solr-http-auth-user", type="string", action="store", 
    dest="solr_http_auth_user", help="http user", default="socialecho")
parser.add_option("-Z", "--solr-http-auth-pass", type="string", action="store", 
    dest="solr_http_auth_pass", help="http pass", default="socialecho_pass")

def convert_string_to_date(dt_string):
    dt_format = "%Y-%m-%dT%H:%M:%SZ"
    date_object = datetime.strptime(dt_string, dt_format)
    return date_object

def handle_document(document):
    print "processing ", document["id"],document["created_at"]

    document["created_at"] = convert_string_to_date(document["created_at"])
    document["user_created_at"] = convert_string_to_date(document["user_created_at"])
    # document["timestamp"] = convert_string_to_date(document["timestamp"])
    
    document["_id"] = document["id"]
    document["_source_index"] = options.host

    sendToMongo("tweets",document)

def loop_paginate_results(page):

    offset = page * PAGE_SIZE

    url = build_url(rows=PAGE_SIZE,offset=offset)
    result = handle_request(url)

    rows = result["response"]["docs"]
    
    pool.map(handle_document,rows)
    # for doc in result["response"]["docs"]:
        # handle_document(doc)

def handle_request(url):
    print "GET ",url

    params = None
    headers = {}

    if(options.use_http_auth):
        auth = base64.encodestring('%s:%s' % (options.solr_http_auth_user, 
                                              options.solr_http_auth_pass)).replace('\n', '')
        headers = {"Authorization": "Basic %s" % auth}

    con = HTTPConnection(options.host, options.port, timeout=float(TIMEOUT))
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

def find_total_docs(): 
    url = build_url(rows=0,offset=0)
    result = handle_request(url)

    numFound = result["response"]["numFound"]
    print "number of found documents : ", numFound

    return numFound

def build_url(rows,offset):    
    url = string.Template(URL_TEMPLATE).substitute(
        host=options.host,
        port=options.port,
        context=options.context,
        rows=rows,
        offset=offset,
        query=quote_plus(QUERY))

    return url

def sendToMongo(collection,jsonObj):
    print "indexing into mongodb", collection
    
    # db[collection].update(key,jsonObj,upsert=True)
    db[collection].save(jsonObj)

def run():
    
    total_docs = find_total_docs()

    num_pages = total_docs/PAGE_SIZE + 1

    for page in range(num_pages):
        print "loading page ",page
        loop_paginate_results(page)

if __name__ == '__main__':
    options, args = parser.parse_args() 

    if not (options.mongo_host or options.mongo_db or options.host):
        parser.print_help()
        sys.exit(3)

    # Create a connection to mongodb
    connection = Connection(options.mongo_host)
    db = connection[options.mongo_db]

    run()
