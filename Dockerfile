FROM arm64v8/openjdk:11.0.8-jdk-buster
WORKDIR /app
COPY ./target/assetz*.jar ./assetz.jar
CMD ["java", "-jar","assetz.jar"]

