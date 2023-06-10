FROM @jdkImage@ as builder

WORKDIR /opt/app

ARG SERVICE_JAR
ARG SERVICE_VER
ARG SERVICE_NAME

COPY bin/  /opt/app/bin/
COPY ${SERVICE_JAR} /opt/app/application.jar

RUN java -Djarmode=layertools -jar application.jar extract; \
    chmod -R +x /opt/app/bin/

FROM @jdkImage@

ARG SERVICE_NAME
ARG SERVICE_VER

ENV JVM_OPTS="-Xms512M -Xmx512M"\
    APP_OPTS="" \
    SERVICE_NAME="${SERVICE_NAME}"\
    SERVICE_VER="${SERVICE_VER}"

WORKDIR /opt/app/

COPY --from=builder /opt/app/bin/ ./bin/
COPY --from=builder /opt/app/dependencies/ ./${SERVICE_NAME}/
COPY --from=builder /opt/app/company-dependencies/ ./${SERVICE_NAME}/
COPY --from=builder /opt/app/spring-boot-loader/ ./${SERVICE_NAME}/
COPY --from=builder /opt/app/snapshot-dependencies/ ./${SERVICE_NAME}/
COPY --from=builder /opt/app/application/ ./${SERVICE_NAME}/
#@copyconfig@

RUN [[ ! -e /opt/app/config ]] && mkdir /opt/app/config; \
    mkdir /opt/app/{dump,logs}; \
    chown -R 999:999 /opt/app

STOPSIGNAL SIGTERM

# application
EXPOSE 8080
# actuator
EXPOSE 8081

ENTRYPOINT ["/opt/app/bin/docker-entrypoint.sh"]

CMD ["-mode","launcher"]
