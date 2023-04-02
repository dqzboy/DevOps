#!/bin/bash

echo
cat << EOF
██████╗  ██████╗ ███████╗██████╗  ██████╗ ██╗   ██╗            ██████╗ ██████╗ ███╗   ███╗
██╔══██╗██╔═══██╗╚══███╔╝██╔══██╗██╔═══██╗╚██╗ ██╔╝           ██╔════╝██╔═══██╗████╗ ████║
██║  ██║██║   ██║  ███╔╝ ██████╔╝██║   ██║ ╚████╔╝            ██║     ██║   ██║██╔████╔██║
██║  ██║██║▄▄ ██║ ███╔╝  ██╔══██╗██║   ██║  ╚██╔╝             ██║     ██║   ██║██║╚██╔╝██║
██████╔╝╚██████╔╝███████╗██████╔╝╚██████╔╝   ██║       ██╗    ╚██████╗╚██████╔╝██║ ╚═╝ ██║
╚═════╝  ╚══▀▀═╝ ╚══════╝╚═════╝  ╚═════╝    ╚═╝       ╚═╝     ╚═════╝ ╚═════╝ ╚═╝     ╚═╝
                                                                                          
EOF

#接收外部参数
harbor_url=$1
#镜像仓库名称
Harbor_repos=$2
#镜像名称
imageName=$3
#调用镜像标签
tag=$4
#定义容器启动和映射端口号
port=$5

images=$harbor_url/$Harbor_repos/$imageName:$tag
echo "$images"
#查询容器是否存在，存在则删除
containerId=`docker ps -a | grep -w ${imageName}:${tag} | awk '{print $1}'`
if [ "$containerId" != "" ] ; then
#停掉容器
docker stop $containerId
#删除容器
docker rm $containerId
echo "成功删除容器"
fi
#查询镜像是否存在，存在则删除
imageId=`docker images | grep -w $imageName | awk '{print $3}'`
if [ "$imageId" != "" ] ; then
#删除镜像
docker rmi -f $imageId
echo "成功删除镜像"
fi
# 登录Harbor私服
docker login -u admin -p admin $harbor_url
# 下载镜像
docker pull $images
# 启动容器
# 启动容器
docker run -di -v /logs:/home/appadmin -p $port:$port $images
container_Id=`docker ps -a | grep -w ${imageName}:${tag} | awk '{print $1}'`
status=`docker ps |grep ${container_Id}|awk '{print $9}'`
sleep 1
if [ "$status" == "Up" ];then
    echo "容器启动成功"
else
echo "容器启动失败"
exit 1
fi
