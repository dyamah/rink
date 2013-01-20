package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters.KernelType;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters.ParametersImpl;

import org.junit.Test;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;


public class ParametersTest {
    static final double E = 0.00000001;

    /**
     * check default parameter
     */
    @Test
    public void testParametersImpl() {

        Parameters params = new Parameters.ParametersImpl();
        assertEquals(100.0, params.getCacheSize(), E);
        assertEquals(3, params.getDegree());
        assertEquals(0.0, params.getGamma(), E);
        assertEquals(0.0, params.getCoef0(), E);
        assertEquals(1.0, params.getC(), E);
        assertEquals(0.001, params.getEpsilon(), E);
        assertEquals(KernelType.RBF, params.getKernelType());
    }

    @Test
    public void testCopy(){
        {
            assertEquals(true, Parameters.ParametersImpl.copy(null)  == null);
        }

        {
            Parameters params = new Parameters.ParametersImpl();
            Parameters clone  =  Parameters.ParametersImpl.copy(params);
            assertEquals(true, params != clone );

            assertEquals(100.0, params.getCacheSize(), E);
            assertEquals(3, params.getDegree());
            assertEquals(0.0, params.getGamma(), E);
            assertEquals(0.0, params.getCoef0(), E);
            assertEquals(1.0, params.getC(), E);
            assertEquals(0.001, params.getEpsilon(), E);
            assertEquals(KernelType.RBF, params.getKernelType());

            assertEquals(100.0, clone.getCacheSize(), E);
            assertEquals(3, clone.getDegree());
            assertEquals(0.0, clone.getGamma(), E);
            assertEquals(0.0, clone.getCoef0(), E);
            assertEquals(1.0, clone.getC(), E);
            assertEquals(0.001, clone.getEpsilon(), E);
            assertEquals(KernelType.RBF, clone.getKernelType());
        }

        {
            Parameters params = new Parameters.ParametersImpl();
            params.setCacheSize(31.1);
            params.setDegree(2);
            params.setGamma(0.1);
            params.setCoef0(0.2);
            params.setC(3.0);
            params.setEpsilon(0.301);
            params.setKernelType(KernelType.SIGMOID);

            assertEquals(31.1, params.getCacheSize(), E);
            assertEquals(2, params.getDegree());
            assertEquals(0.1, params.getGamma(), E);
            assertEquals(0.2, params.getCoef0(), E);
            assertEquals(3.0, params.getC(), E);
            assertEquals(0.301, params.getEpsilon(), E);
            assertEquals(KernelType.SIGMOID, params.getKernelType());

            Parameters clone  =  Parameters.ParametersImpl.copy(params);
            assertEquals(true, params != clone );

            assertEquals(31.1, params.getCacheSize(), E);
            assertEquals(2, params.getDegree());
            assertEquals(0.1, params.getGamma(), E);
            assertEquals(0.2, params.getCoef0(), E);
            assertEquals(3.0, params.getC(), E);
            assertEquals(0.301, params.getEpsilon(), E);
            assertEquals(KernelType.SIGMOID, params.getKernelType());

            assertEquals(31.1, clone.getCacheSize(), E);
            assertEquals(2, clone.getDegree());
            assertEquals(0.1, clone.getGamma(), E);
            assertEquals(0.2, clone.getCoef0(), E);
            assertEquals(3.0, clone.getC(), E);
            assertEquals(0.301, clone.getEpsilon(), E);
            assertEquals(KernelType.SIGMOID, clone.getKernelType());
        }


    }

