#!/bin/bash

readonly ROSTER_HOME='' # where the clone of the github project lives
readonly ROSTER=''      # path the roster jar
readonly SMTP_SERVER='' # SMTP Settings
readonly SMTP_PORT=''

"${ROSTER_HOME}/src/main/bash/run-scala.sh" "${ROSTER_HOME}/src/main/scala/roster.scala" \
    -f "${ROSTER_HOME}/roster.txt" -h "${SMTP_SERVER}" -p "${SMTP_PORT}"
