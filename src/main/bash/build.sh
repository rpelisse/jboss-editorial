#!/bin/bash

mvn package -Pnative -Dquarkus.native.container-build=true -Prpm
