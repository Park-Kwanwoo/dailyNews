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

![배포 아키텍처](https://private-user-images.githubusercontent.com/77375127/456060153-634a1d60-461e-47cd-8930-65bbc3b2daa1.png?jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJnaXRodWIuY29tIiwiYXVkIjoicmF3LmdpdGh1YnVzZXJjb250ZW50LmNvbSIsImtleSI6ImtleTUiLCJleHAiOjE3NTAxNzI0MjMsIm5iZiI6MTc1MDE3MjEyMywicGF0aCI6Ii83NzM3NTEyNy80NTYwNjAxNTMtNjM0YTFkNjAtNDYxZS00N2NkLTg5MzAtNjViYmMzYjJkYWExLnBuZz9YLUFtei1BbGdvcml0aG09QVdTNC1ITUFDLVNIQTI1NiZYLUFtei1DcmVkZW50aWFsPUFLSUFWQ09EWUxTQTUzUFFLNFpBJTJGMjAyNTA2MTclMkZ1cy1lYXN0LTElMkZzMyUyRmF3czRfcmVxdWVzdCZYLUFtei1EYXRlPTIwMjUwNjE3VDE0NTUyM1omWC1BbXotRXhwaXJlcz0zMDAmWC1BbXotU2lnbmF0dXJlPTgyM2I3ZGYwYzE2MDA5MThmMjYzOWRkYzVmZGRkMTZiZGMyZmNlNjQ5ZTIzMjkyYmZmZTFkNGI3NTkwODBkOGUmWC1BbXotU2lnbmVkSGVhZGVycz1ob3N0In0.NB1U9qFtMqJ8Yk-lG58IRGjJv5Iu-XOxCw97ej4Oe-I)

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
