#!/bin/bash

readonly CALENDAR_FILE=${1}
if [ ! -e "${CALENDAR_FILE}" ]; then
  echo "File ${CALENDAR_FILE} does not exist".
  exit 1
fi

current_year=$(date +%Y)
readonly CALENDAR_URL="http://people.redhat.com/~rpelisse/jboss-weekly-editorial-${current_year}.ics"

curl -I "${CALENDAR_URL}" 2> /dev/null | grep -e '^HTTP' | grep -e 200
if [ "${?}" -eq 0 ]; then
  echo "${CALENDAR_URL} can be reached."
  readonly PUBLISHED_CALENDAR=${PUBLISHED_CALENDAR:-$(mktemp)}
  curl "${CALENDAR_URL}" -o "${PUBLISHED_CALENDAR}" 2> /dev/null
  echo 'Removing TZDATA to both file'
  readonly LEFT=$(mktemp)
  readonly RIGHT=$(mktemp)
  cat "${CALENDAR_FILE}" | sed -e '/DTSTAMP:/d' > "${LEFT}"
  cat "${PUBLISHED_CALENDAR}" | sed -e '/DTSTAMP:/d' > "${RIGHT}"
  echo 'comparing online with local'
  diff "${LEFT}" "${RIGHT}"
  status=${?}
  echo diff "${LEFT}" "${RIGHT}"
  #rm -f "${LEFT}" "${RIGHT}" "${PUBLISHED_CALENDAR}"
  if [ "${status}" -ne 0 ]; then
    echo "Local and published file mismatched."
    exit "${status}"
  fi
else
  echo "${CALENDAR_URL} can't be reached."
  exit 2
fi
