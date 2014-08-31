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
RUN wget http://nginx.org/download/nginx-1.7.4.tar.gz
RUN tar -xzvf nginx-1.7.4.tar.gz
RUN mkdir /nginx
RUN cd nginx-1.7.4/ && ./configure --prefix=/nginx --with-http_ssl_module
RUN cd nginx-1.7.4/ && make
RUN cd nginx-1.7.4/ && make install

# Copy a configuration file from the current directory
COPY nginx.conf /nginx/conf/nginx.conf

# Append "daemon off;" to the beginning of the configuration
#RUN echo "daemon off;" >> /etc/nginx/nginx.conf

# Expose ports
EXPOSE 80

# Set the default command to execute
# when creating a new container
#CMD servicr nginx start
#ENTRYPOINT nginx/sbin/nginx
ENTRYPOINT nginx/sbin/nginx

