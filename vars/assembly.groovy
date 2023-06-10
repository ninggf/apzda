/**
 *
 * @param args
 *      <ul>
 *          <li> module:  maven的子模块,不指定时默认为当前模块</li>
 *          <li> assembly:  assembly-descriptor.xml 文件</li>
 *          <li> docker:  assembly-docker.xml 文件</li>
 *          <li> layerjar:  layers.xml 文件</li>
 *          <li> logback:  logback-spring.xml 文件</li>
 *          <li> level: logger level, default is info<li>
 *          <li> packages:  logback-spring.xml/logback.xml中自定义日志级别</li>
 *          <li> skywalking: 启用skywalking Encoder， 谨慎启动 </li>
 *      </ul>
 */
def call(Map args) {
    def module = env.WORKSPACE ?: '.'
    if (args.module) {
        module += "/${args.module}"
    }
    def assemblyDir = "${module}/assembly"
    def sourcesDir = "${module}/src/main/resources"

    new File(assemblyDir).mkdirs()
    new File(sourcesDir).mkdirs()

    // 替换logback-spring.xml
    if (args.logback == 'force' || (args.logback == true && !fileExists(sourcesDir + "/logback-spring.xml"))) {
        def rs = args.skywalking ? '-spring.xml' : '.xml'
        def logback = libraryResource "com/apzda/build/assembly/logback${rs}"
        def level = args.level ?: 'INFO'
        logback = logback.replace('${project.artifactId}', env.server_name)
        if (args.packages instanceof Map) {
            def loggers = []

            args.packages.each { String p, String v -> loggers << "<logger name=\"${p}\" level=\"${v}\"/>" }

            logback = logback.replace('<logger name="com.apzda" level="${logger.level}"/>', loggers.join("\n"))
        } else if (args.packages instanceof List) {
            def loggers = []

            args.packages.each { loggers << "<logger name=\"${it}\" level=\"${level}\"/>" }

            logback = logback.replace('<logger name="com.apzda" level="${logger.level}"/>', loggers.join("\n"))
        } else if (args.packages instanceof String) {
            logback = logback.replace('com.apzda', args.packages)
        }

        logback = logback.replace('${logger.level}', level)

        writeFile file: sourcesDir + "/logback-spring.xml", text: logback
    }

    if (args.assembly == 'force' || (args.assembly == true && !fileExists(assemblyDir + "/assembly-descriptor.xml"))) {
        def content = libraryResource 'com/apzda/build/assembly/assembly-descriptor.xml'

        writeFile file: assemblyDir + "/assembly-descriptor.xml", text: content
    }

    if (args.docker == 'force' || (args.docker == true && !fileExists(assemblyDir + "/assembly-docker.xml"))) {
        def content = libraryResource 'com/apzda/build/assembly/assembly-docker.xml'

        writeFile file: assemblyDir + "/assembly-docker.xml", text: content
    }

    if (args.layerjar == 'force' || (args.layerjar == true && !fileExists(assemblyDir + "/layers.xml"))) {
        def content = libraryResource 'com/apzda/build/assembly/layers.xml'

        writeFile file: assemblyDir + "/layers.xml", text: content
    }
}