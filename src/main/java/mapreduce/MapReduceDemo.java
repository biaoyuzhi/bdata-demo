package mapreduce;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.TextInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.mapreduce.lib.output.TextOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;

/**
 * Created by wuzh on 2019/5/24.
 * Describe：使用原生MapReduce编写文件分析处理代码
 * 打成jar包上传服务器端后，运行命令：hadoop jar bdata-demo-1.0-SNAPSHOT.jar mapreduce.MapReduceDemo HData/input/dept HData/input/emp HData/output
 * 服务器同级路径HData/input/下需要两个文件：dept和emp
 */
public class MapReduceDemo extends Configured implements Tool {

    public static class MapClass extends Mapper<LongWritable,Text,Text,Text>{
        private Map<String,String> deptMap = new HashMap<String, String>();
        private String[] kv;
        @Override
        protected void setup(Context context) throws IOException {
            BufferedReader in = null;
            try {
                // 从当前作业中获取要缓存的文件
//                URI[] paths = DistributedCache.getCacheFiles(context.getConfiguration());
                URI[] paths = context.getCacheFiles();
                String deptIdName;
                for (URI path : paths) {
                    // 对部门文件字段进行拆分并缓存到deptMap中
                    if (path.toString().contains("dept")){
                        //todo 此处FileReader读取文件会抛出找不到文件或文件夹异常
                        in = new BufferedReader(new FileReader(path.toString()));
                        while (null != (deptIdName = in.readLine())){
                            // 对部门文件字段进行拆分并缓存到deptMap中
                            // 其中Map中key为部门编号，value为所在部门名称
                            deptMap.put(deptIdName.split(",")[0],deptIdName.split(",")[1]);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (in!=null){
                    in.close();
                }
            }
        }

        // 【map阶段】 输入的文件自动作为map的输入参数，一行为一个key
        public void map(LongWritable key,Text value,Context context) throws IOException, InterruptedException {
            // 对员工文件字段进行拆分
            kv = value.toString().split(",");
            // map join: 在map阶段过滤掉不需要的数据(保证只有deptMap中存在的部门号)，
            // 在此阶段完成了emp中的部门号和dept中的部门名之间的关联，输出key为部门名称和value为员工工资
            if (deptMap.containsKey(kv[7])){
                if (null != kv[5] && !"".equals(kv[5])){
                    context.write(new Text(deptMap.get(kv[7].trim())),new Text(kv[5].trim()));
                }
            }
        }
    }

    public static class ReduceClass extends Reducer<Text, Text, Text, LongWritable>{
        public void reduce(Text key, Iterable<Text> values, Context context) throws IOException, InterruptedException {
            // 将多个相同key的小键值对 在shuffle阶段已经汇聚为一个整体键值对
            // 对同一部门的员工工资进行求和
            long sumSalary = 0;
            for (Text value : values) {
                sumSalary += Long.parseLong(value.toString());
            }
            // 输出key为部门名称和value为该部门员工工资总和
            context.write(key, new LongWritable(sumSalary));
        }
    }

    public int run(String[] args) throws Exception {
        // 实例化作业对象，设置作业名称、Mapper和Reduce类
//        Job job = new Job(getConf(), "MapReduceDemo");
        Job job = Job.getInstance(getConf(), "MapReduceDemo");
        job.setJobName("MapReduceDemo");
        job.setJarByClass(MapReduceDemo.class);
        job.setMapperClass(MapClass.class);
        job.setReducerClass(ReduceClass.class);

        //设置输出格式类
        job.setInputFormatClass(TextInputFormat.class);

        //设置输出格式
        job.setOutputFormatClass(TextOutputFormat.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        // 第1个参数为缓存的部门数据路径、第2个参数为员工数据路径和第3个参数为输出路径
        String[] otherArgs = new GenericOptionsParser(job.getConfiguration(), args).getRemainingArgs();
//        DistributedCache.addCacheFile(new URI(otherArgs[0]), job.getConfiguration());
        job.addCacheFile(new Path(otherArgs[0]).toUri());
        FileInputFormat.addInputPath(job,new Path(otherArgs[1]));
        FileOutputFormat.setOutputPath(job,new Path(otherArgs[2]));

        //run方法先setup方法运行，在此处等待MapReduce运行完成
        job.waitForCompletion(true);
        return job.isSuccessful() ? 0 : 1;
    }

    /**
     * 主方法，执行入口
     *
     * @param args 输入参数
     */
    public static void main(String[] args) throws Exception {
        int res = ToolRunner.run(new Configuration(), new MapReduceDemo(), args);
        System.exit(res);
    }

}
