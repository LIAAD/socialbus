#!/bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

# -----------------------------------------------------------------------------
# Start/Stop Script for the TwitterEcho Server
#
# Environment Variable Prerequisites
#
#   TWITTERECHO_HOME   May point at your Catalina "build" directory.
#
#   TWITTERECHO_BASE   (Optional) Base directory for resolving dynamic portions
#                   of a Catalina installation.  If not present, resolves to
#                   the same directory that TWITTERECHO_HOME points to.
#
#   TWITTERECHO_OUT    (Optional) Full path to a file where stdout and stderr
#                   will be redirected.
#                   Default is $TWITTERECHO_BASE/logs/catalina.out
#
#   TWITTERECHO_OPTS   (Optional) Java runtime options used when the "start",
#                   or "run" command is executed.
#
#   TWITTERECHO_TMPDIR (Optional) Directory path location of temporary directory
#                   the JVM should use (java.io.tmpdir).  Defaults to
#                   $TWITTERECHO_BASE/temp.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#                   Required to run the with the "debug" argument.
#
#   JRE_HOME        Must point at your Java Development Kit installation.
#                   Defaults to JAVA_HOME if empty.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the "start",
#                   "stop", or "run" command is executed.
#
#   JAVA_ENDORSED_DIRS (Optional) Lists of of colon separated directories
#                   containing some jars in order to allow replacement of APIs
#                   created outside of the JCP (i.e. DOM and SAX from W3C).
#                   It can also be used to update the XML parser implementation.
#                   Defaults to $TWITTERECHO_HOME/endorsed.
#
#   JPDA_TRANSPORT  (Optional) JPDA transport used when the "jpda start"
#                   command is executed. The default is "dt_socket".
#
#   JPDA_ADDRESS    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. The default is 8000.
#
#   JPDA_SUSPEND    (Optional) Java runtime options used when the "jpda start"
#                   command is executed. Specifies whether JVM should suspend
#                   execution immediately after startup. Default is "n".
#
#   JPDA_OPTS       (Optional) Java runtime options used when the "jpda start"
#                   command is executed. If used, JPDA_TRANSPORT, JPDA_ADDRESS,
#                   and JPDA_SUSPEND are ignored. Thus, all required jpda
#                   options MUST be specified. The default is:
#
#                   -agentlib:jdwp=transport=$JPDA_TRANSPORT,
#                       address=$JPDA_ADDRESS,server=y,suspend=$JPDA_SUSPEND
#
#   TWITTERECHO_PID    (Optional) Path of the file which should contains the pid
#                   of catalina startup java process, when start (fork) is used
#
#   LOGGING_CONFIG  (Optional) Override TwitterEcho's logging config file
#                   Example (all one line)
#                   LOGGING_CONFIG="-Djava.util.logging.config.file=$TWITTERECHO_BASE/conf/logging.properties"
#
#   LOGGING_MANAGER (Optional) Override TwitterEcho's logging manager
#                   Example (all one line)
#                   LOGGING_MANAGER="-Djava.util.logging.manager=org.apache.juli.ClassLoaderLogManager"
#
# $Id: catalina.sh 1476739 2013-04-28 09:13:33Z jfclere $
# -----------------------------------------------------------------------------

# OS specific support.  $var _must_ be set to either true or false.
cygwin=false
os400=false
darwin=false
case "`uname`" in
CYGWIN*) cygwin=true;;
OS400*) os400=true;;
Darwin*) darwin=true;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set TWITTERECHO_HOME if not already set
[ -z "$TWITTERECHO_HOME" ] && TWITTERECHO_HOME=`cd "$PRGDIR/.." >/dev/null; pwd`

# Copy TWITTERECHO_BASE from TWITTERECHO_HOME if not already set
[ -z "$TWITTERECHO_BASE" ] && TWITTERECHO_BASE="$TWITTERECHO_HOME"

# Ensure that any user defined CLASSPATH variables are not used on startup,
# but allow them to be specified in setenv.sh, in rare case when it is needed.
CLASSPATH=

if [ -r "$TWITTERECHO_BASE/bin/setenv.sh" ]; then
  . "$TWITTERECHO_BASE/bin/setenv.sh"
