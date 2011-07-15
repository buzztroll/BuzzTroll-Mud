@echo off

set BTMUD_HOME=%~dp0..

for %%i in ("%BTMUD_HOME%\lib\*.jar") do call %BTMUD_HOME%\bin\setcp.bat %%i

java -classpath %LC_CLASSPATH% org.buzztroll.mud.MudFrame %1 %2 %3 %4 %5 %6 %7 %8 %9
