package cn.ningxy.test;

import cn.ningxy.bean.User;
import cn.ningxy.service.CaptchaController;
import cn.ningxy.service.LoginController;

import java.util.Scanner;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-20 23:15
 **/
public class TestMain {
    public static void main(String[] args) {
        LoginController loginController = new LoginController();
        CaptchaController captchaController = new CaptchaController();

        captchaController.getCaptcha();
        User user = new User("1611650720", "05071X");

        Scanner cin = new Scanner(System.in);
        String code = cin.nextLine();

        loginController.setUser(user);
        loginController.setCookies(captchaController.getCookies());
        loginController.setCaptchaCode(code);

        System.out.println(loginController.doLogin());
    }
}
