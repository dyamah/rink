#!/bin/sh

M2_REPO=~/.m2/repository
CLI=${M2_REPO}/commons-cli/commons-cli/1.2/commons-cli-1.2.jar
RINK=${M2_REPO}/jp/gr/java_conf/dyama/rink/rink/1.0-beta/rink-1.0-beta.jar

classpath=${CLI}:${RINK}
pkg=jp.gr.java_conf.dyama.rink
tool=${pkg}.tools.DependencyParser

command="java -Xmx512m -cp ${classpath} ${tool} $@"

${command};
