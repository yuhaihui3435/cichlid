package com.dbd.cms.interceptor;

import com.dbd.cms.Consts;
import com.dbd.cms.kits.CookieKit;
import com.dbd.cms.model.Res;
import com.dbd.cms.model.Role;
import com.dbd.cms.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;

import java.math.BigInteger;


public class CommonInterceptor implements Interceptor {

    public void intercept(Invocation inv) {
        Controller controller = inv.getController();
        String userId= CookieKit.get(controller,Consts.USER_ACCESS_TOKEN);
        String ck=inv.getControllerKey();
        if (StrKit.notBlank(userId)) {
            User user=User.dao.findFirstByCache(Consts.CACHE_NAMES.user.name(),new BigInteger(userId),"select * from s_user where status='0' and id=? ",new BigInteger(userId));
            controller.setAttr(Consts.CURR_USER,user);
            controller.setAttr(Consts.CURR_USER_ROLES, Role.dao.findRolesByUserId(user.getId()));
            controller.setAttr(Consts.CURR_USER_RESES, Res.dao.findResesByUserId(user.getId()));
        }
        inv.invoke();
    }

}
