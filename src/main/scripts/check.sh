#!/bin/sh

data=samples/train.conll.txt
jar=@name@-@version@-exec.jar
pkg=jp.gr.java_conf.dyama.rink
tools=${pkg}.tools

out=test.out
model=test.model
 
learn="java -Xmx1024m -cp  ${jar} ${tools}.DependencyLearner"
parse="java -Xmx512m  -jar ${jar} -i ${data} -m ${model} -v"
evaldeps=" java -Xmx512m  -cp  ${jar} ${tools}.EvalDependencies"


${learn} -i ${data} -o ${model} -l SVM ;

${parse} > ${out};

${evaldeps} ${data} ${out};

rm -f ${out} ${model};
