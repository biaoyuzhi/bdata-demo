package util;


import java.util.Arrays;

/**
 * Created by wuzh on 2019/6/26.
 * Describe：MapReduce内部使用的排序算法：快排、归并以及堆排逻辑实现，时间复杂度均为O(nlog2(n))，对数是底数为2的logn
 */
public class MapReduceSort {
    /**
     * 快速排序逻辑实现
     *
     * @param arr  需要排序的数组
     * @param low  最低位序号，一般为0
     * @param high 最高位序号，一般为arr.length-1
     */
    public static void quickSort(int[] arr, int low, int high) {
        int l = low;
        int h = high;
        int povit;          //基准值
        if (l <= h) {       //待排序的元素至少有两个的情况
            povit = arr[l]; //待排序的第一个元素作为基准元素
            while (l != h) {    //从左右两边交替扫描，直到l = h
                while (h > l && arr[h] >= povit)
                    h--;        //从右往左扫描，找到第一个比基准元素小的元素
                //找到这种元素后将高位值赋给当前低位
                arr[l] = arr[h];
                while (l < h && arr[l] <= povit)
                    l++;        //从左往右扫描，找到第一个比基准元素大的元素
                //找到这种元素后将低位值赋给当前高位
                arr[h] = arr[l];
            }
            //将基准值赋给当前低位(这里给低位或高位都可以，因为此时的l = h)
            arr[l] = povit;
            System.out.println(Arrays.toString(arr));
            System.out.print("l=" + l + ";h=" + h + ";povit=" + povit + "\n");
            quickSort(arr, low, l - 1);    //对基准元素左边的元素进行递归排序
            quickSort(arr, h + 1, high);      //对基准元素右边的元素进行递归排序
        }
    }

    /**
     * 归并排序逻辑实现
     *
     * @param arr  需要排序的数组
     * @param low  最低位序号，一般为0
     * @param high 最高位序号，一般为arr.length-1
     */
    public static void mergeSort(int[] arr, int low, int high) {
        if (low < high) {//当子序列中只有一个元素时结束递归
            int mid = (low + high) / 2;                 //划分子序列
            mergeSort(arr, low, mid);                  //对左侧子序列进行递归排序
            mergeSort(arr, mid + 1, high);        //对右侧子序列进行递归排序
            //合并逻辑
            int[] tmp = new int[arr.length];           //辅助数组
            int p1 = low, p2 = mid + 1, k = low;              //p1、p2是检测指针，k是存放指针
            //把较小的数先移到辅助数组中
            while (p1 <= mid && p2 <= high) {
                if (arr[p1] <= arr[p2])
                    tmp[k++] = arr[p1++];
                else
                    tmp[k++] = arr[p2++];
            }
            while (p1 <= mid) tmp[k++] = arr[p1++];      //如果第一个序列未检测完，直接将后面所有元素加到合并的序列中
            while (p2 <= high) tmp[k++] = arr[p2++];     //同上
            //复制回原素组
            for (int i = low; i <= high; i++)
                arr[i] = tmp[i];
            System.out.println(Arrays.toString(arr));
        }
    }

    /**
     * 堆排序逻辑实现
     *
     * @param arr 需要排序的数组
     */
    public static void heapSort(int[] arr) {
        //构建大顶堆
        for (int i = arr.length / 2 - 1; i >= 0; i--) {
            //从第一个非叶子节点从下至上，从右至左调整结构
            adjustHeap(arr, i, arr.length);
        }
        //调整堆结构+交换堆顶元素与末尾元素
        for (int j = arr.length - 1; j > 0; j--) {
            //将堆顶元素与末尾元素进行交换
            int temp = arr[0];
            arr[0] = arr[j];
            arr[j] = temp;
            //重新对堆进行调整
            adjustHeap(arr, 0, j);
        }
    }
    //调整堆结构逻辑，检索过的节点都构建成大顶堆了，检索起点为最后一个非叶子节点
    private static void adjustHeap(int[] arr, int i, int length) {
        int temp = arr[i];      //先取出当前元素i
        for (int k = i * 2 + 1; k < length; k = k * 2 + 1) {     //从i节点的左子节点开始，也就是2i+1处开始
            if (k + 1 < length && arr[k] < arr[k + 1])     //如果左子节点小于右子节点，k后移1指向右子节点
                k++;
            if (arr[k] > temp) {       //如果子节点大于父节点，将子节点值赋给父节点(不用进行交换)
                arr[i] = arr[k];
                i = k;
            } else {
                break;      //结束循环的此次逻辑，进入循环的下个轮回
            }
        }
        arr[i] = temp;      //将temp值放到最终的位置
        System.out.println(Arrays.toString(arr));
    }

}
