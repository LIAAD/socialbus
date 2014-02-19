package pt.sapo.labs.twitterecho.broker;

import pt.sapo.labs.twitterecho.ApplicationManager;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SocialechoServerSessionStats {

    private ApplicationManager applicationManager;
    private Map<String,Map> clients;
    private Map<String,Map> topics;
    private Map<String,Map> services;
    private long processedMessagesCount;
    private Date runningSince;

    private Date lastProcessedMessageAt;

    public ApplicationManager getApplicationManager() {
        return applicationManager;
    }

    public void setApplicationManager(ApplicationManager applicationManager) {
        this.applicationManager = applicationManager;
    }


    public void addService(String service) {
        if(!this.services.containsKey(service)){
            Map clientStat = new HashMap();

            clientStat.put("count",1l);
            clientStat.put("running_since",new Date().toString());

            this.services.put(service, clientStat);
        }

        Map clientStats = this.services.get(service);
        long total = (Long) clientStats.get("count");
        total++;
        clientStats.put("count",total);
        clientStats.put("last_message_at",new Date().toString());

        this.services.put(service,clientStats);

    }

    public void addClient(String client){
        if(!this.clients.containsKey(client)){
            Map clientStat = new HashMap();

            clientStat.put("count",1l);
            clientStat.put("running_since",new Date().toString());

            this.clients.put(client, clientStat);
        }

        Map clientStats = this.clients.get(client);
        long total = (Long) clientStats.get("count");
        total++;
        clientStats.put("count",total);
        clientStats.put("last_message_at",new Date().toString());

        this.clients.put(client,clientStats);
    }

    public void addTopic(String topic){
        if(!this.topics.containsKey(topic)){
            Map topicStats = new HashMap();

            topicStats.put("count",1l);
            topicStats.put("running_since",new Date().toString());

            this.topics.put(topic, topicStats);
        }

        Map topicStats = this.topics.get(topic);
        long total = (Long) topicStats.get("count");
        total++;
        topicStats.put("count",total);
        topicStats.put("last_message_at",new Date().toString());

        this.topics.put(topic,topicStats);
    }

    public Date getRunningSince() {
        return runningSince;
    }

    public void setRunningSince(Date runningSince) {
        this.runningSince = runningSince;
    }

    private static SocialechoServerSessionStats instance;

    public static SocialechoServerSessionStats getInstance(){
        if(instance == null){
            instance = new SocialechoServerSessionStats();
            instance.topics = new HashMap<String, Map>();
            instance.clients = new HashMap<String, Map>();
            instance.services = new HashMap<String, Map>();
            instance.runningSince = new Date();
            instance.processedMessagesCount = 0;
        }

        return  instance;
    }

    public Map getClients() {
        return clients;
    }

    public Map getServices() {
        return services;
    }

    public Map getTopics() {
        return topics;
    }

    public long getProcessedMessagesCount() {
        return processedMessagesCount;
    }

    public void setProcessedMessagesCount(long processedMessagesCount) {
        this.processedMessagesCount = processedMessagesCount;
    }

    public void increaseProcessedMessagesCount(){
        this.processedMessagesCount++;
    }

    public Date getLastProcessedMessageAt() {
        return lastProcessedMessageAt;
    }

    public void setLastProcessedMessageAt(Date lastProcessedMessageAt) {
        this.lastProcessedMessageAt = lastProcessedMessageAt;
    }


}
