Getting started
================

The following makes it simple to start a SocialEcho instance. 

Quick Start
-------------

- Create a SocialEcho instance
	- Start services
	- Configure a Data Source (Optional)
	- Configure Apache Solr (Optional)
	- Configure MongoDB (Optional)

Create a SocialEcho instance
******************************
Once you have installed SocialEcho you can use the built-in target for creating new projects::

	twitterecho create-instance my-project

This creates a new directory named after your application and containing the project structure below::

	%PROJECT_HOME%
	    + twitterecho-app
	       + conf                 		---> configuration artifacts 
	           + mongodb          		---> optional mongodb config
	           + solr             		---> optional solr config
	           + storm             		---> optional storm config

	       + realtime          	  	---> location of storm topologies
	       + crawlers             	
	       		+ streaming-client  	---> location of crawlers
	       + widgets              		---> location of widgets
	       		+ sentibubbles
	       		+ usernetwork
	       
	   + libraries 				---> lib depencies
	   + scripts                  		---> scripts
	   + web-app
	       + scope-manager			---> Interface manage scopes
	       + search-ui			---> Interface for searching
	       + system-dashboard		---> system activity dashboard


Start SocialEcho
******************
To start your SocialEcho application run the following command::

	twitterecho run-app

This will start up a servlet container (Tomcat). Once the command says the container has started and prints out the URL, copy the link to a browser and view the page.

	http://localhost:8080/twitterecho

Congratulations, you have started your first SocialEcho application!


Configure environment
---------------------

The file `twitterecho-app/conf/DataSource.groovy` which contains details of the database the application uses. This allows you to set common settings. 

By default, an application is configured to use an in-memory HSQLDB database for development and testing. If you want to use MySQL in production for example, you would have to configure it here::
	

	# By default twitterecho server stores tracking configurations using hsdb, 
	# but you can specify your own jdbc supported database
	[mysql]
	mysql.host=localhost
	mysql.port=3304
	mysql.dbname=twitterecho

The file `twitterecho-app/conf/Config.groovy` which contains details of the database the application uses. This allows you to set common settings.::

	[logs]
	#where to log
	logpath=/var/log/twitterecho

	[data]
	#where to store temp files
	tmppath=/var/tmp/twitterecho

	[general]
	# Verbose logging output.
	verbose = false

	[solr]
	# Apache Solr deployment settings (optional)
	solr.host= 192.168.102.195
	solr.port= 8080
	solr.core= portugal
	solr.data= /var/twitterecho/data/solr

	[mongodb]
	# MongoDB deployment settings (optional)
	mongodb.host= localhost
	mongodb.port= 27018
	# Specify custom database name
	mongodb.dbname= twitterecho

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
