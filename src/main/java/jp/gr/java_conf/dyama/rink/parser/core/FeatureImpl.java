package jp.gr.java_conf.dyama.rink.parser.core;

import jp.gr.java_conf.dyama.rink.parser.Feature;

class FeatureImpl implements Feature {
    static final int MAX_BIT_LENGTH = 31;
    static final int POSITION_BIT   =  4;
    static final int RELATION_BIT   =  4;
    static final int TYPE_BIT       =  2;
    static final int VALUE_BIT      = 21;

    static final int POSITION_SHIFT = RELATION_BIT + TYPE_BIT + VALUE_BIT ;
    static final int RELATION_SHIFT = TYPE_BIT + VALUE_BIT ;
    static final int TYPE_SHIFT     = VALUE_BIT;

    static final int POSITION_MASK  = 15 << POSITION_SHIFT;
    static final int RELATION_MASK  = 15 << RELATION_SHIFT;
    static final int TYPE_MASK      =  3 << TYPE_SHIFT ;
    static final int VALUE_MASK     =  (1 << 21) - 1 ;

    static final int MAX_VALUE      = VALUE_MASK + 1;

    // Definition POSITION ID
    private static final int L0_ = 7 << POSITION_SHIFT ;
    private static final int L1_ = 6 << POSITION_SHIFT ;
    private static final int L2_ = 5 << POSITION_SHIFT ;
    private static final int L3_ = 4 << POSITION_SHIFT ;
    private static final int L4_ = 3 << POSITION_SHIFT ;
    private static final int L5_ = 2 << POSITION_SHIFT ;
    private static final int L6_ = 1 << POSITION_SHIFT ;
    private static final int L7_ = 0 << POSITION_SHIFT ;

    private static final int R0_ = 8 << POSITION_SHIFT ;
    private static final int R1_ = 9 << POSITION_SHIFT ;
    private static final int R2_ = 10 << POSITION_SHIFT ;
    private static final int R3_ = 11 << POSITION_SHIFT ;
    private static final int R4_ = 12 << POSITION_SHIFT ;
    private static final int R5_ = 13 << POSITION_SHIFT ;
    private static final int R6_ = 14 << POSITION_SHIFT ;
    private static final int R7_ = 15 << POSITION_SHIFT ;

    public static enum POSITION {
        L0( L0_ ),
        L1( L1_ ),
        L2( L2_ ),
        L3( L3_ ),
        L4( L4_ ),
        L5( L5_ ),
        L6( L6_ ),
        L7( L7_ ),
        R0( R0_ ),
        R1( R1_ ),
        R2( R2_ ),
        R3( R3_ ),
        R4( R4_ ),
        R5( R5_ ),
        R6( R6_ ),
        R7( R7_ );
        private int id_ ;
        private POSITION(int id){
            id_ = id;
        }
        int getID(){
            return id_ ;
        }
        static POSITION decode(int code){
            int c = (code & POSITION_MASK);
            switch(c){
            case L0_ : return L0;
            case L1_ : return L1;
            case L2_ : return L2;
            case L3_ : return L3;
            case L4_ : return L4;
            case L5_ : return L5;
            case L6_ : return L6;
            case L7_ : return L7;
            case R0_ : return R0;
            case R1_ : return R1;
            case R2_ : return R2;
            case R3_ : return R3;
            case R4_ : return R4;
            case R5_ : return R5;
            case R6_ : return R6;
            case R7_ : return R7;

            default: throw new IllegalArgumentException("Undefined Position.");
            }
        }
    }

    private static final int SELF_             =  0 << RELATION_SHIFT;
    private static final int PARENT_           =  1 << RELATION_SHIFT;
    private static final int LEFT_PARENT_      =  2 << RELATION_SHIFT;
    private static final int RIGHT_PARENT_     =  3 << RELATION_SHIFT;
    private static final int GRANPA_           =  4 << RELATION_SHIFT;
    private static final int LEFT_GRANPA_      =  5 << RELATION_SHIFT;
    private static final int RIGHT_GRANPA_     =  6 << RELATION_SHIFT;
    private static final int CHILD_            =  7 << RELATION_SHIFT;
    private static final int LEFT_CHILD_       =  8 << RELATION_SHIFT;
    private static final int RIGHT_CHILD_      =  9 << RELATION_SHIFT;
    private static final int GRANDCHILD_       = 10 << RELATION_SHIFT;
    private static final int LEFT_GRANDCHILD_  = 11 << RELATION_SHIFT;
    private static final int RIGHT_GRANDCHILD_ = 12 << RELATION_SHIFT;
    private static final int SIBLING_          = 13 << RELATION_SHIFT;
    private static final int LEFT_SIBLING_     = 14 << RELATION_SHIFT;
    private static final int RIGHT_SIBLING_    = 15 << RELATION_SHIFT;

