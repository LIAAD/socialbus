package pt.sapo.labs.jobs;

import org.apache.commons.configuration.CompositeConfiguration;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pt.sapo.labs.services.TwitterUserInteractionsService;
import pt.sapo.labs.services.TwitterUserInteractionsGraphService;


/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 15/01/14
 * Time: 02:06
 * To change this template use File | Settings | File Templates.
 */
public class TwitterUserInteractionsJob implements Job {

    private TwitterUserInteractionsService twitterUserInteractions;
	private TwitterUserInteractionsGraphService twitterUserInteractionsGraph;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //To change body of implemented methods use File | Settings | File Templates.

        CompositeConfiguration config = (CompositeConfiguration) jobExecutionContext.getJobDetail().
                getJobDataMap().get("config");

        twitterUserInteractions = new TwitterUserInteractionsService(config);
		new java.lang.Thread(twitterUserInteractions).start();
		
        twitterUserInteractionsGraph = new TwitterUserInteractionsGraphService(config);
		new java.lang.Thread(twitterUserInteractionsGraph).start();
    }
}