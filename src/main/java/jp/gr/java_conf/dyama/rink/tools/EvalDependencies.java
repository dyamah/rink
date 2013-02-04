package jp.gr.java_conf.dyama.rink.tools;

import java.io.FileNotFoundException;
import java.io.IOException;

import jp.gr.java_conf.dyama.rink.parser.Sentence;
import jp.gr.java_conf.dyama.rink.parser.core.CoNLLXSentenceReader;
import jp.gr.java_conf.dyama.rink.parser.core.Evaluator;


public class EvalDependencies {

    /**
     * @param args
     */
    public static void main(String[] args) {
        if (args.length < 2){
            System.err.println("EvalDependencies goldfile systemfile1 [systemfile2...]");
            System.exit(1);
        }
        Evaluator eval = new Evaluator();

        int n = args.length - 1;
        CoNLLXSentenceReader[] readers = new CoNLLXSentenceReader[n];
        Evaluator.Results[] results = new Evaluator.Results[n];
        Sentence[] sentences = new Sentence[n];
        try {
            CoNLLXSentenceReader gold_reader = new CoNLLXSentenceReader(args[0], CoNLLXSentenceReader.Mode.TRAIN);
            Sentence gold = Evaluator.createSentence();
            for(int i = 0; i < n; i++){
                readers[i] = new CoNLLXSentenceReader(args[i+1], CoNLLXSentenceReader.Mode.TRAIN);
                results[i] = new Evaluator.Results(args[i+1]);
                sentences[i] = Evaluator.createSentence();
            }

            while(true){
                if (! gold_reader.read(gold)){
                    for(int i = 0; i < n; i++ ){
                        if (readers[i].read(sentences[i]))
                            throw new RuntimeException("Invalid the number of lines: " + i + "-th system.");

                    }
                    eval.show(results, System.out);
                    break ;
                }

                for(int i = 0; i < readers.length; i++ ){
                    if (! readers[i].read(sentences[i]))
                        throw new RuntimeException("Fail to read: " + i + "-th system");
                    Evaluator.Results r = eval.evalute(gold, sentences[i]);
                    if (r == null)
                        throw new RuntimeException("Fail to evaluate: " + i + "-th system");

                    results[i].add(r);
               }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.exit(1);
        } catch (IOException e) {
            // TODO 自動生成された catch ブロック
            e.printStackTrace();
        }




    }

}
