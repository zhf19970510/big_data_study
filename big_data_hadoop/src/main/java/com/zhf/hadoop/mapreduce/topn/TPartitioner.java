package com.zhf.hadoop.mapreduce.topn;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Partitioner;

public class TPartitioner extends Partitioner<TKey, IntWritable> {
    @Override
    public int getPartition(TKey key, IntWritable value, int numParations) {

        return key.getYear() % numParations;
    }
}
