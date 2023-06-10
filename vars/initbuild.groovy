/**
 * 初始化构建环境.
 * @param args 参数
 *     <ul>
 *         <li> service_name: 服务名称 </li>
 *         <li> env: 服务运行环境 </li>
 *         <li> description: 描述</li>
 *     </ul>
 * @author 宁广丰 <ninggf@yueworld.cn>
 * @version 1.0.0
 * @since 1.0.0
 */
def call(Map args) {
    env.BUILD_DATE = new Date(currentBuild.startTimeInMillis).format('yyyyMMdd', TimeZone.getTimeZone('GMT+08:00')) //构建日期

    if (args.service_name) {
        env.server_name = args.service_name
        env.service_name = args.service_name
    } else {
        env.service_name = currentBuild.projectName;
        env.server_name = currentBuild.projectName;
    }
    def image_with_tag = args.image ?: currentBuild.projectName

    def tags = []
    if (args.tag_with_date) {
        tags << env.BUILD_DATE
    }

    if (args.tag_with_buildid != false) {
        tags << env.BUILD_ID
    }

    env.IMAGE_WITH_TAG = image_with_tag + ':' + tags.join('-')
    env.service_env = args.env ?: 'DEV'
    env.description = args.description ?: currentBuild.description
    env.gitCommit = ''
    env.commitChangeset = ''
}
