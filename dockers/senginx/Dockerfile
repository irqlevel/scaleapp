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
RUN yum -y install wget gcc make
RUN yum -y install pcre pcre-devel openssl openssl-devel
RUN wget http://senginx.org/download/senginx-1.6.0.tar.gz
RUN tar -xzvf senginx-1.6.0.tar.gz
RUN mkdir /senginx
RUN cd senginx-1.6.0/ && ./se-configure.sh --prefix=/senginx --with-http_ssl_module
RUN cd senginx-1.6.0/ && make
RUN cd senginx-1.6.0/ && make install

# Copy a configuration file from the current directory
COPY nginx.conf /senginx/conf/nginx.conf

# Append "daemon off;" to the beginning of the configuration
#RUN echo "daemon off;" >> /etc/nginx/nginx.conf

# Expose ports
EXPOSE 80

# Set the default command to execute
# when creating a new container
#CMD servicr nginx start
#ENTRYPOINT nginx/sbin/nginx
VOLUME senginx/
ENTRYPOINT senginx/sbin/nginx
#ENTRYPOINT /bin/bash
