#!/bin/bash

(
    until MAVEN_OPTS="-Xmx2048m" mvn exec:java -Dexec.mainClass="twitter.datacollector.TweetLocationCollector"; do
        echo "TwitterLocationCollector crashed with exit code $?.  Respawning... " >&2
        sleep 5
    done
) &
