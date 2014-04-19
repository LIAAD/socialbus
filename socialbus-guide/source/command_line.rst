Configuring Twitter Consumer
############################

Clients are instances used to monitor and consume tweets from Twitter. Users can define which keywords or users they are interested in.
When a SocialBus Twitter Consumer instance receives a tweet it sends the tweet to the server. 
You can deploy as many consumers as you want.

SocialBus Twitter Consumer follows the structure below.

	.. code-block:: bash

			+ lib
			+ bin
			+ logs
			- conf
				+ examples
				log4j.properties
				client.conf
				filter_by_keywords.conf
				filter_by_users.conf
				logback-smtp-client.xml
						
			socialecho-twitter-consumer.jar
			LICENSE


You can use this client as standalone without depending on the Server. To run as standalone just specify the option
**home.dir**. It will persists all tweets as files.

If you want to use it with the Server specifying **rabbitmq.host** option is mandatory.

	.. code-block:: bash
	
		## SETTINGS

		## Server connection config
		# this options defines where the client will send the tweets
		rabbitmq.host=localhost

		# this options defines where the client will save the tweets
		home.dir =/tmp/stream/

To run this client just run::

	> bin/startup.sh [-config] [-oauth] [-filter]
	
If not informed the system will assume the following values for each argument::

	-config = conf/client.conf 
	-oauth = None (Mandatory)
	-filter = None (Optional)
	
Optionaly you can specify where your config, oauth and filter file are::

	> bin/startup.sh  -oauth=your-tokens.csv	-config=conf/client.conf -filter=my_filter.conf
	
Collecting tweets from sample stream
------------------------------------
Collecting tweets from sample stream is plain easy. If you don't inform **-filter** at startup it will collect tweets from  `Twitter Sample Stream <https://dev.twitter.com/docs/api/1.1/get/statuses/sample>`_ endpoint.


Watching logs
--------------
While consumer is running it prints the message on log file at logs/socialecho-twitter-consumer.out. 
 
	.. code-block:: bash
	
		tail -f logs/socialecho-twitter-consumer.out

Filtering tweets by topic
--------------------------

The config file **examples/filter_by_topics.conf** contains an example to monitor tweets by keywords.
	.. code-block:: bash

		# This option defines the topic name of your messages. 
		# Messages are indexed according their topic.
		topic.name=my_topic

		#filter type (USERS | KEYWORDS)
		filter.type=KEYWORDS

		# file containing filters
		filter.file=./examples/my_topic_filters.txt

This example filter file contains the following.

	.. code-block:: bash

		> cat ./examples/my_topic_filters.txt
		android
		iphone
		windowsphone
		ios7

IMPORTANT: Be careful with relative path when defining **filter.file** option. Try to use absolute path.
	
Filtering tweets by users
--------------------------

The config file **examples/filter_by_users.conf** contains an example to monitor users.

	.. code-block:: bash
		
		# This option defines the topic name of your messages. 
		# Messages are indexed according their topic.
		topic.name=my_community

		#filter type (USERS | KEYWORDS)
		filter.type=USERS

		# file containing filters
		filter.file=./examples/my_community_filters.txt
	
This example filter file contains the following.

	.. code-block:: bash
	
		> cat ./examples/my_community_filters.txt
		1234
		566788

IMPORTANT: Be careful with relative path when defining **filter.file** option. Try to use absolute path.

Setting up alerts when fails
-----------------------
You will want to know when things goes wrong. SocialBus can send you email when a failure occours specifying your email configuration at **logback-smtp-client.xml**.
	.. code-block:: xml

		<?xml version="1.0" encoding="UTF-8" ?>
		<configuration scan="true" scanPeriod="60 seconds" debug="true">
			<statusListener class="ch.qos.logback.core.status.OnConsoleStatusListener" />

			<appender name="EMAIL" class="ch.qos.logback.classic.net.SMTPAppender">
				<smtpHost>smtp.gmail.com</smtpHost>
				<smtpPort>465</smtpPort>

		        <to>change_it@email.com</to>
				<from>change_it@email.com</from>
				<subject>SocialBus Alert!</subject>

				<username>change_it@email.com</username>
				<password>change_it</password>
				<SSL>true</SSL>

				<layout class="ch.qos.logback.classic.PatternLayout">
					<pattern>%date %-5level %logger{35} - %message%n</pattern>
				</layout>

				<filter class="ch.qos.logback.classic.filter.ThresholdFilter">
					<level>ERROR</level>
				</filter>
			</appender>

			<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
				<encoder class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
					<pattern>%date %-5level %logger{0} - %message%n</pattern>
				</encoder>
			</appender>

			<root level="INFO">
				<!--
					Uncomment this line after setup your smtp above. 

					<appender-ref ref="EMAIL" />
				-->
				<appender-ref ref="STDOUT" />
			</root>
		</configuration>

