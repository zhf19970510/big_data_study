package com.zhf.hadoop.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TestHdfs {

    public Configuration conf = null;

    public FileSystem fs = null;

    @Before
    public void conn() throws Exception {
        conf = new Configuration(true);

        // 这种是本地获取的方式
        fs = FileSystem.get(conf);
        /**
         * 代码在这里会做几件事情：
         * 1. 通过获取core-site.xml文件中的配置：
         * fs.defaultFS配置 获取FileSystem的子类为分布式的文件系统子类：DistributedFileSystem
         *
         * 取环境变量：HADOOP_USER_NAME
         * 后面代码客户端都会使用该用户做事情
         */

        // 还可以在代码中指定hdfs集群地址
//        fs = FileSystem.get(URI.create("hdfs://mycluster/"), conf, "good");
    }

    @Test
    public void mkdir() throws Exception {
        Path dir = new Path("/zhf");
        if (fs.exists(dir)) {
            fs.delete(dir, true);
        }
        fs.mkdirs(dir);
    }

    @Test
    public void upload() throws Exception {
        BufferedInputStream input = new BufferedInputStream(new FileInputStream(new File("/data/hello.txt")));
        Path outFile = new Path("/zhf/out.txt");
        FSDataOutputStream output = fs.create(outFile);
        IOUtils.copyBytes(input, output, conf, true);
    }

    @Test
    public void blocks() throws Exception {
        Path file = new Path("/user/god/data.txt");
        FileStatus fss = fs.getFileStatus(file);
        BlockLocation[] blks = fs.getFileBlockLocations(fss, 0, fss.getLen());
        for (BlockLocation blk : blks) {
            System.out.println(blk);
        }

        // 0,       1048576,        node04,node02 A
        // 1048576, 504319,         node04,node03 B

        // 计算向数据移动
        // 其实用户和程序读取的是文件这个级别，并不知道有块的概念
        FSDataInputStream in = fs.open(file);   // 面向文件打开的输入流，无论怎么读都是从文件开始读起。

        in.seek(1048576);
        // 计算向数据移动后，期望的是分治，只读取自己关心。同时，具备距离的概念。（优先和本地

    }

    @After
    public void close() throws IOException {
        fs.close();
    }
}
