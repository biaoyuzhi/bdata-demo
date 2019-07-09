import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wuzh on 2019/7/8.
  * Describe：读取大文件，再筛选出出现次数前三的单词和出现次数
  * 如果服务器总内存不足，抛异常：java.lang.IllegalArgumentException: System memory 220987392 must be at least 471859200. Please increase heap size using the --driver-memory option or spark.driver.memory in Spark configuration
  */
object SparkFileDemo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkFileDemo").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("file:///root/Desktop/aa")  //对于大文件spark-core会自动分块
    rdd.flatMap(_.split("\\W+"))    //以非单词字符切分，等价于[^A-Za-z0-9_]
      .map((_,1))                           //转换为元组(单词,1)形式
      .reduceByKey(_+_)                    //将元组相同单词的总次数统计出来
      .sortBy(_._2,false)    //按元组的第二个参数排序，参数false为降序
      .take(3).foreach(println)   //取出前3个结果并打印在控制台
    sc.stop()
  }
}
