package bigfile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.*;

/**
 * Created by wuzh on 2019/7/9.
 * Describe：读取大文件，再筛选出出现次数前三的单词和出现次数。这是普通java代码实现，scala包下有SparkCore实现对比类SparkFileDemo.scala
 * 测试虚拟机的总内存为1G，aa文件的大小为3.1G，下面程序运行中内存的浮动为5M左右，无内存溢出异常。
 */
public class ReadFileDemo {
    private static HashMap<String, Long> map = new HashMap<String, Long>();

    public static void main(String[] args) throws Exception {
        //遍历本地路径下的一个大文件aa，并将单词和出现次数组成键值对放入HashMap
        BufferedReader reader = new BufferedReader(new FileReader(new File("/root/Desktop/aa")));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] splits = line.split("\\W+");
            for (String split : splits) {
                if (!"".equals(split)) {
                    if (map.containsKey(split)) {
                        long value = map.get(split) + 1L;
                        map.put(split, value);
                    } else {
                        map.put(split, 1L);
                    }
                }
            }
        }
        //将Map.Entry放入ArrayList集合，并按Map.Entry的value降序排序
        ArrayList<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
            public int compare(Map.Entry<String, Long> o1, Map.Entry<String, Long> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        //取出出现次数最多的前3个单词和出现的次数
        for (int i = 0; i < 3; i++) {
            System.out.println("(" + list.get(i).getKey() + "," + list.get(i).getValue() + ")");
        }
    }
}
