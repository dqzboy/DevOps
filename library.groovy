package org.devops
def getChangeString() {
    def changeString = ""
    def MAX_MSG_LEN = 20
    def changeLogSets = currentBuild.changeSets
for (int i = 0; i < changeLogSets.size(); i++) {
        def entries = changeLogSets[i].items
for (int j = 0; j < entries.length; j++) {
            def entry = entries[j]
            truncatedMsg = entry.msg.take(MAX_MSG_LEN)
            commitTime = new Date(entry.timestamp).format("yyyy-MM-dd HH:mm:ss")
            changeString += " - ${truncatedMsg} [${entry.author} ${commitTime}]\n"
        }
    }
if (!changeString) {
        changeString = " - No new changes"
    }
return (changeString)
}


def HttpReq(Status,CatchInfo=' '){
    wrap([$class: 'BuildUser']){
        def DingTalkHook = "钉钉机器人webhook地址"
        def ChangeLog = getChangeString()
        def ReqBody = """{
            "msgtype": "markdown",
            "markdown": {
                "title":"申请发布生产",
                "text": "生产发布申请,请【${adminUser}】审批@${approvalDD} \n>- 应用名称: **${env.JOB_NAME}**\n- 构建信息: ${Status}${CatchInfo}\n- 构建编号: **${env.BUILD_NUMBER}**\n- 申请人员: **${env.BUILD_USER}**\n- 申请说明: **${approveInfo}**\n- 持续时间: **${currentBuild.durationString}**\n- 随机验证码: **${randomToken}**\n- 审批链接: [请点击该链接登录后审批](${env.BUILD_URL}input) \n"
            },
            "at": {
                "atMobiles": [
                    "${approvalDD}"
                ],
                "isAtAll": false
            }
        }"""
        httpRequest acceptType: 'APPLICATION_JSON_UTF8', 
                consoleLogResponseBody: false, 
                contentType: 'APPLICATION_JSON_UTF8', 
                httpMode: 'POST', 
                ignoreSslErrors: true, 
                requestBody: ReqBody, 
                responseHandle: 'NONE', 
                url: "${DingTalkHook}",
                quiet: true
    }
}
