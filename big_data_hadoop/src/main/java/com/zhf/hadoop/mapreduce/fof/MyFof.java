package com.zhf.hadoop.mapreduce.fof;

import com.zhf.hadoop.mapreduce.topn.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 推荐好友
 */
public class MyFof {
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
        job.setJarByClass(MyFof.class);
        job.setJobName("MyFof");

        Path inFile = new Path(otherArgs[0]);
        TextInputFormat.addInputPath(job, inFile);

        Path outFile = new Path(otherArgs[1]);
        TextOutputFormat.setOutputPath(job, outFile);

        if (outFile.getFileSystem(conf).exists(outFile)) {
            outFile.getFileSystem(conf).delete(outFile, true);
        }

        job.setMapperClass(FMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);

        job.setReducerClass(FReducer.class);
        job.waitForCompletion(true);
    }
}
