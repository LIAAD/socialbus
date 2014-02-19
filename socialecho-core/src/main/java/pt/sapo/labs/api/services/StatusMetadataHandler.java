package pt.sapo.labs.api.services;

import org.apache.commons.configuration.Configuration;
import org.json.simple.JSONObject;


/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 1:41 PM
 * To change this template use File | Settings | File Templates.
 */
public interface StatusMetadataHandler {

    public void setNextHandler(StatusMetadataHandler handler);

    public void handleRequest(JSONObject json);

    public void setConfiguration(Configuration config);
}
