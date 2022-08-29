//解决HTML显示问题
System.setProperty("hudson.model.DirectoryBrowserSupport.CSP","")
pipeline {
   agent { node {label "master"}}
   stages {
      stage("CreateSQLFile"){
      	steps{
      		script{
      			sh "echo '${SQL};' > migrate.sql && cat migrate.sql"
      		}
      	}
      }
 
      stage("SoarSQL"){
      	steps{
      		script{
      			sh """
                              #一般jenkins是普通用户运行的，所以把soar命令拷贝到当前jenkins运行用户的家目录下，防止权限问题导致命令无法执行
      			    /home/deploy/soar -report-type html -query  v${ProjectName}-migrate.sql   > soarsql.html
                    cat soarsql.html
 
                    """
                publishHTML([allowMissing: false, alwaysLinkToLastBuild: false, keepAll: false, reportDir: '', reportFiles: ' soarsql.html', reportName: 'SQLreport', reportTitles: ''])
      		
                Email("SQL审核完成 <a href='http://192.168.66.101:8080/ /view/${JOB_NAME}/job/${JOB_NAME}/SQLreport/'>审核报告</a>","${EmailUser}")  #Job传入email地址
      		}
      	}
      }
   }
}
 
 
 
//定义邮件内容
def Email(status,emailUser){
    emailext body: """
            <!DOCTYPE html> 
            <html> 
            <head> 
            <meta charset="UTF-8"> 
            </head> 
            <body leftmargin="8" marginwidth="0" topmargin="8" marginheight="4" offset="0"> 
                <img src="http://192.168.66.112/jenkins.jpg"> //logo地址
                <table width="95%" cellpadding="0" cellspacing="0" style="font-size: 11pt; font-family: Tahoma, Arial, Helvetica, sans-serif">   
                    <tr> 
                        <td><br /> 
                            <b><font color="#0B610B">构建信息</font></b> 
                        </td> 
                    </tr> 
                    <tr> 
                        <td> 
                            <ul> 
                                <li>项目名称：${JOB_NAME}</li>         
                                <li>构建编号：${BUILD_ID}</li> 
                                <li>构建信息: ${status} </li>                         
                                <li>项目地址：<a href="${BUILD_URL}">${BUILD_URL}</a></li>    
                                <li>构建日志：<a href="${BUILD_URL}console">${BUILD_URL}console</a></li> 
                            </ul> 
                        </td> 
                    </tr> 
                    <tr>  
                </table> 
            </body> 
            </html>  """,
            subject: "Jenkins-${JOB_NAME}项目构建信息 ",
            to: emailUser    
}
