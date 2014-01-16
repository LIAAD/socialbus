#!/usr/local/bin/python
import memcache
import time
import datetime
import cgi
import os, sys
from cgi import escape

print "Content-type: text/html"

## end of http://code.activestate.com/recipes/52220/ }}}


form = cgi.FieldStorage( )
_sort = form.getvalue("sort")
_start = form.getvalue("start")


print "sort", _sort
print "start",_start

#arrancar o cache
mc = memcache.Client(['127.0.0.1:11211'], debug=0)


#cacular data atual

now = time.time()
st = datetime.datetime.fromtimestamp(now).strftime('%Y-%m-%d_%H:%M')
print(st)

mc.set(st, "Some value",1)

#time.sleep(2)


#get solr
#http://reaction.fe.up.pt/brazil/tweets/select?q=*:*%20AND%20retweet_count:%5B0%20TO%20*%5D&rows=25
# params sort, start



value = mc.get(st)
print value

