package jp.gr.java_conf.dyama.rink.parser.core;

import jp.gr.java_conf.dyama.rink.parser.Feature;
import jp.gr.java_conf.dyama.rink.parser.FeatureFunction;
import jp.gr.java_conf.dyama.rink.parser.Sample;
import jp.gr.java_conf.dyama.rink.parser.Word;

public class MIRAFeatureFunction implements FeatureFunction {

    private static final long serialVersionUID = 9129785033181012567L;

    static enum Category {
        DEP(Action.Type.LEFT.getID()),
        CONTEXT(2);

        private int id_ ;
        Category(int id){
            id_ = id;
        }
        int getID(){
            return id_ ;
        }
        private static Category decode(int code){
            if(code == DEP.getID())
                return DEP;
            if(code == CONTEXT.getID())
                return CONTEXT;
            return null;
        }
    }

    static enum Position {
        L2(0),
        L1(1),
        L0(2),
        R0(3),
        R1(4),
        R2(5),

        PARENT(6),
        LEFT_CHILD(7),
        RIGHT_CHILD(8),
        ML_GRANDCHILD(9),
        MR_GRANDCHILD(10);
        static final int BIT_SHIFT = 27 ;
        static final int BIT_MASK = ((1 << 4) - 1) <<  BIT_SHIFT ;
        private int id_ ;
        Position(int id){
            id_ = id;
        }
        int getID(){
            return id_ ;
        }
        private static Position decode(int code){
            switch (code){
            case 0 : return L2 ;
            case 1 : return L1 ;
            case 2 : return L0 ;
            case 3 : return R0 ;
            case 4 : return R1 ;
            case 5 : return R2 ;
            case 6 : return PARENT ;
            case 7 : return LEFT_CHILD ;
            case 8 : return RIGHT_CHILD ;
            case 9 : return ML_GRANDCHILD ;
            case 10 : return MR_GRANDCHILD ;
            }

            return null;
        }
    }

    static enum Type {
        LEXCON(0),
        POS(1);
        static final int BIT_SHIFT = 26 ;
        static final int BIT_MASK = 1 <<  BIT_SHIFT ;

        private int id_ ;
        Type(int id){
            id_ = id;
        }
        int getID(){
            return id_ ;
        }
        private static Type decode(int code){
            switch (code) {
            case 0: return LEXCON;
            case 1: return POS;
            }
            return null;
        }
    }

    static class FeatureImpl implements Feature {
        private static final int VALUE_MASK = (1 << 27) - 1;
        private Position pos_ ;
        private Type type_ ;
        private int value_ ;
        FeatureImpl(){
            pos_ = null;
            type_ = null;
            value_ = 0;
        }
        @Override
        public int encode() {
            return (pos_.getID() << Position.BIT_SHIFT) | (type_.getID() << Type.BIT_SHIFT) | value_;
        }

        @Override
        public void decode(int code) {
            pos_ = Position.decode((code & Position.BIT_MASK) >> Position.BIT_SHIFT);
            type_ = Type.decode((code & Type.BIT_MASK) >> Type.BIT_SHIFT);
            value_ = code & VALUE_MASK;
            // TODO 自動生成されたメソッド・スタブ
        }

        void set(Position pos, Type type, int value){
            pos_ = pos ;
            type_ = type ;
            value_ = value ;
        }
    }

    static class FeatureSequence {
        static final int[] UNIGRAM_ONLY   = {1, 0, 0};
        static final int[] BIGRAM_ONLY    = {0, 1, 0};
        static final int[] TRIGRAM_ONLY   = {0, 0, 1};
        static final int[] UNI_BIGRAM     = {1, 1, 0};
        static final int[] BI_TRIGRAM     = {0, 1, 1};
        static final int[] UNI_BI_TRIGRAM = {1, 1, 1};

        private int[] sequence_ ;
        private int[] pattern_ ;
        private int start_ ;
        private int end_ ;
        FeatureSequence(){

        }
    }

    static class Features {
        private FeatureSequence[] context_ ;
        private FeatureSequence[] dep_left_ ;
        private FeatureSequence[] dep_right_ ;
    }

    private static final int L1w_L0w_R0w_R1w =  0;
    private static final int L1w_L0w_R0w_R1p =  1;
    private static final int L1w_L0w_R0p_R1w =  2;
    private static final int L1w_L0w_R0p_R1p =  3;
    private static final int L1w_L0p_R0w_R1w =  4;
    private static final int L1w_L0p_R0w_R1p =  5;
    private static final int L1w_L0p_R0p_R1w =  6;
    private static final int L1w_L0p_R0p_R1p =  7;
    private static final int L1p_L0w_R0w_R1w =  8;
    private static final int L1p_L0w_R0w_R1p =  9;
    private static final int L1p_L0w_R0p_R1w = 10;
    private static final int L1p_L0w_R0p_R1p = 11;
    private static final int L1p_L0p_R0w_R1w = 12;
    private static final int L1p_L0p_R0w_R1p = 13;
    private static final int L1p_L0p_R0p_R1w = 14;
    private static final int L1p_L0p_R0p_R1p = 15;

    private static final int Pw_LCw_LGCw = 16; // 111
    private static final int Pw_LCw_LGCp = 16; // 001
    private static final int Pw_LCp_LGCw = 16; // 111
    private static final int Pw_LCp_LGCp = 16; // 001
    private static final int Pp_LCw_LGCw = 16; // 111
    private static final int Pp_LCw_LGCp = 16; // 001
    private static final int Pp_LCp_LGCw = 16; // 111
    private static final int Pp_LCp_LGCp = 16; // 001

