package cn.ningxy.service;

import cn.ningxy.bean.User;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description: 处理验证码
 * @Date: 2018-06-17 21:00
 **/
public class Captcha {

    private static final String FILE_PATH = "./img/captcha/";
    private String randomNum;
    private String captchaUrl;

    public Captcha(String randomNum) {
        this.randomNum = randomNum;
        this.captchaUrl = "http://jwpt.tjpu.edu.cn/validateCodeAction.do?random=" + randomNum;
        System.out.println(captchaUrl);
    }

    public String getRandomNum() {
        return randomNum;
    }

    public void setRandomNum(String randomNum) {
        this.randomNum = randomNum;
    }

    public String getCaptchaUrl() {
        return captchaUrl;
    }

    public void setCaptchaUrl(String captchaUrl) {
        this.captchaUrl = captchaUrl;
    }

    /**
     * @Author: ningxy
     * @Description: 发送get请求获取验证码
     * @params: [cookie]
     * @return: void
     * @Date: 2018/6/17 下午10:38
     */
    public void getImg(Set<Cookie> cookie, User user) {
        WebClient webClient = new WebClient();
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
        webClient.getOptions().setCssEnabled(false);
        webClient.getCookieManager().setCookiesEnabled(true);
        Iterator<Cookie> i = cookie.iterator();
        while (i.hasNext()) {
            System.out.println(i);
            webClient.getCookieManager().addCookie(i.next());
        }
        try {
            WebRequest request = new WebRequest(new URL(captchaUrl));
            request.setHttpMethod(HttpMethod.GET);
            //这个是带着cookie获取的单独的验证码页面
            Page page = webClient.getPage(request);
            WebResponse res = page.getWebResponse();
            //通过res创建输入流
            InputStream is = res.getContentAsStream();
            //通过输入流写入文件并保存
            saveImg(is, "img_" + user.getUsername() + ".png");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }
    }

    /**
     * @Author: ningxy
     * @Description: 保存验证码
     * @params: [is, fileName]
     * @return: void
     * @Date: 2018/6/17 下午10:38
     */
    private void saveImg(InputStream is, String fileName) {

        //创建文件的目录结构
        File files = new File(FILE_PATH);
        if (!files.exists()) {
            files.mkdirs();
        }
        try {
            // 创建文件
            File file = new File(FILE_PATH + fileName);
            FileOutputStream out = new FileOutputStream(file);
            int i = 0;
            while ((i = is.read()) != -1) {
                out.write(i);
            }
            is.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
    * @Author: ningxy
    * @Description: 删除验证码
    * @params: [fileName]
    * @return: boolean
    * @Date: 2018/6/19 下午10:12
    */
    public static boolean deleteFile(String fileName) {
        File file = new File(FILE_PATH + fileName);
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }


    @Override
    public String toString() {
        return "Captcha{" +
                "randomNum='" + randomNum + '\'' +
                ", captchaUrl='" + captchaUrl + '\'' +
                '}';
    }
}
