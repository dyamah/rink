#!/bin/sh

jar=@name@-@version@-exec.jar
pkg=jp.gr.java_conf.dyama.rink
tools=${pkg}.tools
main=${tools}.EvalDependencies

command="java -Xmx1024m -cp ${jar} ${main} $@"

${command};
