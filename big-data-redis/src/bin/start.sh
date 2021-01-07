#!/usr/bin/env bash
base_dir=$(dirname $0)/..

#替换bootstrap.yml中的server.address
ip=`hostname -i`
sed -i "s/address: 127.0.0.1/address: ${ip}/" $base_dir/config/bootstrap.yml
echo "完成server.address替换"

echo base_dir;
# loading dependency jar in lib directory
for file in $base_dir/lib/*.jar;
do
  CLASSPATH=$CLASSPATH:$file
done

CLASSPATH=$CLASSPATH:$base_dir/config/

if [ -z "$PUBLISH_CONFIG_OPTS" ]; then
  PUBLISH_CONFIG_OPTS="-Dpublish-config=$base_dir/config/application.yml"
fi
#if [ -z "$PUBLISH_PROFILE_OPTS" ]; then
#  PUBLISH_PROFILE_OPTS="-Dspring.profiles.active=production"
#fi

#应用程序启动读取配置信息文件
java_option="$PUBLISH_CONFIG_OPTS "

#应用程序启动依赖类文件路径
class_path="$CLASSPATH" 

#微服务或后台程序的运行服务名称(需修改)
service_name="big-data-redis"

#应用程序启动入口main函数名称,不同服务的main函数名称不能相同!!!（需修改）
entry_point="com.edison.bigdataredis.BigDataRedisApplication"

$base_dir/bin/daemon.sh start "$java_option" "$class_path" "$service_name" "$entry_point"