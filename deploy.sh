#!/bin/bash
if [ -f .env ]; then
    source .env
    echo "환경 변수 로드"
else
  echo ".env 파일 찾기 실패"
  exit 1
fi

ssh -T -i ~/.ssh/dailynews.pem ec2-user@$EC2_IP \
 "DOCKER_USERNAME=$DOCKER_USERNAME DOCKER_PASSWORD=$DOCKER_PASSWORD bash -s" << 'EOF'

echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin \

if docker ps --filter "name=dailynews-blue" --quiet | grep -E .; then
  RUN_TARGET="dailynews-green"
  STOP_TARGET="dailynews-blue"
  WAS_RUN_PORT=8081
  WAS_STOP_PORT=8080

else
  RUN_TARGET="dailynews-blue"
  STOP_TARGET="dailynews-green"
  WAS_RUN_PORT=8080
  WAS_STOP_PORT=8081
fi

docker run -d --network ec2-user_network -p $WAS_RUN_PORT:8080 --name $RUN_TARGET pgw4712/dailynews:latest
docker logout

sleep 10
STOP_CONTAINER_ID=$(docker ps --filter "name=$STOP_TARGET" --quiet)

if [ -n $STOP_CONTAINER_ID ]; then
  docker stop $STOP_CONTAINER_ID
  docker rm $STOP_CONTAINER_ID
fi

sudo systemctl reload nginx
EOF