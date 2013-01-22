#!/bin/sh

M2_REPO=~/.m2/repository
CLI=${M2_REPO}/commons-cli/commons-cli/1.2/commons-cli-1.2.jar
LIBSVM=${M2_REPO}/tw/edu/ntu/csie/libsvm/3.1/libsvm-3.1.jar
RINK=${M2_REPO}/jp/gr/java_conf/dyama/rink/rink/1.0-beta/rink-1.0-beta.jar

classpath=${CLI}:${LIBSVM}:${RINK}
pkg=jp.gr.java_conf.dyama.rink
tool=${pkg}.tools.DependencyLearner

echo classpath=${classpath}
echo tool=${tool} 
echo argtument=$@

command="java -Xmx1024m -cp ${classpath} ${tool} $@"

${command};
