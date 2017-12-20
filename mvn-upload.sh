#!/usr/bin/env bash

set -ex

while read -r id version file
do
    mvn deploy:deploy-file -DgroupId=com.dafu -DartifactId="$id" -Dversion="$version" \
        -DgeneratePom=true -Dpackaging=jar -DrepositoryId=nexus-releases \
        -Durl=http://mvn.keepwork.com/repository/maven-releases -Dfile=lib/"$file"
done <list

