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
import java.util.Iterator;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-20 22:20
 **/
public class LoginController {
    private static final String LOGIN_URL = "http://jwpt.tjpu.edu.cn";
    private User user;
    private String captchaCode;
    private Set<Cookie> cookies;

    public LoginController() {
    }

    public LoginController(User user, String captcha, Set<Cookie> cookies) {
        this.user = user;
        this.captchaCode = captcha;
        this.cookies = cookies;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Cookie> getCookies() {
        return cookies;
    }

    public void setCookies(Set<Cookie> cookies) {
        this.cookies = cookies;
    }

    public String getCaptchaCode() {
        return captchaCode;
    }

    public void setCaptchaCode(String captchaCode) {
        this.captchaCode = captchaCode;
    }

    public static String getLoginUrl() {
        return LOGIN_URL;
    }

    @Override
    public String toString() {
        return "LoginController{" +
                "user=" + user +
                ", captchaCode='" + captchaCode + '\'' +
                ", cookies=" + cookies +
                '}';
    }

    public boolean doLogin() {
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
        webClient.getCookieManager().setCookiesEnabled(true);

        for (Cookie cookie : cookies) {
            webClient.getCookieManager().addCookie(cookie);
        }

        HtmlPage htmlPage = null;
        boolean loginRes = false;

        try {

            htmlPage = webClient.getPage(LOGIN_URL);                // 获取dom树
            fillForm(htmlPage);                                     // 填充表单

            HtmlImageInput btn = (HtmlImageInput) htmlPage.getElementById("btnSure");
            HtmlPage htmlPageAfterLogin = (HtmlPage) btn.click();   // 提交表单
            webClient.waitForBackgroundJavaScript(5000);

            loginRes = checkLoginStatus(htmlPageAfterLogin);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            webClient.close();
        }

        return loginRes;
    }

    private void fillForm(HtmlPage htmlPage) {

        HtmlForm form = htmlPage.getForms().get(0);                     // 获取第0个form
        HtmlTextInput inputByUsername = form.getInputByName("zjh");     // 获取账号输入框
        inputByUsername.setValueAttribute(user.getUsername());          // 设置账号
        HtmlPasswordInput inputByPassword = form.getInputByName("mm");  // 获取密码输入框
        inputByPassword.setValueAttribute(user.getPassword());          // 设置密码
        HtmlTextInput inputByCaptcha = form.getInputByName("v_yzm");    // 获取验证码输入框
        inputByCaptcha.setValueAttribute(captchaCode);                  // 设置验证码
    }

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
