package jp.gr.java_conf.dyama.rink.ml.svm;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.Feature;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.HyperPlane;
import jp.gr.java_conf.dyama.rink.ml.svm.ClassifierImpl.SVCoefficient;
import jp.gr.java_conf.dyama.rink.ml.svm.Parameters.KernelType;

/**
 * analyzer of LibSVM model
 * @author Hiroyasu Yamada
 *
 */
enum LibSVMAnalyzer {
    INSTANCE;

    /**
     * analyze a libsvm model, and covert into classifier arguments.
     * @param model libsvm's model. throw IllegalArgumentException if model is invalid.
     * throw IllegalArugmentException if the feature space is not binary.
     * @return classifier arguments
     */
    static ClassifierImpl.Arguments analyzeModel(libsvm.svm_model model){
        String errMsg = checkValidModel(model) ;
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        if (! analyzeFeatureSpace(model))
            throw new IllegalArgumentException("Real feature space is unsupported, (but it will be supported in future.");

        KernelType kt = analyzeKernelType(model);

        ClassifierImpl.Arguments args = new ClassifierImpl.Arguments();
        args.params_ = analyzeParameters(model);
        args.labels_ = analyzeLabelInfo(model);
        args.hps_    = analyzeHyperPlaneInfo(model);

        if (kt == KernelType.LINEAR) {
            args.kernel_ = new KernelFunction.Linear();
            analyzeLinearSupportVectors(model, args);
        } else {
            if (kt == KernelType.POLYNOMIAL) {
                args.kernel_ = new KernelFunction.Polynomial(args.params_.getDegree(), args.params_.getGamma(), args.params_.getCoef0());
            } else if (kt == KernelType.RBF) {
                args.kernel_ = new KernelFunction.RBF(args.params_.getGamma());
            } else if (kt == KernelType.SIGMOID){
                args.kernel_ = new KernelFunction.Sigmoid(args.params_.getGamma(), args.params_.getCoef0());
            }
            args.svcoefs_ = LibSVMAnalyzer.analyzeSVCoefficient(model);
            analyzeNoneLinearSupportVectors(model, args);
        }
        return args ;
    }

    /**
     * make lisbsvm's default parameters
     * @return libsvm's default parameters.
     */
    static libsvm.svm_parameter makeDefaultParameters(){
        libsvm.svm_parameter params = new libsvm.svm_parameter();
        params.degree = 3 ;
        params.gamma  = 0.0 ;
        params.coef0  = 0.0 ;
        params.kernel_type = libsvm.svm_parameter.RBF;
        params.svm_type    = libsvm.svm_parameter.C_SVC;
        params.nu = 0.5;
        params.cache_size = 100;
        params.C = 1;
        params.eps = 1e-3;
        params.p = 0.1;
        params.shrinking = 1;
        params.probability = 0;
        params.nr_weight = 0;
        params.weight_label = new int[0];
        params.weight = new double[0];
        return params;
    }

