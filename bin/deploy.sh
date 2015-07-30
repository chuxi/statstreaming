#!/usr/bin/env bash

BASEDIR="$(cd "`dirname "$0"`"/..; pwd)"

BUILDDIR="$BASEDIR/target/scala-2.10"

JARNAME="statisticApp.jar"

# This program is used for spark-submiting

# if you are a ZJUer, = =... you know what following info means, do not use it please
username='hadoop'
password='hadoop'
hostname='10.214.208.11'

function putfile() {
    sshpass -p $password scp $1 $username@$hostname:/tmp
}

# put the jar file to remote driver

putfile "$BUILDDIR/$JARNAME"

sshpass -p $password ssh -t $username@$hostname 'cd /tmp && /usr/local/spark/bin/spark-submit --class cn.edu.zju.king.streaming.StatisticApp --master spark://node1:7077 statisticApp.jar 10.214.208.12:9092,10.214.208.13:9092,10.214.208.14:9092 test'

