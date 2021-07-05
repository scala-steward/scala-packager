#!/bin/sh

JAVACMD="$(which java)"

if [ -z "$JAVACMD" ] ; then
  echo "Not found java executable."
  exit 1
fi

if [ -z "$JAVA_HOME" ] ; then
  echo "Not found JAVA_HOME environment variable."
  exit 1
fi


$JAVACMD -cp {{main_jar}}:{{lib_path}}* {{main_class}} "$@"