#!/bin/bash

(
    until MAVEN_OPTS="-Xmx2048m" mvn exec:java -Dexec.mainClass="twitter.datacollector.TweetKeywordCollector"; do
        echo "TwitterKeywordCollector crashed with exit code $?.  Respawning... " >&2
        sleep 5
    done
) &
