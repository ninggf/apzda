#!groovy

/**
 *  拉取代码。
 * @param args 参数如下:
 *
 *  <ul>
 *      <li> - branch: 分支 </li>
 *      <li> - credentialsId: 凭证ID</li>
 *      <li> - url: 仓库地址</li>
 *      <li> - gitTool: git工具</li>
 *  </ul>
 */
def call(Map<String, String> args) {
    println "获取代码: ${args}"

    env.git_project_branch = args.branch
    args.gitTool = args.gitTool ?: 'Default'

    checkout([$class: 'GitSCM', branches: [[name: args.branch]], gitTool: args.gitTool, doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[credentialsId: "${args.credentialsId}", url: "${args.url}"]]])
    def last_start_date = getLastSuccessTime();

    def git_log = 'git log --pretty=format:"> - %cn@%ad - %s" --date=format:"%Y-%m-%d %H:%M:%S" --since="' + last_start_date + '" '
    env.commitChangeset = sh(returnStdout: true, script: "${git_log}").trim()
    echo "changelog: '${env.commitChangeset}'"
    if (env.commitChangeset == '') {
        git_log = 'git log --pretty=format:"> - %cn@%ad - %s" --date=format:"%Y-%m-%d %H:%M:%S" -n 3'
        env.commitChangeset = sh(returnStdout: true, script: "${git_log}").trim()
    }
    env.gitCommit = sh(returnStdout: true, script: "git rev-parse HEAD").trim()
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