    /**
     * check whether the mode is valid or not.
     * @param model libsvm's model
     * @return null if the model is valid, otherwise return error messages.
     * the conditions of invalid model are follows:<br>
     * model is null. <br>
     * model.params is null. <br>
     * model.params.svm_type is not C_SVC.<br>
     * model.nr_class is less than 2. <br>
     * model.nr_class is not same to model.labels.length <br>
     * model.rho is null<br>
     * k: model.nr_class<br>
     * model.rho.length is not m = k * ( k - 1 )/ 2 . <br>
     * model.sv_coef is null<br>
     * model.sv_coef.length is k-1. <br>
     * model.SV is null. <br>
     * model.SV.length is not model.l. <br>
     * model.nSV is null. <br>
     * sum of model.nSV[0..k-1] is not l <br>
     */
    static String checkValidModel(libsvm.svm_model model){
        if (model == null)
            return "Invalid Model: model is null.";

        if (model.param == null)
            return "Invalid Model: model.param is null.";

        if (model.param.svm_type != libsvm.svm_parameter.C_SVC)
            return "Invalid Model: model.param.sv_type is not C_SVC.";

        if (model.label == null)
            return "Invalid Model: model.label is null.";

        if (model.nr_class < 2)
            return "Invalid Model: model.nr_class is less than 2.";

        if (model.nr_class != model.label.length)
            return "Invalid Model: model.nr_class is not same to model.label.length.";

        if (model.rho == null)
            return "Invalid Model: model.rho is null.";
        int k = model.nr_class;

        if (model.rho.length != (k * (k-1) / 2))
            return "Invalid Model: model.rho.length is not same to k * (k - 1) / 2.";

        if (model.sv_coef == null)
            return "Invalid Model: model.sv_coef is null.";

        if (model.sv_coef.length != k-1)
            return "Invalid Model: model.sv_coef.length is not same to k-1.";

        if (model.SV == null)
            return "Invalid Model: model.SV is null.";

        if (model.SV.length != model.l)
            return "Invalid Model: model.SV.length is not same to model.l.";

        if (model.nSV == null)
            return "Invalid Model: model.nSV is null.";

        int sum = 0 ;
        for(int i = 0 ; i < model.nSV.length ; i++)
            sum += model.nSV[i];

        if (sum != model.l)
            return "Invalid Model: the sum of model.nSV[0..k-1] is not same to model.l.";

        return null ;
    }
    /**
     * analyze the type of kernel functions of libsvm's model
     * @param moodel libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @return type of kernel functions. return null if the kernel function is unknown.
     */
    static KernelType analyzeKernelType(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        KernelType kt = null ;

        if (model.param.kernel_type == libsvm.svm_parameter.LINEAR)
            kt = KernelType.LINEAR;
        if (model.param.kernel_type == libsvm.svm_parameter.POLY)
            kt = KernelType.POLYNOMIAL;
        if (model.param.kernel_type == libsvm.svm_parameter.RBF)
            kt = KernelType.RBF;
        if (model.param.kernel_type == libsvm.svm_parameter.SIGMOID)
            kt = KernelType.SIGMOID;
        return kt;
    }

    /**
     * analyze feature space : binary or not.
     * @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @return true if the feature space is binary, otherwise false.
     */
    static boolean analyzeFeatureSpace(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        for(int l = 0; l < model.SV.length ; l++ ){
            libsvm.svm_node[] sv = model.SV[l];
            for(int i = 0 ; i < sv.length ; i++){
                if (sv[i].value != 1.0)
                    return false ;
            }
        }
        return true ;
    }

    /**
     * analyze libsvm parameters.
     *  @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @return Parameters
     */
    static Parameters analyzeParameters(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        Parameters params = new Parameters.ParametersImpl();
        params.setCacheSize(model.param.cache_size);
        params.setDegree(model.param.degree);
        params.setGamma(model.param.gamma);
        params.setCoef0(model.param.coef0);
        params.setEpsilon(model.param.eps);
        params.setC(model.param.C);
        params.setKernelType(analyzeKernelType(model));
        return params ;
    }

