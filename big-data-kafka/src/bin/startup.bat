@echo off
rem ���ÿ���̨����Ϊ utf-8
chcp 65001
color 0b
set "JAVA_HOME=C:\Program Files\Java\jdk1.8.0_151"
java -Dfile.encoding=utf-8 -Xmx1024M -Xms512M -classpath .;..\lib\*;..\config\ com.edison.bigdatakafka.BigDatakafkaApplication
echo. & pause
