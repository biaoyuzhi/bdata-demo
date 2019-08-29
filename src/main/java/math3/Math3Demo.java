package math3;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.Arrays;

/**
 * Created by wuzh on 2019/8/29.
 * Describe：依赖为pom.xml中的commons-math3，支持各种数学运算，包括向量计算、矩阵运算
 */
public class Math3Demo {
    public static void main(String[] args) {
        double arrA[][] = {{3,2,1},{6,5,4}};
        System.out.println("二维数组A为：\t" + Arrays.deepToString(arrA));
        //将二维数组转换为矩阵，这里只接受double类型的二维数组
        Array2DRowRealMatrix matrixA = new Array2DRowRealMatrix(arrA);
        System.out.println("创建的A矩阵为：\t" + matrixA);
        //获取矩阵的行数
        System.out.println("矩阵的行数为：\t" + matrixA.getRowDimension());
        //获取矩阵的列数
        System.out.println("矩阵的列数为：\t" + matrixA.getColumnDimension());
        double arrB[][] = {{1,2},{3,4},{5,6}};
        Array2DRowRealMatrix matrixB = new Array2DRowRealMatrix(arrB);
        System.out.println("创建的B矩阵为：\t" + matrixB);
        System.out.println("AB矩阵相乘后：\t" + matrixA.multiply(matrixB));
        RealMatrix transposeB = matrixB.transpose();
        System.out.println("B矩阵倒置后：\t" + transposeB);
        System.out.println("A矩阵与B倒置后矩阵相加：\t" + matrixA.add(transposeB));
        System.out.println("A矩阵与B倒置后矩阵相减：\t" + matrixA.subtract(transposeB));
    }
}
