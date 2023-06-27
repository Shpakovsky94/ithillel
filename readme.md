#amazon web services
https://aws.amazon.com/console/

#connect to the ec2
ssh -i "ithillel_test.pem" ubuntu@ec2-16-170-203-215.eu-north-1.compute.amazonaws.com

#change to super user
sudo su -

#install java
sudo apt update
sudo apt install openjdk-17-jdk openjdk-17-jre
java -version

#install mysql
sudo apt install mysql-server

#connect to mysql and set custom pass
sudo mysql
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'rootroot';

#install maven
sudo apt install maven
#for java17 usage we need to install mvn 3.9..
wget https://dlcdn.apache.org/maven/maven-3/3.9.3/binaries/apache-maven-3.9.3-bin.tar.gz
tar xvf apache-maven-3.9.3-bin.tar.gz
cd /usr/share/maven
rm -r *
sudo cp -r /tmp/apache-maven-3.9.3/* .

mvn -version

#navigate to the project folder
cd /opt/

#download the files from the git
git clone https://github.com/Shpakovsky94/ithillel.git
git checkout 2023_lecture47

#build the project
mvn clean install

#start the project

#start in background be
nohup mvn spring-boot:run >/dev/null 2>&1 &

#start in background fe
nohup npm run serve >/dev/null 2>&1 &

#kill process
ps aux
kill -9 30814

#add port on which you host BE app to ec2 security group
https://stackoverflow.com/questions/17161345/how-to-open-a-web-server-port-on-ec2-instance

#see BE response in browser
https://ec2-16-170-203-215.eu-north-1.compute.amazonaws.com:8081/all

#connect ec2 to godaddy
http://fredericpaladin.com/kb/connect-aws-ec2-instance-to-godaddy-domain/


#install nginx wiki
https://www.digitalocean.com/community/tutorials/how-to-install-nginx-on-ubuntu-20-04

#install nginx
sudo apt update
sudo apt install nginx

sudo ufw allow 'Nginx HTTP'
sudo ufw enable
sudo ufw status
systemctl status nginx

sudo systemctl start nginx
sudo systemctl restart nginx

nano /etc/nginx/nginx.conf

#insert into nginx.conf for only BE support
user www-data;
worker_processes auto;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

events {
    worker_connections 1024;
}

http {
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log;

    gzip on;
    gzip_disable "msie6";

    server {
        listen 80;
        server_name www.lafcard.site;

        location / {
            proxy_pass http://localhost:8081;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
        }
    }
}

#insert into nginx.conf for both FE and BE support
user www-data;
worker_processes auto;
pid /run/nginx.pid;
include /etc/nginx/modules-enabled/*.conf;

events {
    worker_connections 1024;
}

http {
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 65;
    types_hash_max_size 2048;
    include /etc/nginx/mime.types;
    default_type application/octet-stream;

    access_log /var/log/nginx/access.log;
    error_log /var/log/nginx/error.log;

    gzip on;
    gzip_disable "msie6";

    server {
        listen 80;
        server_name www.lafcard.site;

        location / {
            proxy_pass http://localhost:8080;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
        }
    }

    server {
        listen 80;
        server_name api.lafcard.site;

        location / {
            proxy_pass http://localhost:8081;
            proxy_set_header Host $host;
            proxy_set_header X-Real-IP $remote_addr;
            proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto $scheme;
            proxy_set_header X-Forwarded-Host $host;
        }
    }
}

sudo nginx -t
sudo systemctl restart nginx

#systemctl service start java app
#1. Create service named ithillel_full using vi command
    sudo nano /etc/systemd/system/ithillel_full.servic

#2. Copy paste the following content into the file
    [Unit]
    Description=ithillel_full application service
    
    [Service]
    User=ubuntu
    ExecStart=/usr/bin/java -jar /opt/ithillel/target/ithillel_full-1.0-SNAPSHOT.jar
    ExitStatus=143
    
    TimeoutStopSec=10
    Restart=on-failure
    RestartSec=5
    
    [Install]
    WantedBy=multi-user.targe

#3. Enable the service
    sudo systemctl enable ithillel_ful

#4. Start the service
    sudo systemctl stop ithillel_ful
    sudo systemctl start ithillel_ful

#5. Reload system daemon
    sudo systemctl daemon-reloa

#6. Check the service status
    sudo systemctl status ithillel_full

#7. Check service logs using the following command
    sudo journalctl -u ithillel_full.service --no-page

#certbot
https://www.digitalocean.com/community/tutorials/how-to-secure-nginx-with-let-s-encrypt-on-ubuntu-20-04

#fix Operation timed out on ec2
#Edit User Data
https://repost.aws/knowledge-center/ec2-linux-resolve-ssh-connection-errors
