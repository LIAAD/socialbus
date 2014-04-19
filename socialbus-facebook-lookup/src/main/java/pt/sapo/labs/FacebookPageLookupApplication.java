package pt.sapo.labs;

import org.apache.commons.configuration.CompositeConfiguration;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.jobs.FacebookPageLookupJob;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;


/**
 * Hello world!
 *
 */
public class FacebookPageLookupApplication implements IApp
{
    private static Logger logger = LoggerFactory.getLogger(FacebookPageLookupApplication.class);

    private CompositeConfiguration config;
    private Scheduler scheduler;

    public FacebookPageLookupApplication(CompositeConfiguration config) {
        this.config = config;
    }

    @Override
    public void start() {

        JobDetail lookupJob = newJob(FacebookPageLookupJob.class).build();
        lookupJob.getJobDataMap().put("config",this.config);

        Trigger lookupTrigger = newTrigger().withIdentity("FacebookPageLookupJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInHours(
                                config.getInt("job.interval", 24)
                        )
                        .repeatForever()).build();

        SchedulerFactory sf = new StdSchedulerFactory();

        try {
            scheduler = sf.getScheduler();
            scheduler.scheduleJob(lookupJob, lookupTrigger );
            scheduler.start();

        } catch (SchedulerException e) {
            logger.error("Error on starting scheduler",e);
        }
    }

    @Override
    public void shutDown() {
        logger.info("Shutting down scheduler");

        if(this.scheduler != null){

            try {
                if(!this.scheduler.isShutdown()){
                    this.scheduler.shutdown();
                }
            } catch (SchedulerException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }
}
