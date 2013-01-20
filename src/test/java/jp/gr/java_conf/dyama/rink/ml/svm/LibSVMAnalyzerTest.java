package jp.gr.java_conf.dyama.rink.ml.svm;

import static org.junit.Assert.*;

import java.util.Map;

import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl;
import jp.gr.java_conf.dyama.rink.ml.svm.LibSVMAnalyzer;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.HyperPlane;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters.KernelType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import java.lang.reflect.Field;


public class LibSVMAnalyzerTest {
    static final double E = 0.00000001;
    static final libsvm.svm_problem train_ ;
    static final libsvm.svm_model default_model_ ;
    libsvm.svm_parameter default_params_ ;

    static libsvm.svm_problem clone(libsvm.svm_problem prob){
        libsvm.svm_problem clone = new libsvm.svm_problem();
        clone.l = prob.l ;
        clone.x = prob.x.clone();
        clone.y = prob.y.clone();
        return clone;
    }
    static {
        {
            train_ = new libsvm.svm_problem();
            train_.l = 10 ;
            train_.y = new double[10];
            train_.y[ 0] = 2; train_.y[ 1] = 1; train_.y[ 2] = 1; train_.y[ 3] = 1; train_.y[ 4] = 2; train_.y[ 5] = 2; train_.y[ 6] = 2; train_.y[ 7] = 2; train_.y[ 8] = 3; train_.y[ 9] = 3;


            train_.x = new libsvm.svm_node[10][] ;

            train_.x[0] = new libsvm.svm_node[2];
            train_.x[1] = new libsvm.svm_node[3];
            train_.x[2] = new libsvm.svm_node[7];
            train_.x[3] = new libsvm.svm_node[1];
            train_.x[4] = new libsvm.svm_node[2];
            train_.x[5] = new libsvm.svm_node[3];
            train_.x[6] = new libsvm.svm_node[1];
            train_.x[7] = new libsvm.svm_node[6];
            train_.x[8] = new libsvm.svm_node[4];
            train_.x[9] = new libsvm.svm_node[2];

            for(int l = 0; l < train_.x.length; l++  )
                for(int i = 0; i < train_.x[l].length; i++)
                    train_.x[l][i] = new libsvm.svm_node();

            for(int i = 0; i < train_.x.length; i++){
                for(int j = 0 ; j <  train_.x[i].length; j++){
                    libsvm.svm_node f = train_.x[i][j];
                    f.index = 10 * i + j + 1;
                    f.value = 1.0 ;
                }
            }
        }

        { // valid model
            libsvm.svm_problem train = clone(train_);
            libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
            params.degree = 2 ;
            params.gamma  = 1.0 ;
            params.coef0  = 1.0 ;
            params.kernel_type = libsvm.svm_parameter.POLY;

            default_model_ = libsvm.svm.svm_train(train, params);
            assertEquals(true, default_model_ != null);
        }

    }

