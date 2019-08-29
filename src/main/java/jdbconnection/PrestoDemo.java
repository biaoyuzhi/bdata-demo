package jdbconnection;

import java.sql.*;

/**
 * Created by wuzh on 2019/8/29.
 * Describe：连接操作presto数据库的测试代码，依赖为pom.xml中的presto-jdbc
 */
public class PrestoDemo {
    //原生的JDBC连接
    public static void main(String[] args) throws Exception {
        Class.forName("com.facebook.presto.jdbc.PrestoDriver");
        Connection conn = DriverManager.getConnection("jdbc:presto://192.168.92.75:8080/default", "ubuntu", null);
        Statement statement = conn.createStatement();
        //sql语句定位表注意：Presto使用Catalog、Schema和Table这3层结构
        ResultSet rs = statement.executeQuery("select * from hive.default.qtec_log limit 2");
        ResultSetMetaData metaData = rs.getMetaData();
        while (rs.next()){
            for (int i = 1;i<=metaData.getColumnCount();i++){
                System.out.println("结果" + i + ":" + rs.getString(metaData.getColumnName(i)));
            }
        }
        rs.close();
        statement.close();
        conn.close();
    }
}
