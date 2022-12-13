1. start the docker container from ``docker-compose.yml``

2. get CONTAINER ID from the list and insert it to the query below
```
docker ps
```
2. login into the container
 ```
 docker exec -it *CONTAINER ID* /bin/bash
 ```
3. connect to the db
``` 
mysql -u root -p rootroot
```
