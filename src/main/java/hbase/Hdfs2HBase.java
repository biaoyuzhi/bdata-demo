package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.mapreduce.TableMapReduceUtil;
import org.apache.hadoop.hbase.mapreduce.TableOutputFormat;
import org.apache.hadoop.hbase.mapreduce.TableReducer;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;

import java.io.IOException;

/**
 * Created by wuzh on 2019/6/12.
 * Describe：将HDFS中的数据插入到HBase表中，需要pom.xml中的hbase-mapreduce依赖
 */
public class Hdfs2HBase {
    //Mapper逻辑
    public static class Hdfs2HBaseMapper extends Mapper<LongWritable,Text,Text,Text> {
        private Text rowKey = new Text();
        private Text outValue = new Text();
        @Override
        protected void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException {
            String[] split = value.toString().split("@");
            //rowKey设置为"3位随机数+日期"
            rowKey.set((int)(Math.random()*900+100)+split[0]);
            outValue.set(split[0]+"@"+split[1]+"@"+split[2]+"@"+split[3]);
            context.write(rowKey,outValue);
        }
    }
    //Reducer逻辑
    public static class Hdfs2HBaseReducer extends TableReducer<Text, Text, NullWritable> {
        @Override
        protected void reduce(Text key, Iterable<Text> v2s, Context context) throws IOException, InterruptedException {
            Put rowKey = new Put(key.getBytes());
            for (Text v2 : v2s) {
                String[] split = v2.toString().split("@");
                switch (split[2]){
                    case "REQUEST_INFO":
                        rowKey.addColumn(Bytes.toBytes("req"), Bytes.toBytes("log_time"), Bytes.toBytes(split[1]));
                        rowKey.addColumn(Bytes.toBytes("req"), Bytes.toBytes("log_name"), Bytes.toBytes(split[2]));
                        rowKey.addColumn(Bytes.toBytes("req"), Bytes.toBytes("log_msg"), Bytes.toBytes(split[3]));
                        break;
                    case "INFO":
                        rowKey.addColumn(Bytes.toBytes("info"), Bytes.toBytes("log_time"), Bytes.toBytes(split[1]));
                        rowKey.addColumn(Bytes.toBytes("info"), Bytes.toBytes("log_name"), Bytes.toBytes(split[2]));
                        rowKey.addColumn(Bytes.toBytes("info"), Bytes.toBytes("log_msg"), Bytes.toBytes(split[3]));
                        break;
                    case "ERROR":
                        rowKey.addColumn(Bytes.toBytes("error"), Bytes.toBytes("log_time"), Bytes.toBytes(split[1]));
                        rowKey.addColumn(Bytes.toBytes("error"), Bytes.toBytes("log_name"), Bytes.toBytes(split[2]));
                        rowKey.addColumn(Bytes.toBytes("error"), Bytes.toBytes("log_msg"), Bytes.toBytes(split[3]));
                        break;
                    default:
                        rowKey.addColumn(Bytes.toBytes("websocket"), Bytes.toBytes("log_time"), Bytes.toBytes(split[1]));
                        rowKey.addColumn(Bytes.toBytes("websocket"), Bytes.toBytes("log_name"), Bytes.toBytes(split[2]));
                        rowKey.addColumn(Bytes.toBytes("websocket"), Bytes.toBytes("log_msg"), Bytes.toBytes(split[3]));
                        //这个break也不能少，不然在连续比较的过程中default的数据会被下一次的第一个case数据覆盖
                        break;
                }
            }
            context.write(NullWritable.get(),rowKey);
        }
    }

    public static void main(String[] args) throws Exception {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "hadoop72:2181,hadoop75:2181,hadoop60:2181");
        conf.set("hbase.rootdir", "hdfs://hadoop72:9820/hbase_temp");
        //第二个参数指定要插入的表名
        conf.set(TableOutputFormat.OUTPUT_TABLE, args[1]);
        Job job = Job.getInstance(conf);
        job.setJobName("Hdfs2HBase");
        TableMapReduceUtil.addDependencyJars(job);
        job.setJarByClass(Hdfs2HBase.class);

        //Mapper类
        job.setMapperClass(Hdfs2HBaseMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(Text.class);
        //Reducer类
        job.setReducerClass(Hdfs2HBaseReducer.class);

        //第一个参数指定hdfs上的文件路径
        FileInputFormat.addInputPath(job, new Path(args[0]));
        job.setOutputFormatClass(TableOutputFormat.class);
        job.waitForCompletion(true);
    }

}
