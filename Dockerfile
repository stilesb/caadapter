FROM ubuntu:16.04

RUN apt-get update -y

RUN mkdir /code
ADD docs software tools /code/
