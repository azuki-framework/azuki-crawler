@echo off
rem Licensed to the Apache Software Foundation (ASF) under one or more
rem contributor license agreements.  See the NOTICE file distributed with
rem this work for additional information regarding copyright ownership.
rem The ASF licenses this file to You under the Apache License, Version 2.0
rem (the "License"); you may not use this file except in compliance with
rem the License.  You may obtain a copy of the License at
rem
rem     http://www.apache.org/licenses/LICENSE-2.0
rem
rem Unless required by applicable law or agreed to in writing, software
rem distributed under the License is distributed on an "AS IS" BASIS,
rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
rem See the License for the specific language governing permissions and
rem limitations under the License.

cd /d %~dp0
cd ../

rem ##################################################################
set PROPERTIESFILE=conf/azuki-crawler.xml
rem ##################################################################

set CLASSPATH=.\*;
set CLASSPATH=%CLASSPATH%.\lib\*;
set MAINCLASS=org.azkfw.crawler.Crawler

if ""%1"" == ""start"" goto doStart
if ""%1"" == ""stop"" goto doStop
if ""%1"" == ""restart"" goto doRestart
if ""%1"" == ""help"" goto doHelp
if ""%1"" == ""version"" goto doVersion

echo Usage:  crawler ( commands ... )
echo commands:
echo   start
echo   stop
echo   restart
echo   help
echo   version
goto end

:doStart
set ACTION=start
goto execCmd

:doStop
set ACTION=stop
goto execCmd

:doRestart
set ACTION=restart
goto execCmd

:doHelp
set ACTION=help
goto execCmd

:doVersion
set ACTION=version
goto execCmd

:execCmd
set MAIN
call java -cp %CLASSPATH% %MAINCLASS% %ACTION% -baseDir ./ -configFile %PROPERTIESFILE%
goto end

:end