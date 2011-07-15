#!/bin/sh

if [ ! -d "$HME_HOME" ] ; then
  echo "Error: HME_HOME not set" 1>&2
  exit 1
fi

BTMUD_HOME=`dirname "${0}"`/..

CP=$HME_HOME/hme.jar
for i in ${BTMUD_HOME}/lib/*.jar
do
   CP=$CP:"$i"
done

java -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -cp $CP com.tivo.hme.sdk.Factory org.buzztroll.mud.tivo.Mud $@
