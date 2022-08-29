# Jenkins集成Soar实现自动化SQL审查

## 流程介绍
### 1、Jenkins界面填写需要审查的SQL语句
### 2、将审查SQL写入到 `.sql` 结尾的文件中
### 3、调用Soar执行命令扫描 `.sql` 文件
### 4、生成SQL审查报告并通过邮件形式推送给指定人员查阅

## 效果演示
![image](https://user-images.githubusercontent.com/42825450/187132248-3247dafc-feb0-403f-94ed-5543d6213aee.png)
![image](https://user-images.githubusercontent.com/42825450/187132275-c49f8d8b-9297-40b0-89c0-c3496cdb3e1d.png)
![image](https://user-images.githubusercontent.com/42825450/187132304-402fb809-aadb-4a68-91c9-db75a30ef84b.png)
![image](https://user-images.githubusercontent.com/42825450/187132325-3110af12-fb83-4ed2-b5bd-4bb89ad18332.png)
