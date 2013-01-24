package jp.gr.java_conf.dyama.rink.parser.core;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import jp.gr.java_conf.dyama.rink.parser.core.BitVector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class BitVectorTest {
    static final double E = 0.000000001;
    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testBitVector() {
        try {
            new BitVector(null);
            fail("");
        } catch (IllegalArgumentException e) {
            assertEquals("the array is null.", e.getMessage());
        } catch (Exception e) {
            fail("");
        }

        {
            double[] v = {0,0, 0,0, 0,0};
            BitVector bv = new BitVector(v);
            assertEquals(0, bv.size());
            assertEquals(0.0, bv.squaredL2norm(), E);
        }

        {
            double[] v = {};
            BitVector bv = new BitVector(v);
            assertEquals(0, bv.size());
            assertEquals(0.0, bv.squaredL2norm(), E);
        }


    }

    @Test
    public void testSize() {
        {
            double[] v = {0, 0, 0, 0, 0, 0};
            BitVector bv = new BitVector(v);
            assertEquals(0, bv.size());
        }
        {
            double[] v = {1.0, 0, 3.0, 0, 0, 6.0};
            BitVector bv = new BitVector(v);
            assertEquals(3, bv.size());
        }

        {
            double[] v = {0.0, 0, 3.0, 0, 0, 0.00000000001};
            BitVector bv = new BitVector(v);
            assertEquals(2, bv.size());
        }
    }

    @Test
    public void testSquaredL2norm() {
        {
            double[] v = {0, 0, 0, 0, 0, 0};
            BitVector bv = new BitVector(v);
            assertEquals(0.0, bv.squaredL2norm(), E);
        }
        {
            double[] v = {1.0, 0, 3.0, 0, 0, 6.0};
            BitVector bv = new BitVector(v);
            assertEquals(46.0, bv.squaredL2norm(), E);
        }

        {
            double[] v = {0.0, 0, 3.0, 0, 0, -1.0};
            BitVector bv = new BitVector(v);
            assertEquals(10.0, bv.squaredL2norm(), E);
        }
    }

    @Test
    public void testPopCount(){

        assertEquals( 1, BitVector.popCount(1L));
        assertEquals( 4, BitVector.popCount(0xfL));
        assertEquals( 8, BitVector.popCount(0xffL));
        assertEquals(12, BitVector.popCount(0xfffL));
        assertEquals(16, BitVector.popCount(0xffffL));
        assertEquals(16, BitVector.popCount(0xffff0000L));
        assertEquals( 1, BitVector.popCount(0x1L));
        assertEquals( 2, BitVector.popCount(0x11L));
        assertEquals( 3, BitVector.popCount(0x111L));
        assertEquals(14, BitVector.popCount(0xff444444L));
    }

    @Test
    public void testGet() {
        {
            double[] v = {0, 0, 0, 0, 0, 0};
            BitVector bv = new BitVector(v);
            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(0.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(0.0, bv.get( 4), E);
            assertEquals(0.0, bv.get( 5), E);
        }
        {
            double[] v = {1.0, 0, 3.0, 0, 0, 6.0};
            BitVector bv = new BitVector(v);
            assertEquals(0.0, bv.get(-1), E);
            assertEquals(1.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(3.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(0.0, bv.get( 4), E);
            assertEquals(6.0, bv.get( 5), E);
            assertEquals(0.0, bv.get( 6), E);
            assertEquals(0.0, bv.get( 7), E);
        }

        {
            double[] v = new double[1024];
            for(int i = 0; i < v.length; i++)
                v[i] = (double) (i+1) ;

            BitVector bv = new BitVector(v);
            assertEquals(1024, bv.size());

            for(int i = 0; i < bv.size(); i++)
                assertEquals((double)(i+1), bv.get(i), E);
        }

        {
            double[] v = new double[130];
            v[126] = 127.0;
            v[127] = 128.0;
            v[128] = 129.0;

            BitVector bv = new BitVector(v);
            assertEquals(3, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(128.0, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(129.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }
        }
    }



    @Test
    public void testSet() {
        {
            double[] v = {0, 0, 0, 0, 0, 0};
            BitVector bv = new BitVector(v);
            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(0.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(0.0, bv.get( 4), E);
            assertEquals(0.0, bv.get( 5), E);

            try {
                bv.set(-1, 1);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the index is negative.", e.getMessage());
            } catch (Exception e){
                fail("");
            }
            bv.set(0, 0);
            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(0.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(0.0, bv.get( 4), E);
            assertEquals(0.0, bv.get( 5), E);

            bv.set(4, 4);
            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(0.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(4.0, bv.get( 4), E);
            assertEquals(0.0, bv.get( 5), E);
        }

        {
            int m = 65;
            double[] v = new double[m];

            assertEquals(65, m);
            for(int i = 0; i < m; i++)
                v[i] = 0;

            BitVector bv = new BitVector(v);
            assertEquals(0, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals(0, bv.get(i), E);

            for(int i = 0; i < m; i++)
                bv.set(i, (double)i+1);

            assertEquals(65, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals((double)i+1, bv.get(i), E);
        }
        {
            int m = 1024;
            double[] v = new double[m];

            assertEquals(1024, m);
            for(int i = 0; i < m; i++)
                v[i] = 0;

            BitVector bv = new BitVector(v);
            assertEquals(0, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals(0, bv.get(i), E);

            for(int i = 0; i < m; i++)
                bv.set(i, (double)i+1);

            assertEquals(1024, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals((double)i+1, bv.get(i), E);
        }

        {
            int m = 256;
            double[] v = new double[0];

            BitVector bv = new BitVector(v);
            assertEquals(0, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals(0, bv.get(i), E);

            for(int i = 0; i < m; i++)
                bv.set(i, (double)i+1);

            assertEquals(256, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals((double)i+1, bv.get(i), E);

            for(int i = 0; i < m; i++){
                bv.set(i, 0);
                assertEquals(0.0, bv.get(i), E);
                assertEquals(256 - i - 1, bv.size());
            }

            assertEquals(0, bv.size());

            for(int i = 0; i < m; i++)
                assertEquals(0, bv.get(i), E);

            for(int i = 0; i < m; i++)
                if (i % 3 == 0)
                    bv.set(i, (double) i+1);
            assertEquals(256 / 3 + 1, bv.size());

            for(int i = 0; i < m; i++){
                if (i % 3 == 0){
                    assertEquals((double) i+1, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }

            }

        }


        {
            double[] v = new double[130];
            v[126] = 127.0;
            v[127] = 128.0;
            v[128] = 129.0;

            BitVector bv = new BitVector(v);
            assertEquals(3, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(128.0, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(129.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }

            bv.set(0, 0);
            assertEquals(3, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(128.0, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(129.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }

            bv.set(0, 1.0);
            assertEquals(4, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 0){
                    assertEquals(1.0, bv.get(i), E);
                } else if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(128.0, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(129.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }

            bv.set(0, 0.0);
            assertEquals(3, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 0){
                    assertEquals(0.0, bv.get(i), E);
                } else if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(128.0, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(129.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }

            bv.set(1, 1.2);
            bv.set(127, 0);
            assertEquals(3, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 1){
                    assertEquals(1.2, bv.get(i), E);
                } else if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(0.0, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(129.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }


            bv.set(127, 1.3);
            bv.set(128, 0);
            assertEquals(3, bv.size());

            for(int i = 0; i < 130; i++){
                if (i == 1){
                    assertEquals(1.2, bv.get(i), E);
                } else if (i == 126){
                    assertEquals(127.0, bv.get(i), E);
                } else if (i == 127){
                    assertEquals(1.3, bv.get(i), E);
                } else if (i == 128){
                    assertEquals(0.0, bv.get(i), E);
                } else {
                    assertEquals(0.0, bv.get(i), E);
                }
            }

        }
    }

    @Test
    public void testProduct() {
        {
            double[] v = {0, 0, 0, 0, 0, 0};
            BitVector bv = new BitVector(v);

            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(0.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(0.0, bv.get( 4), E);
            assertEquals(0.0, bv.get( 5), E);

            bv.product(1.11);

            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(0.0, bv.get( 1), E);
            assertEquals(0.0, bv.get( 2), E);
            assertEquals(0.0, bv.get( 3), E);
            assertEquals(0.0, bv.get( 4), E);
            assertEquals(0.0, bv.get( 5), E);
        }

        {
            double[] v = {0, 1.0, 2.0, 3.0, 4.0, 5.0};
            BitVector bv = new BitVector(v);

            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(1.0, bv.get( 1), E);
            assertEquals(2.0, bv.get( 2), E);
            assertEquals(3.0, bv.get( 3), E);
            assertEquals(4.0, bv.get( 4), E);
            assertEquals(5.0, bv.get( 5), E);

            bv.product(2.0);

            assertEquals(0.0, bv.get(-1), E);
            assertEquals(0.0, bv.get( 0), E);
            assertEquals(2.0, bv.get( 1), E);
            assertEquals(4.0, bv.get( 2), E);
            assertEquals(6.0, bv.get( 3), E);
            assertEquals(8.0, bv.get( 4), E);
            assertEquals(10.0, bv.get( 5), E);
        }
    }

    @Test
    public void testSerialize() throws IOException, ClassNotFoundException{
        File tmpfile = File.createTempFile("BitVectorTest", ".tmp");
        tmpfile.deleteOnExit();
        {

            double[] v = new double[1000];
            for(int i = 0; i < 1000; i++)
                v[i] = i + 1;

            BitVector bv = new BitVector(v);
            assertEquals(1000, bv.size());
            for(int i = 0; i < 1000; i++)
                assertEquals(i + 1, bv.get(i), E);

            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile) );
            out.writeObject(bv);
            out.close();
        }

        {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            BitVector bv = (BitVector)in.readObject();
            assertEquals(1000, bv.size());
            for(int i = 0; i < 1000; i++)
                assertEquals(i + 1, bv.get(i), E);
            in.close();
        }


    }

}
