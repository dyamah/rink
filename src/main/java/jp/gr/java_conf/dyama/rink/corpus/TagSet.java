package jp.gr.java_conf.dyama.rink.corpus;

/**
 * Interface for Tag Set
 * @author Hiroyasu Yamada
 *
 */
public interface TagSet {

    /**
     * Returns the ID of the tag. THe ID must be a positive number.
     * @return The ID.
     */
    public int getID();


    /**
     * Returns the description about the tag.
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
