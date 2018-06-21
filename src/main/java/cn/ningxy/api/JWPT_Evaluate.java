package cn.ningxy.api;

import cn.ningxy.bean.User;
import cn.ningxy.service.CaptchaController;
import cn.ningxy.service.EvaluateController;
import cn.ningxy.service.LoginController;
import cn.ningxy.test.TestMain;
import com.gargoylesoftware.htmlunit.util.Cookie;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-20 00:02
 **/
@Path("/login")
public class JWPT_Evaluate {

    @GET
    @Produces("text/plain")
    public String getMessage() {
        return "JWPT/login";
    }

    @POST
    @Path("/jwpt/evaluate")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public String getUserFromForm(@FormParam("username") String username, @FormParam("pwd") String password,
                                  @FormParam("captcha") String code, @FormParam("cookie") String cooky) {
        Set<Cookie> cookie = null;
        User user = new User(username, password);
        System.out.println(user.toString());

        LoginController loginController = new LoginController();
        loginController.setUser(user);
        loginController.setCookies(cookie);
        loginController.setCaptchaCode(code);

        loginController.doLogin();

        EvaluateController evaluateController = new EvaluateController(loginController.getCookies());
        evaluateController.evaluate();
        return user.toString();
    }

    @GET
    @Path("/jwpt/evaluate/captcha")
//    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    public String getCaptcha() {
        JSONObject returnJSON = new JSONObject();
        JSONArray cookiesArray = new JSONArray();

        CaptchaController captchaController = new CaptchaController();
        captchaController.getCaptcha();

//        cookiesArray = captchaController.getCookiesJsonArray();
        System.out.println(cookiesArray.toString());
        System.out.println(captchaController.getCaptchaImgFileName());
        return captchaController.getCaptchaImgFileName();
    }

}
