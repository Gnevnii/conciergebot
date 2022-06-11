FROM adoptopenjdk/openjdk16:ubi
ARG JAR_FILE=target/*.jar
ENV BOT_NAME=salarievo82_concierge_bot
ENV BOT_TOKEN=5324984587:AAFCQfCOGHC-EAJtbO_zGxNz0NsNRCQYLYc
COPY ${JAR_FILE} conciergebot.jar
ENTRYPOINT ["java", "-Dbot.username=${BOT_NAME}", "-Dbot.token=${BOT_TOKEN}", "-jar", "/conciergebot.jar"]