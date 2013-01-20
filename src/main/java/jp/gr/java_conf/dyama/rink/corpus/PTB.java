package jp.gr.java_conf.dyama.rink.corpus;

import jp.gr.java_conf.dyama.rink.corpus.TagSet.NoneTerminal;
import jp.gr.java_conf.dyama.rink.corpus.TagSet.PreTerminal;

public interface PTB {

    static public enum PhrasalCategory implements NoneTerminal {
        UNDEFINED(0, "Undefined phrasal category"),
        S(       10, "Simple clause (sentence)"),
        SBAR(    11, "S's clause with complementizer" ),
        SBARQ(   12, "Wh-question S' clause"),
        SQ(      13, "Inverted YES/NO question S' clause"),
        SINV(    14, "Declarative inverted S' caluse"),
        ADJP(    15, "Adjective Phrase"),
        ADVP(    16, "Adverbial Phrase"),
        NP(      17, "Noun Phrase"),
        PP(      18, "Prepositional Phrase"),
        QP(      19, "Quantifier Phrase (inside NP)"),
        VP(      20, "Verb Phrase"),
        WHNP(    21, "Wh-Noun Phrase"),
        WHPP(    22, "Wh-Prepositional Phrase"),
        CONJP(   23, "Multiword conjunction phrases"),
        FRAG(    24, "Fragment"),
        INTJ(    25, "Interjection"),
        LST(     26, "List marker" ),
        NAC(     27, "Not A Constituent grouping"),
        NX(      28, "Nominal constituent inside NP"),
        PRN(     29, "Parenthetical"),
        PRT(     30, "Particle"),
        RRC(     31, "Reduced Rellative Clause"),
        UCP(     32, "Unlike Coordinated Phrase"),
        X(       33, "Unknown or uncertain"),
        WHADJP(  34, "Wh-Adjective Phrase"),
        WHADVP(  35, "Wh-Adverb Phrasae")
        ;

        private int id_;
        private String description_ ;
        private PhrasalCategory(int id, String desc){
            if (id < 0 )
                throw new IllegalArgumentException("the id is negative.");

            id_ = id ;
            description_ = desc ;
            if (desc == null)
                description_ = "";
        }
        @Override
        public int getID() {
            return id_ ;
        }

        @Override
        public String getDescription() {
            return description_;
        }

        static private final PhrasalCategory[] ARRAY = PhrasalCategory.values();
        /**
         * parse integer as a none terminal tag.
         * @param i none terminal ID
         * @return None terminal tag. return UNDEFINED if i is undefined.
         */
        static public TagSet.NoneTerminal parseInt(int i){
            for(PhrasalCategory pc : ARRAY){
                if (pc.getID() == i)
                    return pc;
            }
            return UNDEFINED;
        }

        /**
         * parse string as a none terminal tag.
         * @param str none terminal ID
         * @return None terminal tag. return UNDEFINED if str is undefined or null.
         */
        static public TagSet.NoneTerminal parseString(String str){
            if (str == null)
                return UNDEFINED;
            for(PhrasalCategory pc : ARRAY){
                if (pc.toString().equals(str))
                    return pc;
            }
            return UNDEFINED;
        }

    }

    public enum POS implements PreTerminal {

