#!/bin/bash

set -e

ERR_MSG=''

trap 'echo "Error occurred: $ERR_MSG. Exiting deploy script."; exit 1' ERR

if [ -f .env ]; then
    source .env
    echo "환경 변수 로드"
else
  echo ".env 파일 찾기 실패"
  exit 1
fi

ssh -i ~/.ssh/dailynews.pem ec2-user@$EC2_IP
mkdir success