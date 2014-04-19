package pt.sapo.labs.api.impl.adapters;


import com.twitter.hbc.twitter4j.v3.message.DisconnectMessage;
import com.twitter.hbc.twitter4j.v3.message.StallWarningMessage;
import org.apache.commons.configuration.Configuration;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.api.services.StatusAdapter;
import pt.sapo.labs.utils.AppUtils;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.json.z_T4JInternalJSONImplFactory;
import twitter4j.internal.org.json.JSONObject;
import twitter4j.json.JSONObjectType;

import java.io.IOException;
import java.io.StringWriter;


public abstract class AbstractStatusAdapter implements StatusAdapter {
	
	private static Logger logger = LoggerFactory.getLogger(AbstractStatusAdapter.class);

    protected int statusCount = 0;


    protected Configuration configuration;

    private z_T4JInternalJSONImplFactory factory;

    {
        this.factory = new z_T4JInternalJSONImplFactory(new ConfigurationBuilder().build());
    }

    protected boolean enabled;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    protected Status parseJsonStatus(JSONObject json){
        try {

            JSONObjectType type = JSONObjectType.determine(json);
            if (type == JSONObjectType.STATUS) {
                Status status = factory.createStatus(json);

                return status;
            }
        } catch (TwitterException e) {
            logger.error("Fail to parse status json",e);
        }

        return null;
    }

    protected String parseJsonString(Object statusJson){
        StringWriter out = new StringWriter();
        try {
            JSONValue.writeJSONString(statusJson, out);

            String jsonText = out.toString();

            return jsonText;

        } catch (IOException e) {
            logger.error("fail to convert JSONObject to String",e);
        }

        return null;
    }

    @Override
    public void onStatus(JSONObject status) {
//    implement
        logger.debug("onStatus twitter4j.internal.org.json.JSONObject instance");
    }

	@Override
	public void onStatus(Status status) {
        logger.debug("onStatus Status instance");
//    implement
	}

    @Override
    public void onStatus(org.json.simple.JSONObject status) {
        logger.debug("onStatus org.json.simple.JSONObject instance");
//    implement
    }

    @Override
    public void onStatus(String status) {
        logger.debug("onStatus String instance");
//    implement
    }

	@Override
	public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

        logger.info("deleted status " + statusDeletionNotice.getStatusId() +
				" from user " + statusDeletionNotice.getUserId() );
	}

	@Override
	public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
		
		StringBuffer sb = new StringBuffer();

        sb.append(AppUtils.printContextConfiguration(this.getConfiguration()));
        sb.append("Got track limitation notice: " + numberOfLimitedStatuses);

        logger.warn(sb.toString());
	}

	@Override
	public void onException(Exception e) {
		
		StringBuffer sb = new StringBuffer();

        sb.append(AppUtils.printContextConfiguration(this.getConfiguration()));
		sb.append(e.getMessage());

        logger.error(sb.toString());

        System.out.println(sb.toString());
	}

	@Override
	public void onScrubGeo(long lat, long lon) {

		logger.debug("onScrubGeo : lat=" + lat + ", lon="+ lon);
	}

    @Override
    public void onDisconnectMessage(DisconnectMessage disconnectMessage) {
        StringBuffer sb = new StringBuffer();

        sb.append(AppUtils.printContextConfiguration(this.getConfiguration()));

        sb.append("DisconnectMessage:\n" );
        sb.append(" StreamName : " + disconnectMessage.getStreamName() + "\n");
        sb.append(" DisconnectCode : " + disconnectMessage.getDisconnectCode()+ "\n");
        sb.append(" DisconnectReason : " + disconnectMessage.getDisconnectReason()+ "\n");

        logger.error(sb.toString());

        System.out.println(sb.toString());
    }

    @Override
    public void onStallWarningMessage(StallWarningMessage stallWarningMessage) {

        StringBuffer sb = new StringBuffer();

        sb.append( "StallWarningMessage: ");
        sb.append( " Code : " + stallWarningMessage.getCode());
        sb.append( " Message : " + stallWarningMessage.getMessage());
        sb.append( " PercentFull : " + String.valueOf(stallWarningMessage.getPercentFull()));

        logger.warn(sb.toString());

        System.out.println(sb.toString());
    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {

        StringBuffer sb = new StringBuffer();

        sb.append( "StallWarningMessage: ");
        sb.append( " Code : " + stallWarning.getCode());
        sb.append( " Message : " + stallWarning.getMessage());
        sb.append( " PercentFull : " + String.valueOf(stallWarning  .getPercentFull()));

        logger.warn(sb.toString());

        System.out.println(sb.toString());
    }

    @Override
    public void onUnknownMessageType(String s) {
        //To change body of implemented methods use File | Settings | File Templates.

        logger.warn("UnknownMessageType: " + s);
    }
}
