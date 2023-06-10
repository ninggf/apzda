FROM  reg.yueworld.cn/library/openjdk:8-jre-alpine as layer

LABEL maintainer="ninggf@yueworld.cn"

WORKDIR /opt/app

ARG VERSION="1.0.0-SNAPSHOT"
ARG SERVICE_NAME

COPY bin /opt/bin

ADD "${SERVICE_NAME}-${VERSION}.jar" /opt/app

RUN java -Djarmode=layertools -jar "${SERVICE_NAME}-${VERSION}".jar extract

FROM reg.yueworld.cn/library/openjdk:8-jre-alpine

ARG SERVICE_NAME
ARG SERVER_PORT=8080

ENV SERVICE_NAME=$SERVICE_NAME RUN_MODE="docker" SERVER_PORT=${SERVER_PORT:-8080} PROMETHEUS_PORT="5${SERVER_PORT:-8080}"

WORKDIR /opt/

COPY config /opt/config/
COPY config /opt/${SERVICE_NAME}/config/

COPY --from=layer /opt/bin ./bin/
COPY --from=layer /opt/app/dependencies/ ./${SERVICE_NAME}/app/
COPY --from=layer /opt/app/company-dependencies/ ./${SERVICE_NAME}/app/
COPY --from=layer /opt/app/spring-boot-loader/ ./${SERVICE_NAME}/app/
COPY --from=layer /opt/app/snapshot-dependencies/ ./${SERVICE_NAME}/app/
COPY --from=layer /opt/app/application/ ./${SERVICE_NAME}/app/

RUN chmod +x /opt/bin/*.sh \
 && chown -R 999:999 /opt

VOLUME "/opt/${SERVICE_NAME}/config"

EXPOSE $SERVER_PORT
EXPOSE "5${SERVER_PORT}"

STOPSIGNAL SIGTERM

ENTRYPOINT ["/opt/bin/docker-entrypoint.sh"]

CMD ["/opt/bin/jarun.sh","start"]
