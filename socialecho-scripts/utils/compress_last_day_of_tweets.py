#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
Summary     :    This program compress last day tweets files 

crontab example

0 0 * * * python /fasmounts/LABS/socialecho/twitterecho/scripts/general/compress_last_day_of_tweets.py -D /fasmounts/LABS/socialecho/twitterecho/data/v0.2/portugal/json/ >> /fasmounts/LABS/socialecho/log/twitterecho/compress.log
'''

import csv,os, sys, urllib,urllib2, datetime,requests,json,solr,time
from datetime import date
from json import loads
from urllib2 import urlopen
from optparse import OptionParser
from pprint import pprint
import subprocess
import shutil
import tarfile

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-D", "--dir", type="string", action="store", dest="directory", help="Base dir with json files")

(cmd_options, cmd_args) = cmd_parser.parse_args()

if not (cmd_options.directory):
    cmd_parser.print_help()
    sys.exit(3)
    
def make_tarfile_for(filepath):
    print "make tar file for : ", filepath
    
    # source_dir, output_filename = os.path.split(filepath)
    # source_dir = cmd_options.directory
    
    output_filename = generate_file_name()
    
    # source_dir, output_filename = os.path.split(cmd_options.directory)
    
    os.chdir(cmd_options.directory)
    print "save at ", cmd_options.directory
    print "tarfile.open(",output_filename,")"
    print " add(",filepath,")"
    
    with tarfile.open(output_filename, "w:gz") as tar:
        tar.add(filepath)
    
    print "done compressing ",output_filename

def generate_file_name():
    now = datetime.datetime.now()
    one_hour = datetime.timedelta(hours=1)
    last_hour = now - one_hour
    
    strformat = "%Y_%m_%d"
    day_str = last_hour.strftime(strformat)
    
    return day_str + ".tar.gz"   

def getYesterday():
    now = datetime.datetime.now()
    one_hour = datetime.timedelta(hours=1)
    last_hour = now - one_hour
    
    strformat = "/%Y/%m/%d"
    last_hour_str = last_hour.strftime(strformat)
    return last_hour_str   
    
def getYesterdayDir():
    baseDir = cmd_options.directory
    lastHourFile = baseDir + getYesterday()    
    return lastHourFile

fileName = getYesterdayDir()

print "##################################################################"
print " [INFO] Loading last hour tweets file : " + fileName
print "------------------------------------------------------------------"

make_tarfile_for(fileName)
    
print "##################################################################"
print " [INFO] Finished the indexing for " + generate_file_name()
print "##################################################################"  

