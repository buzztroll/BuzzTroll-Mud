@echo off

if "%HME_HOME%" == "" goto noHME
goto HME

:noHME

    echo Error: HME_HOME not set
    goto end

:HME

set BTMUD_HOME=%~dp0..

for %%i in ("%BTMUD_HOME%\lib\*.jar") do call %BTMUD_HOME%\bin\setcp.bat %%i

java -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true -cp "%HME_HOME%"\hme.jar;%LC_CLASSPATH% com.tivo.hme.sdk.Factory org.buzztroll.mud.tivo.Mud %1 %2 %3 %4 %5 %6 %7 %8 %9

:end
