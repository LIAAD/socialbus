package pt.sapo.labs.twitterecho.metadata;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 2:12 PM
 * To change this template use File | Settings | File Templates.
 */

import org.json.simple.JSONObject;
import pt.sapo.labs.api.services.StatusMetadataHandler;

public class StatusMetadataProcessor {

    private StatusMetadataHandler successor;
    private StatusMetadataHandler first;

    public void addHandler(StatusMetadataHandler handler){

        if(this.first == null){
            this.first = handler;
        }else{
            this.successor.setNextHandler(handler);
        }
        this.successor = handler;
    }

    public void handleRequest(JSONObject json) {

        this.first.handleRequest(json);
    }
}
