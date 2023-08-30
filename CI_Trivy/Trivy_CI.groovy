pipeline {
    agent any
 
    stages {
        stage('镜像安全扫描') {
            steps{
                script { 
                    def formatOption = "--format template --template \"@/opt/jenkins/html.tpl\""
 
                    sh("""
                        trivy image --skip-db-update --exit-code 1 --severity CRITICAL <IMAGES>:<TAG> --cache-dir trivy_db $formatOption --timeout 10m --output trivy.html
                    """)
 
                    // reportDir 报告所在目录；reportFiles 报告名称；reportName 在Jenkins菜单栏显示的名称 ；reportTitles 点进报告显示的Title
                    publishHTML (target : [allowMissing: false,
                        alwaysLinkToLastBuild: true,
                        keepAll: true,
                        reportDir: '.',
                        reportFiles: "trivy.html",
                        reportName: 'Trivy Scan',
                        reportTitles: 'Trivy Scan'])
                }
            }
        }
    }
}
