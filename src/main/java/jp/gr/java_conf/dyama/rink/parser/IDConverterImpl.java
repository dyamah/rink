package jp.gr.java_conf.dyama.rink.parser;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.gr.java_conf.dyama.rink.common.IDConverter;

public interface IDConverterImpl {

    public class ImmutableIDConverter implements IDConverter, Serializable {

        private static final long serialVersionUID = -8050807583042795786L;

        private Map<String, Integer> map_ ;

        /**
         * Constructor
         * @param converter mutable ID converter. throw IllegalArgumentException if converter is null.
         */
        ImmutableIDConverter(MutableIDConverter converter){
            if (converter == null)
                throw new IllegalArgumentException("the converter is null.");
            map_ = converter.map_;
        }
        /**
         * convert a string into unique ID
         * @param string source string
         * @return unique ID. The ID is greater than 10 if string has already been defined as word, otherwise 0.
         * return 0 if string is null.
         */
        @Override
        public int convert(String string) {
            if (string == null)
                return UNDEFINED ;
            Integer id = map_.get(string);
            if (id == null)
                return UNDEFINED ;
            return id ;
        }

        public int size(){
            return map_.size() ;
        }

        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
            map_ = new HashMap<String, Integer>();
            synchronized (map_){

                int size = in.readInt();
                for(int i = 0 ; i < size; i++){
                    String key = (String)in.readObject();
                    int id     = in.readInt();
                    map_.put(key, id);
                }
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException{
            synchronized (map_) {
                out.writeInt(map_.size());
                for(Entry<String, Integer> e : map_.entrySet()){
                    out.writeObject(e.getKey());
                    out.writeInt(e.getValue());
                }
            }
        }

    }


    public class MutableIDConverter implements IDConverter, Serializable{

        private static final long serialVersionUID = -7633434536883311768L;

        /** string to ID map */
        private Map<String, Integer> map_ ;

        /**
         * Constructor
         */
        public MutableIDConverter(){
            map_ = new HashMap<String, Integer>();
        }

        /**
         * change to the immutable converter
         * @return immutable converter
         * Note that this instance is initialized to the default after this method is called.
         */
        public ImmutableIDConverter toImmutable(){
            ImmutableIDConverter x = new ImmutableIDConverter(this);
            map_ = new HashMap<String, Integer>();
            return x ;
        }


        /**
         * convert a string into unique ID.
         * @param string source string.
         * @return unique ID. The id must be greater than 0 except for null.
         * return 0 if string is null.
         */
        @Override
        public int convert(String string) {
            if (string == null)
                return 0 ;
            Integer id = map_.get(string);
            if (id != null)
                return id;
            int n =  map_.size() + START;
            map_.put(string, n);
            return n;
        }

        /**
         * get the number of converted strings
         * @return the number of converted strings
         */
        public int size(){
            return map_.size() ;
        }


        private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException{
            map_ = new HashMap<String, Integer>();
            synchronized (map_){
                int size = in.readInt();
                for(int i = 0 ; i < size; i++){
                    String key = (String)in.readObject();
                    int id     = in.readInt();
                    map_.put(key, id);
                }
            }
        }

        private void writeObject(ObjectOutputStream out) throws IOException{
            synchronized (map_) {
                out.writeInt(map_.size());
                for(Entry<String, Integer> e : map_.entrySet()){
                    out.writeObject(e.getKey());
                    out.writeInt(e.getValue());
                }
            }
        }

    }

}
