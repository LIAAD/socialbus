"""
=========================
examples:
python get_timeline_data.py -H reaction.fe.up.pt -G HOUR -L DAY -T 1
python get_timeline_data.py -H reaction.fe.up.pt -G HOUR -L DAY -T 5
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
TIMEOUT = 60.0   
HTTP_USERNAME = "socialecho"
HTTP_PASSWORD = "socialecho_pass"
# USER_HTTP_AUTH = False

#pagination
QUERY = "*:*"
# URL_TEMPLATE = "http://$host:$port/$context/select?wt=json&q=*:*&sort=created_at%20desc&rows=$rows&start=$offset"
URL_TEMPLATE = "http://$host:$port/$context/select?wt=json&q=*:*&facet=true&facet.date=created_at&facet.date.start=NOW-$times$start_date_times_less&facet.date.end=NOW&facet.date.gap=%2B1$time_gap&rows=0"
OUPUTFILE = "/var/www/twitterecho-widgets/timeline"

parser = optparse.OptionParser()
parser.add_option('-H', '--solr-host', dest='host',help='The hostname Solr is running on, default localhost')
parser.add_option('-C', '--solr-context', dest='context', default='portugal/tweets',help='Solr context')
parser.add_option('-P', '--solr-port', dest='port', default='80',help='The port the the server is running on, default 80')
parser.add_option('-G', '--gap', dest='time_gap', default='DAY',help='(MONTH|DAY|HOUR)')
parser.add_option('-L', '--less', dest='time_less', default='DAY',help='(MONTH|DAY|HOUR)')
parser.add_option('-T', '--times', dest='times',help='Number of times to compute. If time_gap = HOUR, default = 24')


def get_solr_results():

    url = build_url()
    result = handle_request(url)

    ignore = ["end","start","gap"]
    facet_dates = result["facet_counts"]["facet_dates"]["created_at"]

    outputfile = OUPUTFILE +  "/last_" + str(options.times) + "_" + options.time_less + "_by_" + options.time_gap + ".txt"
    f = open(outputfile, 'w')

    for dt in facet_dates:
        if not (dt in ignore):
            print dt,facet_dates[dt]
            f.write(dt + "," + str(facet_dates[dt]) + "\n")

def handle_request(url):
    print "GET ",url

    params = None
    headers = {}

    # if(options.use_http_auth):
    auth = base64.encodestring('%s:%s' % (HTTP_USERNAME, 
                                          HTTP_PASSWORD)).replace('\n', '')
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

def build_url():    
    url = string.Template(URL_TEMPLATE).substitute(
        host=options.host,
        port=options.port,
        context=options.context,
        time_gap=options.time_gap,
        start_date_times_less=options.time_less,
        times=options.times,
        query=quote_plus(QUERY))
    return url

def run():    
    get_solr_results()

if __name__ == '__main__':
    options, args = parser.parse_args() 

    ntimes = 24
    if not (options.time_gap or options.host):
        parser.print_help()
        sys.exit(3)

    # if(options.time_gap == "DAY"):
    #     ntimes = 5
    # if(options.time_gap == "HOUR"):
    #     ntimes = 24
    # if(options.time_gap == "MONTH"):
    #     ntimes = 12
    
    # ntimes = options.times

    run()
