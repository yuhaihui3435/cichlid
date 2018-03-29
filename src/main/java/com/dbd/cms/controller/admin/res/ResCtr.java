package com.dbd.cms.controller.admin.res;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.model.Res;
import com.dbd.cms.model.RoleRes;
import com.dbd.cms.model.UserRole;
import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:  [cms]
 * 包:       [com.dbd.cms.controller.admin]
 * 类名称:    [ResCtr]
 * 类描述:    [菜单管理]
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/2]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
@Before(UserInterceptor.class)
public class ResCtr extends DBDController {

    public void index() {
        render("/admin/res/main.html");
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/5
     * @Description: 树形json数据
     **/
    public void listTree() {
        int id = getParaToInt("id",0);
        renderJson(Res.listTree(id));
    }

    @Before({ResValidator.class,Tx.class})
    public void move(){
        int id=getParaToInt("id");
        int pId=getParaToInt("pId");
        Res res=Res.dao.findById(id);
        res.setPid(pId);
        res.update();
        renderSuccessJSON("操作成功","");
    }
    @Before({ResValidator.class,Tx.class})
    public void save(){
        Res res=getModel(Res.class);
        res.save();
        renderSuccessJSON("新增成功","");
    }
    @Before({ResValidator.class,Tx.class})
    public void update(){
        Res res=getModel(Res.class);
        res.update();
        renderSuccessJSON("更新成功","");
    }
    @Before(Tx.class)
    public void del(){
        int id=getParaToInt("id");
        Res.dao.deleteById(id);
        List<RoleRes> list=RoleRes.dao.find("select * from s_role_res where resId=?",id);
        for(RoleRes rr:list){
            RoleRes.dao.deleteById(rr.getId().longValue());
            List<UserRole> userRoles=UserRole.dao.find("select * from s_user_role where rid=?",rr.getRoleId());
            for(UserRole userRole:userRoles){
                CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),userRole.getUid());
                CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),userRole.getUid());
            }
        }
        renderSuccessJSON("删除成功","");
    }

}
