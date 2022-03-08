# CICD流水线

## 实现功能
### 1、代码自动构建(我这里做的手动触发，你可以调整为自动触发构建)
### 2、流转审批节点
### 3、触发钉钉或企业微信或其他平台机器人发布审批信息
### 4、审批人登入Jenkins填写随机Token确认审批发布
### 5、Token认证成功,发布站内信进行通知发版具体时间
### 6、到达发布时间即可自动发布线上K8s集群,发布时间可自行定义！
### 7、流程中出现Token认证失败或被审批人员拒绝那么整个流程结束，并通过机器人发布通知信息！

## 效果演示
![K8sCICD](https://user-images.githubusercontent.com/42825450/157217005-3c3d99ef-aaf2-4d15-bf39-bab615618781.gif)
![K8sCICD01](https://user-images.githubusercontent.com/42825450/157217381-7495d022-b317-4bcf-a40f-52021decff2a.jpg)
