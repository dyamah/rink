package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;

public class BitVector implements Serializable{

    private static final long serialVersionUID = -7366643082593603246L;

    /** the base number */
    private static int BASE = 64;

    /** the array for bit masks */
    private static long[] BIT_MASK = {
                         1L << (64 -  1), 1L << (64 -  2), 1L << (64 -  3), 1L << (64 -  4), 1L << (64 -  5), 1L << (64 -  6), 1L << (64 -  7), 1L << (64 -  8), 1L << (64 -  9),
        1L << (64 - 10), 1L << (64 - 11), 1L << (64 - 12), 1L << (64 - 13), 1L << (64 - 14), 1L << (64 - 15), 1L << (64 - 16), 1L << (64 - 17), 1L << (64 - 18), 1L << (64 - 19),
        1L << (64 - 20), 1L << (64 - 21), 1L << (64 - 22), 1L << (64 - 23), 1L << (64 - 24), 1L << (64 - 25), 1L << (64 - 26), 1L << (64 - 27), 1L << (64 - 28), 1L << (64 - 29),
        1L << (64 - 30), 1L << (64 - 31), 1L << (64 - 32), 1L << (64 - 33), 1L << (64 - 34), 1L << (64 - 35), 1L << (64 - 36), 1L << (64 - 37), 1L << (64 - 38), 1L << (64 - 39),
        1L << (64 - 40), 1L << (64 - 41), 1L << (64 - 42), 1L << (64 - 43), 1L << (64 - 44), 1L << (64 - 45), 1L << (64 - 46), 1L << (64 - 47), 1L << (64 - 48), 1L << (64 - 49),
        1L << (64 - 50), 1L << (64 - 51), 1L << (64 - 52), 1L << (64 - 53), 1L << (64 - 54), 1L << (64 - 55), 1L << (64 - 56), 1L << (64 - 57), 1L << (64 - 58), 1L << (64 - 59),
        1L << (64 - 60), 1L << (64 - 61), 1L << (64 - 62), 1L << (64 - 63), 1L << (64 - 64)
    };

    /** the array for pop count */
    private static long[] POP_COUNT_MASK = {
                                       ((1L <<  1) - 1) << (64 -  1), ((1L <<  2) - 1) << (64 -  2), ((1L <<  3) - 1) << (64 -  3), ((1L <<  4) - 1) << (64 -  4), ((1L <<  5) - 1) << (64 -  5), ((1L <<  6) - 1) << (64 -  6), ((1L <<  7) - 1) << (64 -  7), ((1L <<  8) - 1) << (64 -  8), ((1L <<  9) - 1) << (64 -  9),
        ((1L << 10) - 1) << (64 - 10), ((1L << 11) - 1) << (64 - 11), ((1L << 12) - 1) << (64 - 12), ((1L << 13) - 1) << (64 - 13), ((1L << 14) - 1) << (64 - 14), ((1L << 15) - 1) << (64 - 15), ((1L << 16) - 1) << (64 - 16), ((1L << 17) - 1) << (64 - 17), ((1L << 18) - 1) << (64 - 18), ((1L << 19) - 1) << (64 - 19),
        ((1L << 20) - 1) << (64 - 20), ((1L << 21) - 1) << (64 - 21), ((1L << 22) - 1) << (64 - 22), ((1L << 23) - 1) << (64 - 23), ((1L << 24) - 1) << (64 - 24), ((1L << 25) - 1) << (64 - 25), ((1L << 26) - 1) << (64 - 26), ((1L << 27) - 1) << (64 - 27), ((1L << 28) - 1) << (64 - 28), ((1L << 29) - 1) << (64 - 29),
        ((1L << 30) - 1) << (64 - 30), ((1L << 31) - 1) << (64 - 31), ((1L << 32) - 1) << (64 - 32), ((1L << 33) - 1) << (64 - 33), ((1L << 34) - 1) << (64 - 34), ((1L << 35) - 1) << (64 - 35), ((1L << 36) - 1) << (64 - 36), ((1L << 37) - 1) << (64 - 37), ((1L << 38) - 1) << (64 - 38), ((1L << 39) - 1) << (64 - 39),
        ((1L << 40) - 1) << (64 - 40), ((1L << 41) - 1) << (64 - 41), ((1L << 42) - 1) << (64 - 42), ((1L << 43) - 1) << (64 - 43), ((1L << 44) - 1) << (64 - 44), ((1L << 45) - 1) << (64 - 45), ((1L << 46) - 1) << (64 - 46), ((1L << 47) - 1) << (64 - 47), ((1L << 48) - 1) << (64 - 48), ((1L << 49) - 1) << (64 - 49),
        ((1L << 50) - 1) << (64 - 50), ((1L << 51) - 1) << (64 - 51), ((1L << 52) - 1) << (64 - 52), ((1L << 53) - 1) << (64 - 53), ((1L << 54) - 1) << (64 - 54), ((1L << 55) - 1) << (64 - 55), ((1L << 56) - 1) << (64 - 56), ((1L << 57) - 1) << (64 - 57), ((1L << 58) - 1) << (64 - 58), ((1L << 59) - 1) << (64 - 59),
        ((1L << 60) - 1) << (64 - 60), ((1L << 61) - 1) << (64 - 61), ((1L << 62) - 1) << (64 - 62), ((1L << 63) - 1) << (64 - 63), ~0L
    };

    /** the table for pop count */
    static private final int[] POP_COUNT_TABLE;
    static {
        POP_COUNT_TABLE = new int[1 << 16];
        for(int n = 0; n < POP_COUNT_TABLE.length; n++){
            int popcount = 0 ;
            for(int i = 0; i < BASE; i++){
                if ((n & BIT_MASK[i]) == 0)
                    continue;
                popcount ++ ;
            }
            POP_COUNT_TABLE[n] = popcount;
        }
    }

