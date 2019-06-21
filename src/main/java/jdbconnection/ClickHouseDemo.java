package jdbconnection;

import java.sql.*;

/**
 * Created by wuzh on 2019/6/21.
 * Describe：连接操作clickhouse数据库的测试代码，依赖为pom.xml中的clickhouse-jdbc
 */
public class ClickHouseDemo {
    //原生的JDBC连接
    public static void main(String[] args) throws Exception {
        //注册驱动
        Class.forName("ru.yandex.clickhouse.ClickHouseDriver");
        //创建连接
        Connection conn = DriverManager.getConnection("jdbc:clickhouse://192.168.92.75:8123/default");
        //获得Statement对象
        Statement statement = conn.createStatement();
        //执行SQL语句，返回结果集
        ResultSet rs = statement.executeQuery("select * from hdfs_qtec_log limit 3");
        //可以获得结果集中的元数据集
        ResultSetMetaData metaData = rs.getMetaData();
        //遍历展示结果集
        while (rs.next()){
            for (int i = 1;i<=metaData.getColumnCount();i++){
                System.out.println("结果" + i + ":" + rs.getString(metaData.getColumnName(i)));
            }
        }
        //关闭资源
        rs.close();
        statement.close();
        conn.close();
    }
}
