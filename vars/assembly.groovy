/**
 *
 * @param args
 *      <ul>
 *          <li> module:  子模块</li>
 *          <li> descriptor:  assembly-descriptor.xml 文件</li>
 *          <li> docker:  assembly-docker.xml 文件</li>
 *          <li> layers:  layers.xml 文件</li>
 *          <li> groupId: layers.xml 中依赖的groupId</li>
 *          <li> logback:  logback-spring.xml 文件</li>
 *          <li> level: logger level, default is info<li>
 *          <li> package:  logback-spring.xml/logback.xml中自定义日志级别</li>
 *          <li> skywalking: 支持skywalking的tid</li>
 *      </ul>
 */
def call(Map args) {
    def module = env.WORKSPACE
    if (args.module) {
        module += "/${args.module}"
    }
    def assemblyDir = "${module}/src/assembly"
    def sourcesDir = "${module}/src/main/resources"

    new File(assemblyDir).mkdirs()
    new File(sourcesDir).mkdirs()

    // 替换logback-spring.xml
    if (args.logback == 'force' || (args.logback == true && !fileExists(sourcesDir + "/logback-spring.xml"))) {
        def rs = args.skywalking ? '-spring.xml' : '.xml'
        def logback = libraryResource "com/apzda/build/assembly/logback${rs}"
        def level = args.level ?: 'INFO'
        logback = logback.replace('${project.artifactId}', env.server_name)
        if (args.package instanceof Map) {
            def loggers = []

            args.package.each { String p, String v -> loggers << "<logger name=\"${p}\" level=\"${v}\"/>" }

            logback = logback.replace('<logger name="com.apzda" level="${logger.level}"/>', loggers.join("\n"))
        } else if (args.package instanceof List) {
            def loggers = []

            args.package.each { loggers << "<logger name=\"${it}\" level=\"${level}\"/>" }

            logback = logback.replace('<logger name="com.apzda" level="${logger.level}"/>', loggers.join("\n"))
        } else if (args.package) {
            logback = logback.replace('com.apzda', args.package)
        }

        logback = logback.replace('${logger.level}', level)

        writeFile file: sourcesDir + "/logback-spring.xml", text: logback
    }

    if (args.descriptor == 'force' || (args.descriptor == true && !fileExists(assemblyDir + "/assembly-descriptor.xml"))) {
        def content = libraryResource 'com/apzda/build/assembly/assembly-descriptor.xml'

        writeFile file: assemblyDir + "/assembly-descriptor.xml", text: content
    }

    if (args.docker == 'force' || (args.docker == true && !fileExists(assemblyDir + "/assembly-docker.xml"))) {
        def content = libraryResource 'com/apzda/build/assembly/assembly-docker.xml'

        writeFile file: assemblyDir + "/assembly-docker.xml", text: content
    }

    if (args.layers == 'force' || (args.layers == true && !fileExists(assemblyDir + "/layers.xml"))) {
        def content = libraryResource 'com/apzda/build/assembly/layers.xml'

        if (args.groupId instanceof List) {
            def groups = []
            args.groupId.each {
                groups << "<include>${it}:*</include>"
            }
            content = content.replace('<include>com.apzda:*</include>', groups.join("\n"))
        } else if (args.groupId) {
            content = content.replace('com.apzda', args.groupId)
        }

        writeFile file: assemblyDir + "/layers.xml", text: content
    }
}