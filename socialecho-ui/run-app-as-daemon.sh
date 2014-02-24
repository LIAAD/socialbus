#!/bin/bash
#
# Customize this section
#
# BASE_DIR is taken from the WORKSPACE environment variable set by Jenkins
#BASE_DIR=$WORKSPACE
BASE_DIR=/mnt/hd1/socialecho/socialecho-0.6/socialecho-ui
# Where is your server checked out from a VCS
SERVER_DIR=$BASE_DIR
GRAILS=/mnt/hd1/env/grails-2.3.4/bin/grails
# Should not conflict with Jenkins port
SERVER_HTTP_PORT=9190
SERVER_HTTPS_PORT=9443
# For polling the application
SERVER_URL=http://reaction.fe.up.pt:$SERVER_HTTP_PORT/socialecho
# How many times will the script check if the server is up and running
RETRIES=50

GRAILS_ENV=prod

# Pause between server polls
PAUSE_BETWEEN_RETRIES=3s

function start_server {
    /usr/local/sbin/daemonize -v -c $SERVER_DIR -e $BASE_DIR/grails.err -o $BASE_DIR/grails.out -p $BASE_DIR/grails.pid $GRAILS -Dserver.port=$SERVER_HTTP_PORT -Dserver.port.https=$SERVER_HTTPS_PORT $GRAILS_ENV run-app $1
    echo 'Grails server starting...'
}

function wait_until_server_started {
    INIT_TS=`date +%s`

    while [[ $RETRIES -gt 0 && -z $SERVER_STARTED ]]; do
        sleep $PAUSE_BETWEEN_RETRIES
       
        curl -Ifs $SERVER_URL > /dev/null
        RETVAL=$?
        if [ $RETVAL -eq 0 ]; then
            SERVER_STARTED=true
        else
            let CUR_TS=`date +%s`-$INIT_TS
            echo "waiting for the server; $CUR_TS s elapsed"
        fi

        let RETRIES-=1
    done

    if [[ $SERVER_STARTED ]]; then
        echo "Grails server started as daemon; pid `cat $BASE_DIR/grails.pid`"
        return 0
    else
        echo "Timeout"
        return 1
    fi
}

function stop_server {
    echo "Stopping Grails server with pid `cat $BASE_DIR/grails.pid`"
    if [ -e $BASE_DIR/grails.pid ]; then
        kill $1 `cat $BASE_DIR/grails.pid`
    fi
}

echo "BASE_DIR: $BASE_DIR"
echo "SERVER_DIR: $SERVER_DIR"
echo "SERVER_HTTP_PORT: $SERVER_HTTP_PORT"
echo "SERVER_HTTPS_PORT: $SERVER_HTTPS_PORT"
echo "SERVER_URL: $SERVER_URL"
echo "GRAILS: $GRAILS"

case "$1" in
    start)
        start_server
        wait_until_server_started
        if [[ $? -ne 0 ]]; then
            stop_server -KILL
        fi
        ;;
    start-https)
        start_server -https
        wait_until_server_started
        if [[ $? -ne 0 ]]; then
            stop_server -KILL
        fi
        ;;
    stop)
        stop_server
        ;;
    force-stop)
        stop_server -KILL
        ;;
    *)
        echo 'Usage: start|start-https stop|force-stop'
        ;;
esac
