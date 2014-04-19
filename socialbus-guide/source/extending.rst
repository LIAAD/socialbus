Extending SocialBus
=====================

Java ServiceLoader API
**************************

Defining an API and developing the corresponding implementation has become an uber mainstream practice when developing large Java applications. Modularization is such an useful design principle, especially to avoid spaghetti code, for testing and debugging, and for re-implementation of old code.

Many developers seek to separate API interfaces and abstract classes from their implementation in separate packages. Yet, this almost always leads to cycles between java packages (api refers to impl, and vice-versa). There has been many frameworks, such as OSGi, supporting modularization, but the documentation has not always been good and complete. Many developers have struggled with class loading issues too.

Fortunately, Java has delivered the ServiceLoader (http://docs.oracle.com/javase/6/docs/api/java/util/ServiceLoader.html) utility since release 6. The example described in this post is available from Github in the Java-ServiceLoader directory. It is inspired from here.

SocialBus makes use of ServiceLoader API to extend new modules. This section shows how to define custom adapters to implement custom and creative ways for indexing tweets.

Defining custom adapters
************************

SocialBus already offers builtin modules for file and MongoDB indexing. But you may face diferent needs and in order to accomplish this is how you must implement your custom indexing specifications.

For SocialBus definition, **Adapters** handle each tweet message that comes into the system. You can define your own adapter to get messages to index to MySQL for example or even send it to a message-broker system. It is up to you. 

First thing you need is to create a java project and make sure you have **twitterecho-core.jar** into your classpath. You find it at **lib/** folder.
Given our interface for adapters you can implement your own class. This is the interface:

	.. code-block:: java
	
		package pt.sapo.labs.api.services;
		
		import com.twitter.hbc.twitter4j.v3.RawJsonStatusListener;
		import com.twitter.hbc.twitter4j.v3.handler.StatusStreamHandler;
		import org.apache.commons.configuration.Configuration;
		import org.json.simple.JSONObject;

		import java.util.Map;
		import java.util.Properties;
 
		public interface StatusAdapter 
		  	extends StatusStreamHandler,RawJsonStatusListener{
        
		  Configuration getConfiguration();
		  void setConfiguration(Configuration configuration);
        
		  void initialize();
		  void onStatus(JSONObject jsonObject);
		  void onStatus(String jsonString);
        
		  boolean isEnabled();
		}

Now as an example this is an implementation to print tweets into the log:
	
	.. code-block:: java
 
		package pt.sapo.labs.api.impl.adapters;

		import org.slf4j.Logger;
		import org.slf4j.LoggerFactory;
		import twitter4j.Status;
		import twitter4j.User;
		import twitter4j.internal.org.json.JSONObject;
		
		public class ConsoleStatusAdapter extends AbstractStatusAdapter {
        
		  private static Logger logger = 
		  	LoggerFactory.getLogger(ConsoleStatusAdapter.class);
        
		  private String topicName;
		
		  public ConsoleStatusAdapter(){}
        
		  public void initialize(){
		   logger.info("initializing file status adapter");
        
		   this.topicName = this.getConfiguration().getString("topic.name","sample");
        
		   this.setEnabled(true);
		  }
        
		  @Override
		  public void onStatus(JSONObject json) {
          
		   Status status = parseJsonStatus(json);
          
		   if(status !=null){
		    ++statusCount;
           
 		    User user = status.getUser();
           
		    String messageHeader = String.format("New status (%d) for topic (%s)", 
		   			statusCount,this.topicName);
		   			
		    String messageBody = String.format("@%s : %s", 
		   			user.getScreenName(),status.getText());
		   
		    logger.info(messageHeader);            
		    logger.info(messageBody);
		   }
          
		   logger.trace(json.toString());
		  }
		}
				
Important: The implementation must have a public parameterless constructor.


Next, we declare the implementation in a file having the fully qualified name of the API under the META-INF/services directory in the .jar:

.. image:: _static/twitterecho/meta_inf.png

The file contains fully qualified name of the implementation. This would be the content in our example::

		pt.sapo.labs.api.impl.adapters.ConsoleStatusAdapter

Build your project in a jar and deploy it at SocialBus *lib* folder and restart it.
	
The service implementation is loaded at bootstrap.

Defining custom processors
--------------------------

*TODO*