    @Before
    public void setUp() throws Exception {
        default_params_ = LibSVMAnalyzer.makeDefaultParameters();
        default_params_.degree = 2 ;
        default_params_.gamma  = 1.0 ;
        default_params_.coef0  = 1.0 ;
        default_params_.kernel_type = libsvm.svm_parameter.POLY;
        default_model_.param = default_params_ ;
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testAnalyzeModel() {
        {
            try {
                LibSVMAnalyzer.analyzeModel(null);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("Invalid Model: model is null.", e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                fail("");
            }

            libsvm.svm_problem train = new libsvm.svm_problem();
            train.l = 2 ;
            train.y = new double[2];
            train.y[0] = 1.0 ;
            train.y[1] = -1.0;
            train.x = new libsvm.svm_node[2][];
            train.x[0] = new libsvm.svm_node[1]; train.x[0][0] = new libsvm.svm_node(); train.x[0][0].value = 1.1; train.x[0][0].index = 1;
            train.x[1] = new libsvm.svm_node[1]; train.x[1][0] = new libsvm.svm_node(); train.x[1][0].value = 1.2; train.x[1][0].index = 2;

            libsvm.svm_model model = libsvm.svm.svm_train(train, default_params_);
            try {
                LibSVMAnalyzer.analyzeModel(model);
                fail("");
            } catch (IllegalArgumentException e) {
                assertEquals("Real feature space is unsupported, (but it will be supported in future.", e.getMessage());
            } catch (Exception e) {
                fail("");
            }
        }

        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            ClassifierImpl.Arguments args = LibSVMAnalyzer.analyzeModel(model);
            assertNotNull(args);
            assertNotNull(args.features_);
            assertNotNull(args.hps_);
            assertNotNull(args.kernel_);
            assertNotNull(args.labels_);
            assertNotNull(args.params_);
            assertNotNull(args.svcoefs_);
        }

        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.param.kernel_type = libsvm.svm_parameter.LINEAR;
            ClassifierImpl.Arguments args = LibSVMAnalyzer.analyzeModel(model);

            assertNotNull(args);
            assertNotNull(args.features_);
            assertNotNull(args.hps_);
            assertNotNull(args.kernel_);
            assertNotNull(args.labels_);
            assertNotNull(args.params_);
            assertNotNull(args.svcoefs_);
        }

        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.param.kernel_type = libsvm.svm_parameter.POLY;
            ClassifierImpl.Arguments args = LibSVMAnalyzer.analyzeModel(model);

            assertNotNull(args);
            assertNotNull(args.features_);
            assertNotNull(args.hps_);
            assertNotNull(args.kernel_);
            assertNotNull(args.labels_);
            assertNotNull(args.params_);
            assertNotNull(args.svcoefs_);
        }

        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.param.kernel_type = libsvm.svm_parameter.RBF;
            ClassifierImpl.Arguments args = LibSVMAnalyzer.analyzeModel(model);

