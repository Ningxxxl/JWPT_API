package cn.ningxy.api;

import cn.ningxy.bean.User;
import cn.ningxy.service.CaptchaController;
import cn.ningxy.service.EvaluateController;
import cn.ningxy.service.LoginController;
import cn.ningxy.util.CookieUtil;
import cn.ningxy.util.Image;
import com.gargoylesoftware.htmlunit.util.Cookie;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
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
                                  @FormParam("captchaCode") String code, @FormParam("cookieForm") String cooky) {
        Set<Cookie> cookies = CookieUtil.jsonArrayToSet(JSONArray.fromObject(cooky));
        for(Cookie cookie : cookies) {
            System.out.println(cookie);
        }
        User user = new User(username, password);
        LoginController loginController = new LoginController();
        loginController.setUser(user);
        loginController.setCookies(cookies);
        loginController.setCaptchaCode(code);
//
        loginController.doLogin();
//
        EvaluateController evaluateController = new EvaluateController(loginController.getCookies());
        evaluateController.evaluate();
        return user.toString();
    }

    @GET
    @Path("/jwpt/evaluate/captcha")
//    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.TEXT_PLAIN)
    public Response getCaptcha() {
        JSONObject returnJSON = new JSONObject();
        int respondStatus = 404;
        String respondMessage = "NULL";
        JSONObject dataJson = new JSONObject();
        try {
            CaptchaController captchaController = new CaptchaController();
            captchaController.getCaptcha();
            Set<Cookie> cookies = captchaController.getCookies();
            String captchaImg = Image.GetImageStr(captchaController.getFileURL());
            dataJson.put("cookie", cookies);
            dataJson.put("captchaImg", captchaImg.replace("\n", ""));
            respondStatus = 200;
            respondMessage = "OK";
        } catch (Exception e) {
            respondStatus = 500;
            respondMessage = "Server Error";
            e.printStackTrace();
        }
        returnJSON.put("status", respondStatus);
        returnJSON.put("message", respondMessage);
        returnJSON.put("data", dataJson);

        System.out.println(returnJSON);
        return Response.status(200).entity(returnJSON.toString()).build();
    }

}
