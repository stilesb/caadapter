FROM openjdk:6

RUN apt-get update -y && apt-get install -y git build-essential \
    ruby-dev ruby maven ant

ENV HUDSON_HOME $HOME/hudson_data

ENV DISPLAY :1.0
ENV PS1 '[\u@\h \W]\$ '
ENV JAVA_OPTS "-Djava.awt.headless=true"

RUN mkdir /code

ADD software /code/software
WORKDIR /code/software/build
RUN ant

ADD tools /code/tools
WORKDIR /code/tools/hudson-manager
# RUN ant

WORKDIR /code
