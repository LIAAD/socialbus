Configuring Server 
####################

The server is responsable for processing tweets, extracting metadata, indexing, tokenization and other computations.

SocialEcho Server follows the structure below.

	.. code-block:: bash

			+ lib
			+ bin
			+ logs
			- conf
				log4j.properties
				server.conf
		
			socialecho-server.jar
			LICENSE

All server configuration must be provided at **conf/server.conf**.
The option **rabbitmq.host** is mandatory to collect the messages from clients.
As default the server just prints the message on log file at **logs/socialecho.out**. To persist the messages as files you must provide
the option **home.dir**.

	.. code-block:: bash

		#message-broker settings from where 
		#it will collect the messages
		rabbitmq.host=localhost
		rabbitmq.port=5672
	
		# you can have multiple setups running at the same server
		cluster.name=twitterecho-1

		#uncomment this option to save tweets as files 
		home.dir =/tmp/stream/
		
		#uncomment MongoDB options to store tweets as a collection 
		mongodb.active = true
		mongodb.host =localhost
		mongodb.port = 27017
		mongodb.database =socialecho_server_1
		
Command to start the server::

	bin/startup.sh
	
Watching logs
--------------
While server is running it prints the message on log file at logs/socialecho.out. 
 
	.. code-block:: bash
	
		tail -f logs/socialecho.out
	
	
Command to shutdown the server::

	bin/shutdown.sh

Important: shutdown.sh kills all twitterecho-server running instances.



Next
****

One you have it up and running you can check :doc:`webconsole` to monitor stream activity.		