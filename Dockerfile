FROM gradle:jdk15 as builder
COPY --chown=gradle:gradle . /app
WORKDIR /app
RUN gradle clean build buildFatJar

FROM openjdk:15
EXPOSE 8080
COPY --from=builder /app/build/libs/*.jar /app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
