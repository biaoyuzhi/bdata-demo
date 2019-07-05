import java.util.UUID

import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}


/**
  * Created by wuzh on 2019/5/24.
  * Describe：使用Spark-Streaming来消费Kafka中的某个topic中的数据(处理req,info,error,websocket日志)
  */
object SparkKafkaDemo {
  def main(args: Array[String]): Unit = {
    //创建StreamingContext
    val conf = new SparkConf()
      .setAppName("SparkKafkaDemo")         //不指定运行环境，留给提交时指定--master
      .set("spark.streaming.stopGracefullyOnShutdown", "true")    //设置优雅退出
      .set("spark.default.parallelism","6")
    val sc = new SparkContext(conf)
    sc.setLogLevel("ERROR")
    //todo 如果程序异常退出再重启，kafka中间产生的数据要想正常读取，这里需要先删除checkpointDir文件夹再重启该程序，也可以不删除，但等待的时间比较长，暂时怀疑是测试机的内存太小造成的
    val checkpointDir = "/kafka-checkpoint"
    val ssc = StreamingContext.getOrCreate(checkpointDir,()=>createStreaming(sc,checkpointDir))
    //启动
    ssc.start()
    //保持运行
    ssc.awaitTermination()
  }

  def createStreaming(sc: SparkContext,checkpointDir: String): StreamingContext = {
    val ssc = new StreamingContext(sc,Seconds(10))  //设置批次时间，批处理间隔时间
    //offset保存路径，会保存在hdfs路径上
    ssc.checkpoint(checkpointDir)
    //读取kafka数据
    val kafkaParams = Map(
      "bootstrap.servers" -> "192.168.92.60:9092",
      "group.id" -> "test-consumer-group",
      "enable.auto.commit" -> "true",     //自动提交位移，如果在消息处理完成前就提交了offset，那么就有可能造成数据的丢失，建议设置为false，在消息被完整处理之后再手动提交位移
      "key.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer",
      "value.deserializer" -> "org.apache.kafka.common.serialization.StringDeserializer"
    )
    //—————————————————————————处理reqinfo.log—————————————————————————————————————
    //这里代码运行后会在Kafka上自动创建指定(qtec-req)的topic
    val streamReq = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](List("qtec-req"),kafkaParams))
    //处理读取的数据
    //    streamReq.map(_.value().toString).print()     //直接打印在控制台
    streamReq.map(_.value()).mapPartitions(iter => {
      iter.filter(_.contains("requestUrl")).map(line => {
        line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,39)+"@"+line.substring(line.indexOf(" - ") + 3)
      })
    }).foreachRDD(rdd => if (!rdd.isEmpty()) rdd.saveAsTextFile("hdfs://hadoop72:9820/req/"+UUID.randomUUID().toString().replaceAll("-","")))
    //—————————————————————————处理info.log—————————————————————————————————————
    val streamInfo = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](List("qtec-info"),kafkaParams))
    streamInfo.map(_.value()).map(line => {
      line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,31)+"@"+line.substring(line.indexOf(" - ") + 3)
    }).foreachRDD(rdd => if (!rdd.isEmpty()) rdd.saveAsTextFile("hdfs://hadoop72:9820/info/"+UUID.randomUUID().toString().replaceAll("-","")))
    //—————————————————————————处理error.log—————————————————————————————————————
    val streamError = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](List("qtec-error"),kafkaParams))
    streamError.map(_.value()).map(line => {
      line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,32)+"@"+line.substring(line.indexOf(" - ") + 3)
    }).foreachRDD(rdd => if (!rdd.isEmpty()) rdd.saveAsTextFile("hdfs://hadoop72:9820/error/"+UUID.randomUUID().toString().replaceAll("-","")))
    //—————————————————————————处理tcpinfo.log—(最多只能处理四个，不明原因)————————————————————————————————————
    //    val streamTcp = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](List("qtec-tcp"),kafkaParams))
    //    streamTcp.map(_.value()).map(line => {
    //      line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,35)+"@"+line.substring(line.indexOf(" - ") + 3)
    //    }).foreachRDD(rdd => if (!rdd.isEmpty()) rdd.saveAsTextFile("hdfs://hadoop72:9820/tcp/"+UUID.randomUUID().toString().replaceAll("-","")))
    //—————————————————————————处理websocketinfo.log—————————————————————————————————————
    val streamWebSocket = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](List("qtec-websocket"),kafkaParams))
    streamWebSocket.map(_.value()).map(line => {
      line.substring(0,10)+"@"+line.substring(14,22)+"@"+line.substring(27,41)+"@"+line.substring(line.indexOf(" - ") + 3)
    }).foreachRDD(rdd => if (!rdd.isEmpty()) rdd.saveAsTextFile("hdfs://hadoop72:9820/websocket/"+UUID.randomUUID().toString().replaceAll("-","")))
    ssc
  }

}
