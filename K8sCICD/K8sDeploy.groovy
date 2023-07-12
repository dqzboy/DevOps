#!groovy
@Library('jenkinslib') _  
//调用方法库文件
def approval = new org.devops.library()

def skipRemainingStages = false   //当前节点是否跳过
def input_message   //提示语
def randomToken    //发布秘钥
def skipadminUser = false


pipeline {
    agent any
    options {
        timestamps() // 在日志中打印时间
        skipDefaultCheckout() // 删除隐式的checkout scm 语句
        timeout(time:1, unit:'HOURS') // 流水线超时设置为1H
    }
    environment {
        //生成随机数0-10000
        max = 100000
        randomToken = "${Math.abs(new Random().nextInt(max+1))}"
        approve = "${approve}"  //jenkins参数化构建定义审批人员名单
    }

        stage("发送审批通知"){
            when {
                expression { env.approve != 'NO' }
            }

            steps{
                wrap([$class: 'BuildUser']) {
                script {
                    //获取当前登录用户账户、姓名
                    Applier_id = "${env.BUILD_USER_ID}"
                    Applier_name = "${env.BUILD_USER}"
                    }
                }
                script{
                    //判断审批人
                    if ("$adminUser" != ""){
                        adminUser = "$adminUser"
                        //如果审批人为自己，则退出任务
                        if (Applier_id == adminUser){
                            error '审批人不能为本人，任务已终止'
                        } 
                    } else{
                        error '审批人不能为空，任务已终止'
                    }
                    if ("$adminUser" == "dingqz") {
                        env.approvalDD = "xxx"
                        input_message = "$Applier_name 申请发布生产"
                        approval.HttpReq("生产发布申请")
                    } else if ("$adminUser" == "xxx") {
                        env.approvalDD = "xxxx"   //定义变量，赋值对应审批人手机号，实现钉钉消息推送@指定审批人员
                        input_message = "$Applier_name 申请发布生产"
                        approval.HttpReq("生产发布申请")
                    } else {
                        error '审批人信息获取失败，任务已终止'
                    }
                }
            }
        }
   
        stage("等待审批"){
            when {
                expression { env.approve != 'NO' }
            }
            steps{
                script{
                    def isAbort  = false   //取消按钮
                    timeout(time:1, unit:'HOURS'){  //等待审批人审批，并通过timeout设置任务过期时间，防止任务永远挂起
                        try {
                           def token = input(
                               id: 'inputap', message: "$input_message", ok:"同意", submitter:"$adminUser", parameters: [
                               [$class: 'StringParameterDefinition',defaultValue: "", name: 'token',description: '请输入发布的秘钥' ]
                               ])
                            if ( "${token}" == env.randomToken) {
                            } else {
                               error '秘钥错误，任务已终止'
                            }
                        }catch(e) { // input false
                           throw e
                       }
                   }
                }
            }
        }
          
        stage('版本发布') {
            steps{
                script {
                    echo "版本发布"
                }
            }
        }
    }
    post {
        success {
            script {
                echo "success"
            }
        }
        failure {
            script {
                echo "failure"
            }
        }
        aborted {           
            script {
                echo "aborted"
            }
        }
    }
}
