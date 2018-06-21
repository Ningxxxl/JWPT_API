package cn.ningxy.test;

import cn.ningxy.bean.User;
import cn.ningxy.service.CaptchaController;
import cn.ningxy.service.EvaluateController;
import cn.ningxy.service.LoginController;
import cn.ningxy.util.CookieUtil;
import com.gargoylesoftware.htmlunit.util.Cookie;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.io.File;
import java.net.CookieHandler;
import java.util.Date;
import java.util.Scanner;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-20 23:15
 **/
public class TestMain {

    public static void aaa() {
        CaptchaController captchaController = new CaptchaController();
        captchaController.getCaptcha();
        System.out.println(captchaController.getCaptchaImgFileName());
    }
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        CaptchaController captchaController = new CaptchaController();

        captchaController.getCaptcha();
        User user = new User("1611650720", "05071X");

        Set<Cookie> set = captchaController.getCookies();
        JSONArray jsonArray = JSONArray.fromObject(set);
        System.out.println(jsonArray);

        Set<Cookie> set1 = CookieUtil.jsonArrayToSet(jsonArray);
        for(Cookie cookie : set1) {
            System.out.println(cookie);
        }

        Scanner cin = new Scanner(System.in);
        String code = cin.nextLine();

        loginController.setUser(user);


        loginController.setCookies(captchaController.getCookies());
        loginController.setCaptchaCode(code);

        loginController.doLogin();

        EvaluateController evaluateController = new EvaluateController(loginController.getCookies());
        evaluateController.evaluate();

    }
}
