package hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * Created by wuzh on 2019/6/11.
 * Describe：使用HBase的API操作集群中的HBase表
 */
public class HBaseDemo {
    public static void main(String[] args) {
        System.out.println("+++++++++++++connect begin!+++++++++++++++");
        //获取配置信息
        Configuration conf = HBaseConfiguration.create();
        try {
            Connection conn = ConnectionFactory.createConnection(conf);
            //先假设HBase集群中已经create 'emp',{NAME=>'col-family',VERSIONS=>3}，这里直接找到emp表
            TableName name = TableName.valueOf("emp");
            //连接emp表
            Table emp = conn.getTable(name);
            //设置emp表的row key
            Put put = new Put(Bytes.toBytes("row3"));
            //设置emp表同一row key下的列族和cell值
            put.addColumn(Bytes.toBytes("col-family"), Bytes.toBytes("message"), Bytes.toBytes("value"));
            emp.put(put);
            System.out.println("+++++++++++++put 'emp','row3','col-family:message','value' end!+++++++++++++++");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
