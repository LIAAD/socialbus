package pt.sapo.labs.twitterecho;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import ch.qos.logback.core.util.StatusPrinter;
import org.apache.commons.cli.*;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class Main{

    private static org.slf4j.Logger logger = LoggerFactory.getLogger(Main.class);

    private static final String INPUT_FILE = "logback-smtp-server.xml";
    private static final String APP_VERSION = loadVersion();


    private static void startLogger() throws JoranException{
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(context);
        context.reset();

        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(INPUT_FILE);
        if(inputStream == null) throw new IllegalStateException("File not found: " + INPUT_FILE);
        configurator.doConfigure(inputStream);


        StatusPrinter.printInCaseOfErrorsOrWarnings(context);
    }

    public static void startApp(String configFilePath) {

        logger.info("Starting application...");

        try {

            AbstractConfiguration.setDefaultListDelimiter(';');

            logger.debug("loading config file ..." + configFilePath);

            PropertiesConfiguration clientConfig = new PropertiesConfiguration(configFilePath);

            CompositeConfiguration config = new CompositeConfiguration();

            config.addProperty("config.file.path", System.getProperty("config"));

            config.addConfiguration(clientConfig);

//          Print all properties
            Iterator<String> keys = config.getKeys();

            logger.info("[configuration] ------------------");
            while ( keys.hasNext() ){
                String configKey = keys.next();

                logger.info(configKey + ": " + config.getProperty(configKey));
            }
            logger.info("----------------------------------");

            IApp app = new TwitterechoServerApplication(config);
            ShutdownInterceptor shutdownInterceptor = new ShutdownInterceptor(app);
            Runtime.getRuntime().addShutdownHook(shutdownInterceptor);

            logger.info("Initializing application...");
            app.start();

        } catch (ConfigurationException e) {
            logger.error("Unable to read config file" ,e);
        }
    }

    private static void loadConfigFiles(CommandLine line) {
        logger.info("Loading configuration files...");

        startApp(line.getOptionValue("config"));
    }

    public static String loadVersion() {
        String userAgent = "socialbus-Server";
        try {
            InputStream stream = Main.class.getClassLoader().getResourceAsStream("build.properties");
            try {
                Properties prop = new Properties();
                prop.load(stream);

                String version = prop.getProperty("version");
                userAgent += " " + version;
            } finally {
                stream.close();
            }
        } catch (IOException ex) {
            logger.error("fail to load build properties info",ex);
            // ignore
        }
        return userAgent;
    }


    /**
     * Main entry of this application.
     */
    public static void main(String[] args) throws JoranException {

        startLogger();

        logger.info("************************************************************");
        logger.info("Version : " + APP_VERSION);
        logger.info("************************************************************");

        Option basicConfigFile = OptionBuilder.withArgName("config")
                .hasArg()
                .withDescription(  "basic configuration file" )
                .create( "config" );


        // create Options object
        Options options = new Options();

        // add t option
        options.addOption(basicConfigFile);

        CommandLineParser parser = new GnuParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse( options, args );

            // validate that block-size has been set
            if( !line.hasOption( "config" )) {
                // automatically generate the help statement
                HelpFormatter formatter = new HelpFormatter();
                formatter.printHelp( " ", options );

                System.exit(0);
            }

            loadConfigFiles(line);
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            //logger.error("Parsing failed.  Reason: ", exp);
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }
    }
}