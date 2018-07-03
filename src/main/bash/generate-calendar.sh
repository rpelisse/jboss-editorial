#!/bin/bash

readonly CALENDAR_FILE=${1:-"jboss-weekly-editorial-$(date +%Y).ics"}
readonly ROSTER_FILE=${2:-"$(pwd)/roster.txt"}
readonly SCRIPT_FILE=$(pwd)/src/main/scala/roster.scala

mvn scala:script -DscriptFile=${SCRIPT_FILE}  -Droster.roster-file=${ROSTER_FILE} -Droster.generate-ical-file=${CALENDAR_FILE}
