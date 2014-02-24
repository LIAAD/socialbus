grails.servlet.version = "2.5" // Change depending on target container compliance (2.5 or 3.0)
grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir = "target/test-reports"
grails.project.target.level = 1.6
grails.project.source.level = 1.6
//grails.project.war.file = "target/${appName}-${appVersion}.war"

grails.project.dependency.resolver="maven"

// uncomment (and adjust settings) to fork the JVM to isolate classpaths
//grails.project.fork = [
//   run: [maxMemory:1024, minMemory:64, debug:false, maxPerm:256]
//]

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits("global") {
        // specify dependency exclusions here; for example, uncomment this to disable ehcache:
        // excludes 'ehcache'
    }
    log "error" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    checksums true // Whether to verify checksums on resolve
    legacyResolve false // whether to do a secondary resolve on plugin installation, not advised and here for backwards compatibility

    repositories {
        inherits true // Whether to inherit repository definitions from plugins

        grailsPlugins()
        grailsHome()
        grailsCentral()

        mavenLocal()
        mavenCentral()

        mavenRepo "http://twitter4j.org/maven2"
		mavenRepo 'http://repo.spring.io/milestone'
        // uncomment these (or add new ones) to enable remote dependency resolution from public Maven repositories
        //mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        //mavenRepo "http://repository.jboss.com/maven2/"
    }

    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes e.g.

        // runtime 'mysql:mysql-connector-java:5.1.20'

        compile(group: "org.twitter4j", name: "twitter4j-core", version: "3.0.2")
        compile(group: "org.twitter4j", name: "twitter4j-async", version: "3.0.2")
        compile(group: "org.twitter4j", name: "twitter4j-stream", version: "3.0.2")

        compile(group: "org.mongodb", name: "mongo-java-driver", version: "2.11.0")
		
		compile(group: "com.googlecode.json-simple", name: "json-simple", version: "1.1.1")

		runtime 'postgresql:postgresql:9.0-801.jdbc4' 
		
		compile (group: "com.rabbitmq", name: "amqp-client", version: "3.2.1")
		
		compile (group: "org.apache.solr", name: "solr-solrj", version: "4.4.0"){
		 excludes 'org.slf4j:jcl-over-slf4j'
	 	}
		compile (group:'org.apache.solr', name:'solr-core', version:'4.4.0'){
		 excludes 'org.slf4j:jcl-over-slf4j'
		 excludes 'org.slf4j:slf4j-log4j12'
		}
		
		compile (group:'commons-httpclient', name:'commons-httpclient', version:'3.1')
		
            
    }

    plugins {
 		 compile ':scaffolding:2.0.0' 
		 
         runtime ":jquery:1.8.3"
         runtime ":resources:1.1.6"

         // Uncomment these (or add new ones) to enable additional resources capabilities
         //runtime ":zipped-resources:1.0"
         //runtime ":cached-resources:1.0"
         //runtime ":yui-minify-resources:0.1.4"

		   build ':tomcat:7.0.47'
		 runtime ':hibernate:3.6.10.6'

        // Uncomment these (or add new ones) to enable additional resources capabilities
        //runtime ":zipped-resources:1.0"
        //runtime ":cached-resources:1.0"
        //runtime ":yui-minify-resources:0.1.4"

        runtime ":database-migration:1.2.1"

        compile ':cache:1.0.1'

        runtime ":twitter-bootstrap:2.3.0"
        runtime ":fields:1.3"

        compile "org.grails.plugins:spring-security-core:2.0-RC2"
        compile "org.grails.plugins:spring-security-twitter:0.6"
		
		compile ':mail:1.0', {
		    excludes 'spring-test'
		  }
		  
        compile ":jquery-ui:1.8.7"
        compile ":famfamfam:1.0"

/*        compile ":oauth:2.1.0"*/

//        runtime ":app-info:1.0.2"

        compile ":spring-events:1.2"

        compile ":quartz:1.0-RC6"
        compile ":quartz-monitor:0.3-RC1"


    }
}
