#!/bin/bash
cd /scaleapp && git pull origin master
cd /scaleapp && /apache-maven-3.2.3/bin/mvn package 
cd /scaleapp && java -cp target/myapp-jar-with-dependencies.jar com.mycompany.myapp.App
