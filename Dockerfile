FROM openjdk:8-jre

RUN apt-get update && \
    apt-get install -y \
    less \
    nano \
    util-linux \
    procps

RUN mkdir -p /ithillel/scripts
RUN mkdir -p /ithillel/logs

ADD target/myapp-jar-with-dependencies.jar ithillel/scripts/myapp.jar

COPY start.sh /ithillel/scripts

EXPOSE 8070

ENTRYPOINT ["/bin/bash"]