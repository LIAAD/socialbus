Getting started
================

The following makes it simple to start a SocialBus instance. 


Quick Start
-------------

.. toctree::
	:maxdepth: 1

Create a SocialBus instance
******************************
Once you have installed SocialBus you can use the built-in target for creating new projects::

	twitterecho create-instance my-project

This creates a new directory named after your application and containing the project structure below:

	.. code-block:: bash

		$TWITTERECHO_HOME   
			+ bin                  	--> main binaries
			+ scripts               --> scripts
			+ conf                 	--> configuration artifacts 
			+ realtime          	--> location of storm topologies
			+ crawlers  		--> location of crawlers
			  + streaming
			  + user_profiles
			  + user_network
			+ modules              	
			  + sentiment_analysis
			  + language_detection
			  + topic_detection
			  + user_influence
			+ widgets              	--> location of widgets
			  + sentibubbles
			  + usernetwork
			+ extensions
			  + solr 			--> solr schemes

			+ libraries 		--> lib depencies	  


Start SocialBus
******************
To start all SocialBus services run the following command::

	twitterecho start-all

It will start all the following services::

	twitterecho start-storm
	twitterecho start-crawler
	twitterecho start-ui
	twitterecho start-dashboard

This will start up a servlet container (Tomcat). Once the command says the container has started and prints out the URL, copy the link to a browser and view the page.

	- http://localhost:8080/scopes
	- http://localhost:3030/dashboard

Congratulations, you have started your SocialBus!

Configure environment
---------------------

The file `$TWITTERECHO_HOME/conf/twitterecho.conf` which contains details of the database the application uses. This allows you to set common settings:: 

	[logs]
	#where to log
	logpath=/var/log/twitterecho
	# Verbose logging output.
	verbose = false

	[data]
	#where to store temp files
	tmppath=/var/tmp/twitterecho

	[mongodb]
	# MongoDB deployment settings (optional)
	mongodb.host= localhost
	mongodb.port= 27018
	# Specify custom database name
	mongodb.dbname= twitterecho

	[solr]
	# Apache Solr deployment settings (optional)
	solr.host= 192.168.102.195
	solr.port= 8080
	solr.core= portugal
	solr.data= /var/twitterecho/data/solr

	# Storm deployment settings (optional)
	[stream-processing]
	storm.host=localhost

	# RabbitMQ deployment settings (optional)
	[queues]
	amqp.host=192.168.102.195
	amqp.port=5672
	amqp.user=guest
	amqp.pass=guest
	amqp.topic=tweets