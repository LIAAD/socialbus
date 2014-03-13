Facebook scripts
===========================
This repo contain scripts for text minning porpouses.

Using [Facebook Graph API](https://developers.facebook.com/docs/reference/api/) the script "fb_load_comments.py" loads comments from an specific facebook object (e.g. posts, photos, etc). 

# Command-Line Tools

### Usage

**Basic Usage**

	./fb_load_comments.py -h

	  Options:
	  --version             show program's version number and exit
	  -h, --help            show this help message and exit

	  -T OAUTH_ACCESS_TOKEN, --OAuthToken=OAUTH_ACCESS_TOKEN 
	  						Facebook OAuth Access Token (You can get your on here
	                        https://developers.facebook.com/tools/explorer/) (optional)

	  -F FACEBOOK_OBJECT_ID, --FacebookObjectId=FACEBOOK_OBJECT_ID
	                        Facebook Object Id. You can find more about it here
	                        https://developers.facebook.com/docs/reference/api/

	  -L PAGE_SIZE, --PageSize=PAGE_SIZE
	                        Pagination size (1-999) (optional)

	  -O OFFSET, --Offset=OFFSET
	                        Pagination offset (optional)

	  -S SOLR_ADDRESS, --Solr=SOLR_ADDRESS
	                        Solr Address (optional)

**Getting Facebook object id**

From a Facebook post with the following link https://www.facebook.com/photo.php?fbid=10151165605121749&set=a.53081056748.66806.6815841748&type=1&theater the Facebook Object Id in this case is 10151165605121749 represented by fbid parameter on the URL.


	./fb_load_comments.py -F 10151165605121749

**Authentication**

In order to get more results you must provide an OAuth access token. You can get a temporary one at [Facebook Developer Explorer](https://developers.facebook.com/tools/explorer/)

**Indexing**

Its possible to index the comments on [Apache Solr](http://lucene.apache.org/solr/).

	./fb_load_comments.py -F 10151165605121749 -S http://localhost:8983/solr

Your Solr schema.xml must be something like this :

	<fields>   
		<field name="id" 	type="string"	indexed="true" stored="true" required="true"/>
		<field name="user_id"	type="string"	indexed="true" stored="true"/>
		<field name="name"	type="string"	indexed="true" stored="true"/>
		<field name="in_reply_to_object_id" type="string"	indexed="true" stored="true"/>
		<field name="like_count"	type="int"	indexed="true" stored="true"/>
		<field name="text"	type="text_general" indexed="true" stored="true" />
		<field name="created_at"	type="date"	indexed="true" stored="true"/>			
	<fields>

**Important note**

For performance reasons it is not responsible for committing. 
I highly recommend leaving this job for Apache Solr. Take a look at [autoCommit](http://wiki.apache.org/solr/SolrConfigXml#Update_Handler_Section) feature.

**TODO**

Send documents to Solr in batch.

### Dependencies

It depends on [facepy](https://github.com/jgorset/facepy) and [solrpy](http://code.google.com/p/solrpy/). 


