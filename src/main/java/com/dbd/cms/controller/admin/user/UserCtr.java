package com.dbd.cms.controller.admin.user;

import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.ext.BCrypt;
import com.dbd.cms.model.Role;
import com.dbd.cms.model.User;
import com.dbd.cms.model.UserRole;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/12/2.
 */
@Before(UserInterceptor.class)
public class UserCtr extends DBDController {
    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/2
     * @Description: 查询
     **/
    public void index() {
        render("/admin/user/list.html");
    }

    public void list() {
        Page<User> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from s_user userinfo where 1=1 ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(userinfo.email,?)>0 or instr(userinfo.phone,?)>0 or instr(userinfo.nickname,?)>0 or instr(userinfo.loginname,?)>0)");
            page = User.dao.paginate(getPN(), getPS(), "select * ", where.toString(), serach, serach, serach, serach);
        } else {
            page = User.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }
        rendBootTable(page);
    }

    @Before({UserValidator.class, Tx.class})
    public void save() {
        User user = getModel(User.class);
        Integer[] roledIds = null;
        if (isParaExists("roleIds")) {
            roledIds = getParaValuesToInt("roleIds");
        }
        user.setCAt(new Date());
        user.setEmailStatus(Consts.YORN.no.isVal());
        user.setPhoneStatus(Consts.YORN.no.isVal());
        user.setMAt(new Date());
        user.setStatus(Consts.STATUS.enable.getVal());
        user.setSignature("写点啥吧～");
        user.setSalt(BCrypt.gensalt());
        user.setPassword(BCrypt.hashpw(user.getLoginname(), user.getSalt()));
        String default_avart = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "qn_url") + "image/avatar/dft-avatar.jpg";
        user.setAvatar(default_avart);
        user.save();
        if (roledIds != null) {
            UserRole ur = null;
            for (Integer i : roledIds) {
                ur = new UserRole();
                ur.setRid(i);
                ur.setUid(user.getLong("id"));
                ur.save();
            }
        }


        renderSuccessJSON("新增用户信息成功", "");
    }

    @Before({UserValidator.class, Tx.class})
    public void update() {
        User user = getModel(User.class);
        user.setMAt(new Date());
        Integer[] roledIds = null;
        if (isParaExists("roleIds")) {
            roledIds = getParaValuesToInt("roleIds");
        }


        if (roledIds != null) {
            UserRole.dao.delByUserId(user.getId().intValue());
            UserRole ur = null;
            for (Integer i : roledIds) {
                ur = new UserRole();
                ur.setRid(i);
                ur.setUid(user.getId().longValue());
                ur.save();
            }
        }CacheKit.remove(Consts.CACHE_NAMES.user.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),user.getId());
        user.update();
        renderSuccessJSON("更新用户信息成功", "");
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/10
     * @Description: 查询用户持有的角色和全部角色
     **/
    public void loadRoles() {
        int userId = getParaToInt("userId", -1);
        List<Role> ownRoles = Lists.newArrayList();
        List<Role> allRoles = Lists.newArrayList();
        if (userId != -1) {
            ownRoles = Role.dao.find("select sr.* from s_role sr,s_user_role sur where sr.id=sur.rId and sur.uId=?", userId);
        }
        allRoles = Role.dao.find("select * from s_role ");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("ownRoles", ownRoles);
        jsonObject.put("allRoles", allRoles);
        LogKit.debug(jsonObject.toJSONString());
        renderJson(jsonObject.toJSONString());
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/10
     * @Description:用户禁用操作处理
     **/
    @Before(Tx.class)
    public void forbidden() {
        String userIds = getPara("ids");
        Iterable iterable = Splitter.on(",").split(userIds);
        Iterator<String> it = iterable.iterator();
        int id;
        User user = null;
        while (it.hasNext()) {
            id = Integer.parseInt(it.next());
            user = new User();
            user.setId(BigInteger.valueOf(id));
            user.setStatus(Consts.STATUS.forbidden.getVal());
            user.update();
        }

        renderSuccessJSON("禁用操作执行成功。", "");
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/10
     * @Description:恢复操作处理
     **/
    @Before(Tx.class)
    public void resumed() {
        String userIds = getPara("ids");
        Iterable iterable = Splitter.on(",").split(userIds);
        Iterator<String> it = iterable.iterator();
        int id;
        User user = null;
        while (it.hasNext()) {
            id = Integer.parseInt(it.next());
            user = new User();
            user.setId(BigInteger.valueOf(id));
            user.setStatus(Consts.STATUS.enable.getVal());
            user.update();
        }

        renderSuccessJSON("恢复操作执行成功。", "");
    }
    @Before(Tx.class)
    public void modifyPassword(){
        String loginname=getPara("loginname");
        String old_pwd=getPara("oldPwd");
        String new_pwd=getPara("newPwd");
        if(old_pwd.equals(new_pwd)){
            renderFailJSON("新密码不能同旧密码一致。","");
        }else{
            User user=User.dao.findFirst("select * from s_user where loginname=? ",loginname);
            Boolean bl=BCrypt.checkpw(old_pwd,user.getPassword());
            if(bl){
                user.setSalt(BCrypt.gensalt());
                user.setPassword(BCrypt.hashpw(new_pwd,user.getSalt()));
                user.update();
                renderSuccessJSON("密码修改成功。","");
            }else{
                renderFailJSON("您输入的旧密码不正确！","");
            }
        }
    }




}
