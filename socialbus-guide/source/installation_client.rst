Installing the Client
======================

Clients are instances used to monitor and consume tweets from Twitter. Users can define which keywords or users they are interested in.
When a SocialEcho Twitter Consumer instance receives a tweet it sends the tweet to the server. 
You can deploy as many consumers as you want.


These are instructions to deploy an instance of SocialEcho Twitter Consumer.

Requirements
-------------------------

Before installing SocialEcho Twitter Consumer you will need as a minimum the following requiriments 

	- Java JDK, 1.6 or above

Download the appropriate JDK for your operating system, run the installer, and then set up an environment variable called JAVA_HOME pointing to the location of this installation.
On some platforms (for example OS X) the Java installation is automatically detected. However in many cases you will want to manually configure the location of Java. For example.

	.. code-block:: bash

		export JAVA_HOME=/Library/Java/Home
		export PATH="$PATH:$JAVA_HOME/bin"

Downloading and Installing
---------------------------------

Download a binary distribution of `SocialEcho <http://goo.gl/HwX0f5>`_ and extract the resulting zip file to a location of your choice.
Your extracted dir should look like this:

	.. code-block:: bash
	
		+ socialbus-server-consumer
		+ socialbus-twitter-consumer
		+ socialbus-twitter-oauth
		+ docs
		LICENSE

Open socialbus-twitter-consumer directory:

	.. code-block:: bash	
	
		+ lib
		+ bin
		+ conf
		+ logs
		socialbus-twitter-consumer.jar
		LICENSE

If SocialEcho is working correctly you should now be able to type in the terminal::

	bin/startup.sh

You should see output similar to this::

	##############################################################################
		  _________             .__       .__  ___________      .__            
		 /   _____/ ____   ____ |__|____  |  | \_   _____/ ____ |  |__   ____  
		 \_____  \ /  _ \_/ ___\|  \__  \ |  |  |    __)__/ ___\|  |  \ /  _ \ 
		 /        (  <_> )  \___|  |/ __ \|  |__|        \  \___|   Y  (  <_> )
		/_______  /\____/ \___  >__(____  /____/_______  /\___  >___|  /\____/ 
		        \/            \/        \/             \/     \/     \/        
	##############################################################################
	 SocialEcho Twitter Consumer, 2013, Version 0.5
	##############################################################################
	
To shutdown client services::

	bin/shutdown.sh

Important: shutdown.sh kills all socialbus-twitter-consumer running instances.

Next step
#########	

Now you have it installed learn all configuration details at :doc:`command_line`.
