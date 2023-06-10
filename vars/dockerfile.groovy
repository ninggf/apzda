#!groovy

/**
 * 生成Dockerfile。
 *
 * @param args 参数
 *
 * <ul>
 *      <li> - type: static, fatjar, layerjar </li>
 *      <li> - basedir: type = static时指定</li>
 *      <li> - jar: jar file prefix</li>
 *      <li> - path: 相对于workspace的路径. 默认是工作区根路径.</li>
 *      <li> - launcher: [true|''] 默认为true</li>
 *      <li> - libs_dir: loader path</li>
 *      <li> - profiles: active profiles</li>
 *      <li> - service_name: 服务名</li>
 *      <li> - service_path: 服务路径, 如果与service_name不致时需要指定</li>
 *      <li> - server_port: 端口</li>
 *      <li> - args: 运行时参数</li>
 * </ul>
 */
def call(Map args) {
    println "dockerfile args: ${args}"
    env.WORKSPACE = env.WORKSPACE ?: '.'
    env.server_name = args.service_name ?: (env.service_name ?: currentBuild.fullProjectName)

    if (!env.BUILD_DATE) {
        env.BUILD_DATE = new Date(currentBuild.startTimeInMillis).format('yyyyMMdd', TimeZone.getTimeZone('GMT+08:00'))
        //构建日期
    }

    if (!args.containsKey("path")) {
        env.DOCKER_CONTEXT_PATH = '.'
        args.path = "${env.WORKSPACE}"
    } else {
        env.DOCKER_CONTEXT_PATH = args.path
        args.path = "${env.WORKSPACE}/${args.path}"
    }

    args.server_port = args.server_port ?: 8080
    args.type = args.type ?: 'static'
    if (args.type =~ /^(static|openresty)$/) {
        // 加载nginx.conf for nginx and openresty
        String nginxConf = libraryResource "com/apzda/build/docker/${args.type}/nginx.conf"
        String basedir = args.basedir ?: ''
        nginxConf = nginxConf.replace('base_dir/', "$basedir/")

        writeFile file: "${args.path}/nginx.conf", text: nginxConf
    } else {

        if (args.type == 'thinjar') {
            new File("${args.path}/libs").mkdirs();
        }

        new File("${args.path}/config").mkdirs();
        new File("${args.path}/bin/lib").mkdirs();

        // layerjar 配置文件
        def config = """\
[global]
mode = docker
user = 999

[${args.service_name}]
port     = ${args.server_port}"""

        if (args.service_path) {
            config += "\npath     = ${args.service_path}"
        } else {
            config += "\npath     = ${env.server_name}"
        }

        if (args.jar) {
            config += "\njar      = ${args.jar}"
        } else {
            config += "\njar      = ${env.server_name}"
        }

        if (args.launcher) {
            config += "\nlauncher = ${args.launcher}"
        }

        if (args.profiles) {
            config += "\nprofiles = ${args.profiles}"
        }

        if (args.libs_dir) {
            config += "\nlibs_dir = ${args.libs_dir}"
        }

        if (args.args) {
            config += "\nargs     = ${args.args}"
        }

        config += "\n# end of service configuration\n"

        // 写配置文件
        writeFile file: "${args.path}/config/services.ini", text: config

        // 写脚本
        def bins = ['docker-entrypoint', 'jarun', 'rcall', 'lib/common', 'lib/functions', 'lib/ini-file-parser', 'lib/run_jar', 'lib/stop_jar']

        for (file in bins) {
            writeFile file: "${args.path}/bin/${file}.sh", text: libraryResource("com/apzda/build/bin/${file}.sh")
        }
    }

    // 写Dockerfile
    writeFile file: "${args.path}/Dockerfile", text: libraryResource("com/apzda/build/docker/${args.type}/Dockerfile")
}