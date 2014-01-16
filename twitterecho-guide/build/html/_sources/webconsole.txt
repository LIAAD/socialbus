Web console
===========

The SocialEcho webconsole provides an HTTP-based API for monitoring of your SocialEcho server, along with a browser-based UI. 

Features include
-----------------

- List topics, clients hosts, system, uptime info, etc.
- Monitor tweets counter length, globally, per topic and per client.
- Visualize tweets being processed in realtime

Screenshots
------------

.. image:: _static/twitterecho/webconsole.png
   :width: 550px


Getting started
----------------

The web UI is located at: http://[server-name]:9090/.
The HTTP API its located at: http://[server-name]:9191/api/sysinfo

.. Configuration
.. ----------------
.. Options are managed through the main SocialEcho configuration file **conf/server.conf**.
..  
.. .. code-block:: bash
.. 
.. 	#default port 9090
.. 	http.console.port = 9090	

Accessing system information via HTTP REST API
------------------------------------------------

It provides information like topics, clients, up time, tweets counter, exentensions enabled and basic system info. 
It will serve only resources of type application/json.::

	curl -l http://[server-name]:9191/api/sysinfo

Here an example of an instance running Twitter Sample Streaming.
Results:

	.. code-block:: javascript
	
		{
		stats: {
		  topics: {
		  	sample: {
		  	 count: 128,
		  	 last_message_at: "Thu Oct 10 15:56:12 WEST 2013",
		  	 running_since: "Thu Oct 10 15:54:56 WEST 2013"
		  	}
		  },
		  _global_: {
		  	count: 127,
		  	last_message_at: "Thu Oct 10 15:56:12 WEST 2013",
		  	running_since: "Thu Oct 10 15:52:47 WEST 2013"
		  },
		  clients: {
		  	192.168.1.70: {
		  	 count: 128,
		  	 last_message_at: "Thu Oct 10 15:56:12 WEST 2013",
		  	 running_since: "Thu Oct 10 15:54:56 WEST 2013"
		  	}
		  }
		},
		extensions: {
		 adapters: [
		 "pt.sapo.labs.api.impl.adapters.ConsoleStatusAdapter",
		 "pt.sapo.labs.api.impl.adapters.FileStatusAdapter",
		 "pt.sapo.labs.api.impl.adapters.MessageBrokerStatusAdapter"
		 ],
		 preprocessors: [
		 "pt.sapo.labs.api.impl.handlers.EntitiesExtractionStatusMetadataHandler",
		 "pt.sapo.labs.api.impl.handlers.TokenizerStatusMetadataHandler"
		 ]
		},
		sysinfo: {
		   host: "Sapo-Labs-UP-Servers-Mac-mini.local",
		   os.arch: "x86_64",
		   java.version: "1.6.0_51",
		   java.vendor: "Apple Inc.",
		   os.name: "Mac OS X"
		},
		version: "SocialEcho-Server 1.4.1-SNAPSHOT"
		}