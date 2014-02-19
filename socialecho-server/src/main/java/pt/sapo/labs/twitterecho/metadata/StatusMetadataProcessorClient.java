package pt.sapo.labs.twitterecho.metadata;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 8/18/13
 * Time: 2:17 PM
 * To change this template use File | Settings | File Templates.
 */

import org.json.simple.JSONObject;
import pt.sapo.labs.api.services.StatusMetadataHandler;

import java.util.List;

public class StatusMetadataProcessorClient {

    private List<StatusMetadataHandler> handlers;
    private StatusMetadataProcessor processor;

    public StatusMetadataProcessorClient(List<StatusMetadataHandler> handlers){
        this.handlers= handlers;
        createProcessor();
    }

    private void createProcessor() {

        this.processor = new StatusMetadataProcessor();

        for(StatusMetadataHandler handler : this.handlers){
            this.processor.addHandler(handler);
        }

//        this.processor.addHandler(new TokenizerStatusMetadataHandler());
//        this.processor.addHandler(new EntitiesExtractionStatusMetadataHandler());
//        this.processor.addHandler(new LanguageDetectionStatusHandler());
    }

    public void statusReceived(JSONObject json){
        this.processor.handleRequest(json);
    }
}
