package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.PrintStream;

import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.SampleWriter;

public class OriginalSampleWriter implements SampleWriter {

    public OriginalSampleWriter(){
    }

    @Override
    public void write(Sample sample, PrintStream out) throws IOException {
        if (sample == null)
            throw new IllegalArgumentException("the sample is null.");

        if (out == null)
            return ;
        SampleImpl sampleImpl = (SampleImpl) sample;
        State state = sampleImpl.getState();
        SentenceImpl sentence = (SentenceImpl) sampleImpl.getSentence();

        DependencyRelations x = state.getDependencies();

        for(int i = 0 ; i < x.size(); i++){
            StringBuilder builder = new StringBuilder();
            int p = - state.size();
            int y__ = x.getParentID(i);
            if (y__ >= 0)
                p = y__ ;
            WordImpl word = (WordImpl) sentence.getWord(i);
            builder.append(word.getSurface());
            builder.append("\t");
            builder.append(word.getPOS().toString());
            builder.append("\t");
            builder.append(word.getParent());
            builder.append("\t");
            builder.append(p);
            out.println(builder.toString());
        }
        out.println("");
    }

}
