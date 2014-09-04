############################################################
# Dockerfile to build Nginx Installed Containers
# Based on Ubuntu
############################################################

# Set the base image to Ubuntu
FROM fedora

# File Author / Maintainer
MAINTAINER Maintaner Name

# Install Nginx

# Add application repository URL to the default sources
#RUN echo "deb http://archive.ubuntu.com/ubuntu/ raring main universe" >> /etc/apt/sources.list

# Update the repository
#RUN yum update

# Install necessary tools
RUN yum -y install git which
RUN yum -y install wget

RUN wget https://s3-eu-west-1.amazonaws.com/javapackages/apache-maven-3.2.3-bin.tar.gz
RUN wget https://s3-eu-west-1.amazonaws.com/javapackages/jdk-8u20-linux-x64.rpm

RUN yum -y localinstall jdk-8u20-linux-x64.rpm

RUN tar -xzvf apache-maven-3.2.3-bin.tar.gz

RUN git clone https://github.com/irqlevel/scaleapp.git
RUN git config --global user.email irqlevel@gmail.com
RUN git config --global user.name "Andrey Smetanin"

RUN cd /scaleapp && /apache-maven-3.2.3/bin/mvn package

COPY run.sh run.sh
RUN chmod +x run.sh

VOLUME /scaleapp

# Expose ports
EXPOSE 8080

RUN yum -y install vim
RUN yum -y install postgresql
# Set the default command to execute
# when creating a new container
#CMD /bin/bash
ENTRYPOINT ./run.sh
