package pt.sapo.labs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

import java.awt.*;
import java.io.*;
import java.net.URI;
import java.util.Properties;


//	CONSUMER_KEY = "GJGALwtkuTDljV3eBDAYaQ";
//	CONSUMER_SECRET = "lGENO2QQVasMzalcpp6A3fAciNO1u0wA2ZihMpibyY0";

class Main {

    public static final String START_HELP = "This program will help you through the process of generating Twitter user token.\n" +
            "Before start you must have.\n\n" +

            "1) Twitter user account. Signup at https://twitter.com\n" +
            "2) Twitter application key. Register yours at https://dev.twitter.com/apps.\n" +
            "3) Once you have registered your application please follow the instructions:\n";

    public static final String ENTER_THE_APPLICATION_CONSUMER_KEY = "1) Enter the application consumer key: ";
    public static final String ENTER_THE_APPLICATION_CONSUMER_SECRET_KEY = "2) Enter the application consumer secret key: ";
    public static final String OPEN_THE_FOLLOWING_URL_AND_GRANT_ACCESS_TO_YOUR_ACCOUNT = "3) Open the following URL and grant access to your account:";
    public static final String ENTER_THE_PIN_IF_AVAILABLE_OR_JUST_HIT_ENTER = "Enter the PIN (if available) or just hit enter:";
    public static final String DO_YOU_WANT_TO_QUIT_Y_N = "Do you want create more tokens? N/y";
    public static final String VALUE_CAN_T_BE_BLANK = "Sorry. Value can't be blank";
    public static final String OPENING_TWITTER_AUTHORIZATION_URL = "Opening Twitter Authorization url";

    private static Logger logger = LoggerFactory.getLogger(Main.class);


    private static final String APP_VERSION = loadVersion();

    private static String loadVersion() {
        String userAgent = "Twitterecho-Authentication-Helper";
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

    static String consumerKey;
    static String consumerSecret;

    static File outputFile;

    public static void main(String args[]) throws IOException, TwitterException {

        logger.info("************************************************************");
        logger.info("Version : " + APP_VERSION);
        logger.info("************************************************************\n");
       /**
        if(args.length > 0){
            String outputFilePath = args[0];

            outputFile = new File(outputFilePath);
        }else{
//            String userHomeDir = System.getProperty("user.home");
//
//            outputFile = new File(userHomeDir + "/.twitterecho/tokens.conf");
            outputFile = new File("tokens.conf");
        }

        **/

        outputFile = new File("tokens.csv");

        logger.info("Output file : " + outputFile.getAbsolutePath());

        logger.info(START_HELP);

        while (true) {
            Twitter twitter = new TwitterFactory().getInstance();
            execCommandLoop(twitter);
        }
    }

    public static void execCommandLoop(Twitter twitter) throws IOException, TwitterException {

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        if (consumerKey != null) {
            logger.info(ENTER_THE_APPLICATION_CONSUMER_KEY + "[" + consumerKey + "]");
            String line = br.readLine().trim();

            if ((line != null) && !("".equals(line))) {
                consumerKey = line;
            }
        } else {
            logger.info(ENTER_THE_APPLICATION_CONSUMER_KEY);

            String readLine = null;
            while((readLine == null) || ("".equals(readLine))){
                readLine = br.readLine().trim();

                if((readLine == null) || ("".equals(readLine))){
                    logger.info(VALUE_CAN_T_BE_BLANK);
                } else{
                    consumerKey = readLine;
                }
            }
        }

        if (consumerSecret != null) {
            logger.info(ENTER_THE_APPLICATION_CONSUMER_SECRET_KEY + "[" + consumerSecret + "]");
            String line = br.readLine().trim();

            if ((line != null) && !("".equals(line))) {
                consumerSecret = line;
            }

        } else {
            logger.info(ENTER_THE_APPLICATION_CONSUMER_SECRET_KEY);

            String readLine = null;
            while((readLine == null) || ("".equals(readLine))){
                readLine = br.readLine().trim();

                if((readLine == null) || ("".equals(readLine))){
                    logger.info(VALUE_CAN_T_BE_BLANK);
                }else{
                    consumerSecret = readLine;
                }
            }
        }

        requestTwitterAuth(twitter, br);
    }

    private static void requestTwitterAuth(Twitter twitter, BufferedReader br) throws TwitterException, IOException {
        logger.info("Requesting authorization at Twitter...");
        twitter.setOAuthConsumer(consumerKey, consumerSecret);

        RequestToken requestToken = twitter.getOAuthRequestToken();
        AccessToken accessToken = null;

        while (null == accessToken) {



            logger.info("\n");

            if(Desktop.isDesktopSupported()){
                logger.info(OPENING_TWITTER_AUTHORIZATION_URL);
                logger.info(requestToken.getAuthorizationURL());

                Desktop d = Desktop.getDesktop();
                d.browse(URI.create(requestToken.getAuthorizationURL()));
            }else{
                logger.info(OPEN_THE_FOLLOWING_URL_AND_GRANT_ACCESS_TO_YOUR_ACCOUNT);
                logger.info(requestToken.getAuthorizationURL());
            }

            logger.info(ENTER_THE_PIN_IF_AVAILABLE_OR_JUST_HIT_ENTER);
            String pin = br.readLine();
            try {
                if (pin.length() > 0) {
                    accessToken = twitter.getOAuthAccessToken(requestToken, pin);
                } else {
                    accessToken = twitter.getOAuthAccessToken();
                }
            } catch (TwitterException te) {
                if (401 == te.getStatusCode()) {
                    logger.error("Unable to get the access token.");
                } else {
                    logger.error("Error", te);
                }
            }
        }

        //persist to the accessToken for future reference.
        storeAccessToken(accessToken,consumerKey, consumerSecret);

        logger.info("--------------------------------------------");
        logger.info(DO_YOU_WANT_TO_QUIT_Y_N);
        String isQuit = br.readLine();
        if (isQuit.equalsIgnoreCase("N")) {
            logger.info("bye");
            System.exit(1);
        }
    }

    private static void storeAccessToken(AccessToken accessToken,String consumerKey, String consumerSecret) {
        logger.info("Authentication info for screen name: " + accessToken.getScreenName());
        logger.info("Consumer Key : " + consumerKey);
        logger.info("Consumer Secret: " + consumerSecret);
        logger.info("Token: " + accessToken.getToken());
        logger.info("Secret: " + accessToken.getTokenSecret());

        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outputFile, true)));
            out.println(accessToken.getToken() + "," +
                        accessToken.getTokenSecret() + "," +
                        consumerKey + "," +
                        consumerSecret + "," +
                        accessToken.getScreenName()
            );

            out.close();
        } catch (IOException e) {
            //oh noes!
            logger.error("Failed to write file: ", e);
        }
    }
}