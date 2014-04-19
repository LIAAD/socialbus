Network analysis scripts
===========================


## Command-Line Tools

### Usage

The script *"$TWITTERECHO_HOME/scripts/extract_users_interactions.py"* computes interactions between users and build a network of retweets, mentions and replies.
You can also import this dataset into [gephi](http://gephi.org) for advanced analysis.

**Basic Usage**

	./extract_users_interactions.py -h


	  Options:
	  --version             show program's version number and exit
	  -h, --help            show this help message and exit

	  -D , --database		= mongodb database name
	  -S , --server			= mongodb host
	  -M , --mapcollection 	= input collection


![screenshoot](/markengine/images/twitterecho/screenshot_users_network.png)

### Dependencies

It depends on [pymongo] and [neo4j]