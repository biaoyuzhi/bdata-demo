import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType}

/**
  * Created by wuzh on 2019/7/18.
  * Describe：通过 structType 创建 DataFrames（编程接口）
  */
object SparkSqlDemo2 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("SparkSqlDemo2").master("local[*]").getOrCreate()
    val rdd = spark.sparkContext.textFile("hdfs://hadoop72:9820/dept")
    val rowRDD = rdd.map(line => {
      val splits = line.split(",")
      Row(splits(0), splits(1), splits(2))
    })
    //定义schema方法一
    //    val schema = StructType(
    //字段名，字段类型，是否可以为空
    //      StructField("id", StringType, true) ::
    //        StructField("name", StringType, true) ::
    //        StructField("address", StringType, true) :: Nil
    //    )
    //定义schema方法二
//    val schema = StructType(
//      Array(
//        StructField("id",StringType,true),
//        StructField("name",StringType,true),
//        StructField("address",StringType,true)
//      )
//    )
    //定义schema方法三
    //如果上面StructField的字段类型一致，可以如下写
    val schema = StructType("id name address".split(" ").map(fieldName => StructField(fieldName, StringType, true)))
    val dept = spark.createDataFrame(rowRDD, schema)
    //    dept.createOrReplaceTempView("aa")
    //    spark.sql("select * from aa")
    //上面select *操作可以直接用下面写法
    dept.show()
  }
}
