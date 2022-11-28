package org.devops
def Ding() {
	sh """
		curl --location --request POST 'hppts://oapi.dingtalk.com/robot/send?access_token=<webhook>' \
		--header 'Content-Type: application/json' \
		--data-raw '{
            "msgtype": "markdown",
            "markdown": {
                "title": "项目构建信息",
                "text": "## 消息1 \n- 消息2 \n- 消息3 \n- 消息4"
            },
            "at": {
                "isAtAll": false
            }
        }'

	"""
}
