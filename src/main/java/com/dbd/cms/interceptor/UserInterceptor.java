package com.dbd.cms.interceptor;


import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.kits.CookieKit;
import com.dbd.cms.kits.ReqKit;
import com.dbd.cms.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLEncoder;


public class UserInterceptor implements Interceptor {


    public void intercept(Invocation inv) {
        String domain=PropKit.use("config.properties").get("domain");

        DBDController controller = (DBDController)inv.getController();

        String ck=inv.getControllerKey();
        HttpServletRequest request=controller.getRequest();
        String userId=CookieKit.get(controller, Consts.USER_ACCESS_TOKEN);
        boolean flag = false;
        User user = null;
        if(StrKit.notBlank(userId)) {
            user = User.dao.findFirstByCache(Consts.CACHE_NAMES.user.name(),new BigInteger(userId),"select * from s_user where status='0' and id=? ",new BigInteger(userId));
            if(user != null) {
                flag = true;
            }
        }
        if(flag) {
            inv.invoke();
        } else {
            String querystring = request.getQueryString();
            String beforeUrl = request.getRequestURL() + "?" + querystring;
            if (StrKit.isBlank(querystring)) {
                beforeUrl = request.getRequestURL().toString();
            }


            CookieKit.remove(controller, Consts.USER_ACCESS_TOKEN);
            if(ReqKit.isAjaxRequest(controller.getRequest())){
                if(ck.contains("admin"))
                    controller.renderUnauthorizedJSON("admin");
                else if(ck.equals("/wc")){
                    //wxOAuth2(controller);
                    controller.getSession().setAttribute(Consts.WX_OAUTH2_TOKEN,StrKit.getRandomUUID());
                    controller.getResponse().setStatus(401);
                    controller.renderFailJSON("未授权","");
                }
                else {
                    controller.renderUnauthorizedJSON("front");
                    return ;
                }
            }else {

                try {
                    if (ck.contains("admin"))
//                        controller.redirect(PropKit.use("config.properties").get("domain")+"/admincc/login?callback=" + URLEncoder.encode(beforeUrl, "UTF-8"));nginx 反向代理 会出现路径问题
                        controller.redirect(domain+"/admincc/login");
                    else if(ck.equals("/wc")){

                    }else
                        controller.redirect(domain+"/index?v=pleaseLogin&callback=" + URLEncoder.encode(beforeUrl, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    LogKit.error("encode转码失败："+e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }


}
