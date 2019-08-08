import com.typesafe.config.{Config, ConfigFactory}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka010._

/**
  * Created by wuzh on 2019/8/8.
  * Describe：测试功能1、ConfigFactory.load()通过pom.xml中的config依赖实现读取resources/application.conf文件中的内容
  *           测试功能2、spark-streaming中直接new StreamingContext()，不需要中间new SparkContext()
  *           测试功能3、对auto.offset.reset和enable.auto.commit两个参数的理解
  */
object AppParmsUtils {
  /**
    * 解析application.conf配置文件
    * 加载resource下面的配置文件，默认规则：application.conf->application.json->application.properties
    */
  private lazy val config: Config = ConfigFactory.load()

  val topic = config.getString("kafka.topic").split(",")
  val borkers = config.getString("kafka.broker.list")
  val groupId = config.getString("kafka.group.id")

  /**
    * kafka的相关参数
    */
  val kafkaParams = Map[String, Object](
    "bootstrap.servers" -> borkers,
    "key.deserializer" -> classOf[StringDeserializer],
    "value.deserializer" -> classOf[StringDeserializer],
    "group.id" -> groupId,
    /** 默认为latest，当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，消费新产生的该分区下的数据(即如果是新创建的消费组，从该消费者启动时间开始消费，以前的数据不消费)。
      * 若为earliest，当各分区下有已提交的offset时，从提交的offset开始消费；无提交的offset时，从头开始消费。
      * 若为none，topic各分区都存在已提交的offset时，从offset后开始消费；只要有一个分区不存在已提交的offset，则抛出异常，所以一般不设为none。
      */
    "auto.offset.reset" -> "earliest",
    //是否开启自动提交。默认为true，消费完一条数据后，kafka服务端会将该消费组的偏移量增加1.如果为false，偏移量不会自动增加，直到遇到消费端的consumer.commitSync()。
    "enable.auto.commit" -> "false"
  )

  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("AppParmsUtils").setMaster("local[*]")
      .set("spark.streaming.stopGracefullyOnShutdown", "true")    //设置优雅退出
    //将rdd以序列化格式来保存以减少内存的占用
    //默认采用org.apache.spark.serializer.JavaSerializer
    //这是最基本的优化
      .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    //rdd压缩
      .set("spark.rdd.compress", "true")
    //batchSize = partitionNum * 分区数量 * 采样时间
      .set("spark.streaming.kafka.maxRatePerPartition", "100")
    val ssc = new StreamingContext(conf,Seconds(2))
    /** 获取kafka的数据
      * LocationStrategies：位置策略，如果kafka的broker节点跟Executor在同一台机器上给一种策略，不在一台机器上给另外一种策略
      * 设定策略后会以最优的策略进行获取数据
      * 一般在企业中kafka节点跟Executor不会放到一台机器的，原因是kakfa是消息存储的，Executor用来做消息的计算，
      * 因此计算与存储分开，存储对磁盘要求高，计算对内存、CPU要求高
      * 如果Executor节点跟Broker节点在一起的话使用PreferBrokers策略，如果不在一起的话使用PreferConsistent策略
      * 使用PreferConsistent策略的话，将来在kafka中拉取了数据以后尽量将数据分散到所有的Executor上
      */
    val dStream = KafkaUtils.createDirectStream(ssc,LocationStrategies.PreferConsistent,ConsumerStrategies.Subscribe[String,String](topic,kafkaParams))
    dStream.foreachRDD(rdd=>{
      rdd.foreach(x=>println(x.value()))  //处理数据
      //手动提交offset
      val offset = rdd.asInstanceOf[HasOffsetRanges].offsetRanges
//      offset.foreach(println)
      dStream.asInstanceOf[CanCommitOffsets].commitAsync(offset)
    })
    //启动
    ssc.start()
    //保持运行
//    ssc.awaitTermination()
  }
}
