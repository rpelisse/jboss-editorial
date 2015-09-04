#!/bin/bash


readonly ROSTER_VERSION=${ROSTER_VERSION:-'0.1-SNAPSHOT'}
readonly ROSTER=${ROSTER:-"${HOME}/.m2/repository/org/jboss/weekly/roster/${ROSTER_VERSION}/roster-${ROSTER_VERSION}.jar"}
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

scala  -classpath ".:${ROSTER}" "${SCRIPT}" ${@}