    public static enum RELATION {
                    SELF( SELF_ ),
                  PARENT( PARENT_ ),
             LEFT_PARENT( LEFT_PARENT_ ),
            RIGHT_PARENT( RIGHT_PARENT_ ),
                  GRANPA( GRANPA_ ),
             LEFT_GRANPA( LEFT_GRANPA_ ),
            RIGHT_GRANPA( RIGHT_GRANPA_ ),
                   CHILD( CHILD_ ),
              LEFT_CHILD( LEFT_CHILD_ ),
             RIGHT_CHILD( RIGHT_CHILD_ ),
              GRANDCHILD( GRANDCHILD_ ),
         LEFT_GRANDCHILD( LEFT_GRANDCHILD_ ),
        RIGHT_GRANDCHILD( RIGHT_GRANDCHILD_ ),
                 SIBLING( SIBLING_ ),
            LEFT_SIBLING( LEFT_SIBLING_ ),
           RIGHT_SIBLING( RIGHT_SIBLING_ );
        private int id_ ;
        private RELATION(int id){
            id_ = id;
        }
        int getID(){
            return id_ ;
        }
        static RELATION decode(int code){
            int c = code & RELATION_MASK ;
            switch(c){
            case SELF_             : return SELF;
            case PARENT_           : return PARENT;
            case LEFT_PARENT_      : return LEFT_PARENT;
            case RIGHT_PARENT_     : return RIGHT_PARENT;
            case GRANPA_           : return GRANPA;
            case LEFT_GRANPA_      : return LEFT_GRANPA;
            case RIGHT_GRANPA_     : return RIGHT_GRANPA;
            case CHILD_            : return CHILD;
            case LEFT_CHILD_       : return LEFT_CHILD;
            case RIGHT_CHILD_      : return RIGHT_CHILD;
            case GRANDCHILD_       : return GRANDCHILD;
            case LEFT_GRANDCHILD_  : return LEFT_GRANDCHILD;
            case RIGHT_GRANDCHILD_ : return RIGHT_GRANDCHILD;
            case SIBLING_          : return SIBLING;
            case LEFT_SIBLING_     : return LEFT_SIBLING;
            case RIGHT_SIBLING_    : return RIGHT_SIBLING;
            default: throw new IllegalArgumentException("Undefined Relation.");
            }
        }
    }

    private static final int LEXCON_ = 0 << TYPE_SHIFT;
    private static final int POS_    = 1 << TYPE_SHIFT;
    private static final int LEAF_   = 2 << TYPE_SHIFT;
    private static final int OTHERS_ = 3 << TYPE_SHIFT;

    public static enum TYPE {
        LEXCON( LEXCON_ ),
           POS( POS_ ),
          LEAF( LEAF_ ),
        OTHERS( OTHERS_ );

        private int id_ ;
        private TYPE(int id){
            id_ = id;
        }
        int getID(){
            return id_ ;
        }
        static TYPE decode(int code){
            int c = code & TYPE_MASK;
            switch(c){
            case LEXCON_ : return LEXCON;
            case POS_    : return POS;
            case LEAF_   : return LEAF;
            case OTHERS_ : return OTHERS;
            default: throw new IllegalArgumentException("Undefined Type.");
            }
        }
    }

    private POSITION position_ ;
    private RELATION relation_;
    private TYPE     type_;
    private int      value_;

    FeatureImpl(){
       position_ = POSITION.L7;
       relation_ = RELATION.SELF;
       type_     = TYPE.LEXCON;
       value_    = 0 ;
    }

    /**
     * Sets the feature information.
     * @param position the position of the feature.
     * @param relation the type of relations.
     * @param type     the type of the feature.
     * @param value    the feature's value.
     * @throws IllegalArgumentException if the position is null.
     * @throws IllegalArgumentException if the relation is null.
     * @throws IllegalArgumentException if the type is null.
     * @throws IllegalArgumentException if the value is out of range (value  < 0 || value >= 2^21)
     */
    void set(POSITION position, RELATION relation, TYPE type, int value){
        if (position == null)
            throw new IllegalArgumentException("the position is null.");

        if (relation == null)
            throw new IllegalArgumentException("the relation is null.");

        if (type == null)
            throw new IllegalArgumentException("the type is null.");

        if (value < 0 || value >= MAX_VALUE)
            throw new IllegalArgumentException("the value is out of range.");

        position_ = position;
        relation_ = relation;
        type_     = type ;
        value_    = value;
    }

    /**
     * Returns the position of the feature.
     * @return position
     */
    public POSITION getPosition(){
        return position_ ;
    }

    /**
     * Returns the relation of the feature
     * @return relation
     */
    public RELATION getRelation(){
        return relation_;
    }

    /**
     * Returns the type of feature
     * @return type
     */
    public TYPE getType(){
        return type_ ;
    }

    /**
     * Returns the feature's value.
     * @return value
     */
    public int getValue(){
        return value_ ;
    }

    @Override
    public int encode() {
        return (position_.getID() | relation_.getID()  | type_.getID()  | value_ ) ;
    }

    @Override
    public void decode(int code) {
        position_ = POSITION.decode(code);
        relation_ = RELATION.decode(code);
        type_     = TYPE.decode(code);
        value_    = code & VALUE_MASK ;
    }

}
