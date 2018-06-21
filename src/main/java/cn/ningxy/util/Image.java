package cn.ningxy.util;

import sun.misc.BASE64Encoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-21 21:58
 **/
public class Image {
    public static String GetImageStr(String filePath) {//将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        InputStream in = null;
        byte[] data = null;
        //读取图片字节数组
        try {
            in = new FileInputStream(filePath);
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        return "data:image/jpeg;base64," + encoder.encode(data);//返回Base64编码过的字节数组字符串
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }
}
