#navigation
pwd
cd

double tab

#see tree
ls
ls -l

#grant access
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

setsid ./start.sh User >/dev/null

#kill process
ps aux
kill -9 PID