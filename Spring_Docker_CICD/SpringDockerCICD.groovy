pipeline {
    //确认使用主机/节点机
    agent  { 
        node { label 'master'} 
    }
    //定义全局工具变量
    tools {
        maven 'maven3.6.2'
        jdk   'jdk1.8'
    }

    //定义所需环境变量参数
    environment {
        // 定义项目名称方便全局引用
        project = "test"
        dev_serverIP = "192.168.66.30"
        prod_serverIP = "192.168.66.30"

        //定义ssh远程主机的用户
        server_user = "root"

        DEPLOY = "$DEPLOY"
        JRA_FILE = "jra包存储路径"
        imageName = "$imageName"
        TAG = "$TAG"
        Harbor_repos = "$Harbor_repos"
    }

    parameters {
        gitParameter(branch: '',
            branchFilter: 'origin/(.*)',
            defaultValue: 'master',
            description: '',
            name: 'REVISION', 
            quickFilterEnabled: false,
            selectedValue: 'NONE',
            sortMode: 'NONE',
            tagFilter: '*',
            type: 'PT_BRANCH_TAG') 
    }

    stages {
        stage('Checkout') {
            steps {
                checkout([$class: 'GitSCM',
                          branches: [[name: "${params.REVISION}"]], // 传入分支
                          doGenerateSubmoduleConfigurations: false,
                          extensions: [],
                          gitTool: 'Default',
                          submoduleCfg: [],
                          userRemoteConfigs: [[url: 'git@192.168.66.30:root/solo-b3log.git']] 
                        ])
            }
        }

        stage('源码构建') {
            steps{
                sh "mvn clean package -Dmaven.test.skip=true"
            }
        }

	    stage('镜像构建') {
            when {
                environment name: 'DEPLOY',value: 'dev'
            }
            steps{
                // docker build
                sh '''
                    cd ${JRA_FILE}
                    docker build --build-arg JAR_FILE=yp-wms-war-1.0.0.jar -t ${imageName}:${TAG} .
                '''

            }
        }
        
        stage ('上传镜像') {
            when {
                environment name: 'DEPLOY',value: 'dev'
            }
            steps {
                script {
			        //给镜像打tag标签
                     sh "docker tag ${imageName}:${TAG} ${harbor_url}/${Harbor_repos}/${imageName}:${TAG}"

                    withCredentials([usernamePassword(credentialsId: '凭据ID', passwordVariable: 'password', usernameVariable: 'username')]) {
                        sh "docker login -u ${username} -p ${password} ${harbor_url}"
                        sh "docker push ${harbor_url}/${Harbor_repos}/${imageName}:${TAG} "
                    }
			        //删除本地刚才构建的镜像
                    sh "docker rmi -f ${imageName}:${TAG}"
                    sh "docker rmi -f ${harbor_url}/${Harbor_repos}/${imageName}:${TAG}"
                }
            }
        }
        stage('镜像拉取') {
            when {
                environment name: 'DEPLOY',value: 'dev'
            }
            steps {
                script {
                    sh '''
                        ssh ${server_user}@${dev_serverIP} "/script/pull_docker_images ${harbor_url} ${Harbor_repos} ${imageName} ${TAG} ${port}"
                    '''
                }
            }
        }

        stage('测试环境') {
            when {
                environment name: 'DEPLOY',value: 'test'
            }
            steps {
                script {
	            withEnv(['JENKINS_NODE_COOKIE=dontkillme']) {
                    sh '''
                        ssh ${server_user}@${test_serverIP} "/script/pull_docker_images ${harbor_url} ${Harbor_repos} ${imageName} ${TAG} ${port}"
                    '''
                }
            }
        }


        stage('生产环境') {
            when {
                environment name: 'DEPLOY',value: 'prod'
            }
 
            steps {
                input message: '是否部署至生产环境', ok: 'OK'
                script {
	            withEnv(['JENKINS_NODE_COOKIE=dontkillme']) {
                    sh '''
                        ssh ${server_user}@${prod_serverIP} "/script/pull_docker_images ${harbor_url} ${Harbor_repos} ${imageName} ${TAG} ${port}"
                    '''
                }
            }
        }

        stage('访问测试') {       
            steps {
                sleep time: 1, unit: 'MINUTES' 
                script {                    
                    sh label: '', script: 'curl -I -s ${HTTP_URL} |grep 200|awk \'{print $2}\''
                }
            }
        }
    }
    post {
        //成功通知
        success {
	        script {
	            if ("${DEPLOY}" == "uat" || "${DEPLOY}" == "prod"){
	                qyWechatNotification mentionedId: '', 
	                mentionedMobile: '', 
	                webhookUrl: '机器人webhook'
	            }
           }
        //失败通知
        failure {
	        script {
	            if ("${DEPLOY}" == "uat" || "${DEPLOY}" == "prod"){
	                qyWechatNotification mentionedId: '', 
	                mentionedMobile: '', 
	                webhookUrl: '机器人webhook'
	            }
            }
        }
    }
}
