package cn.ningxy.service;

import cn.ningxy.bean.User;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.util.Cookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.Scanner;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description: 获取登录页
 * @Date: 2018-06-17 20:16
 **/
public class Login {

    private User user;
    private String captchaCode;
    private static final String LOGIN_URL = "http://jwpt.tjpu.edu.cn";

    public Login(User user) {
        this.user = user;
    }

    /**
     * @Author: ningxy
     * @Description: 模拟登录
     * @params: []
     * @return: boolean
     * @Date: 2018/6/17 下午11:19
     */
    public Set<Cookie> login() {

        WebClient webClient = new WebClient(BrowserVersion.CHROME);

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

        HtmlPage htmlPage = null;
        Set<Cookie> cookie = null;

        try {
            htmlPage = webClient.getPage(LOGIN_URL);                // 获取dom树
            cookie = webClient.getCookieManager().getCookies();     // 获取cookies

            captchaCode = getCaptchaCode(htmlPage, cookie);         // 获取验证码
            fillForm(htmlPage);                                     // 填充表单

            HtmlImageInput btn = (HtmlImageInput) htmlPage.getElementById("btnSure");
            HtmlPage htmlPageAfterLogin = (HtmlPage) btn.click();   // 提交表单
            webClient.waitForBackgroundJavaScript(5000);

//            System.out.println(htmlPageAfterLogin.asXml());

            if (checkLoginStatus(htmlPageAfterLogin)) return cookie;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webClient.close();
            Captcha.deleteFile("img_" + user.getUsername() + ".png");
        }
        return null;
    }

    /**
     * @Author: ningxy
     * @Description: 填充表单
     * @params: [htmlPage]
     * @return: void
     * @Date: 2018/6/17 下午10:41
     */
    private void fillForm(HtmlPage htmlPage) {
        HtmlForm form = htmlPage.getForms().get(0);                     // 获取第0个form
        HtmlTextInput inputByUsername = form.getInputByName("zjh");     // 获取账号输入框
        inputByUsername.setValueAttribute(user.getUsername());          // 设置账号
        HtmlPasswordInput inputByPassword = form.getInputByName("mm");  // 获取密码输入框
        inputByPassword.setValueAttribute(user.getPassword());          // 设置密码
        HtmlTextInput inputByCaptcha = form.getInputByName("v_yzm");    // 获取验证码输入框
        inputByCaptcha.setValueAttribute(captchaCode);                  // 设置验证码
    }

    /**
     * @Author: ningxy
     * @Description: 获取random值
     * @params: [src]
     * @return: java.lang.String
     * @Date: 2018/6/17 下午10:35
     */
    private String getRandom(String src) {
        return src.substring(30);
    }

    /**
     * @Author: ningxy
     * @Description: 获取验证码
     * @params: [htmlPage, cookie]
     * @return: java.lang.String
     * @Date: 2018/6/17 下午11:17
     */
    private String getCaptchaCode(HtmlPage htmlPage, Set<Cookie> cookie) {
        String vchartSrc = htmlPage.getElementById("vchart").getAttribute("src");
        String randomNum = getRandom(vchartSrc);
        System.out.println(randomNum);

        new Captcha(randomNum).getImg(cookie, user);

        System.out.println("请输入验证码");
        Scanner cin = new Scanner(System.in);
        String code = cin.nextLine();

        return code;
    }

    /**
     * @Author: ningxy
     * @Description: 检查是否登录成功
     * @params: [htmlPage]
     * @return: boolean
     * @Date: 2018/6/17 下午11:29
     */
    private boolean checkLoginStatus(HtmlPage htmlPage) {

        Document document = Jsoup.parse(htmlPage.asXml());

        String documentTitle = document.title();

        if ("URP综合教务系统 - 登录".equals(documentTitle)) {
            String msg = document.select("font[color=#990000]").text();
            System.out.println("登录失败：" + msg);
            return false;
        } else {
            String msg = document.select("table.title01 > tbody > tr > td").get(0).text();
            if ("我需留意".equals(msg)) {
                System.out.println("登录成功！");
                return true;
            } else {
                System.out.println("登录状态未知");
                return false;
            }
        }
    }
}
