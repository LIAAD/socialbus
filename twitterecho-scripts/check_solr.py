#! /usr/bin/python
#
'''
Version     :    0.1
Author      :    Arian Pasquali
'''
import urllib2, base64
import os, sys, datetime
from xml.dom.minidom import *
from optparse import OptionParser
import smtplib
# Import the email modules we'll need
from email.mime.text import MIMEText

# Command Line Arguments Parser
cmd_parser = OptionParser(version="%prog 0.1")
cmd_parser.add_option("-S", "--solr", type="string", action="store", dest="solr_server", help="Solr Server")
cmd_parser.add_option("-T", "--timeinterval", type="int", action="store", dest="interval", help="interval in minutes")
cmd_parser.add_option("-U", "--httpuser", type="string", action="store", dest="http_user", help="Http user", default="")
cmd_parser.add_option("-P", "--httppass", type="string", action="store", dest="http_pass", help="Http pass", default="")

(cmd_options, cmd_args) = cmd_parser.parse_args()

if not (cmd_options.solr_server and cmd_options.interval):
    cmd_parser.print_help()
    sys.exit(3)




def send_email_alert(msg):
    _from = "twitterecho@arianpasquali.com"
    _dest = ["socialecho-status@lists.labs.sapo.pt","arianpasquali@facebook.com"]

    msg = MIMEText(msg)
    msg['Subject'] = 'TwitterEcho Alert!'
    # msg['From'] = _from
    # msg['To'] = _dest

    server = smtplib.SMTP('smtp.gmail.com',587) #port 465 or 587
    server.ehlo()
    server.starttls()
    server.ehlo()
    server.login(_from,'reaction123')
    # Send the message via our own SMTP server, but don't include the
    # envelope header.
    server.sendmail(_from, _dest, msg.as_string())
    server.close()

def collectNumFound():
        period = getPeriod()
        url = cmd_options.solr_server + '/select?q=*:*+AND+created_at:['+period+']&rows=1'
        #print url

        request = urllib2.Request(url)
        base64string = base64.encodestring('%s:%s' % (cmd_options.http_user, cmd_options.http_pass)).replace('\n', '')
        request.add_header("Authorization", "Basic %s" % base64string)
        xml = urllib2.urlopen(request)

        solr_select_xml = parse(xml)
        return solr_select_xml.getElementsByTagName('result')[0].getAttribute('numFound')

def getPeriod():
        today = datetime.datetime.today()
        today30min = today - datetime.timedelta(minutes=cmd_options.interval)
        formatPeriod = "%s+TO+%s" % (today30min.strftime("%Y-%m-%dT%H:%M:%SZ"),today.strftime("%Y-%m-%dT%H:%M:%SZ"))
        return formatPeriod

def itsok():
        print 'OK: Everything looks fine'
        sys.exit(0)

def itsnotok():
        msg = "WARNING: I couldn't find new messages in the last "+ str(cmd_options.interval) +" minutes. Solr address " + cmd_options.solr_server
        print msg
        print "sending email alert"
        send_email_alert(msg)
        sys.exit(2)

try:
        found = collectNumFound()
except (IOError):
        itsnotok()

if (int(found) > 0):
        itsok()
else:
        itsnotok()
