#!/bin/sh

data=samples/train.conll.txt
classpath=rink-0.1-beta-exec.jar
pkg=jp.gr.java_conf.dyama.rink
tools=${pkg}.tools

out=test.out
model=test.model
 
learn="java -Xmx1024m -cp  ${classpath} ${tools}.DependencyLearner"
parse="java -Xmx512m  -jar ${classpath} -i ${data} -m ${model} -v"
evaldeps=" java -Xmx512m  -cp  ${classpath} ${tools}.EvalDependencies"


${learn} -i ${data} -o ${model} -l SVM ;

${parse} > ${out};

${evaldeps} ${data} ${out};

rm -f ${out} ${model};
