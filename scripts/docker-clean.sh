#!/bin/bash

echo "> 🧹 Docker Clean up Start"

# 실행 중인 컨테이너 전부 종료
docker ps -q | xargs -r docker stop

# 컨테이너 전부 삭제
docker ps -a -q | xargs -r docker rm

# 안쓰는 네트워크 삭제
docker network prune -f

# 안쓰는 볼륨 삭제
docker volume prune -f

# 안쓰는 이미지 전부 삭제
docker image prune -a -f

echo "> 🧹 Docker Clean up Complete"