package com.zhf.hadoop.mapreduce.topn;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.util.StringUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TMapper extends Mapper<LongWritable, Text, TKey, IntWritable> {

    // 因为map可能被调用多次，定义在外边减少gc，同时，你要知道源码中看到了map输出的key，value是会发生序列化，变成字节数组进入buffer的
    TKey mKey = new TKey();
    IntWritable mval = new IntWritable();

    @Override
    protected void map(LongWritable key, Text value, Mapper<LongWritable, Text, TKey, IntWritable>.Context context) throws IOException, InterruptedException {
        String[] strs = StringUtils.split(value.toString(), '\t');
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(strs[0]);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        mKey.setYear(cal.get(Calendar.YEAR));
        mKey.setMonth(cal.get(Calendar.MONTH) + 1);
        mKey.setDay(cal.get(Calendar.DAY_OF_MONTH));
        int wd = Integer.parseInt(strs[2]);
        mKey.setWd(wd);
        mval.set(wd);

        context.write(mKey, mval);
    }
}
