package com.zhf.hadoop.mapreduce.topn;

import com.zhf.hadoop.mapreduce.wc.MyMapper;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import java.io.IOException;

public class MyTopN {

    public static void main(String[] args) throws Exception {
        Configuration conf = new Configuration(true);

        // 让框架知道是windows异构平台运行
        conf.set("mapreduce.app-submission.cross-platform", "true");

        // 设置本地启动
        conf.set("mapreduce.framework.name", "local");

        // 工具类帮我们把-D 等等的属性直接set到conf里，会留下commandOptions
        GenericOptionsParser parser = new GenericOptionsParser(conf, args);
        String[] otherArgs = parser.getRemainingArgs();

        Job job = Job.getInstance(conf);
        job.setJarByClass(MyTopN.class);
        job.setJobName("TopN");

        Path inFile = new Path(otherArgs[0]);
        TextInputFormat.addInputPath(job, inFile);

        Path outFile = new Path(otherArgs[1]);
        TextOutputFormat.setOutputPath(job, outFile);

        if (outFile.getFileSystem(conf).exists(outFile)) {
            outFile.getFileSystem(conf).delete(outFile, true);
        }

        job.setMapperClass(TMapper.class);
        job.setMapOutputKeyClass(TKey.class);
        job.setMapOutputValueClass(IntWritable.class);
        // 按 年、月 分区 -》 分区 > 分组
        // 分区器的潜台词： 满足 相同的key获得相同的分区号就可以
        job.setPartitionerClass(TPartitioner.class);
        // 年、月、温度、且温度倒序
        job.setSortComparatorClass(TSortComparator.class);
        // combine
//        job.setCombinerClass();

        job.setGroupingComparatorClass(TGroupingComparator.class);

        job.setReducerClass(TReducer.class);
        job.waitForCompletion(true);
    }
}
