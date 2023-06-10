# build-tools

## 代码检查

本项目仅集成了checkstyle规则和pmd规则：

1. checkstyle 规则:
    1. checkstyle/google_checks.xml
    2. checkstyle/ys_checks.xml
2. pmd 规则:
    1. pmd/pmd_example.xml
    2. pmd/ys_ruleset.xml

### 使用方法

参见[附件](scaffold.zip).

## Jenkins Library

### 需要的插件

1. Groovy PostBuild
2. build-user-vars-plugin
3. HTTP Request

### 一般使用

```groovy
pipeline {
    agent any
    stages {
        stage('init') {
            steps {
                initbuild env: 'UAT', service_name: 'service-name' // 初始化一些变量
            }
        }

        stage('example') {
            steps {
                // your steps here
            }
        }
    }

    post {
        always {
            wechat token: '企业微信群机器人token'
        }
    }
}
```

### vars 详解

1. `pullcode`: 拉取代码
    - `branch`: 分支
    - `url`： 仓库地址
    - `credentialsId`: git 用户凭证
2. `initbuild`: 初始化构建
    - `service_name`: 服务名
3. `wechat`: 发送企业微信通知
    - `token`: Token， 为空或`false`时将不会发送通知
4. `dockerfile`: 生成Dockerfile
    - `type`: static, fatjar, layerjar
    - `basedir`: type = static时指定，默认为'/'
    - `path`: 相对于workspace的路径. 默认是工作区根路径
    - `jar`: jar file 前缀
    - `launcher`: `[true|'']` 默认为true
    - `libs_dir`: loader path
    - `profiles`: active profiles
    - `service_name`: 服务名,打java服务镜像时必须
    - `service_path`: 服务的路径，如果与service_name不致时需要指定
    - `server_port`: 端口, 默认`8080`
    - `args`: 参数
5. `assembly`:
    - `module`: 模块
    - `descriptor`: [true|'force'] assembly-descriptor.xml
    - `docker`: [true|'force'] assembly-docker.xml
    - `layers`: [true|'force] layers.xml
    - `groupId`: layers.xml中公司依赖groupId
    - `logback`: [true|'force] logback-spring.xml
    - `level`: logger level, 默认为 info
    - `package`: logback中自定义日志级别
    - `skywalking`: 使用支持skywalking的logback配置文件
