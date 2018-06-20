package cn.ningxy.api;

import cn.ningxy.bean.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

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
    public String getUserFromForm(@FormParam("username") String username, @FormParam("pwd") String password) {
        User user = new User(username, password);
        System.out.println(user.toString());





        return user.toString();
    }

}