    /**
     * analyze label information
     * @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @return array of labels
     */
    static int[] analyzeLabelInfo(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);
        return Arrays.copyOf(model.label, model.label.length);
    }

    /**
     * analyze hyperplanes
     * @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     */
    static HyperPlane[] analyzeHyperPlaneInfo(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        int n = model.nr_class ;
        int m = n * ( n - 1 ) / 2;
        HyperPlane[] hps = new HyperPlane[m];
        assert(m == model.rho.length);
        int k = 0 ;
        for(int i = 0; i < n-1; i++){
            for(int j = i+1; j < n; j++){
                hps[k] = new HyperPlane(i, j, model.rho[k]);
                k++;
            }
        }
        return hps ;
    }

    /**
     * analyze frequency of SV features.
     * @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @return map feature ID to it's frequency.
     */
    static Map<Integer, Integer> analyzeFeatureFrequency(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        Map<Integer, Integer> svfreq = new HashMap<Integer, Integer>();
        for(int l = 0; l < model.SV.length ; l++ ){
            for(int i = 0 ; i < model.SV[l].length ; i++){
                Integer fi = svfreq.get(model.SV[l][i].index);
                int num = 1 ;
                if (fi != null)
                    num = fi + 1;
                svfreq.put(model.SV[l][i].index, num);
            }
        }
        return svfreq;
    }

    static ClassifierImpl.SVCoefficient[] analyzeSVCoefficient(libsvm.svm_model model){
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        ClassifierImpl.SVCoefficient[] svcoefs = new ClassifierImpl.SVCoefficient[model.l];

        int n = model.nr_class;
        int[] start = new int[n];
        start[0] = 0 ;
        for (int i = 1 ; i < n; i++)
            start[i] = start[i-1] + model.nSV[i-1];

        int k = 0;
        for(int i = 0; i < n ; i++){
            int si = start[i];
            for (int j = i + 1; j < n; j++) {
                int sj = start[j];

                for(int m = 0 ; m < model.nSV[i]; m++ ){
                    int l = si + m;
                    double alpha = model.sv_coef[j-1][l];
                    if (alpha == 0.0)
                        continue ;
                    libsvm.svm_node[] sv = model.SV[l];
                    double sql2norm = 0.0;
                    for(int c = 0; c < sv.length; c++){
                        double fvalue = sv[c].value;
                        sql2norm += fvalue * fvalue;
                    }
                    SVCoefficient svcoef = svcoefs[l];
                    if (svcoef == null){
                        svcoef = new SVCoefficient(i, sql2norm, n);
                        svcoefs[l] = svcoef ;
                    }
                    svcoef.addCoefficient(k, alpha);
                }

                for(int m = 0 ; m < model.nSV[j]; m++ ){
                    int l = sj + m ;
                    double alpha = model.sv_coef[i][l];
                    if (alpha == 0.0)
                        continue ;
                    libsvm.svm_node[] sv = model.SV[l];
                    double sql2norm = 0.0;

                    for(int c = 0; c < sv.length; c++){
                        double fvalue = sv[c].value ;
                        sql2norm += fvalue * fvalue ;
                    }
                    SVCoefficient svcoef = svcoefs[l];
                    if (svcoef == null){
                        svcoef = new SVCoefficient(j, sql2norm, n);
                        svcoefs[l] = svcoef ;
                    }
                    svcoef.addCoefficient(k, alpha);
                }
                k++ ;
            }
        }
        return svcoefs ;
    }
    /**
     * analyze support vectors in none linear kernel functions.
     * @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @param args  model arguments. throw IllegalArgumentException if args is null ;
     */
    static void analyzeNoneLinearSupportVectors(libsvm.svm_model model, ClassifierImpl.Arguments args) {
        if (args == null)
            throw new IllegalArgumentException("the arguments is null.");
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        Map<Integer, Integer> ffreq = analyzeFeatureFrequency(model);

        args.features_ = new Feature.Binary[ffreq.size()];

        Map<Integer, Set<Integer> > map = new TreeMap<Integer, Set<Integer> >();

        int n = model.nr_class;
        int[] start = new int[n];
        start[0] = 0 ;
        for (int i = 1 ; i < n; i++)
            start[i] = start[i-1] + model.nSV[i-1];

        for(int i = 0; i < n ; i++){
            int si = start[i];
            for (int j = i + 1; j < n; j++) {
                int sj = start[j];

                for(int m = 0 ; m < model.nSV[i]; m++ ){
                    int l = si + m;
                    double alpha = model.sv_coef[j-1][l];
                    if (alpha == 0.0)
                        continue ;
                    libsvm.svm_node[] sv = model.SV[l];
                    for(int c = 0; c < sv.length; c++){
                        int fid = sv[c].index ;
                        Set<Integer> bf =  map.get(fid);
                        if (bf == null){
                            bf = new TreeSet<Integer>();
                            map.put(fid, bf);
                        }
                        bf.add(l);
                    }

                }

                for(int m = 0 ; m < model.nSV[j]; m++ ){
                    int l = sj + m ;
                    double alpha = model.sv_coef[i][l];
                    if (alpha == 0.0)
                        continue ;
                    libsvm.svm_node[] sv = model.SV[l];

                    for(int c = 0; c < sv.length; c++){
                        int fid = sv[c].index ;
                        Set<Integer> bf =  map.get(fid);
                        if (bf == null){
                            bf = new TreeSet<Integer>();
                            map.put(fid, bf);
                        }
                        bf.add(l);
                    }
                }
            }
        }

        int i = 0 ;
        for(Entry<Integer, Set<Integer>> f: map.entrySet()){
            Set<Integer> svids = f.getValue() ;
            Feature.Binary bf = new Feature.Binary(f.getKey(), svids.size());
            for(Integer id : svids){
                bf.addSvID(id);
            }
            args.features_[i++] = bf;
        }
    }
    /**
     * analyze support vectors in linear kernel functions.
     * @param model libsvm's model. throw IllegalArgumentException if {@link #checkValidModel(libsvm.svm_model)} is not null.
     * @param args  model arguments. throw IllegalArgumentException if args is null ;
     */
    static void analyzeLinearSupportVectors(libsvm.svm_model model, ClassifierImpl.Arguments args) {
        if (args == null)
            throw new IllegalArgumentException("the arguments is null.");
        String errMsg = checkValidModel(model);
        if (errMsg != null)
            throw new IllegalArgumentException(errMsg);

        Map<Integer, Integer> ffreq = analyzeFeatureFrequency(model);
        args.features_ = new Feature.Real[ffreq.size()];

        Map<Integer, Feature.Real> map = new TreeMap<Integer, Feature.Real>();
        Map<Integer, Double> compressed_sv = new TreeMap<Integer, Double>();

        int n = model.nr_class;
        int[] start = new int[n];
        start[0] = 0 ;
        for (int i = 1 ; i < n; i++)
            start[i] = start[i-1] + model.nSV[i-1];

        args.svcoefs_ = new SVCoefficient[n * (n-1) / 2];

        int svid = 0 ;
        for(int i = 0; i < n ; i++){
            int si = start[i];
            for (int j = i + 1; j < n; j++) {
                int sj = start[j];

                for(int m = 0 ; m < model.nSV[i]; m++ ){
                    int l = si + m;
                    double alpha = model.sv_coef[j-1][l];
                    if (alpha == 0.0)
                        continue;

                    libsvm.svm_node[] sv = model.SV[l];

                    for(int c = 0; c < sv.length; c++){
                        Double v = compressed_sv.get(sv[c].index);
                        if ( v == null)
                            v = 0.0;
                        compressed_sv.put(sv[c].index, v + alpha * sv[c].value);
                    }
                }

                for(int m = 0 ; m < model.nSV[j]; m++ ){
                    int l = sj + m ;
                    double alpha = model.sv_coef[i][l];
                    if (alpha == 0.0)
                        continue;
                    libsvm.svm_node[] sv = model.SV[l];
                    for(int c = 0; c < sv.length; c++){
                        Double v = compressed_sv.get(sv[c].index);
                        if ( v == null)
                            v = 0.0;
                        compressed_sv.put(sv[c].index, v + alpha * sv[c].value);
                    }
                }

                for(Entry<Integer, Double> f : compressed_sv.entrySet()){
                    int fid         = f.getKey();
                    double fvalue  = f.getValue();
                    Feature.Real rf = map.get(fid);
                    if ( rf == null)
                        rf = new Feature.Real(fid, ffreq.get(fid));
                    rf.addValue(fvalue, svid);
                    map.put(fid, rf);
                }

                args.svcoefs_[svid] = new SVCoefficient(i, 1.0, n); // set dummy alpha and dummy squared l2 norm
                args.svcoefs_[svid].addCoefficient(svid, 1.0);
                svid++;
                compressed_sv.clear();
            }
        }

        int i = 0 ;
        for(Entry<Integer, Feature.Real> f: map.entrySet()){
            args.features_[i++] = f.getValue();
        }
    }
}
