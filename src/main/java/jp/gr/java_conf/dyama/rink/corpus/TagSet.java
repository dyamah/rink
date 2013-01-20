package jp.gr.java_conf.dyama.rink.corpus;

/**
 * Interface for Tag Set
 * @author Hiroyasu Yamada
 *
 */
public interface TagSet {

    /**
     * get the tag ID. tag ID must be positive number.
     * @return tag ID
     */
    public int getID();


    /**
     * get the description about the tag.
     * @return description
     */
    public String getDescription();


    public static interface PartOfSpeech extends TagSet {
    }

    public static interface NoneTerminal extends TagSet {
    }

    public static interface PreTerminal extends PartOfSpeech {
    }


}
