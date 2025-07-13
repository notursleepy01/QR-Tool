@rem
@rem Copyright 2015 the original author or authors.
@rem
@rem Licensed under the Apache License, Version 2.0 (the "License");
@rem you may not use this file except in compliance with the License.
@rem You may obtain a copy of the License at
@rem
@rem      http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem Unless required by applicable law or agreed to in writing, software
@rem distributed under the License is distributed on an "AS IS" BASIS,
@rem WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem See the License for the specific language governing permissions and
@rem limitations under the License.
@rem

@echo off

setlocal

rem Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
set DEFAULT_JVM_OPTS=-Xmx64m -Xms64m

set APP_HOME=%~dp0
set APP_NAME="Gradle"
set APP_BASE_NAME=%~nx0

rem Find Java
if defined JAVA_HOME (
    set JAVACMD="%JAVA_HOME%\bin\java.exe"
) else (
    set JAVACMD=java.exe
)

if not exist %JAVACMD% (
    echo ERROR: JAVA_HOME is not set and no 'java.exe' command could be found in your PATH.
    echo.
    echo Please set the JAVA_HOME variable in your environment to match the location of your Java installation.
    goto :eof
)

rem Set the classpath for the wrapper
set CLASSPATH=%APP_HOME%gradle\wrapper\gradle-wrapper.jar

rem Execute Java command
"%JAVACMD%" %DEFAULT_JVM_OPTS% %JAVA_OPTS% %GRADLE_OPTS% -classpath "%CLASSPATH%" org.gradle.wrapper.GradleWrapperMain %*
