#!/bin/bash

readonly ROSTER_FILE=${ROSTER_FILE:-'roster.txt'}

for trigram in $(head "${ROSTER_FILE}"  | grep "=" | cut -f1 -d\  )
do
    nbPost=$(grep -e "${trigram}" "${ROSTER_FILE}" | sed -e '/=/d' | wc -l )
    name=$(grep -e "${trigram}" "${ROSTER_FILE}" | grep -e '=' | sed -e 's/^[^"]*"//' -e 's/".*$//')
    echo "${name}: ${nbPost}"
done