    static final int popCount(long x){
        int popcount = POP_COUNT_TABLE[(int)(x & 0xffff)];
        x = x >>> 16;
        popcount += POP_COUNT_TABLE[(int)(x & 0xffff)];
        x = x >>> 16;
        popcount += POP_COUNT_TABLE[(int)(x & 0xffff)];
        x = x >>> 16;
        popcount += POP_COUNT_TABLE[(int)(x & 0xffff)];
        return popcount;
    }

    /** the array for index */
    private long[] bit_ ;

    /** the array for the number of elements */
    private int[] num_elements_ ;

    /** the array for elements */
    private double[] elements_;

    /**
     * Constructor
     * @param array the original array.
     * @throws IllegalArgumentException if the array is null.
     */
    public BitVector(double[] array){
        if (array == null)
            throw new IllegalArgumentException("the array is null.");

        int max = 0 ;
        int nonezero = 0;
        for(int i = 0 ; i < array.length; i++){
            if (array[i] == 0.0)
                continue;
            max = i;
            nonezero ++ ;
        }


        bit_ = new long[max / BASE + 1];
        num_elements_ = new int[bit_.length + 1];
        elements_ = new double[nonezero];
        int num = 0;
        for(int i = 0; i < array.length; i++){
            double v = array[i];
            if (v == 0.0)
                continue;
            num ++;
            set(i, num, v);
        }

        int start = 0;
        for(int i = 0 ; i < num_elements_.length; i++){
            int k = num_elements_[i];
            if (k == 0){
                num_elements_[i] = start ;
            } else {
                start = k ;
            }
        }
    }

    private void readObject(ObjectInputStream in) throws IOException{
        int size = in.readInt();
        bit_ = new long[size];
        for(int i = 0 ; i < size ; i++)
            bit_[i] = in.readLong();

        size = in.readInt();
        num_elements_ = new int[size];
        for(int i = 0 ; i < size ; i++)
            num_elements_[i] = in.readInt();

        size = in.readInt();
        elements_ = new double[size];
        for(int i = 0 ; i < size ; i++)
            elements_[i] = in.readDouble();
    }

    private void writeObject(ObjectOutputStream out) throws IOException{
        out.writeInt(bit_.length);
        for(int i = 0 ; i < bit_.length ; i++)
            out.writeLong(bit_[i]);

        out.writeInt(num_elements_.length);
        for(int i = 0 ; i < num_elements_.length ; i++)
            out.writeInt(num_elements_[i]);

        out.writeInt(elements_.length);
        for(int i = 0 ; i < elements_.length ; i++)
            out.writeDouble(elements_[i]);
    }

    /**
     * Returns the number of non-zero elements
     * @return the number of non zero elements
     */
    public int size(){
        int s = 0;
        for(double v : elements_)
            if (v != 0)
                s++;
        return s;
    }

    /**
     * Returns the value of squared L2 norm
     * @return get the value of squared L2 norm
     */
    public double squaredL2norm(){
        double sq = 0.0;
        for(double v : elements_)
            sq += v * v ;
        return sq;
    }

    /**
     * Sets the element.
     * @param id the ID of the elements.
     * @param num the number of non zero elements until the ID.
     * @param value the value of the element.
     */
    private void set(int id, int num, double value){
        int x = id / BASE;
        int y = id % BASE;

        bit_[x] |= BIT_MASK[y];
        if (num > 0)
            elements_[num-1] = value;
        num_elements_[x+1] = num;
    }

    /**
     * Returns the value corresponding the index
     * @param index the index
     * @return the value corresponding the index.
     * Note that if the index is out of range, this method returns 0.0.
     */
    public double get(int index){
        if (index < 0)
            return 0 ;
        int x = index / BASE;
        int y = index % BASE;
        if (x >= bit_.length)
            return 0;

        long m = bit_[x];
        if ((m & BIT_MASK[y]) == 0)
            return 0;
        int n = num_elements_[x] + popCount(m & POP_COUNT_MASK[y]) - 1;
        return elements_[n];
    }

    /**
     * Updates  all elements to the value of z times.
     * @param z the value.
     */
    void product(double z){
        for(int i = 0 ; i < elements_.length; i++){
            elements_[i] *= z;
        }
    }
    /**
     * Sets the value of the index.
     * @param index the index of vector.
     * @param value the value of the index.
     * @throws IllegalArgumentException if the index is negative.
     */
    void set(int index, double value){
        if (index < 0)
            throw new IllegalArgumentException("the index is negative.");

        int x = index / BASE;
        int y = index % BASE;
        if (x >= bit_.length){
            int s = bit_.length;
            int k = 0;
            if (s > 0)
                k = num_elements_[s];

            bit_ = Arrays.copyOf(bit_, x+1);
            num_elements_ = Arrays.copyOf(num_elements_, x+2);
            for(; s <= x + 1; s++){
                if (s <= x)
                    bit_[s] = 0 ;
                num_elements_[s] = k;
            }
        }

        long m = bit_[x];
        if ((m & BIT_MASK[y]) == 0){

            bit_[x] |= BIT_MASK[y];
            m = bit_[x];
            for(int s = x + 1; s < num_elements_.length; s++)
                num_elements_[s] ++;

            int n = num_elements_[x] + popCount(m & POP_COUNT_MASK[y]) - 1;
            elements_ = Arrays.copyOf(elements_, elements_.length + 1);

            for(int i = n; i < elements_.length; i++ ){
                double v = elements_[i];
                elements_[i] = value;
                value = v ;
            }
        } else {
            int n = num_elements_[x] + popCount(m & POP_COUNT_MASK[y]) - 1;
            elements_[n] = value;
        }
    }
}
