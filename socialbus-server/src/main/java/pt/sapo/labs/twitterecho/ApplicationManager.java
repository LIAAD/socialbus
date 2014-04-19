package pt.sapo.labs.twitterecho;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.api.services.StatusMetadataHandler;
import pt.sapo.labs.twitterecho.metadata.StatusMetadataProcessorClient;

import java.util.List;

public class ApplicationManager {

	private static Logger logger = LoggerFactory.getLogger(ApplicationManager.class);	
	

	public ApplicationManager(CompositeConfiguration config) {
		this.config = config;
        this.config.setListDelimiter(';');
	}

    public void setAdapters(List<StatusAdapter> adapters) {
        this.adapters = adapters;
    }

    public  List<StatusAdapter> getAdapters() {
        return adapters;
    }

    public Configuration getConfig() {
        return config;
    }


    public List<StatusMetadataHandler> getMetadataHandlers() {
        return handlers;
    }

    public void setMetadataHandlers(List<StatusMetadataHandler> handlers) {
        this.handlers = handlers;
    }


    public StatusMetadataProcessorClient getStatusMetadataProcessorClient() {
        return statusMetadataProcessorClient;
    }

    public void setStatusMetadataProcessorClient(StatusMetadataProcessorClient statusMetadataProcessorClient) {
        this.statusMetadataProcessorClient = statusMetadataProcessorClient;
    }

    private List<StatusAdapter> adapters;
    private List<StatusMetadataHandler> handlers;
    private CompositeConfiguration config;
    private StatusMetadataProcessorClient statusMetadataProcessorClient;
}

