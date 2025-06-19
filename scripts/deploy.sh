##!/bin/bash
#cd /home/ec2-user/pium
#docker-compose down
#docker-compose up -d --build


#!/bin/bash
REPOSITORY=/home/ec2-user/pium
CONTAINER_NAME=pium-spring
ECR_REGISTRY=073658113926.dkr.ecr.ap-northeast-2.amazonaws.com

cd $REPOSITORY

# docker-compose.yml이 없으면 복사해서 이름 변경
if [ ! -f docker-compose.yml ] && [ -f release-docker-compose.yml ]; then
  echo "> 📝 Rename release-docker-compose.yml -> docker-compose.yml"
  mv release-docker-compose.yml docker-compose.yml
fi

echo "> 🔵 PULL DOCKER IMAGE FROM ECR"
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin $ECR_REGISTRY

echo "> 🔵 RUN APPLICATION CONTAINER"
docker compose pull spring
docker compose up -d
