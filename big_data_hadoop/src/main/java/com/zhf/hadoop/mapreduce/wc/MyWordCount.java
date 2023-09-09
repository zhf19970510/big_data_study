package com.zhf.hadoop.mapreduce.wc;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

/**
 * 执行mapReduce程序：
 * hadoop jar hadoop-hdfs-1.0.0.jar com.zhf.hadoop.mapreduce.wc.MyWordCount
 * hadoop jar ooxx.jar ooxx -D ooxx=ooxx inpath outpath
 */
public class MyWordCount {

    public static void main(String[] args) throws Exception {

        Configuration conf = new Configuration(true);

        // 工具类帮我们把-D 等等的属性直接set到conf里，会留下commandOptions
        GenericOptionsParser parser = new GenericOptionsParser(conf, args);
        String[] otherArgs = parser.getRemainingArgs();


        // 让框架知道是windows异构平台运行
        conf.set("mapreduce.app-submission.cross-platform", "true");

        // 设置本地启动
        conf.set("mapreduce.framework.name", "local");


        Job job = Job.getInstance(conf);
        // 设置jar包路径，方便别的节点运行的时候能够把jar上传到hdfs，这样才可以找到对应运行的类

        // 设置了mapreduce.framework.name = local 之后，是不需要在指定jar包位置上传了
        job.setJar("E:\\workspace\\code\\big_data_study\\big_data_hadoop\\target\\big_data_hadoop-1.0-SNAPSHOT.jar");

        job.setJarByClass(MyWordCount.class);
        job.setJobName("wordCountJob");

//        Path inFile = new Path("/data/wc/input");
        Path inFile = new Path(otherArgs[0]);
        TextInputFormat.addInputPath(job, inFile);

//        Path outFile = new Path("/data/wc/output");
        Path outFile = new Path(otherArgs[1]);
        TextOutputFormat.setOutputPath(job, outFile);

        if (outFile.getFileSystem(conf).exists(outFile)) {
            outFile.getFileSystem(conf).delete(outFile, true);
        }

        job.setMapperClass(MyMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(IntWritable.class);
        job.setReducerClass(MyReducer.class);

        job.setNumReduceTasks(2);
        job.waitForCompletion(true);
    }
}
