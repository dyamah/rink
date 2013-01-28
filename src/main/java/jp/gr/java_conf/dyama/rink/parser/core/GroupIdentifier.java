package jp.gr.java_conf.dyama.rink.parser.core;

import java.io.Serializable;

import jp.gr.java_conf.dyama.rink.corpus.Common;
import jp.gr.java_conf.dyama.rink.corpus.PTB;

interface GroupIdentifier extends Serializable {
    /**
     * Returns the group ID.
     * @param sample the target sample.
     * @return the group ID (0 and positive number.)
     * @throws IllegalArgumentException if the sample is null.
     */
    public int getGroupID(SampleImpl sample);

    /**
     * Returns the string corresponding to the group ID.
     * @param groupID group ID.
     * @return the string corresponding to the group ID.
     */
    public String getString(int groupID);

    static class UniGroupIdentifier implements GroupIdentifier {

        private static final long serialVersionUID = 7396762690348055567L;

        UniGroupIdentifier(){
        }

        @Override
        public int getGroupID(SampleImpl sample) {
            if (sample == null)
                throw new IllegalArgumentException("the sample is null.");
            return 0;
        }

        @Override
        public String getString(int groupID) {
            return "";
        }

    }

    static class POSGroupIdentifier implements GroupIdentifier {

        private static final long serialVersionUID = 1137513516579927993L;

        POSGroupIdentifier(){
        }

        @Override
        public int getGroupID(SampleImpl sample) {
            if (sample == null)
                throw new IllegalArgumentException("the sample is null.");
            int i = sample.getState().getLeftTarget();
            return sample.getSentence().getWord(i).getPOS().getID();
        }

        @Override
        public String getString(int groupID) {
            return PTB.POS.parseInt(groupID).toString();
        }
    }

    static class ExtPOSGroupIdentifier implements GroupIdentifier {

        private static final long serialVersionUID = 7827857037205272253L;

        ExtPOSGroupIdentifier(){
        }

        @Override
        public int getGroupID(SampleImpl sample) {
            if (sample == null)
                throw new IllegalArgumentException("the sample is null.");

            if (sample.getState().getRightNode(1) < 0)
                return Common.POS.EOS.getID();
            int i = sample.getState().getLeftTarget();
            return sample.getSentence().getWord(i).getPOS().getID();
        }

        @Override
        public String getString(int groupID) {
            if (groupID == Common.POS.EOS.getID())
                return Common.POS.EOS.toString();
            return PTB.POS.parseInt(groupID).toString();
        }
    }
}
