FROM openjdk:8-jre

RUN apt-get update && \
    apt-get install -y \
    less \
    nano \
    util-linux \
    cron \
    procps

RUN mkdir -p /ithillel/scripts
RUN mkdir -p /ithillel/logs

ADD target/myapp.jar ithillel/scripts/myapp.jar

COPY start-cron-task.sh /ithillel/scripts
COPY start-main-1200.sh /ithillel/scripts
COPY start-main-4500.sh /ithillel/scripts

EXPOSE 8070

ENTRYPOINT ["/bin/bash"]
