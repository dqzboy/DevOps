package org.devops
def HttpReq(Status,CatchInfo=' ') {    
	sh """
		curl --location --request POST 'https://oapi.dingtalk.com/robot/send?access_token=da3ac2c663dd9af1c0c513b82963f0050be83868fdf04d333962630f5e081cd6' \
		--header 'Content-Type: application/json' \
		--data '{
            "msgtype": "markdown",
            "markdown": {
                "title": "项目构建信息",
                "text": "### 生产发布申请,请【${adminUser}】审批@${approvalDD}\n- 应用名称: ${env.JOB_NAME}\n- 构建信息: ${Status}${CatchInfo}\n- 随机验证码: ${randomToken}"
            },
            "at": {
                "isAtAll": false
            }
        }'

	"""
}
