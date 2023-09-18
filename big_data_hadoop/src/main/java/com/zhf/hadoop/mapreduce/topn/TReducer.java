package com.zhf.hadoop.mapreduce.topn;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;
import java.util.Iterator;

public class TReducer extends Reducer<TKey, IntWritable, Text, IntWritable> {

    Text rKey = new Text();
    IntWritable rVal = new IntWritable();

    @Override
    protected void reduce(TKey key, Iterable<IntWritable> values, Reducer<TKey, IntWritable, Text, IntWritable>.Context context) throws IOException, InterruptedException {
        Iterator<IntWritable> iter = values.iterator();

        int flg = 0;
        int day = 0;
        while (iter.hasNext()) {
            IntWritable val = iter.next();  // context.nextKeyValue() -> 对key和value更新值！！！
            if(flg == 0) {
                rKey.set(key.getYear() + "-" + key.getMonth() + "-" + key.getDay()+ "@" + key.getLocation());
                rVal.set(key.getWd());
                context.write(rKey, rVal);
                day = key.getDay();
                flg++;
            }
            if(flg != 0 && day != 0) {
                rKey.set(key.getYear() + "-" + key.getMonth() + "-" + key.getDay() + "@" + key.getLocation());
                rVal.set(key.getWd());
                context.write(rKey, rVal);
                break;
            }


        }
    }
}
