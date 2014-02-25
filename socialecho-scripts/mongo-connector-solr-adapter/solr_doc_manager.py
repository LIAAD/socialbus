# Copyright 2012 10gen, Inc.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# This file will be used with PyPi in order to package and distribute the final
# product.

"""Receives documents from the oplog worker threads and indexes them
into the backend.

This file is a document manager for the Solr search engine, but the intent
is that this file can be used as an example to add on different backends.
To extend this to other systems, simply implement the exact same class and
replace the method definitions with API calls for the desired backend.
"""
import sys
import logging

from pysolr import Solr
from threading import Timer
from util import verify_url, retry_until_ok


class DocManager():
    """The DocManager class creates a connection to the backend engine and
    adds/removes documents, and in the case of rollback, searches for them.

    The reason for storing id/doc pairs as opposed to doc's is so that multiple
    updates to the same doc reflect the most up to date version as opposed to
    multiple, slightly different versions of a doc.
    """

    def __init__(self, url, auto_commit=True, unique_key='_id'):
        """Verify Solr URL and establish a connection.
        """
        # if verify_url(url) is False:
#             raise SystemError

        self.solr = Solr(url)
        self.unique_key = unique_key
        self.auto_commit = auto_commit

        if auto_commit:
            self.run_auto_commit()

    def stop(self):
        self.auto_commit = False

    def upsert(self, doc):
        """Update or insert a document into Solr

        This method should call whatever add/insert/update method exists for
        the backend engine and add the document in there. The input will
        always be one mongo document, represented as a Python dictionary.
        """
        
        metadata = doc["metadata"]
        
        self.sendDocumentToSolr(doc,metadata)
        
        if("retweeted_status" in doc):
            # retweeted_status_count = doc["retweeted_status"]["retweet_count"]
            self.sendDocumentToSolr(doc["retweeted_status"],metadata)
        
        # {u'type': u'Point', u'coordinates': [13.58223318, 43.18765332]}        
        

    def sendDocumentToSolr(self,doc,metadata):
        # service = doc["metadata"]["service"]
        # retweeted_status_count = 0

        print doc["id"]
        
        doc_coordinate = None
        
        if(doc["coordinates"] != None):
            print doc["coordinates"]
            if(doc["coordinates"]["type"] == "Point"):
                
                doc_coordinate = str(doc["coordinates"]["coordinates"][1]) + "," + str(doc["coordinates"]["coordinates"][0])
                
                print doc_coordinate
                
        # only tweets
        self.solr.add([{
                      "_id":doc["id"],
                      # corrigir essa redundancia
                      "id":doc["id"],
                      "text":doc["text"],
                      "created_at":doc["created_at"],
                      
                      "geo_location":doc_coordinate,
                  
                      "service":metadata["service"],
                      "client":metadata["client"],
                      "topic":metadata["topic"],
                      "tokenized":metadata["tokenized"],

                      "mentions":metadata["mentions"],
                      "hashtags":metadata["hashtags"],
                      "emoticons":metadata["emoticons"],
                  
                      "lang":doc["lang"],
                      "retweeted":doc["retweeted"],
                      # "retweet_count":retweeted_status_count,
                      "retweet_count":doc["retweet_count"],
                      "source":doc["source"],
                  
                      "in_reply_to_status_id":doc["in_reply_to_status_id"],
                      "in_reply_to_user_id":doc["in_reply_to_user_id"],

                      "user_created_at":doc["user"]["created_at"],
                      "screen_name":doc["user"]["screen_name"],
                      "user_id":doc["user"]["id"],
                      "statuses_count":doc["user"]["statuses_count"],
                      "description":doc["user"]["description"],
                      "verified":doc["user"]["verified"],
                      "name":doc["user"]["name"],
                      "profile_background_color":doc["user"]["profile_background_color"],
                      "followers_count":doc["user"]["followers_count"],
                      "profile_image_url_https":doc["user"]["profile_image_url_https"],
                      "profile_background_image_url":doc["user"]["profile_background_image_url"],
                      "friends_count":doc["user"]["friends_count"],
                      "listed_count":doc["user"]["listed_count"],
                      "profile_image_url":doc["user"]["profile_image_url"],
                      "location":doc["user"]["location"],
    
                  
                  }], commit=False)              

    def remove(self, doc):
        """Removes documents from Solr

        The input is a python dictionary that represents a mongo document.
        """
        self.solr.delete(id=str(doc[self.unique_key]), commit=False)

    def search(self, start_ts, end_ts):
        """Called to query Solr for documents in a time range.
        """
        query = '_ts: [%s TO %s]' % (start_ts, end_ts)
        return self.solr.search(query, rows=100000000)

    def _search(self, query):
        """For test purposes only. Performs search on Solr with given query
            Does not have to be implemented.
        """
        return self.solr.search(query, rows=200)

    def commit(self):
        """This function is used to force a commit.
        """
        retry_until_ok(self.solr.commit)

    def run_auto_commit(self):
        """Periodically commits to the Solr server.
        """
        self.solr.commit()
        if self.auto_commit:
            Timer(1, self.run_auto_commit).start()

    def get_last_doc(self):
        """Returns the last document stored in the Solr engine.
        """
        #search everything, sort by descending timestamp, return 1 row
        result = self.solr.search('*:*', sort='_ts desc', rows=1
                                  )

        if len(result) == 0:
            return None

        return result.docs[0]
