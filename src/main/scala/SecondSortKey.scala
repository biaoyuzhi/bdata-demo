import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by wuzh on 2019/7/11.
  * Describe：二次排序自定义类，SparkCore的map方法中基于该类实现的对象作为key值来实现二次排序逻辑
  */
class SecondSortKey(val first:Int,val second:Int) extends Ordered[SecondSortKey] with Serializable {
  override def compare(that: SecondSortKey): Int = {
    if (this.first != that.first){  //第一字段不相等的情况
      this.first - that.first         //按升序排列
    }else{
      this.second - that.second       //第一字段相等的情况按第二字段升序排列
    }
  }
}

/**
  *SparkCore样例中调用上面二次排序自定义类
  */
object SecondSortDemo{
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("SecondSortDemo").setMaster("local[*]")
    val sc = new SparkContext(conf)
    val rdd = sc.textFile("/root/Desktop/temp")   //读取本地文件，即需要进行排序的数据源
    rdd.map(line=>(new SecondSortKey(line.split(" ")(0).toInt,line.split(" ")(1).toInt),line))    //对每一行转化为（SecondSortKey对象，该行值）的key-value结构
      .sortByKey()       //按key排序，即应用上自定义类排序规则的SecondSortKey对象来排序
      .map(_._2)          //按顺序取出排好序的value值
      .foreach(println)   //打印在控制台
  }
}