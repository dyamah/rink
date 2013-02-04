package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.PrintStream;

import jp.gr.java_conf.dyama.rink.common.IDConverter;
import jp.gr.java_conf.dyama.rink.corpus.PTB;
import jp.gr.java_conf.dyama.rink.corpus.TagSet;
import jp.gr.java_conf.dyama.rink.parser.IDConverterImpl;
import jp.gr.java_conf.dyama.rink.parser.Sentence;



public class Evaluator {

    static private WordImpl.Generator generator_ ;
    static {
        IDConverter idconverter = new IDConverterImpl.MutableIDConverter();
        generator_ = new WordImpl.Generator(idconverter);
    }

    public static class Results {
        private String name_ ;
        private int correct_pos_ ;
        private int correct_dependencies_;
        private int dependencies_ ;
        private int words_ ;
        private int punctuations_ ;
        private int sentences_;
        private int complete_ ;
        private int correct_root_ ;

        public Results(String name){
            name_ = name;
            correct_pos_ = 0;
            correct_dependencies_ = 0;
            dependencies_ = 0;
            words_ = 0;
            punctuations_ = 0;
            sentences_ = 0;
            complete_ = 0;
            correct_root_ = 0;
        }
        String getName(){
            return name_;
        }
        int getCorrectPOS(){
            return correct_pos_ ;
        }
        int getCorrectDependencies(){
            return correct_dependencies_ ;
        }
        int getDependencies(){
            return dependencies_;
        }
        int getWords(){
            return words_ ;
        }
        int getPunctuations(){
            return punctuations_ ;
        }
        int getSentences(){
            return sentences_ ;
        }
        int getComplete(){
            return complete_ ;
        }
        int getCorrectRoot(){
            return correct_root_;
        }



        public void add(Results results){
            correct_pos_ += results.correct_pos_;
            correct_dependencies_ += results.correct_dependencies_;
            dependencies_ += results.dependencies_;
            words_ += results.words_;
            punctuations_ += results.punctuations_;
            sentences_ += results.sentences_;
            complete_ += results.complete_;
            correct_root_ += results.correct_root_;
        }
    }
    private boolean isPunctuation(TagSet.PartOfSpeech pos){

        if (PTB.POS.COMMA == pos ||
                PTB.POS.COLON == pos ||
                PTB.POS.PERIOD == pos ||
                PTB.POS.ODQ == pos ||
                PTB.POS.CDQ == pos)
            return true;

        return false ;
    }

    private int getNumberOfRootNodes(Sentence sentence){
        int root = 0;
        for(int i = 0; i < sentence.size(); i++){
            WordImpl w = (WordImpl) sentence.getWord(i);
            if (w.getParent() < 0)
                root ++;
        }
        return root;
    }


    public static Sentence createSentence(){
        return new SentenceImpl(generator_, null);
    }

    public Results evalute(Sentence gold, Sentence test){
        if (gold == null || test == null)
            return null;
        if (gold.size() != test.size())
            return null;
        Results results = new Results(null);
        if (gold.size() == 0)
            return results;
        results.words_ = gold.size();
        int num_roots = getNumberOfRootNodes(test);
        for(int i = 0; i < results.words_; i++){
            WordImpl g = (WordImpl) gold.getWord(i);
            WordImpl t = (WordImpl) test.getWord(i);

            if (! g.getSurface().equals(t.getSurface()))
                return null;

            if (g.getPOS() == t.getPOS())
                results.correct_pos_ ++ ;

            if (isPunctuation(g.getPOS())){
                results.punctuations_ ++ ;
                continue ;
            }
            results.dependencies_ ++;
            int head = t.getParent();
            if (head < 0)
                head = head * num_roots ;

            if (g.getParent() == head){
                results.correct_dependencies_ ++ ;
                if (g.getParent() < 0)
                    results.correct_root_ ++ ;
            }
        }

        if (results.correct_dependencies_ == results.dependencies_){
            results.complete_++;
        }

        results.sentences_ ++ ;
        return results;
    }

    private double ratio(int a, int b){
        return (double ) a / b;
    }

    public void show(Results[] results, PrintStream out){
        for(int  i = 0 ; i < results.length; i++){
            out.printf("# [%2d]:%s", i,  results[i].name_);
            out.println();
        }
        out.println();
        out.print("#                   ");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t[%3d]", i);
        out.println();

        out.print("POS Accuracy       :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%.3f", ratio(results[i].correct_pos_ , results[i].words_));
        out.println();

        out.print("Dependnecy Accuracy:");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%.3f", ratio(results[i].correct_dependencies_, results[i].dependencies_));
        out.println();

        out.print("Complete rate      :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%.3f", ratio(results[i].complete_, results[i].sentences_) );
        out.println();

        out.print("Root Accuracy      :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%.3f", ratio(results[i].correct_root_, results[i].sentences_ ));
        out.println();
        out.println();

        out.print("#correct dep.      :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].correct_dependencies_);
        out.println();

        out.print("#dependencies      :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].dependencies_);
        out.println();

        out.print("#punctuations      :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].punctuations_);
        out.println();

        out.print("#correct POS       :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].correct_pos_);
        out.println();

        out.print("#words             :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].words_);
        out.println();

        out.print("#correct root      :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].correct_root_);
        out.println();

        out.print("#complete sent.    :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].complete_);
        out.println();

        out.print("#sentences         :");
        for(int  i = 0 ; i < results.length; i++)
            out.printf("\t%d", results[i].sentences_);
        out.println();


    }

}