    private static final int Pw_LCw_RGCw = 16; // 001
    private static final int Pw_LCw_RGCp = 16; // 001
    private static final int Pw_LCp_RGCw = 16; // 001
    private static final int Pw_LCp_RGCp = 16; // 001
    private static final int Pp_LCw_RGCw = 16; // 001
    private static final int Pp_LCw_RGCp = 16; // 001
    private static final int Pp_LCp_RGCw = 16; // 001
    private static final int Pp_LCp_RGCp = 16; // 001

    private static final int Pw_RCw_LGCw = 16;


    private static final int Pw_RCw_RGCw = 16;




    private void generateSoucreFeatureSequence(Word l1, Word l0, Word r0, Word r1, int[][] source){
        FeatureImpl f = new FeatureImpl();

        f.set(Position.L1, Type.POS, l1.getPOS().getID()); source[L1p_L0w_R0w_R1p][0] = f.encode();
        f.set(Position.L0, Type.LEXCON, l0.getID());       source[L1p_L0w_R0w_R1p][1] = f.encode();
        f.set(Position.R0, Type.LEXCON, r0.getID());       source[L1p_L0w_R0w_R1p][2] = f.encode();
        f.set(Position.R1, Type.POS, r1.getPOS().getID()); source[L1p_L0w_R0w_R1p][3] = f.encode();

        f.set(Position.L1, Type.POS, l1.getPOS().getID()); source[L1p_L0w_R0p_R1p][0] = f.encode();
        f.set(Position.L0, Type.LEXCON, l0.getID());       source[L1p_L0w_R0p_R1p][1] = f.encode();
        f.set(Position.R0, Type.POS, r0.getPOS().getID()); source[L1p_L0w_R0p_R1p][2] = f.encode();
        f.set(Position.R1, Type.POS, r1.getPOS().getID()); source[L1p_L0w_R0p_R1p][3] = f.encode();

        f.set(Position.L1, Type.POS, l1.getPOS().getID()); source[L1p_L0p_R0w_R1p][0] = f.encode();
        f.set(Position.L0, Type.POS, l0.getPOS().getID()); source[L1p_L0p_R0w_R1p][1] = f.encode();
        f.set(Position.R0, Type.LEXCON, r0.getID());       source[L1p_L0p_R0w_R1p][2] = f.encode();
        f.set(Position.R1, Type.POS, r1.getPOS().getID()); source[L1p_L0p_R0w_R1p][3] = f.encode();

        f.set(Position.L1, Type.POS, l1.getPOS().getID()); source[L1p_L0p_R0p_R1p][0] = f.encode();
        f.set(Position.L0, Type.POS, l0.getPOS().getID()); source[L1p_L0p_R0p_R1p][1] = f.encode();
        f.set(Position.R0, Type.POS, r0.getPOS().getID()); source[L1p_L0p_R0p_R1p][2] = f.encode();
        f.set(Position.R1, Type.POS, r1.getPOS().getID()); source[L1p_L0p_R0p_R1p][3] = f.encode();
    }

    private void generateContextFeature(int[][] source){
        FeatureSequence fs = new FeatureSequence();
        fs.sequence_ = source[L1p_L0w_R0w_R1p];

    }
    private void generateDependencyLeftFeature(int[][] source, Word parent, Word child, Word gcl, Word  gcr){
        FeatureSequence fs = new FeatureSequence();
        FeatureImpl f = new FeatureImpl();

        {
            int[] x = new int[3];
            f.set(Position.PARENT, Type.LEXCON, parent.getID());     x[0] = f.encode();
            f.set(Position.RIGHT_CHILD, Type.LEXCON, child.getID()); x[1] = f.encode();
            f.set(Position.ML_GRANDCHILD, Type.LEXCON, gcl.getID()); x[2] = f.encode();
        }

        {
            int[] x = new int[3];
            f.set(Position.PARENT, Type.LEXCON, parent.getID());     x[0] = f.encode();
            f.set(Position.RIGHT_CHILD, Type.LEXCON, child.getID()); x[1] = f.encode();
            f.set(Position.ML_GRANDCHILD, Type.LEXCON, gcr.getID()); x[2] = f.encode();
        }

        // fs.sequence_ = source[Pw_Cw_GCw];
    }
    private void generateDependencyRightFeature(int[][] source, Word parent, Word child, Word grandchild_){
    }

    @Override
    public void apply(Sample _sample) {

        SampleImpl sample = (SampleImpl) _sample;

        State state = sample.getState();
        Features features = new Features();

        Word l1 = WordImpl.BOS;
        Word r1 = WordImpl.EOS;
        int i = state.getIDofLeftNode(-1);
        if (i >= 0)
            l1 = sample.getSentence().getWord(i);
        i = state.getIDofLeftNode(0);
        Word l0 = sample.getSentence().getWord(i);
        i = state.getRightNode(0);
        Word r0 = sample.getSentence().getWord(i);
        i = state.getRightNode(0);
        if (i >= 0)
            r1 = sample.getSentence().getWord(i);

        int[][] source = new int[23][];
        for(int t = 0; t < source.length; t++)
            source[t] = new int[4];

        generateSoucreFeatureSequence(l1, l0, r0, r1, source);
        generateContextFeature(source);
        // generateDependencyLeftFeature(source);
        // generateDependencyRightFeature(source);

    }
}
