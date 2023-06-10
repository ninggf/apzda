## Adjust memory settings if necessary
#JAVA_OPTS="-Xms256m -Xmx1G -Xss256k -XX:MetaspaceSize=128m -XX:MaxMetaspaceSize=384m -XX:NewSize=4096m -XX:MaxNewSize=4096m -XX:SurvivorRatio=8"
JAVA_OPTS="${JAVA_OPTS:--Xms256m -Xmx256m}"

## Adjust gc options is necessary
JVM_GC_OPTS="${JVM_GC_OPTS} -XX:+DisableExplicitGC -Xlog:gc:${WORKSPACE}/logs/gc.log"

## Adjust oom dump options if necessary
JVM_OOM_OPTS="${JVM_OOM_OPTS} -XX:+HeapDumpOnOutOfMemoryError -XX:-OmitStackTraceInFastThrow -XX:HeapDumpPath=${WORKSPACE}/dump/"
