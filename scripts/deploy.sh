##!/bin/bash
#cd /home/ec2-user/pium
#docker-compose down
#docker-compose up -d --build


#!/bin/bash
REPOSITORY=/home/ec2-user/pium
CONTAINER_NAME=pium-spring
ECR_REGISTRY=063355381577.dkr.ecr.ap-northeast-2.amazonaws.com

cd $REPOSITORY

# docker-compose.yml이 없으면 복사해서 이름 변경
if [ -f release-docker-compose.yml ]; then
  echo "> 📝 Copy release-docker-compose.yml → docker-compose.yml (overwrite)"
  cp -f release-docker-compose.yml docker-compose.yml
fi

echo "> 🔵 PULL DOCKER IMAGE FROM ECR"
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

sudo chown root:root ./filebeat/filebeat.yml
sudo chmod 644 ./filebeat/filebeat.yml

echo "> 🔵 RUN APPLICATION CONTAINER"
docker compose down
docker compose pull spring
docker compose up -d
