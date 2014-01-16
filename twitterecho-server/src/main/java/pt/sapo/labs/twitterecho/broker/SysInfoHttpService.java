package pt.sapo.labs.twitterecho.broker;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.json.simple.JSONObject;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.api.services.StatusMetadataHandler;
import pt.sapo.labs.twitterecho.Main;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 9/10/13
 * Time: 1:00 AM
 * To change this template use File | Settings | File Templates.
 */
public class SysInfoHttpService implements Processor {

    private TwitterechoServerSessionStats twitterechoServerSessionStats;

    public void process(Exchange exchange) throws Exception {
        // just get the body as a string
//            String body = exchange.getIn().getBody(String.class);

        // we have access to the HttpServletRequest here and we can grab it if we need it
//        HttpServletRequest req = exchange.getIn().getBody(HttpServletRequest.class);
        twitterechoServerSessionStats = TwitterechoServerSessionStats.getInstance();

        JSONObject json = new JSONObject();
        json.put("version", Main.loadVersion());

        JSONObject sysinfo = new JSONObject();
        sysinfo.put("host",InetAddress.getLocalHost().getHostName());
        sysinfo.put("os.arch",System.getProperty("os.arch"));
        sysinfo.put("os.name",System.getProperty("os.name"));
//        sysinfo.put("os.version",System.getProperty("os.version"));
        sysinfo.put("java.version",System.getProperty("java.version"));
        sysinfo.put("java.vendor",System.getProperty("java.vendor"));
//        sysinfo.put("config.file",twitterechoServerSessionStats.getApplicationManager().getConfig().getProperty("config.file.path"));

        JSONObject stats = new JSONObject();
        stats.put("clients",twitterechoServerSessionStats.getClients());
        stats.put("topics",twitterechoServerSessionStats.getTopics());

        JSONObject global = new JSONObject();
        global.put("running_since",twitterechoServerSessionStats.getRunningSince().toString());

        if(twitterechoServerSessionStats.getLastProcessedMessageAt() != null){
            global.put("last_message_at",twitterechoServerSessionStats.getLastProcessedMessageAt().toString());
        }

        global.put("count",twitterechoServerSessionStats.getProcessedMessagesCount());

        List<StatusMetadataHandler> handlers = twitterechoServerSessionStats.getApplicationManager().getMetadataHandlers();
        List<StatusAdapter> adapters = twitterechoServerSessionStats.getApplicationManager().getAdapters();

        List<String> handlersString = new ArrayList<String>();

        for (StatusMetadataHandler _clazz : handlers){
            handlersString.add(_clazz.getClass().getName());
        }

        List<String> adaptersString = new ArrayList<String>();

        for (StatusAdapter _clazz : adapters){
            adaptersString.add(_clazz.getClass().getName());
        }

        JSONObject extensions = new JSONObject();
        extensions.put("adapters",adaptersString);
        extensions.put("preprocessors",handlersString);

        stats.put("_global_",global);
        json.put("stats",stats);
        json.put("sysinfo",sysinfo);
        json.put("extensions",extensions);

        // send a html response
        exchange.getOut().setBody("jsonCallback(" + json.toJSONString() + ")");
    }
}
