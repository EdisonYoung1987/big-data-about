#!/bin/sh

# 微服务或后台程序的运行服务名称(需修改)
service_name="big-data-kafka"

# 定位脚本所在当前目录
script_path=$(cd "$(dirname "$0")"; pwd)
# 定位脚本所在父目录
base=$(dirname ${script_path})
$base/bin/daemon.sh status "" "" "$service_name"
