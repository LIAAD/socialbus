Configuring Facebook Consumer
############################

Clients are instances used to monitor and search posts from Facebook. Users can define which keywords they are interested in.
When a SocialBus Facebook Consumer instance finds a post it sends to the server. 
You can deploy as many consumers as you want.

SocialBus Facebook Consumer follows the structure below.

	.. code-block:: bash

			+ lib
			+ bin
			+ logs
			- conf
				+ examples
				log4j.properties
				client.conf
				filter.conf
				logback-smtp-client.xml
						
			socialbus-facebook-consumer.jar
			LICENSE

Socialecho Server is mandatory for Facebook. Specifying **rabbitmq.host** option is mandatory.

	.. code-block:: bash
	
		## SETTINGS

		## Server connection config
		# this options defines where the client will send posts
		rabbitmq.host=localhost

		# defines where the client will save posts (optional)
		home.dir =/tmp/stream/
		
		# request interval in minutes
		facebook.search.interval=5
		facebook.page.monitor.interval=5

		# authentication
		facebook.application.key=<your-app-key>
		facebook.application.secret=<your-app-secret-key>

To run this client just run::

	> bin/startup.sh [-config] [-filter]
	
If not informed the system will assume the following values for each argument::

	-config = conf/client.conf 
	-filter = None (Mandatory)
	
Optionaly you can specify where your config, oauth and filter file are::

	> bin/startup.sh -config=conf/client.conf -filter=my_filter.conf
	
Watching logs
--------------
While consumer is running it prints the message on log file at logs/socialbus-facebook-consumer.out. 
 
	.. code-block:: bash
	
		tail -f logs/socialbus-facebook-consumer.out

Filtering posts by topic
--------------------------

The config file **examples/filter_by_topics.conf** contains an example to monitor tweets by keywords.
	.. code-block:: bash

		# This option defines the topic name of your messages. 
		# Messages are indexed according their topic.
		topic.name=my_topic

		# file containing filters
		keywords.file=./examples/keywords.txt


This example filter file contains the following.

	.. code-block:: bash

		> cat ./examples/keywords.txt
		android
		iphone
		windowsphone
		ios7

IMPORTANT: Be careful with relative path when defining **filter.file** option. Try to use absolute path.

