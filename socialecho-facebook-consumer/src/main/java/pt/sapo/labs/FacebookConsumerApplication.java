package pt.sapo.labs;

import org.apache.commons.configuration.CompositeConfiguration;
import org.quartz.JobDetail;
import org.quartz.SchedulerFactory;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.sapo.labs.jobs.FacebookPageJob;
import pt.sapo.labs.jobs.FacebookSearchJob;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;

public class FacebookConsumerApplication implements IApp {

	private static Logger logger = LoggerFactory.getLogger(FacebookConsumerApplication.class);

	private CompositeConfiguration config;
    private Scheduler scheduler;

    public FacebookConsumerApplication(CompositeConfiguration config) {
		this.config = config;
    }

    @Override
	public void start() {

        JobDetail searchJob = newJob(FacebookSearchJob.class).build();
        searchJob.getJobDataMap().put("config",this.config);

        JobDetail pageMonitorJob = newJob(FacebookPageJob.class).build();
        pageMonitorJob.getJobDataMap().put("config",this.config);


        Trigger searchTrigger = newTrigger().withIdentity("FacebookSearchJobTrigger")
                        .withSchedule(simpleSchedule()
                                .withIntervalInMinutes(
                                        config.getInt("facebook.search.interval",5)
                                )
                                .repeatForever()).build();

        Trigger pageMonitorTrigger = newTrigger().withIdentity("FacebookPageMonitorJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInMinutes(
                                config.getInt("facebook.page.monitor.interval",60)
                        )
                        .repeatForever()).build();

        SchedulerFactory sf = new StdSchedulerFactory();

        try {
            scheduler = sf.getScheduler();
            scheduler.scheduleJob(searchJob, searchTrigger );
            scheduler.scheduleJob(pageMonitorJob, pageMonitorTrigger  );
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
