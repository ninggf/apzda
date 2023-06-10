/**
 * 生成Dockerfile。
 *
 * @param args 参数
 *
 * <ul>
 *      <li> - tpl: nginx, openresty, fatjar, layerjar, jar, php </li>
 *      <li> - basedir: type = nginx 或 openresty时指定,默认为""</li>
 *      <li> - module: 当项目为maven项目时指定, 单项目时不需要指定,默认为"."</li>
 *      <li> - jdkImage: jdk镜像名, java必须在路径上，默认使用openjdk:17</li>
 *      <li> - path: Dockerfile等文件存放位置,默认为"."或"${module}/target/docker"</li>
 * </ul>
 */
def call(Map args) {
    println "dockertpl args: ${args}"

    if (!["nginx", "openresty", "fatjar", "layerjar", "jar", "php"].contains(args.tpl)) {
        println("[${args.tpl}] is not supported!")
        sh(returnStdout: false, script: "exit(1)")
    }

    if (!env.BUILD_DATE) {
        env.BUILD_DATE = new Date(currentBuild.startTimeInMillis).format('yyyyMMdd', TimeZone.getTimeZone('GMT+08:00'))
    }

    env.WORKSPACE = env.WORKSPACE ?: '.'
    env.MVN_MODULE = args.module ?: false
    env.jdkImage = args.jdkImage ?: 'openjdk:17'

    if (!args.containsKey("path")) {
        if (env.MVN_MODULE) {
            args.path = "${env.WORKSPACE}/${env.MVN_MODULE}/target/docker"
        } else {
            args.path = "${env.WORKSPACE}"
        }
    } else {
        if (env.MVN_MODULE) {
            args.path = "${env.WORKSPACE}/${env.MVN_MODULE}/${args.path}"
        } else {
            args.path = "${env.WORKSPACE}/${args.path}"
        }
    }

    if (args.tpl =~ /^(nginx|openresty)$/) {
        // 加载nginx.conf for nginx and openresty
        String nginxConf = libraryResource "com/apzda/build/tpl/${args.tpl}/nginx.conf"
        String basedir = args.basedir ?: ''
        nginxConf = nginxConf.replace('base_dir/', "$basedir/")

        writeFile file: "${args.path}/nginx.conf", text: nginxConf
        writeFile file: "${args.path}/Dockerfile", text: libraryResource("com/apzda/build/tpl/${args.tpl}/Dockerfile")
    } else {
        // 写脚本
        def bins = ['docker-entrypoint', 'env']
        for (file in bins) {
            writeFile file: "${args.path}/bin/${file}.sh", text: libraryResource("com/apzda/build/tpl/bin/${file}.sh")
        }
        // 写Dockerfile
        String dockerFileContent = libraryResource "com/apzda/build/tpl/${args.tpl}/Dockerfile"
        dockerFileContent = dockerFileContent.replaceAll('@jdkImage@', env.jdkImage)
        if (args.config && args.config instanceof String) {
            dockerFileContent = dockerFileContent.replaceAll('#@copyconfig@', "COPY ${args.config}/ /opt/app/config/")
        }
        writeFile file: "${args.path}/Dockerfile", text: dockerFileContent
    }
}