package com.dbd.cms.controller.front.userinfo;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.kits.DateKit;
import com.dbd.cms.kits.ext.BCrypt;
import com.dbd.cms.model.OnceLog;
import com.dbd.cms.model.User;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfplugin.mail.MailKit;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.front]
 * 类名称:    [UserInfoCtr]
 * 类描述:    [社区用户控制]
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/17]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class UserInfoCtr extends DBDController {
    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/17
     * @Description:密码找回界面
     **/
    public void tfp() {
        render("/front/userinfo/findPassword.html");
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/17
     * @Description:密码找回
     **/
    @Before(Tx.class)
    public void fp() {
        String loginname = getPara("loginname");
        String email = getPara("email");
        String phone = getPara("phone");

        User user = User.dao.findFirst("select * from s_user where email=? ", email);
        if (user == null) {
            renderFailJSON("您填写的密码找回信息不一致！", "");
        } else {
            String timelimit = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "find_password_timelimt");
            timelimit = (StrKit.notBlank(timelimit)) ? timelimit : "1800";
            Long currentTimeMillisr = System.currentTimeMillis();
            String salt = BCrypt.gensalt();
            String num = BCrypt.hashpw(currentTimeMillisr.toString(), salt);
            OnceLog onceLog = new OnceLog();
            Date now = new Date();
            onceLog.setCAt(now);
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTime(now);
            gc.add(7, +Integer.parseInt(timelimit));
            gc.set(gc.get(Calendar.YEAR), gc.get(Calendar.MONTH), gc.get(Calendar.DATE), gc.get(Calendar.HOUR), gc.get(Calendar.MINUTE), gc.get(Calendar.SECOND));
            SimpleDateFormat sf = new SimpleDateFormat(DateKit.STR_DATEFORMATE);
            onceLog.setLAt(gc.getTime());
            onceLog.setOperName("/userinfo/toSetNewPassword");
            String randomCode = loginname + email + phone + System.currentTimeMillis();
            onceLog.setCode(BCrypt.hashpw(randomCode, BCrypt.gensalt()));
            onceLog.save();
            String emailTxt = "您好：请点击<a href=\"" + getRequest().getContextPath() + "userinfo/toSetNewPassword/" + randomCode + "/" + loginname + "\">重置密码</a>，请妥善保存密码。";
            MailKit.send(email, null, (String) CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "appName"), emailTxt);
            renderSuccessJSON("密码找回成功，请在" + Integer.parseInt(timelimit) / 60 + "分钟内登录您注册时使用的邮箱设置新密码!", "");
        }
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/17
     * @Description:设置新密码
     **/
    @Before(Tx.class)
    public void tsnp() {
        String code = getPara(0);
        String loginname = getPara(1);
        OnceLog onceLog = OnceLog.dao.findFirst("select * from s_once_log where code=? and d_at is null", code);
        if (onceLog == null) {
            setAttr(ERROR_MSG, "操作失效");
            LogKit.info("操作失效，已经被处理");
            render("/common/f_bns_err.html");
            return;
        }
        Date lat = onceLog.getLAt();
        Date now = new Date();
        if (!onceLog.getOperName().equals("/userinfo/toSetNewPassword.html")) {
            onceLog.setResult("02");//对应的操作不一致
            onceLog.setDAt(new Date());
            onceLog.update();
            setAttr(ERROR_MSG, "操作不一致");
            LogKit.info("操作不一致");
            render("/common/f_bns_err");
            return;
        }
        if (now.compareTo(lat) < 0) {
            onceLog.setResult("00");//已经操作
            onceLog.update();
            render("/admin/userinfo/setNewPassword.html");
        } else {
            LogKit.info("操作超时");
            setAttr(ERROR_MSG, "操作因超时已失效，请重新申请密码找回");
            onceLog.setResult("01");//过期
            onceLog.setDAt(new Date());
            onceLog.update();
            render("/common/f_bns_err.html");
        }
    }


}
