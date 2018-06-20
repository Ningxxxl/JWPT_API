package cn.ningxy.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.text.MessageFormat;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-19 23:09
 **/
@Path("/")
public class Hello {

    @GET
//    @Produces("text/plain")
    @Produces(MediaType.APPLICATION_JSON)
    public String getMessage() {
        return "JWPT";
    }
}
