package cn.ningxy.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Properties;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-21 14:59
 **/
public class PropertyUtil {
    private static Properties props;

    static {
        loadProps();
    }

    synchronized static private void loadProps() {
        System.out.println(System.getProperty("user.dir"));
        System.out.println();
        String projectSrc = System.getProperty("user.dir");
        System.out.println("开始加载properties文件内容.......");
        props = new Properties();
        InputStream in = null;
        try {
            in = new FileInputStream("src/jwpt.properties");
            props.load(in);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("properties文件未找到");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IOException");
        } finally {
            try {
                if (null != in) {
                    in.close();
                }
            } catch (IOException e) {
                System.out.println("properties文件流关闭出现异常");
            }
        }
        System.out.println("加载properties文件内容完成......");
    }

    public static String getProperty(String key) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key);
    }

    public static String getProperty(String key, String defaultValue) {
        if (null == props) {
            loadProps();
        }
        return props.getProperty(key, defaultValue);
    }
}
