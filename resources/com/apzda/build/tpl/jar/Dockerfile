FROM @jdkImage@

ARG SERVICE_JAR
ARG SERVICE_VER
ARG SERVICE_NAME

ENV JAVA_OPTS="-Xms512M -Xmx512M"\
    APP_OPTS="" \
    SERVICE_NAME="${SERVICE_NAME}"\
    SERVICE_VER="${SERVICE_VER}"\
    SERVER_PORT=8080

WORKDIR /opt/app/

COPY bin/  /opt/app/bin/
COPY libs/ /opt/app/libs/
#@copyconfig@
COPY ${SERVICE_JAR} /opt/app/${SERVICE_NAME}/${SERVICE_NAME}.jar

RUN [[ ! -e /opt/app/config ]] && mkdir /opt/app/config; \
    mkdir /opt/app/{dump,logs}; \
    chmod -R +x /opt/app/bin/; \
    chown -R 999:999 /opt/app

STOPSIGNAL SIGTERM

# application
EXPOSE 8080
# actuator
EXPOSE 8081

ENTRYPOINT ["/opt/app/bin/docker-entrypoint.sh"]

CMD ["-mode","jar","/opt/app/libs/"]
