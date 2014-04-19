# Install job that classifies users as either bot or humans.
# Quick usage:
# python setup.py
#
# dependends on : https://pypi.python.org/pypi/python-crontab

from crontab import CronTab
import os

if __name__ == "__main__":
    print "##################################################################"
    print " [INFO] Setup Twitter Bot classifier "
    print "------------------------------------------------------------------"
    
    system_cron   = CronTab()
    command = "python " + os.getcwd() + "/process_tweets.py"
    
    print command
    job  = system_cron.new(command=command)
    job.every().day()
    
    if job.is_valid():
        job.enable()
    
    system_cron.write()
        
    print "------------------------------------------------------------------"
    print " [INFO] Done "