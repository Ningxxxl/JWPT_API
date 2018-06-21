package cn.ningxy.service;

import cn.ningxy.util.RandomUtil;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.util.Cookie;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-20 21:51
 **/
public class CaptchaController {

    private WebClient webClient;
    private Set<Cookie> cookies;
    private static final String FILE_PATH = "./img/captcha/";
    private static final String CAPTCHA_URL = "http://jwpt.tjpu.edu.cn/validateCodeAction.do";

    public CaptchaController() {
        webClient = new WebClient(BrowserVersion.CHROME);

        webClient.getOptions().setJavaScriptEnabled(true);                      // 启用JS
        webClient.getOptions().setThrowExceptionOnScriptError(false);           // 当JS执行出错的时候是否抛出异常
        webClient.getOptions().setThrowExceptionOnScriptError(false);
        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);     // 当HTTP的状态非200时是否抛出异常
        webClient.getOptions().setActiveXNative(false);
        webClient.getOptions().setDoNotTrackEnabled(false);
        webClient.getOptions().setCssEnabled(false);                            // 是否启用CSS
        webClient.getOptions().setTimeout(5000);                                // 连接超时时间
        webClient.setAjaxController(new NicelyResynchronizingAjaxController()); // 设置支持AJAX
        webClient.getCookieManager().setCookiesEnabled(true);                   // 设置cookies
    }

    public void getCaptcha() {
        try {
            WebRequest request = new WebRequest(new URL(CAPTCHA_URL));
            request.setHttpMethod(HttpMethod.GET);
            // 这个是带着cookie获取的单独的验证码页面
            Page page = webClient.getPage(request);
            WebResponse res = page.getWebResponse();
            // 通过res创建输入流
            InputStream is = res.getContentAsStream();
            // 通过输入流写入文件并保存
            String filename = RandomUtil.getRandomFileName("img_", ".jpeg");
            saveImg(is, filename);

            // 获取cookies
            cookies = webClient.getCookieManager().getCookies();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }
    }

    private static void saveImg(InputStream is, String fileName) {
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
            System.out.println("验证码保存成功");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public static String getFilePath() {
        return FILE_PATH;
    }

    public static String getCaptchaUrl() {
        return CAPTCHA_URL;
    }

}
