package jdbconnection;

import java.sql.*;

/**
 * Created by wuzh on 2019/6/21.
 * Describe：连接操作hive数据库的测试代码，依赖为pom.xml中的hive-jdbc，resources包下放入hive-site.xml文件
 */
public class HiveDemo {
    //原生的JDBC连接
    public static void main(String[] args) throws Exception {
        //注册驱动
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        //创建连接
        Connection conn = DriverManager.getConnection("jdbc:hive2://192.168.92.72:10006/default","ubuntu","");
        //获得Statement对象
        Statement statement = conn.createStatement();
        //执行SQL语句，返回结果集
        ResultSet rs = statement.executeQuery("select * from qtec_log limit 2");
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
