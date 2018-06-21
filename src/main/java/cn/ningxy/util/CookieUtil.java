package cn.ningxy.util;

import com.gargoylesoftware.htmlunit.util.Cookie;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @Author: ningxy
 * @Description:
 * @Date: 2018-06-21 14:17
 **/
public class CookieUtil {

    public static final Set<Cookie> jsonArrayToSet(JSONArray jsonArray) {
        Set<Cookie> cookies = new HashSet<>();
        for (Object o : jsonArray) {
            JSONObject jsonObject = JSONObject.fromObject(o);
            String domain = jsonObject.getString("domain");
            String name = jsonObject.getString("name");
            String value = jsonObject.getString("value");
            String path = jsonObject.getString("path");
            Date expires = (jsonObject.get("expires") == null) ? (Date) jsonObject.get("expires") : null;
            boolean secure = jsonObject.getString("secure").equals("true");
            boolean httpOnly = jsonObject.getString("httpOnly").equals("true");
            Cookie cookie = new Cookie(domain, name, value, path, expires, secure, httpOnly);
            cookies.add(cookie);
        }
        return cookies;
    }
}
