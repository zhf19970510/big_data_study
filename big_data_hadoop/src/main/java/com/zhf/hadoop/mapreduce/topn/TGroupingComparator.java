package com.zhf.hadoop.mapreduce.topn;

import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;

public class TGroupingComparator extends WritableComparator {

    @Override
    public int compare(WritableComparable a, WritableComparable b) {
        TKey k1 = (TKey) a;
        TKey k2 = (TKey) b;

        // 年、月、温度，且温度倒序
        int c1 = Integer.compare(k1.getYear(), k2.getYear());
        if(c1 == 0) {
            return Integer.compare(k1.getMonth(), k2.getMonth());
        }

        return c1;
    }
}
