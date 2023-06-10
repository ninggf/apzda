FROM reg.yueworld.cn/library/openjdk:8-jre-alpine

LABEL maintainer="ninggf@yueworld.cn"

ARG VERSION="1.0.0-SNAPSHOT"
ARG SERVICE_NAME
ARG SERVER_PORT=8080
ARG LIBS_DIR="${SERVICE_NAME}/libs"

ENV SERVICE_NAME=$SERVICE_NAME RUN_MODE="docker" SERVER_PORT=${SERVER_PORT:-8080} PROMETHEUS_PORT="5${SERVER_PORT:-8080}"

WORKDIR /opt/

ADD "${SERVICE_NAME}-${VERSION}.jar" /opt/${SERVICE_NAME}/

COPY bin         /opt/bin
COPY config      /opt/config/
COPY config      /opt/${SERVICE_NAME}/config
COPY ${LIBS_DIR} /opt/${SERVICE_NAME}/libs

RUN chmod +x /opt/bin/*.sh \
 && chown -R 999:999 /opt

VOLUME "/opt/${SERVICE_NAME}/config"

EXPOSE $SERVER_PORT
EXPOSE "5${SERVER_PORT}"

STOPSIGNAL SIGTERM

ENTRYPOINT ["/opt/bin/docker-entrypoint.sh"]

CMD ["/opt/bin/jarun.sh","start"]
