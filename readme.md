1. build jar file 
```
mvn clean compile assembly:single
```
2. build docker image.
```
docker build -t myapp . -d
```
3. start the docker container from 
```
docker-compose up -d
```
4. get CONTAINER ID from the list and insert it to the query below
```
docker ps
```
2. login into the container
 ```
 docker exec -it *CONTAINER ID* /bin/bash
 ```
