#!/bin/bash

readonly PIDS=$(ps -eF | grep scala | grep roster | sed -e 's/^rpelisse *//' -e 's/^\([0-9]*\).*$/\1/')

if [ ! -z "${PIDS}" ]; then
  echo "PIDS found: ${PIDS}"
  echo "Killing PIDS"
  kill -9 ${PIDS}
fi
