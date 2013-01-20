package jp.gr.java_conf.dyama.rink.tools;

import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;

public class PerformanceMeasuring {

    private long time_ ;
    private long heap_size_ ;

    public PerformanceMeasuring(){
        time_ = System.currentTimeMillis();
        heap_size_ = _getHeapMemorySize();
    }

    /**
     * get the time from the construction this instance to the calling to this method.
     * @return time [ms]
     */
    public long getTime(){
        return System.currentTimeMillis() - time_ ;
    }

    private long _getHeapMemorySize(){
        long heap_size = 0 ;
        for (MemoryPoolMXBean mbean : ManagementFactory
                .getMemoryPoolMXBeans()) {
            if (mbean.getType() != MemoryType.HEAP)
                continue;
            if (!mbean.isValid())
                continue;
            MemoryUsage usage = mbean.getPeakUsage();
            heap_size += usage.getUsed();
        }
        return heap_size ;
    }

    /**
     * get the peak size of used heap memory
     * @return the size of memory [byte]
     */
    public long getPeakHeapMemorySize(){
        return _getHeapMemorySize();
    }

    /**
     * get the size of used heap memory after this instance has been constructed.
     * @return the size of heap memory [byte]
     */
    public long getHeapMemorySize(){
        return _getHeapMemorySize() - heap_size_  ;
    }
    /**
     * show performance
     * @param out output print stream. nothing done if out is null.
     */
    public void show(PrintStream out){
        if (out == null)
            return ;
        long time = getTime();
        long peak = getPeakHeapMemorySize();
        out.printf("Time[s]\t%.2f", ((double)time) / 1000);
        out.println();
        out.printf("Memory[MB]\t%.2f", ((double)peak) / 1024 / 1024);
        out.println();
    }

    /**
     * show performance
     * @param prefix prefix string for output. it's same to the show(out) if prefix is null.
     * @param out output print stream. nothing done if out is null.
     */
    public void show(String prefix, PrintStream out){
        if (out == null)
            return ;
        if (prefix == null)
            prefix = "";
        long time = getTime();
        long peak = getPeakHeapMemorySize();
        out.printf(prefix + "Time [s]    :\t%.2f", ((double)time) / 1000);
        out.println();
        out.printf(prefix + "Memory [MB] :\t%.2f", ((double)peak) / 1024 / 1024);
        out.println();
    }
}
