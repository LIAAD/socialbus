package pt.sapo.labs.api.impl.handlers;

import org.apache.commons.configuration.Configuration;
import org.json.simple.JSONObject;
import pt.sapo.labs.api.services.StatusMetadataHandler;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 2:22 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractStatusMetadataHandler implements StatusMetadataHandler {

    protected volatile StatusMetadataHandler nextHandler;
    protected Configuration config;

    public final void setNextHandler(StatusMetadataHandler handler) {
        this.nextHandler = handler;
    }

    public final void handleRequest(JSONObject json) {
        if (this.accept(json)) {
            this.doHandle(json);
        }
        else {
            this.nextHandler.handleRequest(json);
        }
    }

    public Configuration getConfiguration() {
        return config;
    }

    public void setConfiguration(Configuration config){
        this.config = config;
    }

    protected abstract boolean accept(JSONObject json);

    protected abstract void doHandle(JSONObject json);

}
