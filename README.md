# 📰 DailyNews

> OpenAI API와 Spring Scheduler를 활용한 자동 뉴스 생성 프로젝트

---

## 📌 프로젝트 개요

매일 아침 정해진 시간에 OpenAI API를 통해 사용자 맞춤형 뉴스를 자동 생성하여 사용자에게 제공합니다.  
Vue 기반의 프론트엔드와 Spring Boot 기반 백엔드가 Docker로 컨테이너화되어 AWS 환경에서 운영됩니다.

---

## 🧠 주요 기능

- ✅ OpenAI GPT를 활용한 뉴스 자동 생성
- ✅ Spring Scheduler를 이용한 정기 실행
- ✅ Redis를 통한 세션 관리 및 캐싱
- ✅ MySQL (AWS RDS) 기반 사용자 및 뉴스 저장
- ✅ Vue + Spring Boot 구성
- ✅ GitHub Actions 기반 CI/CD 자동화

---
![Image](https://github.com/user-attachments/assets/4628805c-6ebb-4a34-9db2-fd79294a9e29)

---

## 🗂️ 기술 스택

| 영역 | 기술                     |
|------|------------------------|
| 프론트엔드 | Vue + Vite + Typescript |
| 백엔드 | Spring Boot |
| AI | OpenAI GPT API         |
| DB | AWS RDS (MySQL)        |
| 캐시 | Redis                  |
| CI/CD | GitHub Actions         |
| 배포 | Docker, AWS EC2, Nginx |
