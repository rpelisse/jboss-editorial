#!/bin/bash

readonly CLASSPATH=${CLASSPATH}
readonly SCRIPT=${1}
shift

if [ -z ${SCRIPT} ]; then
  echo "No scala script provided"
  exit 1
fi

if [ ! -e "${SCRIPT}" ]; then
  echo "No such file:${SCRIPT}"
  exit 2
fi

if [ -z "${CLASSPATH}" ]; then
  echo "CLASSPATH undefined."
  exit 3
fi

scala -nocompdaemon -classpath "${CLASSPATH}" "${SCRIPT}" ${@}
