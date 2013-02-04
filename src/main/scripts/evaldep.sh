#!/bin/sh

M2_REPO=~/.m2/repository
RINK=${M2_REPO}/jp/gr/java_conf/dyama/rink/rink/0.1-beta/rink-0.1-beta.jar

classpath=${RINK}
pkg=jp.gr.java_conf.dyama.rink
tool=${pkg}.tools.EvalDependencies

command="java -Xmx128m -cp ${classpath} ${tool} $@"

${command};
