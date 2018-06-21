package cn.ningxy.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-21 08:31
 **/
public class RandomUtil {
    public static String getRandomFileName(String prefix, String suffix) {

        SimpleDateFormat simpleDateFormat;

        simpleDateFormat = new SimpleDateFormat("yyyyMMdd");

        Date date = new Date();

        String str = simpleDateFormat.format(date);

        Random random = new Random();

        int rannum = (int) (random.nextDouble() * (99999 - 10000 + 1)) + 10000;// 获取5位随机数

        return prefix + rannum + str + suffix;// 当前时间
    }
}
