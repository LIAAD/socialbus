package pt.sapo.labs.api.services;

import org.apache.commons.configuration.Configuration;

import twitter4j.StatusListener;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/22/13
 * Time: 4:49 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StatusAdapter extends StatusListener {

    public Configuration getConfiguration();
    public void setConfiguration(Configuration configuration);

    public void initialize();
//    public Map getStatusMetadata();
//    public void setStatusMetadata(Map metadata);
    
    void onStatus(twitter4j.JSONObject jsonObject);
    void onStatus(org.json.simple.JSONObject jsonObject);
    void onStatus(String jsonString);

    boolean isEnabled();
}
