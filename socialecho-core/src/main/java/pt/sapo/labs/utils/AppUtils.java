package pt.sapo.labs.utils;

import com.google.common.collect.Lists;
import com.twitter.hbc.httpclient.ClientContext;
import org.apache.commons.configuration.Configuration;

import java.net.UnknownHostException;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/22/13
 * Time: 1:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppUtils {

    public static ClientContext convertConfigurationToClientContext(Configuration config){
        ClientContext context = new ClientContext();

        String[] ignoreKeys = {"application.consumer.key","application.consumer.secret","twitter.tokens"};
        Iterator<String> keys = config.getKeys();

        while ( keys.hasNext() ){
            String configKey = keys.next();

            if(configKey == " ") continue;

            if(!Lists.newArrayList(ignoreKeys).contains(configKey)){
                Object propValue = config.getProperty(configKey);

                context.setProperty(configKey,(String)propValue);
            }
        }

        return context;
    }

    public static String getLocalIpAddress() {

        try {
            String localAddress = java.net.InetAddress.getLocalHost().getHostAddress();
            return localAddress;

        } catch (UnknownHostException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        return "0";
    }

    public static String printContextConfiguration(Configuration config){
        StringBuffer sb = new StringBuffer();

        sb.append("\n");
        sb.append("Twitterecho Alert");sb.append("\n");

        sb.append("------------------------------------- \n");

        Iterator<String> keys = config.getKeys();

        while ( keys.hasNext() ){
            String configKey = keys.next();

            sb.append(configKey + ", " + config.getProperty(configKey));
            sb.append("\n");
        }

        sb.append("ip address : " + AppUtils.getLocalIpAddress());sb.append("\n");

        sb.append("------------------------------------- \n");

        return sb.toString();
    }
}
