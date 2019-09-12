import org.apache.commons.math3.linear.Array2DRowRealMatrix

/**
  * Created by wuzh on 2019/6/6.
  * Describe：该测试类在本地的正常执行需要安装scala-2.12.0
  */
object TestScala {
  def main(args: Array[String]): Unit = {
    //定义并初始化二维数组
    val arrA = Array(Array(1.0,2,3),Array(4.0,5,6))
    //使用math3实现数组转矩阵
    val A = new Array2DRowRealMatrix(arrA)
    print(A)
  }
}
