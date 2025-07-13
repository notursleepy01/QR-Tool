#!/usr/bin/env sh

# Determine the Java command to use to start the JVM.
if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        # IBM's JDK on AIX uses strange locations for the executables
        JAVACMD="$JAVA_HOME/jre/sh/java"
    else
        JAVACMD="$JAVA_HOME/bin/java"
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME\n\nIf this is a JDK, rather than JRE, make sure that $JAVA_HOME/bin/java exists."
    fi
else
    JAVACMD="java"
    which java >/dev/null 2>&1 || die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.\n\nPlease set the JAVA_HOME variable in your environment to match the location of your Java installation."
fi

# Determine the script directory.
SCRIPT_DIR=$(dirname "$0")

# Add default JVM options here. You can also use JAVA_OPTS and GRADLE_OPTS to pass JVM options to this script.
DEFAULT_JVM_OPTS="-Xmx64m -Xms64m"

APP_HOME="$SCRIPT_DIR"
APP_NAME="Gradle"
APP_BASE_NAME=$(basename "$0")

# OS specific support (must be 'true' or 'false').
cygwin=false
darwin=false
mingw=false
case "`uname`" in
  CYGWIN*)    cygwin=true;;
  Darwin*)    darwin=true;;
  MINGW*)     mingw=true;;
esac

# For Cygwin, ensure paths are in UNIX format before anything else.
if $cygwin ; then
    [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
    [ -n "$CLASSPATH" ] && CLASSPATH=`cygpath --path --unix "$CLASSPATH"`
fi

# For Darwin, add a special option when running as root.
if $darwin && [ "$(id -u)" = "0" ]; then
    DEFAULT_JVM_OPTS="$DEFAULT_JVM_OPTS -Djava.awt.headless=true"
fi

# Escape application args
quote_app_args() {
    printf "%s" "$*" | sed -e 's/\\/\\\\/g' -e 's/"/\\"/g' -e 's/`/\\`/g' -e 's/\$/\\\$/g'
}

# Collect all arguments for the Java command
APP_ARGS="$(quote_app_args "$@")"

# Determine the class path for the launcher
CLASSPATH="$APP_HOME/gradle/wrapper/gradle-wrapper.jar"

# For Cygwin, switch paths to Windows format before running java
if $cygwin ; then
    JAVA_HOME=`cygpath --windows "$JAVA_HOME"`
    CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
fi

# Execute Java command
exec "$JAVACMD" $DEFAULT_JVM_OPTS "$JAVA_OPTS" "$GRADLE_OPTS" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$APP_ARGS"