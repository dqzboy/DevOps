package org.devops
def HttpReq() { 
	sh """
	    curl --location --request POST 'https://oapi.dingtalk.com/robot/send?access_token=<you token>' \
	    --header 'Content-Type: application/json' \
	    --data-raw '{
            "msgtype": "markdown",
            "markdown": {
                "title": "项目构建信息",
                "text": "### 生产发布申请,请【${adminUser}】审批@${approvalDD}\n- 应用名称: ${env.JOB_NAME}\n- 随机验证码: ${randomToken}"
            },
            "at": {
                "isAtAll": false
            }
        }'
	"""
}
