package pt.sapo.labs.jobs;

import org.apache.commons.configuration.CompositeConfiguration;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import pt.sapo.labs.FacebookPageLookupService;

/**
 * Created with IntelliJ IDEA.
 * User: arian
 * Date: 15/01/14
 * Time: 02:06
 * To change this template use File | Settings | File Templates.
 */
public class FacebookPageLookupJob implements Job {

    private FacebookPageLookupService facebookPageLookupService;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        //To change body of implemented methods use File | Settings | File Templates.

        CompositeConfiguration config = (CompositeConfiguration) jobExecutionContext.getJobDetail().
                getJobDataMap().get("config");

        facebookPageLookupService = new FacebookPageLookupService(config);
        facebookPageLookupService.run();
    }
}