        CC(        101, "Coordinating conjunction" ),                        //    1.  CC  Coordinating conjunction
        CD(        102, "Cardinal number"),                                  //    2.  CD  Cardinal number
        DT(        103, "Determiner"),                                       //    3.  DT  Determiner
        EX(        104, "Existential there"),                                //    4.  EX  Existential there
        FW(        105, "Foreign word"),                                     //    5.  FW  Foreign word
        IN(        106, "Preposition or subordinating conjunction"),         //    6.  IN  Preposition or subordinating conjunction
        JJ(        107, "Adjective"),                                        //    7.  JJ  Adjective
        JJR(       108, "JJR Adjective, comparative"),                       //    8.  JJR Adjective, comparative
        JJS(       109, "Adjective, superlative"),                           //    9.  JJS Adjective, superlative
        LS(        110, "List item marker"),                                 //    10. LS  List item marker
        MD(        111, "Modal"),                                            //    11. MD  Modal
        NN(        112, "Noun, singular or mass"),                           //    12. NN  Noun, singular or mass
        NNS(       113, "Noun, plural"),                                     //    13. NNS Noun, plural
        NNP(       114, "Proper noun, singular"),                            //    14. NNP Proper noun, singular
        NNPS(      115, "Proper noun, plural"),                              //    15. NNPS    Proper noun, plural
        PDT(       116, "Predeterminer"),                                    //    16. PDT Predeterminer
        POS(       117, "Possessive ending"),                                //    17. POS Possessive ending
        PRP(       118, "Personal pronoun"),                                 //    18. PRP Personal pronoun
        PRP$(      119, "Possessive pronoun"),                               //    19. PRP$    Possessive pronoun
        RB(        120, "Adverb"),                                           //    20. RB  Adverb
        RBR(       121, "Adverb, comparative"),                              //    21. RBR Adverb, comparative
        RBS(       122, "Adverb, superlative"),                              //    22. RBS Adverb, superlative
        RP(        123, "Particle"),                                         //    23. RP  Particle
        SYM(       124,  "SYM Symbol"),                                      //    24. SYM Symbol
        TO(        125,  "to"),                                              //    25. TO  to
        UH(        126,  "Interjection"),                                    //    26. UH  Interjection
        VB(        127, "Verb, base form"),                                  //    27. VB  Verb, base form
        VBD(       128, "Verb, past tense"),                                 //    28. VBD Verb, past tense
        VBG(       129, "gerund or present participle"),                     //    29. VBG Verb, gerund or present participle
        VBN(       130, "Verb, past participle"),                            //    30. VBN Verb, past participle
        VBP(       131, "Verb, non-3rd person singular present"),            //    31. VBP Verb, non-3rd person singular present
        VBZ(       132, "Verb, 3rd person singular present"),                //    32. VBZ Verb, 3rd person singular present
        WDT(       133, "Wh-determiner"),                                    //    33. WDT Wh-determiner
        WP(        134, "Wh-pronoun"),                                       //    34. WP  Wh-pronoun
        WP$(       135, "Possessive wh-pronoun"),                            //    35. WP$ Possessive wh-pronoun
        WRB(       136, "WRB Wh-adverb"),                                    //    36. WRB Wh-adverb

        PERIOD(    137, "period"),                  // period
        COMMA(     138, "comma"),                   // comma
        COLON(     139, "colon"),                   // colon
        ODQ(       140, "Opening quotation: ``"),   // Opening quotation
        CDQ(       141, "Closing quotation: ''"),   // Closing quotation
        ORB(       142, "Opening parenthesis: ("),  // Opening parenthesis
        CRB(       143, "Closing parenthesis: )"),  // Closing parenthesis
        SHARP(     144, "sharp: #"),                // sharp
        DOLLAR(    145, "dollar: $"),               // dollar
        DASH(      146, "dash: --" ),               // dash
        AUX(       147, "AUX"),
        AUXG(      148, "AUXG"),
        NONE(      149, "GAP"),
        ;
        private int id_;
        private String description_ ;
        private POS(int id, String desc){
            if (id  < 0)
                throw new IllegalArgumentException("the id is negative.");
            id_ = id ;
            description_ = desc ;
            if (desc == null)
                description_ = "";
        }

        @Override
        public String toString(){
            if (this == PERIOD)
                return ".";

            if (this == COMMA)
                return ",";

            if (this == COLON)
                return ":";

            if (this == ODQ)
                return "``";

            if (this == CDQ)
                return "''";

            if (this == ORB)
                return "-LRB-";

            if (this == CRB)
                return "-RRB-";

            if (this == SHARP)
                return "#";

            if (this == DOLLAR)
                return "$";

            if (this == DASH)
                return "--";

            if (this == NONE)
                return "-NONE-";

            return super.toString();

        }

        @Override
        public int getID() {
            return id_ ;
        }

        @Override
        public String getDescription() {
            return description_;
        }

        static private final PTB.POS[] ARRAY = PTB.POS.values();
        /**
         * parse integer as a POS tag
         * @param i POS ID
         * @return POS tag. return UNDEFINED if i is undefined.
         */
        static public TagSet.PartOfSpeech parseInt(int i){
            for(PTB.POS pos : ARRAY){
                if (pos.getID() == i)
                    return pos;
            }
            return Common.POS.UNDEFINED;
        }

        /**
         * parse string as a POS tag
         * @param str POS tag string.
         * @return POS tag. return UNDEFINED if str is undefined or null.
         */
        static public TagSet.PartOfSpeech parseString(String str){
            if (str == null)
                return Common.POS.UNDEFINED;
            for(PTB.POS pos : ARRAY){
                if (pos.toString().equals(str))
                    return pos;
            }
            return Common.POS.UNDEFINED;
        }
    }
}
