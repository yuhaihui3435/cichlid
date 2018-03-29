package com.dbd.cms.controller.admin;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.CommonInterceptor;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.CookieKit;
import com.dbd.cms.kits.ext.BCrypt;
import com.dbd.cms.model.User;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/12/1.
 */
@Before(UserInterceptor.class)
public class IndexCtr extends DBDController {
    public void index() {
        render("/admin/index.html");
    }
    @Clear({CommonInterceptor.class,UserInterceptor.class})
    public void captcha() {
        renderCaptcha();
    }
    @Clear({CommonInterceptor.class,UserInterceptor.class})
    public void login() {
        String cc_username = getPara("cc_username");
        String cc_password = getPara("cc_password");
        String callBack = getPara("callback");
        String rm=getPara("rememberMe");

        if (StrKit.isBlank(cc_username) || StrKit.isBlank(cc_password)) {
            setAttr("callback", callBack);
            render("/admin/login.html");
            return;
        }


        boolean bl = validateCaptcha("inputRandomCode");

        if (!bl) {
            renderFailJSON("验证码不正确", "");
            return;
        }

        User user = User.dao.findFirst("select * from s_user where loginname=? and isAdmin='0' ", cc_username);

        if (user == null) {
            renderFailJSON("用户不存在", "");
            return;
        }

        if (BCrypt.checkpw(cc_password, user.getPassword())) {
            if (user.getStatus().equals(Consts.STATUS.enable.getVal())) {
                if(StrKit.notBlank(rm)&&rm.equals("0"))
                    CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), 60*60*24*14);
                else
                    CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), Consts.COOKIE_TIMEOUT);
                Map<String, String> data = new HashMap<String, String>();
                data.put("callback", callBack);

                user.setLogged(new Date());
                user.update();
                renderSuccessJSON("登录成功", data);

                return;
            } else {
                renderFailJSON("该用户被禁用", "");
                return;
            }
        } else {
            renderFailJSON("密码不正确", "");
            return;
        }

    }

    public void logout(){
        CookieKit.remove(this,Consts.USER_ACCESS_TOKEN);
        CacheKit.remove(Consts.CACHE_NAMES.user.name(),currUser().getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),currUser().getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),currUser().getId());
        redirect(PropKit.use("config.properties").get("domain")+"/admincc/toLogin");
    }
    @Clear({CommonInterceptor.class,UserInterceptor.class})
    public void toLogin(){
        render("/admin/login.html");
    }

    public void refreshCache(){
        DBDCmsConfig.initCache();
        renderSuccessJSON("缓存刷新成功","");
    }

    public void goSetting(){
        render("/admin/userSetting.html");
    }


}
