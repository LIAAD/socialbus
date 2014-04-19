package pt.sapo.labs;

import ch.qos.logback.core.joran.spi.JoranException;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.io.FileUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;

import pt.sapo.labs.jobs.UrlExpanderJob;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * Hello world!
 *
 */
public class Application implements IApp
{
    private static Logger logger = LoggerFactory.getLogger(Application.class);

    private CompositeConfiguration config;
    private Scheduler scheduler;

    public Application(CompositeConfiguration config) {
        this.config = config;
    }

    @Override
    public void start() {

        JobDetail urlExpanderJob = newJob(UrlExpanderJob.class).build();
        urlExpanderJob.getJobDataMap().put("config",this.config);

        Trigger urlExpanderJobTrigger = newTrigger().withIdentity("urlExpanderJobTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInHours(
                                config.getInt("url.expander.job.interval",24)
                        )
                        .repeatForever()).build();
		
        SchedulerFactory sf = new StdSchedulerFactory();

        try {
            scheduler = sf.getScheduler();
			scheduler.scheduleJob(urlExpanderJob, urlExpanderJobTrigger );
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
