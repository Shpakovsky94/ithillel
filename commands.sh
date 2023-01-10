#navigation
pwd
cd ../
cd /

double tab

#see tree
ls
ls -lh

#grant access
#https://chmod-calculator.com/
chmod 777

#show only match
grep | ""

#file content read
cat

zgrep 'Sleep param' myapp.log

tail -f

less

#how to search in less
to search :
down ?+text
up /+text

next key N
back key N + shift


#maven build assebly with jar
mvn clean compile assembly:single

#docker build image
docker build -t myapp .

#docker start container
docker-compose up -d

#docker copy container
docker cp  83f52fd5d2a4:/ithillel/scripts/start.sh start2.sh

#java app start
sh start.sh
java -jar target/myapp-jar-with-dependencies.jar 1200

setsid ./start-main-1200.sh User >/dev/null
setsid ./start-main-4500.sh User >/dev/null

#kill process
ps aux
kill -9 PID

#cron task
#https://crontab.guru/
# NB! server time
crontab -e
crontab -l

5 21 * * * sh /ithillel/scripts/start-cron-task.sh > /ithillel/scripts/logs/cron.log 2>&1
50 21 * * * root cd /ithillel/scripts && /bin/sh ./start-cron-task.sh
* * * * *  /bin/bash /ithillel/scripts/start-cron-task.sh