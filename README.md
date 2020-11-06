# Aop2YarnClient

## Description

Sometimes, we might execute a yarn job in a SHELL script using 'yarn jar myYarnJob.jar mainClass [args]'.
If we want to get the submitted application id in the SHELL script, we have to parse the logs by regex 'application_\d+_\d+'.

However, it's a little ugly and has performance issues. The **Aop2YarnClient** give a better solution to achieve it, which 
registers an aspect when executing 'yarn jar' command, and weaves a join point to 
**org.apache.hadoop.yarn.client.api.impl.YarnClientImpl.submitApplication**, 
where we can get the submitted application id & tracking URL, then output them into a local file.

## Usage

### yarn jar

`export YARN_CLIENT_OPTS="-javaagent:~/aspectjweaver-1.9.6.jar"`

`export YARN_USER_CLASSPATH=~/Aop2YarnClient-1.0-SNAPSHOT.jar`

`export YARN_CLIENT_ASPECTJ_FILE_PATH=~/appInfo.txt`

`export PARA_NAME_ASPECTJ_DEBUG=true`

`yarn jar /apache/hadoop/share/hadoop/mapreduce/hadoop-mapreduce-examples-2.7.1.2.4.2.66-4.jar  pi 1 100000`

you will find the application id and tracking url in the ~/appInfo.txt file

### hive -e

`export HADOOP_CLIENT_OPTS="-javaagent:~/aspectjweaver-1.9.6.jar"`

`export HIVE_AUX_JARS_PATH=~/Aop2YarnClient-1.0-SNAPSHOT.jar`

`export YARN_CLIENT_ASPECTJ_FILE_PATH=~/appInfo.txt`

`export PARA_NAME_ASPECTJ_DEBUG=true`

hive -e "select count(1) from tableName"

you will also find the application id and tracking url in the ~/appInfo.txt file

### others

you can use **Aop2YarnClient** in any tools and system, which submits a yarn job.
What you should to do is to adding the "-javaagent:~/aspectjweaver-1.9.6.jar" option to java, and adding Aop2YarnClient-1.0-SNAPSHOT.jar to CLASSPATH.
YARN_CLIENT_ASPECTJ_FILE_PATH env indicates the file location storing the application id and tracking url