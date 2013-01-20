package jp.gr.java_conf.dyama.rink.utils;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility Class for BenchMark
 * @author Hiroyasu Yamada
 */
public class BenchMark {
    public static abstract class Task {
        /**
         * The name for the default task.
         */
        private static final String DEFAULT_NAME = "";

        /** task name */
        private String name_;

        /** the starting time of the task */
        private long start_;

        /** the ending time of the task */
        private long end_;

        /** the size of peak memory during the task (byte) */
        private long peak_;

        /**
         * Constructor:
         * @param name the name of the task. use DEFAULT_NAME if name is null.
         */
        public Task(String name) {
            name_ = String.format("%20s", DEFAULT_NAME);
            if (name != null)
                name_ = String.format("%20s", name);
            start_ = System.currentTimeMillis();
        }

        /**
         * call back trigger
         */
        public abstract void run();

        /**
         * get the size of peak memory during the task.
         * @return the size of peak memory (byte)
         */
        private long getPeakMemory() {
            long peak = 0;
            for (MemoryPoolMXBean mbean : ManagementFactory
                    .getMemoryPoolMXBeans()) {
                if (mbean.getType() != MemoryType.HEAP)
                    continue;
                if (!mbean.isValid())
                    continue;
                MemoryUsage usage = mbean.getPeakUsage();
                peak += usage.getUsed();
            }
            return peak;
        }

        /**
         * doing the task
         */
        void doTask() {

            start_ = System.currentTimeMillis();
            run();
            end_ = System.currentTimeMillis();
            peak_ = getPeakMemory();
        }

        /**
         * get the name of the task.
         * @return the name of the task.
         */
        String getName() {
            return name_;
        }

        /**
         * get the time during the task.
         * @return the time during the task (mill second).
         */
        long getTime() {
            return end_ - start_;
        }

        /**
         * get the size of peak memory.
         * @return the size of peak memory (byte)
         */
        long getMemory() {
            return peak_;
        }
    };

    /** output format */
    public static final String FORMAT = "%2s %20s: %20s %5s %20s %5s";

    /** the header string for output*/
    public static final String HEADER = String.format(FORMAT, "ID", "Task",
            "time[ms]", "( % )", "memory[kb]", "( % )");

    /** decimal format for number */
    public static final DecimalFormat DFORMAT = new DecimalFormat(".#");

    /** List of Tasks t*/
    private List<Task> tasks_;

    /**
     * Constructor
     */
    public BenchMark() {
        tasks_ = new ArrayList<Task>();
    }

    /**
     * add any tasks to the list
     * @param tasks any tasks. do nothing if tasks is null.
     */
    public void addTask(Task... tasks) {
        if (tasks == null)
            return ;

        for (Task t : tasks)
            tasks_.add(t);
    }

    /**
     * clear the list of tasks
     */
    public void clear() {
        tasks_.clear();
    }

    /**
     * run all tasks.
     * @param os output stream for displaying
     */
    public void runAlls(PrintStream os) {
        os.println(HEADER);
        if (tasks_.size() < 1)
            return;

        int id = 1;
        for (Task task : tasks_) {
            task.doTask();
            Task baseline = tasks_.get(0);
            long baseTime = baseline.getTime();
            double baseMemory = (double) baseline.getMemory() / 1024;

            long time = task.getTime();
            double memory = task.getMemory();
            double tRate = ((double) time) / baseTime * 100;
            double mRate = ((double) memory) / 1024 / baseMemory * 100;
            if (baseMemory == 0.0)
                mRate = 0.0;
            os.printf(FORMAT, id++, task.getName(), time,
                    DFORMAT.format(tRate), DFORMAT.format(memory / 1024),
                    DFORMAT.format(mRate));
            os.println();
        }
    }
}
