#! /bin/sh

./tmio

 ${JAVA_HOME}/bin/java -classpath ./lib/wsdl4j.jar:./bin/BuzzTrollMud.jar:./lib/JFontChooser.jar org.buzztroll.mud.MudCommandLine poop.xml

stty sane