            assertNotNull(args);
            assertNotNull(args.features_);
            assertNotNull(args.hps_);
            assertNotNull(args.kernel_);
            assertNotNull(args.labels_);
            assertNotNull(args.params_);
            assertNotNull(args.svcoefs_);
        }

        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.param.kernel_type = libsvm.svm_parameter.SIGMOID;
            ClassifierImpl.Arguments args = LibSVMAnalyzer.analyzeModel(model);

            assertNotNull(args);
            assertNotNull(args.features_);
            assertNotNull(args.hps_);
            assertNotNull(args.kernel_);
            assertNotNull(args.labels_);
            assertNotNull(args.params_);
            assertNotNull(args.svcoefs_);

            model.param.kernel_type = libsvm.svm_parameter.POLY;
        }

    }

    @Test
    public void testCheckValidModel() {
        { // model is null
            assertEquals("Invalid Model: model is null.", LibSVMAnalyzer.checkValidModel(null));
        }

        { // default model is valid
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            assertEquals(null, LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.param == null
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.param = null ;
            assertEquals("Invalid Model: model.param is null.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.param.svm_type != C_SVC
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.param.svm_type = libsvm.svm_parameter.NU_SVR;
            assertEquals(false, model.param.svm_type == libsvm.svm_parameter.C_SVC);
            assertEquals("Invalid Model: model.param.sv_type is not C_SVC.", LibSVMAnalyzer.checkValidModel(model));
            default_params_.svm_type = libsvm.svm_parameter.C_SVC;
        }

        { // model.nr_class < 2
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.nr_class = 1;
            assertEquals("Invalid Model: model.nr_class is less than 2.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.label = null
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.label = null;
            assertEquals("Invalid Model: model.label is null.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.nr_class != model.label.length
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.nr_class = model.label.length + 1;
            assertEquals("Invalid Model: model.nr_class is not same to model.label.length.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.rho == null
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.rho = null ;
            assertEquals("Invalid Model: model.rho is null.", LibSVMAnalyzer.checkValidModel(model));
            model.rho = new double[2];
            assertEquals("Invalid Model: model.rho.length is not same to k * (k - 1) / 2.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.sv_coef.length != k-1
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            assertEquals(model.nr_class - 1, model.sv_coef.length );

            model.sv_coef = null ;
            assertEquals("Invalid Model: model.sv_coef is null.", LibSVMAnalyzer.checkValidModel(model));

            model.sv_coef = new double[model.nr_class + 1][];
            assertEquals("Invalid Model: model.sv_coef.length is not same to k-1.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // model.SV == null
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            model.SV = null;
            assertEquals("Invalid Model: model.SV is null.", LibSVMAnalyzer.checkValidModel(model));
            model.SV = new libsvm.svm_node[model.l + 5 ][];
            assertEquals("Invalid Model: model.SV.length is not same to model.l.", LibSVMAnalyzer.checkValidModel(model));
        }

        { // sum model.nSV[0..k-1] != model.l
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            int sum = 0 ;
            for(int k = 0 ; k < model.nSV.length; k++)
                sum += model.nSV[k];
            assertEquals(sum, model.l);
            model.nSV[0] -- ;
            assertEquals("Invalid Model: the sum of model.nSV[0..k-1] is not same to model.l.", LibSVMAnalyzer.checkValidModel(model));

            model.nSV = null;
            assertEquals("Invalid Model: model.nSV is null.", LibSVMAnalyzer.checkValidModel(model));
        }
    }

    @Test
    public void testAnalyzeKernelType() {
        libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
        assertEquals(KernelType.POLYNOMIAL, LibSVMAnalyzer.analyzeKernelType(model));
        model.param.kernel_type = libsvm.svm_parameter.LINEAR;
        assertEquals(KernelType.LINEAR, LibSVMAnalyzer.analyzeKernelType(model));

        model.param.kernel_type = libsvm.svm_parameter.POLY;
        assertEquals(KernelType.POLYNOMIAL, LibSVMAnalyzer.analyzeKernelType(model));

        model.param.kernel_type = libsvm.svm_parameter.RBF;
        assertEquals(KernelType.RBF, LibSVMAnalyzer.analyzeKernelType(model));

        model.param.kernel_type = libsvm.svm_parameter.SIGMOID;
        assertEquals(KernelType.SIGMOID, LibSVMAnalyzer.analyzeKernelType(model));

        model.param.kernel_type = -1024;
        assertEquals(false,  model.param.kernel_type == libsvm.svm_parameter.LINEAR);
        assertEquals(false,  model.param.kernel_type == libsvm.svm_parameter.POLY);
        assertEquals(false,  model.param.kernel_type == libsvm.svm_parameter.RBF);
        assertEquals(false,  model.param.kernel_type == libsvm.svm_parameter.SIGMOID);
        assertEquals(null, LibSVMAnalyzer.analyzeKernelType(model));
        model.param.kernel_type = libsvm.svm_parameter.POLY;

        try {
            LibSVMAnalyzer.analyzeKernelType(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("Invalid Model: model is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

    }

    @Test
    public void testAnalyzeFeatureSpace() {
        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            assertEquals(true, LibSVMAnalyzer.analyzeFeatureSpace(model));
        }

        {
            libsvm.svm_problem train = clone(train_);
            train.x[2][5].value = 1.1;
            libsvm.svm_model model = libsvm.svm.svm_train(train, default_params_);
            assertEquals(false, LibSVMAnalyzer.analyzeFeatureSpace(model));
        }

        try {
           LibSVMAnalyzer.analyzeFeatureSpace(null);
            fail("");
        } catch (IllegalArgumentException e){
            assertEquals("Invalid Model: model is null.", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            fail("");
        }

    }

    @Test
    public void testAnalyzeParameters() {
        {
            libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
            Parameters params = LibSVMAnalyzer.analyzeParameters(model);
            assertEquals(default_params_.cache_size, params.getCacheSize(), E);
            assertEquals(default_params_.degree, params.getDegree());
            assertEquals(default_params_.gamma, params.getGamma(), E);
            assertEquals(default_params_.coef0, params.getCoef0(), E);
            assertEquals(default_params_.C, params.getC(), E);
            assertEquals(default_params_.eps, params.getEpsilon(), E);
            assertEquals(KernelType.POLYNOMIAL, params.getKernelType());
        }

        {
            libsvm.svm_parameter libsvm_params = LibSVMAnalyzer.makeDefaultParameters();
            libsvm.svm_model model = libsvm.svm.svm_train(train_, libsvm_params);
            Parameters params = LibSVMAnalyzer.analyzeParameters(model);
            assertEquals(libsvm_params.cache_size, params.getCacheSize(), E);
            assertEquals(libsvm_params.degree, params.getDegree());
            assertEquals(libsvm_params.gamma, params.getGamma(), E);
            assertEquals(libsvm_params.coef0, params.getCoef0(), E);
            assertEquals(libsvm_params.C, params.getC(), E);
            assertEquals(libsvm_params.eps, params.getEpsilon(), E);
            assertEquals(KernelType.RBF, params.getKernelType());
        }

        try {
            LibSVMAnalyzer.analyzeParameters(null);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("Invalid Model: model is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }
    }

    @Test
    public void testAnalyzeLabelInfo() {
        libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);

        int[] labels = LibSVMAnalyzer.analyzeLabelInfo(model);
        assertEquals(3, labels.length);
        assertEquals(2, labels[0]);
        assertEquals(1, labels[1]);
        assertEquals(3, labels[2]);

        try {
            LibSVMAnalyzer.analyzeLabelInfo(null);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("Invalid Model: model is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }
    }

    @Test
    public void testAnalyzeHyperPlaneInfo() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        libsvm.svm_model model = libsvm.svm.svm_train(train_, default_params_);
        assertEquals(3, model.nr_class);
        HyperPlane[] hps = LibSVMAnalyzer.analyzeHyperPlaneInfo(model);
        assertEquals(3, hps.length);

        Field positive_ = HyperPlane.class.getDeclaredField("positive_");
        positive_.setAccessible(true);
        Field negative_ = HyperPlane.class.getDeclaredField("negative_");
        negative_.setAccessible(true);

        {
            HyperPlane hp = hps[0];
            assertEquals(0, positive_.getInt(hp));
            assertEquals(1, negative_.getInt(hp));
        }

        {
            HyperPlane hp = hps[1];
            assertEquals(0, positive_.getInt(hp));
            assertEquals(2, negative_.getInt(hp));
        }

        {
            HyperPlane hp = hps[2];
            assertEquals(1, positive_.getInt(hp));
            assertEquals(2, negative_.getInt(hp));
        }

        try {
            LibSVMAnalyzer.analyzeHyperPlaneInfo(null);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("Invalid Model: model is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }
    }

    @Test
    public void testAnalyzeFeatureFrequency() {
        libsvm.svm_problem train = new libsvm.svm_problem();
        train.l = 4 ;
        train.y = new double[4];
        train.y[ 0] = 3; train.y[ 1] = 1; train.y[ 2] = 2; train.y[ 3] = 4;
        train.x = new libsvm.svm_node[4][] ;

        train.x[0] = new libsvm.svm_node[3];
        train.x[0][0] = new libsvm.svm_node(); train.x[0][0].index = 1; train.x[0][0].value = 1.0;
        train.x[0][1] = new libsvm.svm_node(); train.x[0][1].index = 2; train.x[0][0].value = 1.0;
        train.x[0][2] = new libsvm.svm_node(); train.x[0][2].index = 3; train.x[0][0].value = 1.0;

        train.x[1] = new libsvm.svm_node[1];
        train.x[1][0] = new libsvm.svm_node(); train.x[1][0].index = 1; train.x[1][0].value = 1.0;

        train.x[2] = new libsvm.svm_node[2];
        train.x[2][0] = new libsvm.svm_node(); train.x[2][0].index = 3; train.x[2][0].value = 1.0;
        train.x[2][1] = new libsvm.svm_node(); train.x[2][1].index = 4; train.x[2][1].value = 1.0;

        train.x[3] = new libsvm.svm_node[4];
        train.x[3][0] = new libsvm.svm_node(); train.x[3][0].index = 1; train.x[3][0].value = 1.0;
        train.x[3][1] = new libsvm.svm_node(); train.x[3][1].index = 2; train.x[3][1].value = 1.0;
        train.x[3][2] = new libsvm.svm_node(); train.x[3][2].index = 4; train.x[3][2].value = 1.0;
        train.x[3][3] = new libsvm.svm_node(); train.x[3][3].index = 9; train.x[3][3].value = 1.0;

        libsvm.svm_model model = libsvm.svm.svm_train(train, default_params_);
        Map<Integer, Integer> ffreq = LibSVMAnalyzer.analyzeFeatureFrequency(model);
        assertEquals(5, ffreq.size());
        Integer r = null ;
        r = ffreq.get(1) ; assertEquals(false,  r == null); assertEquals(3,  (int)r);
        r = ffreq.get(2) ; assertEquals(false,  r == null); assertEquals(2,  (int)r);
        r = ffreq.get(3) ; assertEquals(false,  r == null); assertEquals(2,  (int)r);
        r = ffreq.get(4) ; assertEquals(false,  r == null); assertEquals(2,  (int)r);
        r = ffreq.get(9) ; assertEquals(false,  r == null); assertEquals(1,  (int)r);

        try {
            LibSVMAnalyzer.analyzeFeatureFrequency(null);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("Invalid Model: model is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }
    }

    @Test
    public void testAnalyzeNoneLinearSupportVectors() {
        libsvm.svm_problem train = new libsvm.svm_problem();
        train.l = 4 ;
        train.y = new double[4];
        train.y[ 0] = 3; train.y[ 1] = 1; train.y[ 2] = 2; train.y[ 3] = 4;
        train.x = new libsvm.svm_node[4][] ;

        train.x[0] = new libsvm.svm_node[3];
        train.x[0][0] = new libsvm.svm_node(); train.x[0][0].index = 1; train.x[0][0].value = 1.0;
        train.x[0][1] = new libsvm.svm_node(); train.x[0][1].index = 2; train.x[0][0].value = 1.0;
        train.x[0][2] = new libsvm.svm_node(); train.x[0][2].index = 3; train.x[0][0].value = 1.0;

        train.x[1] = new libsvm.svm_node[1];
        train.x[1][0] = new libsvm.svm_node(); train.x[1][0].index = 1; train.x[1][0].value = 1.0;

        train.x[2] = new libsvm.svm_node[2];
        train.x[2][0] = new libsvm.svm_node(); train.x[2][0].index = 3; train.x[2][0].value = 1.0;
        train.x[2][1] = new libsvm.svm_node(); train.x[2][1].index = 4; train.x[2][1].value = 1.0;

        train.x[3] = new libsvm.svm_node[4];
        train.x[3][0] = new libsvm.svm_node(); train.x[3][0].index = 1; train.x[3][0].value = 1.0;
        train.x[3][1] = new libsvm.svm_node(); train.x[3][1].index = 2; train.x[3][1].value = 1.0;
        train.x[3][2] = new libsvm.svm_node(); train.x[3][2].index = 4; train.x[3][2].value = 1.0;
        train.x[3][3] = new libsvm.svm_node(); train.x[3][3].index = 9; train.x[3][3].value = 1.0;

        libsvm.svm_model model = libsvm.svm.svm_train(train, default_params_);
        ClassifierImpl.Arguments args = new ClassifierImpl.Arguments();
        LibSVMAnalyzer.analyzeNoneLinearSupportVectors(model, args);


        assertEquals(5, args.features_.length) ;

        try {
            LibSVMAnalyzer.analyzeNoneLinearSupportVectors(null, args);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("Invalid Model: model is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }

        try {
            LibSVMAnalyzer.analyzeNoneLinearSupportVectors(model, null);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("the arguments is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }
    }

    @Test
    public void testAnalyzeLinearSupportVectors() {
        libsvm.svm_problem train = new libsvm.svm_problem();
        train.l = 4 ;
        train.y = new double[4];
        train.y[ 0] = 1; train.y[ 1] = 1; train.y[ 2] = 2; train.y[ 3] = 2;
        train.x = new libsvm.svm_node[4][] ;

        train.x[0] = new libsvm.svm_node[3];
        train.x[0][0] = new libsvm.svm_node(); train.x[0][0].index = 1; train.x[0][0].value = 1.0;
        train.x[0][1] = new libsvm.svm_node(); train.x[0][1].index = 2; train.x[0][0].value = 1.0;
        train.x[0][2] = new libsvm.svm_node(); train.x[0][2].index = 3; train.x[0][0].value = 1.0;

        train.x[1] = new libsvm.svm_node[1];
        train.x[1][0] = new libsvm.svm_node(); train.x[1][0].index = 1; train.x[1][0].value = 1.0;

        train.x[2] = new libsvm.svm_node[2];
        train.x[2][0] = new libsvm.svm_node(); train.x[2][0].index = 3; train.x[2][0].value = 1.0;
        train.x[2][1] = new libsvm.svm_node(); train.x[2][1].index = 4; train.x[2][1].value = 1.0;

        train.x[3] = new libsvm.svm_node[4];
        train.x[3][0] = new libsvm.svm_node(); train.x[3][0].index = 1; train.x[3][0].value = 1.0;
        train.x[3][1] = new libsvm.svm_node(); train.x[3][1].index = 2; train.x[3][1].value = 1.0;
        train.x[3][2] = new libsvm.svm_node(); train.x[3][2].index = 4; train.x[3][2].value = 1.0;
        train.x[3][3] = new libsvm.svm_node(); train.x[3][3].index = 9; train.x[3][3].value = 1.0;
        libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
        params.kernel_type = libsvm.svm_parameter.LINEAR;
        libsvm.svm_model model = libsvm.svm.svm_train(train, params);
        ClassifierImpl.Arguments args = new ClassifierImpl.Arguments();
        LibSVMAnalyzer.analyzeLinearSupportVectors(model, args);

        assertEquals(1, args.svcoefs_.length);
        assertEquals(5, args.features_.length) ;

        try {
            LibSVMAnalyzer.analyzeLinearSupportVectors(null, args);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("Invalid Model: model is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }

        try {
            LibSVMAnalyzer.analyzeLinearSupportVectors(model, null);
             fail("");
         } catch (IllegalArgumentException e){
             assertEquals("the arguments is null.", e.getMessage());
         } catch (Exception e) {
             e.printStackTrace();
             fail("");
         }
    }

    @Test
    public void testMakeDefaultParameters() {
        libsvm.svm_parameter params = LibSVMAnalyzer.makeDefaultParameters();
        assertEquals(libsvm.svm_parameter.C_SVC, params.svm_type);
        assertEquals(libsvm.svm_parameter.RBF, params.kernel_type);
        assertEquals(3, params.degree);
        assertEquals(0.0, params.gamma, E);
        assertEquals(0.0, params.coef0, E);
        assertEquals(0.5, params.nu, E);
        assertEquals(100.0, params.cache_size, E);
        assertEquals(1.0, params.C, E);
        assertEquals(1e-3, params.eps, E);
        assertEquals(0.1, params.p, E);
        assertEquals(1, params.shrinking);
        assertEquals(0.0, params.probability, E);
        assertEquals(0,   params.nr_weight);
        assertEquals(true, params.weight_label != null);
        assertEquals(0, params.weight_label.length);
        assertEquals(true, params.weight != null);
        assertEquals(0, params.weight.length);
    }

}
