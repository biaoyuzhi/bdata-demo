package util;


/**
 * Created by wuzh on 2019/6/10.
 * Describe：工具类
 */
public class CommonUtils {

    /**
     * 十进制转化为62进制(0-9A-Za-z)
     *
     * @param num 最大值为2147483647
     * @return
     */
    public static String from10To62(long num)
    {
        int scale = 62;
        StringBuilder sb = new StringBuilder();
        char[] charArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

        int remainder;
        do {
            remainder = (int) num % scale;
            sb.append(charArray[remainder]);
            num = num / scale;
        } while (num > scale - 1);
        //最高位这里不保留0
        if (num > 0){
            sb.append(charArray[(int) num]);
        }
        char[] chars = sb.toString().toCharArray();
        arrayReverse(chars);
        String result = new String(chars);
        return result;
    }

    /**
     * 实现数组的倒置
     *
     * @param chars
     */
    public static void arrayReverse(char[] chars) {
        int i = 0;
        int j = chars.length-1;
        while (i < j){
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
            i ++;
            j --;
        }
    }

    /**
     * 62进制(0-9A-Za-z)转化为十进制
     *
     * @param str 最高为不带0
     * @return
     */
    public static long from62To10(String str)
    {
        int scale = 62;
        String charArray = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

        long num = 0;
        int index;
        for(int i = 0; i < str.length(); i++){
            index = charArray.indexOf(str.charAt(i));
            //Math.pow(x,y) 返回x的y次方
            num += (long)(index * (Math.pow(scale, str.length() - i - 1)));
        }
        return num;
    }

}
