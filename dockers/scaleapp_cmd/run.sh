#!/bin/bash
cd /scaleapp && git pull
cd /scaleapp && /apache-maven-3.2.3/bin/mvn package 
cd /scaleapp && /bin/bash
