FROM gradle:7.5-jdk18 AS build
WORKDIR /source
# Copying all build related files
COPY ../gradle/wrapper/gradle-wrapper.properties gradle/wrapper/gradle-wrapper.properties
COPY ../gradle/wrapper/gradle-wrapper.jar gradle/wrapper/gradle-wrapper.jar
COPY ../gradlew gradlew
COPY ../settings.gradle.kts settings.gradle.kts
COPY ../build.gradle.kts build.gradle.kts
# Only downloading dependencies
# Eat the expected build failure since no source code has been copied yet
RUN ./gradlew clean build --no-daemon > /dev/null 2>&1 || true
#Copying code to run the build ful
COPY ../src src/
RUN ./gradlew shadowJar --no-deamon

FROM eclipse-temurin:18-alpine  AS unzip
WORKDIR /source
COPY --from=build source/build/libs/Level2Bot-*-all.jar appliction.jar
# Extracting jar
RUN jar -xf application.jar

FROM eclipse-temurin:18-alpine AS extractDependencies
WORKDIR /extractDependencies
#Copying everything from unziped stageing
COPY --from=unzip source/ ./

FROM eclipse-temurin:18-alpine
WORKDIR /
# Copy all dependencies
COPY --from=extractDependencies extractDependencies/ ./
# Copy log4j configurations
COPY --from=unzip source/Log4j* ./
COPY --from=unzip source/log4j2.xml config/log4j2.xml
# Copy Meta Inf
COPY --from=unzip source/META-INF/ META-INF/
# Copy Resources
COPY --from=unzip source/locale* ./
COPY --from=unzip source/database database
COPY --from=unzip source/Thankswords.json Thankswords.json
COPY --from=unzip source/version version
# Copy classes of repbot
COPY --from=unzip source/de/chojo/repbot de/chojo/repbot
# Add start script
ADD docker/start.sh start.sh
RUN chmod +x start.sh
CMD ["sh", "start.sh"]