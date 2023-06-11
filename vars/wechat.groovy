/**
 *
 * @param args 参数
 *     <ul>
 *          <li> token: Token </li>
 *          <li> type:  消息类型 </li>
 *          <li> message: 消息 </li>
 *     </ul>
 * @return
 */
def call(Map args) {
    def res = manager.getResult()
    def icon = '○'
    def res_color = 'info'

    if (res == 'SUCCESS') {
        res = '成功'
        icon = '✅'
    } else if (res == 'FAILURE') {
        res = '失败'
        icon = '❌'
        res_color = 'red'
    } else if (res == 'ABORTED') {
        res = '取消'
        res_color = 'gray'
    }

    wrap([$class: 'BuildUser']) {
        env.total_time = "${currentBuild.durationString}".split("and counting")[0]

        if (!env.gitCommit) {
            def last_start_date = getLastSuccessTime();

            def git_log = 'git log --pretty=format:"> - %cn@%ad - %s" --date=format:"%Y-%m-%d %H:%M:%S" --since="' + last_start_date + '" '
            env.commitChangeset = sh(returnStdout: true, script: "${git_log}").trim()

            env.gitCommit = sh(returnStdout: true, script: "git rev-parse HEAD").trim()
        }

        def start_time = new Date(currentBuild.startTimeInMillis).format('yyyy-MM-dd HH:mm:ss', TimeZone.getTimeZone('GMT+08:00'))
        //开始构建时间
        def server_name = env.SERVICE_NAME ?: currentBuild.fullProjectName
        def commitChangeset = env.commitChangeset ?: ''
        def git_project_branch = env.git_project_branch ?: ''
        def server_env = env.SERVICE_ENV ?: ''
        def commit = env.gitCommit ?: ''

        def head = "<font color=\\\"${res_color}\\\">${icon}</font> **${JOB_NAME}${BUILD_DISPLAY_NAME}**(${env.description})@**${server_env}**[构建${res}](${BUILD_URL}):"
        def change_log = "> 变更记录：\n${commitChangeset}"
        def gcommit = "> Commit: <font color=\\\"comment\\\">${commit}</font>"
        def publish_user = "> 部署人：<font color=\\\"comment\\\">${BUILD_USER}</font>"
        def total_time = "> 持续时间：<font color=\\\"comment\\\">${total_time}</font>"
        def publish_branch = "> 部署分支：<font color=\\\"comment\\\">${git_project_branch}</font>"
        def publish_server = "> 部署服务：<font color=\\\"comment\\\">${server_name}</font>"
        def publish_time = "> 部署时间：<font color=\\\"comment\\\">${start_time}</font>"
        def image = "> 镜像标签: <font color=\\\"comment\\\">${env.IMAGE_WITH_TAG}</font>"

        def msg = "\"${head}" + "\n" + "${publish_user}" + "\n" + "${publish_server}\n${image}" + "\n" + "${total_time}" + "\n" + "${publish_branch}" + "\n" + "${publish_time}" + "\n" + "${gcommit}\n${change_log}\""
        def body = "{ \"msgtype\": \"markdown\", \"markdown\": { \"content\": ${msg} } }"
        def type = args.type ?: 'APPLICATION_JSON_UTF8'
        def message = args.message ?: body

        if (args.token) {
            httpRequest contentType: type, httpMode: 'POST', requestBody: "${message}", responseHandle: 'NONE', url: "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=${args.token}", wrapAsMultipart: false
        } else {
            echo "${type} => \n${message}"
        }
    }
}

/**
 * 获取上次构建成功时间
 * @return 上次构建成功时间
 */
def getLastSuccessTime() {
    def res = new Date().format('yyyy-MM-dd HH:mm:ss', TimeZone.getTimeZone('GMT+08:00'))
    def build = currentBuild.previousBuild

    while (build != null) {
        if (build.result == "SUCCESS") {
            res = currentBuild.rawBuild.getPreviousBuild().getTime().format('yyyy-MM-dd HH:mm:ss', TimeZone.getTimeZone('GMT+08:00'))
            break
        }
        build = build.previousBuild
    }

    return res
}