package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;

import jp.gr.java_conf.dyama.rink.ml.svm.KernelFunction;

import org.junit.Test;

public class KernelFunctionTest {

    static final double E = 0.0000000001;
    @Test
    public void testLinearKernel() {
        KernelFunction kernel = new KernelFunction.Linear();
        assertEquals(  0.0, kernel.getValue(1.0, 0.0,   1.0),   E);
        assertEquals(0.248, kernel.getValue(1.0, 0.248, 1.0), E);
    }

    @Test
    public void testPolynomialKernel() {

        { // [0]
            KernelFunction kernel = new KernelFunction.Polynomial(2, 1.1, 2.0);
            assertEquals(  4.0, kernel.getValue(0,  0, 0), E);
            assertEquals(169.0, kernel.getValue(0, 10, 0), E);
        }

        { // [1]
            KernelFunction kernel = new KernelFunction.Polynomial(1, 1.1, 2.0);
            assertEquals( 2.0, kernel.getValue(1,  0, 1), E);
            assertEquals(13.0, kernel.getValue(1, 10, 1), E);
        }

        {  // [2]
            try {
                new KernelFunction.Polynomial(0, 1.1, 2.0);
                fail("");
            } catch (IllegalArgumentException e){
                assertEquals("the degree is less than 1.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
        }

        {
            KernelFunction kernel = new KernelFunction.Polynomial(3, 3.0, 2.0);
            assertEquals(  8.0, kernel.getValue(1, 0.0, 1), E);
            assertEquals(125.0, kernel.getValue(1, 1.0, 1), E);
        }

        {
            KernelFunction kernel = new KernelFunction.Polynomial(4, 1.1, 2.2);
            assertEquals(     23.4256, kernel.getValue(2, 0.0, 2), E);
            assertEquals(844.02451441, kernel.getValue(2, 2.9, 1), E);
        }

    }

    @Test
    public void testRBFKernel() {
        {
            KernelFunction kernel = new KernelFunction.RBF(0.0);
            assertEquals(  1.0, kernel.getValue(1, 0.0, 2),   E);
            assertEquals(  1.0, kernel.getValue(1, 1.1, 2),   E);
        }

        {
            KernelFunction kernel = new KernelFunction.RBF(1.2);
            assertEquals(0.00022486732417884827, kernel.getValue(3, 0.0, 4),   E);
            assertEquals(0.00315111159844444055, kernel.getValue(3, 1.1, 4),   E);
        }
    }

    @Test
    public void testSigmoidKernel() {
        {
            KernelFunction kernel = new KernelFunction.Sigmoid(0.0, 0.0);
            assertEquals(Math.tanh(0.0 * 0.0 + 0.0), kernel.getValue(0, 0.0, 1),   E);
            assertEquals(Math.tanh(0.0 * 1.1 + 0.0), kernel.getValue(0, 1.1, 1),   E);
        }

        {
            KernelFunction kernel = new KernelFunction.Sigmoid(1.2, 2.3);
            assertEquals(Math.tanh(1.2 * 0.0 + 2.3), kernel.getValue(2, 0.0, 2),   E);
            assertEquals(Math.tanh(1.2 * 1.1 + 2.3), kernel.getValue(9, 1.1, 9),   E);
        }
    }

    @Test
    public void testSerializatio() throws IOException, ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        File tmpfile = File.createTempFile("KernelFunctionTest", ".tmp");
        tmpfile.deleteOnExit();

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            KernelFunction.Linear src = new KernelFunction.Linear();
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            KernelFunction.Linear dist = (KernelFunction.Linear) in.readObject();
            assertEquals(3.0, dist.getValue(9, 3, 10), E);
            in.close();
        }

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            KernelFunction.Polynomial src = new KernelFunction.Polynomial(4, 1.0, 2.0);
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            KernelFunction.Polynomial dist = (KernelFunction.Polynomial) in.readObject();
            assertEquals(src.getValue(1, 2.2, 1), dist.getValue(1, 2.2, 1), E);
            assertEquals(src.getValue(1, 2.7, 1), dist.getValue(1, 2.7, 1), E);
            in.close();
        }

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            KernelFunction.Polynomial src = new KernelFunction.Polynomial(1, 1.0, 2.0);
            Field degree = KernelFunction.Polynomial.class.getDeclaredField("degree_");
            degree.setAccessible(true);
            degree.setInt(src, 0);
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));

            try {
                in.readObject();
                fail("");
            } catch (InvalidObjectException e){
                assertEquals("the degree is less than 1.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            in.close();
        }

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            KernelFunction.RBF src = new KernelFunction.RBF(0.3);
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            KernelFunction.RBF dist = (KernelFunction.RBF) in.readObject();
            assertEquals(src.getValue(11, 2.2, 3), dist.getValue(11, 2.2, 3), E);
            assertEquals(src.getValue(11, 2.7, 3), dist.getValue(11, 2.7, 3), E);
            in.close();
        }

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            KernelFunction.Sigmoid src = new KernelFunction.Sigmoid(1.1, 2.2);
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            KernelFunction.Sigmoid dist = (KernelFunction.Sigmoid) in.readObject();
            assertEquals(src.getValue(1, 2.2, 1), dist.getValue(1, 2.2, 1), E);
            assertEquals(src.getValue(1, 2.7, 1), dist.getValue(1, 2.7, 1), E);
            in.close();
        }


    }

}
