#!/bin/sh

JAVA_HOME=/usr/local/jdk1.6.0_18
export JAVA_HOME

BTMUD_HOME=`pwd`/`dirname "${0}"`/..
BTMUD_HOME=/home/bresnaha/Mud/
echo $BTMUD_HOME

CP=""
ch=""
for i in ${BTMUD_HOME}/lib/*.jar
do
   CP="$CP""$ch""$i"
   ch=":"
done

PATH=${JAVA_HOME}/bin:${PATH}
cmd="${JAVA_HOME}/bin/java -cp $CP org.buzztroll.mud.MudFrame $@"

echo $cmd
$cmd
