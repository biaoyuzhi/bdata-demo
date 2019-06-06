import java.util.UUID

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wuzh on 2019/5/30.
  * Describe：使用Spark来处理压缩文件.gz(处理req,info,error,websocket日志)
  */
object SparkGZDemo {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SparkGZDemo")
    val sc = new SparkContext(conf)
    //先读取所有的gz文件，再分别过滤出不同的文件类型(下面处理方式在每种类型下会产生大量的空文件,约是正常文件的3倍)
    val rdd = sc.textFile("file:///home/ubuntu/bigData-temp/*/*.gz")
      //—————————————————————————处理reqinfo.log.gz—————————————————————————————————————
      rdd.filter(_.contains("CST REQUEST_INFO")).filter(_.contains("requestUrl"))
      .map(line => {
        line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,39)+"@"+line.substring(line.indexOf(" - ") + 3)
      }).saveAsTextFile("hdfs://hadoop72:9820/req/"+UUID.randomUUID().toString().replaceAll("-",""))
    //—————————————————————————处理info.log.gz—————————————————————————————————————
      rdd.filter(_.contains("CST INFO")).map(line => {
        line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,31)+"@"+line.substring(line.indexOf(" - ") + 3)
      }).saveAsTextFile("hdfs://hadoop72:9820/info/"+UUID.randomUUID().toString().replaceAll("-",""))
    //—————————————————————————处理error.log.gz—————————————————————————————————————
      rdd.filter(_.contains("CST ERROR")).map(line => {
        line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,32)+"@"+line.substring(line.indexOf(" - ") + 3)
      }).saveAsTextFile("hdfs://hadoop72:9820/error/"+UUID.randomUUID().toString().replaceAll("-",""))
    //—————————————————————————处理websocket.log.gz—————————————————————————————————————
      rdd.filter(_.contains("CST WEBSOCKET_INFO")).map(line => {
        line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,41)+"@"+line.substring(line.indexOf(" - ") + 3)
      }).saveAsTextFile("hdfs://hadoop72:9820/websocket/"+UUID.randomUUID().toString().replaceAll("-",""))
  }
}