    @Test
    public void testSetCacheSize() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(100.0, params.getCacheSize(), E);
        params.setCacheSize(0.0);
        assertEquals(30.0, params.getCacheSize(), E);
        params.setCacheSize(29.9);
        assertEquals(30.0, params.getCacheSize(), E);
        params.setCacheSize(30.0);
        assertEquals(30.0, params.getCacheSize(), E);
        params.setCacheSize(31.0);
        assertEquals(31.0, params.getCacheSize(), E);
    }

    @Test
    public void testSetDegree() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(3, params.getDegree());

        params.setDegree(2);
        assertEquals(2, params.getDegree());
        params.setDegree(1);
        assertEquals(1, params.getDegree());

        try {
            params.setDegree(0);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("the degree is less than 1.", e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
            fail("");
        }

        params.setDegree(7);
        assertEquals(7, params.getDegree());
    }

    @Test
    public void testSetGamma() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(0.0, params.getGamma(), E);

        params.setGamma(1.11);
        assertEquals(1.11, params.getGamma(), E);

        params.setGamma(-1.12);
        assertEquals(-1.12, params.getGamma(), E);

        params.setGamma(9.002);
        assertEquals(9.002, params.getGamma(), E);
    }

    @Test
    public void testSetCoef0() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(0.0, params.getCoef0(), E);

        params.setCoef0(2.34);
        assertEquals(2.34, params.getCoef0(), E);

        params.setCoef0(-1.34);
        assertEquals(-1.34, params.getCoef0(), E);

        params.setCoef0(9.99);
        assertEquals(9.99, params.getCoef0(), E);
    }

    @Test
    public void testSetC() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(1.0, params.getC(), E);

        params.setC(1.9);
        assertEquals(1.9, params.getC(), E);

        params.setC(-1.9);
        assertEquals(-1.9, params.getC(), E);

        params.setC(0);
        assertEquals(0.0, params.getC(), E);

    }

    @Test
    public void testSetEpsilon() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(0.001, params.getEpsilon(), E);

        params.setEpsilon(0.00001);
        assertEquals(0.00001, params.getEpsilon(), E);

        params.setEpsilon(11.0201);
        assertEquals(11.0201, params.getEpsilon(), E);
    }

    @Test
    public void testSetKernelType() {
        Parameters params = new Parameters.ParametersImpl();
        assertEquals(KernelType.RBF, params.getKernelType());

        params.setKernelType(KernelType.LINEAR);
        assertEquals(KernelType.LINEAR, params.getKernelType());

        params.setKernelType(KernelType.POLYNOMIAL);
        assertEquals(KernelType.POLYNOMIAL, params.getKernelType());

        params.setKernelType(KernelType.RBF);
        assertEquals(KernelType.RBF, params.getKernelType());

        params.setKernelType(KernelType.SIGMOID);
        assertEquals(KernelType.SIGMOID, params.getKernelType());
    }

    @Test
    public void testSerialization() throws IOException, ClassNotFoundException, SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
        File tmpfile = File.createTempFile("ParametersTest", ".tmp");
        tmpfile.deleteOnExit();

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            Parameters.ParametersImpl src =  new Parameters.ParametersImpl();
            src.setC(1.11);
            src.setCacheSize(32.3);
            src.setCoef0(4.44);
            src.setDegree(5);
            src.setEpsilon(6.12);
            src.setGamma(1.91);
            src.setKernelType(Parameters.KernelType.SIGMOID);
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            Parameters.ParametersImpl dist = (ParametersImpl) in.readObject();
            assertEquals(1.11, dist.getC(), E);
            assertEquals(32.3, dist.getCacheSize(), E);
            assertEquals(4.44, dist.getCoef0(), E);
            assertEquals(5, dist.getDegree());
            assertEquals(6.12, dist.getEpsilon(), E);
            assertEquals(1.91, dist.getGamma(), E);
            assertEquals(Parameters.KernelType.SIGMOID, dist.getKernelType());
            in.close();
        }

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            Parameters.ParametersImpl src =  new Parameters.ParametersImpl();
            src.setC(1.11);
            src.setCacheSize(32.3);
            src.setCoef0(4.44);
            src.setDegree(5);
            src.setEpsilon(6.12);
            src.setGamma(1.91);
            src.setKernelType(Parameters.KernelType.SIGMOID);

            Field cache = Parameters.ParametersImpl.class.getDeclaredField("cache_");
            cache.setAccessible(true);
            cache.setDouble(src, 29.97);

            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));
            try {
                in.readObject();
                fail("");
            } catch (InvalidObjectException e) {
                assertEquals("the cache size is invalid.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            in.close();
        }

        {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(tmpfile));
            Parameters.ParametersImpl src =  new Parameters.ParametersImpl();
            src.setC(1.11);
            src.setCacheSize(32.3);
            src.setCoef0(4.44);
            src.setDegree(5);
            src.setEpsilon(6.12);
            src.setGamma(1.91);
            src.setKernelType(Parameters.KernelType.SIGMOID);

            Field degree = Parameters.ParametersImpl.class.getDeclaredField("degree_");
            degree.setAccessible(true);
            degree.setInt(src, 0);
            out.writeObject(src);
            out.close();

            ObjectInputStream in = new ObjectInputStream(new FileInputStream(tmpfile));

            try {
                in.readObject();
                fail("");
            } catch (InvalidObjectException e) {
                assertEquals("the degree is less than 1.", e.getMessage());
            } catch (Exception e){
                e.printStackTrace();
                fail("");
            }
            in.close();
        }


    }
}
