FROM java:8

ADD . /GameServer

WORKDIR /GameServer

#RUN apt-get update
#RUN apt-get -qq install maven
#RUN mvn package

CMD ["java", "-Djava.util.logging.SimpleFormatter.format=%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s %2$s %5$s%6$s%n", "-jar", "target/GameServer-1.0.0.jar"]

EXPOSE 8080
