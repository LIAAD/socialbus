package pt.sapo.labs.jobs;

import org.apache.commons.configuration.CompositeConfiguration;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pt.sapo.labs.services.TwitterTrendingTopicsService;
import pt.sapo.labs.services.TwitterTrendingUsersService;


/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 15/01/14
 * Time: 02:06
 * To change this template use File | Settings | File Templates.
 */
public class TwitterTrendingJob implements Job {

    private TwitterTrendingTopicsService twitterTrendingTopicsService;
	private TwitterTrendingUsersService twitterTrendingUsersService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //To change body of implemented methods use File | Settings | File Templates.

        CompositeConfiguration config = (CompositeConfiguration) jobExecutionContext.getJobDetail().
                getJobDataMap().get("config");

        twitterTrendingTopicsService = new TwitterTrendingTopicsService(config);
		new java.lang.Thread(twitterTrendingTopicsService).start();
		
        twitterTrendingUsersService = new TwitterTrendingUsersService(config);
		new java.lang.Thread(twitterTrendingUsersService).start();
    }
}