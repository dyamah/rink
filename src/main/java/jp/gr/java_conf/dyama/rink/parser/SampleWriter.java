package jp.gr.java_conf.dyama.rink.parser;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Sample Writer
 * @author Hiroyasu Yamada
 *
 */
public interface SampleWriter {

    /**
     * write one sample to the print stream.
     * @param sample the sample.
     * @param out the print stream. do nothing if the out is null.
     * @throws IOException if it failed to write.
     * @throws IllegalArgumentException if the sample is null.
     */
    void write(Sample sample, PrintStream out) throws IOException ;

}