elif [ -r "$TWITTERECHO_HOME/bin/setenv.sh" ]; then
  . "$TWITTERECHO_HOME/bin/setenv.sh"
fi

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$JRE_HOME" ] && JRE_HOME=`cygpath --unix "$JRE_HOME"`
  [ -n "$TWITTERECHO_HOME" ] && TWITTERECHO_HOME=`cygpath --unix "$TWITTERECHO_HOME"`
  [ -n "$TWITTERECHO_BASE" ] && TWITTERECHO_BASE=`cygpath --unix "$TWITTERECHO_BASE"`
  [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  export QIBM_MULTI_THREADED=Y
fi

# Get standard Java environment variables
if $os400; then
  # -r will Only work on the os400 if the files are:
  # 1. owned by the user
  # 2. owned by the PRIMARY group of the user
  # this will not work if the user belongs in secondary groups
  BASEDIR="$TWITTERECHO_HOME"
  . "$TWITTERECHO_HOME"/bin/setclasspath.sh
else
  if [ -r "$TWITTERECHO_HOME"/bin/setclasspath.sh ]; then
    BASEDIR="$TWITTERECHO_HOME"
    . "$TWITTERECHO_HOME"/bin/setclasspath.sh
  else
    echo "Cannot find $TWITTERECHO_HOME/bin/setclasspath.sh"
    echo "This file is needed to run this program"
    exit 1
  fi
fi

if [ -z "$TWITTERECHO_BASE" ] ; then
  TWITTERECHO_BASE="$TWITTERECHO_HOME"
fi

# Add tomcat-juli.jar and bootstrap.jar to classpath
# tomcat-juli.jar can be over-ridden per instance
if [ ! -z "$CLASSPATH" ] ; then
  CLASSPATH="$CLASSPATH":
fi
if [ "$TWITTERECHO_BASE" != "$TWITTERECHO_HOME" ] && [ -r "$TWITTERECHO_BASE/twitterecho-server.jar" ] ; then
  CLASSPATH="$CLASSPATH""$TWITTERECHO_BASE"/twitterecho-server.jar
else
  CLASSPATH="$CLASSPATH""$TWITTERECHO_HOME"/twitterecho-server.jar
fi

if [ -z "$TWITTERECHO_OUT" ] ; then
  TWITTERECHO_OUT="$TWITTERECHO_BASE"/logs/twitterecho.out
fi

if [ -z "$TWITTERECHO_TMPDIR" ] ; then
  # Define the java.io.tmpdir to use for Catalina
  TWITTERECHO_TMPDIR="$TWITTERECHO_BASE"/temp
fi

# Bugzilla 37848: When no TTY is available, don't output to console
have_tty=0
if [ "`tty`" != "not a tty" ]; then
    have_tty=1
fi

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  JRE_HOME=`cygpath --absolute --windows "$JRE_HOME"`
  TWITTERECHO_HOME=`cygpath --absolute --windows "$TWITTERECHO_HOME"`
  TWITTERECHO_BASE=`cygpath --absolute --windows "$TWITTERECHO_BASE"`
  TWITTERECHO_TMPDIR=`cygpath --absolute --windows "$TWITTERECHO_TMPDIR"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
fi

# Set juli LogManager config file if it is present and an override has not been issued
if [ -z "$LOGGING_CONFIG" ]; then
  if [ -r "$TWITTERECHO_BASE"/conf/logging.properties ]; then
    LOGGING_CONFIG="-Djava.util.logging.config.file=$TWITTERECHO_BASE/conf/log4j.properties"
  else
    # Bugzilla 45585
    LOGGING_CONFIG="-Dnop"
  fi
fi

# ----- Execute The Requested Command -----------------------------------------

# Bugzilla 37848: only output this if we have a TTY
if [ $have_tty -eq 1 ]; then
  echo "Using TWITTERECHO_BASE:   $TWITTERECHO_BASE"
  echo "Using TWITTERECHO_HOME:   $TWITTERECHO_HOME"
  echo "Using TWITTERECHO_TMPDIR: $TWITTERECHO_TMPDIR"
  if [ "$1" = "debug" ] ; then
    echo "Using JAVA_HOME:       $JAVA_HOME"
  else
    echo "Using JRE_HOME:        $JRE_HOME"
  fi
  echo "Using CLASSPATH:       $CLASSPATH"
  if [ ! -z "$TWITTERECHO_PID" ]; then
    echo "Using TWITTERECHO_PID:    $TWITTERECHO_PID"
  fi
fi

if [ "$1" = "start" ] ; then

echo "##############################################################################"
echo "___________       .__  __    __              ___________      .__            "
echo "\__    ___/_  _  _|__|/  |__/  |_  __________\_   _____/ ____ |  |__   ____  "
echo "  |    |  \ \/ \/ /  \   __\   __\/ __ \_  __ \    __)__/ ___\|  |  \ /  _ \ "
echo "  |    |   \     /|  ||  |  |  | \  ___/|  | \/        \  \___|   Y  (  <_> )"
echo "  |____|    \/\_/ |__||__|  |__|  \___  >__| /_______  /\___  >___|  /\____/ "
echo "                                      \/             \/     \/     \/        "
echo "##############################################################################"
echo " TwitterEcho Server, 2013, Version 0.3"
echo "##############################################################################"

  if [ ! -z "$TWITTERECHO_PID" ]; then
    if [ -f "$TWITTERECHO_PID" ]; then
      if [ -s "$TWITTERECHO_PID" ]; then
        echo "Existing PID file found during start."
        if [ -r "$TWITTERECHO_PID" ]; then
          PID=`cat "$TWITTERECHO_PID"`
          ps -p $PID >/dev/null 2>&1
          if [ $? -eq 0 ] ; then
            echo "TwitterEcho appears to still be running with PID $PID. Start aborted."
            exit 1
          else
            echo "Removing/clearing stale PID file."
            rm -f "$TWITTERECHO_PID" >/dev/null 2>&1
            if [ $? != 0 ]; then
              if [ -w "$TWITTERECHO_PID" ]; then
                cat /dev/null > "$TWITTERECHO_PID"
              else
                echo "Unable to remove or clear stale PID file. Start aborted."
                exit 1
              fi
            fi
          fi
        else
          echo "Unable to read PID file. Start aborted."
          exit 1
        fi
      else
        rm -f "$TWITTERECHO_PID" >/dev/null 2>&1
        if [ $? != 0 ]; then
          if [ ! -w "$TWITTERECHO_PID" ]; then
            echo "Unable to remove or write to empty PID file. Start aborted."
            exit 1
          fi
        fi
      fi
    fi
  fi

  shift
  touch "$TWITTERECHO_OUT"
    "$_RUNJAVA" "$LOGGING_CONFIG" $LOGGING_MANAGER $JAVA_OPTS $TWITTERECHO_OPTS \
      -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" -classpath "$CLASSPATH" \
	  -Dtwitterecho.home="$TWITTERECHO_HOME" \
	  -Djava.io.tmpdir="$TWITTERECHO_TMPDIR" \
	  -Dconfig="$TWITTERECHO_HOME"/conf/server.conf \
	  -Dlangprofiles="$TWITTERECHO_HOME"/conf/langprofiles \
      pt.sapo.labs.twitterecho.Main "$@" start \
      >> "$TWITTERECHO_OUT" 2>&1 &

  if [ ! -z "$TWITTERECHO_PID" ]; then
    echo $! > "$TWITTERECHO_PID"
  fi

elif [ "$1" = "stop" ] ; then
	echo "Stopping TwitterEcho Server"
	ps aux | grep twitterecho-server.sh | awk '{print $2}' | xargs kill -9
	echo "Done"

else

  echo "Usage: twitterecho-server.sh ( commands ... )"
  echo "commands:"
  echo "  start             Start Twitterecho in a separate window"
  echo "  stop              Stop Twitterecho, waiting up to 5 seconds for the process to end"
  echo "  stop n            Stop Twitterecho, waiting up to n seconds for the process to end"
  echo "  stop -force       Stop Twitterecho, wait up to 5 seconds and then use kill -KILL if still running"
  echo "  stop n -force     Stop Twitterecho, wait up to n seconds and then use kill -KILL if still running"
  echo "Note: Waiting for the process to end and use of the -force option require that \$TWITTERECHO_PID is defined"
  exit 1

fi
