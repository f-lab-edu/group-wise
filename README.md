# Group-wise (공동구매 서비스)
공동구매 서비스 연습 프로젝트입니다.
spring boot 와 jpa, querydsl 을 사용하여 구현합니다.

## 요구사항 정리 (작업중)
- 상품
  - 상품 필수 구성요소
    - 상품명
    - 가격
    - 재고
    - 상품 설명
    - 상품 이미지
  - 고민중인 것
    - 구매자가 선택할 수 있는 상품의 세부종류별 재고 수량 구성
- 공동구매
  - 공동구매 필수 구성요소
    - 상품
    - 구매 최소 인원 (참여자)
    - 공동구매 시작 시간과 종료 시간
    - 가격 할인 혜택
  - 공동구매 진행 상태
    - PENDING: 시작 전 대기
    - ONGOING: 진행 중 (최소 인원 미달)
    - FULFILLED: 진행 중 (최소 인원 달성)
    - CLOSED_SUCCESS: 마감 (목표 달성)
    - CLOSED_FAILURE: 마감 (목표 미달)
    - CANCELED: 취소됨
  - 공동구매 비즈니스 규칙
    - 종료 시간 전에 최소 인원을 충족하지 못하면 구매 실패 (CLOSED_FAILURE)
    - 상품의 재고보다 많은 수량을 구매할 수 없음

## Features (주요 기능)
앞으로 구현해나갈 주요기능입니다.
- 회원 관리
  - 회원 가입/로그인
  - 주소지 관리
- 상품 관리
  - 상품 CRUD
  - 재고 관리
- 공동구매 관리
  - 공구 시작/종료 시간 설정
  - 최소 인원 설정
  - 현재 참여자수 표시, 남은시간 표시
  - 공구 게시물에 대한 개인별 찜 설정
  - 알림 서비스 (시작, 마감임박, 성공/실패 알림, 배송상태 알림)

## Tech Stack (기술 스택)
- Java 17
- Spring Boot 3.4.1
- Spring Security
- Spring Data JPA
- MySQL 9.1.0
- Gradle
- Thymeleaf

## Environment Variables
프로젝트 실행을 위해 다음 환경변수가 필요합니다:
- DB_HOST: 데이터베이스 호스트 주소
- DB_NAME_PROD: 운영 데이터베이스 이름
- DB_NAME_TEST: 테스트 데이터베이스 이름
- DB_USERNAME: 데이터베이스 사용자 이름
- DB_PASSWORD: 데이터베이스 비밀번호