#!/bin/sh

jar=@name@-@version@-exec.jar
pkg=jp.gr.java_conf.dyama.rink
tools=${pkg}.tools

command="java -Xmx512m -jar ${jar} $@"

${command};
