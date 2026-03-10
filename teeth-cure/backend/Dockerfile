# =======================================
# 1. 빌드 단계 (Builder Stage)
# =======================================
FROM bellsoft/liberica-openjdk-alpine:21 AS builder

WORKDIR /build

# Gradle 캐싱을 위해 설정 파일 먼저 복사
COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .

# 실행 권한 부여
RUN chmod +x ./gradlew

# 종속성 다운로드 (소스 코드가 바뀌어도 종속성은 캐시되어 빌드 속도 향상)
RUN ./gradlew dependencies --no-daemon

# 소스 코드 복사 및 빌드
COPY src src
RUN ./gradlew clean build -x test --no-daemon

# =======================================
# 2. 실행 단계 (Runtime Stage)
# =======================================
FROM bellsoft/liberica-openjdk-alpine:21

WORKDIR /app

# 빌드 단계에서 생성된 최종 JAR 파일만 복사
COPY --from=builder /build/build/libs/*-SNAPSHOT.jar app.jar

# 컨테이너 시간대를 한국 시간(KST)으로 설정 (DB 저장 시 시간 오차 방지)
ENV TZ=Asia/Seoul
RUN apk add --no-cache tzdata && cp /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

EXPOSE 8080

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]