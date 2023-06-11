@Library('commons') _

pipeline {
    agent none

    tools {
        jdk "jdk"
        maven "3.6.2"
        nodejs "12.8.3"
        git "Default"
    }

    options {
        //设置在项目打印日志时带上对应时间
        timestamps()
        // 设置流水线运行超过n分钟，Jenkins将中止流水线
        timeout time: 60, unit: 'MINUTES'
        // 禁止并行构建
        disableConcurrentBuilds()
        // 表示保留100次构建历史
        buildDiscarder(logRotator(numToKeepStr: '100', artifactNumToKeepStr: '0'))
    }

    stages {
        stage("Assembly") {
            steps {
                assembly layerjar: true, docker: true, assembly: true
            }
        }
        stage('JAVA - 镜像模板') {
            steps {
                dockertpl tpl: 'layerjar', module: 'apzda-demo', jdkImage: 'openjdk:17'
            }
        }

        stage('NGINX - 镜像模板') {
            steps {
                dockertpl tpl: 'nginx', basedir: '/'
            }
        }
    }
}