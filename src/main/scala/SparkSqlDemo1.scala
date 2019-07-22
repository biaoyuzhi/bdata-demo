import org.apache.spark.sql.SparkSession

/**
  * Created by wuzh on 2019/7/18.
  * Describe：使用case class反射的方式实现RDD转换为DataFrame。局限：case class最多支持22个字段
  */
object SparkSqlDemo1 {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().appName("SparkSqlDemo1").master("local[*]").getOrCreate()
    val rdd = spark.sparkContext.textFile("hdfs://hadoop72:9820/dept").map(line=>{
      val splits = line.split(",")
      Dept(splits(0),splits(1),splits(2))
    })
    //该处需导入下面包，import后面的spark为上面第一步声明的SparkSession的变量名
    import spark.implicits._
    val df = rdd.toDF()
    df.createOrReplaceTempView("aa")
    spark.sql("select * from aa")
  }
}
case class Dept(id:String,name:String,address:String)
