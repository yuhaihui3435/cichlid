package com.dbd.cms.controller.admin.role;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.model.Res;
import com.dbd.cms.model.Role;
import com.dbd.cms.model.RoleRes;
import com.dbd.cms.model.UserRole;
import com.google.common.base.Splitter;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.Iterator;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2016/12/5.
 */
@Before(UserInterceptor.class)
public class RoleCtr extends DBDController{


    public void list(){
        String search=getPara("search");
        StringBuffer where =new StringBuffer("from s_role sr where 1=1 ");
        Page page=null;
        if(!isParaBlank("search")){
            where.append(" and instr(sr.name,?)>0");
             page=Role.dao.paginate(getPN(),getPS(),"select * ",where.toString(),search);
        }else{
             page=Role.dao.paginate(getPN(),getPS(),"select * ",where.toString());
        }
        rendBootTable(page);
    }
    @Before({RoleValidator.class,Tx.class})
    public void save(){
        Role role=getModel(Role.class);
        role.save();
        renderSuccessJSON("角色新增成功","");
    }
    @Before({RoleValidator.class,Tx.class})
    public void update(){
        Role role=getModel(Role.class);
        role.update();


        renderSuccessJSON("角色更新成功","");
    }
    @Before(Tx.class)
    public void del(){
        if(isParaExists("ids")){
            String ids=getPara("ids");
            String[] _dis=ids.split(",");
            int id=0;
            for(String s:_dis) {
                id=Integer.parseInt(s);
                Role.dao.deleteById(id);
                RoleRes.dao.delByRoleId(id);
                UserRole.dao.delByRoleId(id);
            }
            renderSuccessJSON("角色删除成功","");

        }else{
            renderFailJSON("角色删除失败,没有得到角色id","");
        }
    }
    public void loadRes(){
        int roleId=getParaToInt("roleId",-1);
        List<Res> list=Res.dao.find("select sr.* from s_res sr,s_role_res srr where sr.id=srr.resId and srr.roleId=?",roleId);
        List<Res> all= Res.dao.find("select * from s_res");
        all.removeAll(list);
        JSONArray jsonArray=new JSONArray();
        JSONObject jo=null;
        for(Res r:all){
            jo=new JSONObject();
            jo.put("id",r.getId());
            jo.put("name",r.getName());
            jo.put("pId",r.getPid());
            jsonArray.add(jo);
        }
        for(Res rr:list){
            jo=new JSONObject();
            jo.put("id",rr.getId());
            jo.put("name",rr.getName());
            jo.put("pId",rr.getPid());
            jo.put("checked",true);
            jsonArray.add(jo);
        }
        renderJson(jsonArray.toJSONString());
    }
    @Before(Tx.class)
    public void setRes(){
        int roleId=getParaToInt("roleId");
        RoleRes.dao.delByRoleId(roleId);
        String resIds=getPara("resIds");
        if(StrKit.notBlank(resIds)) {
            Iterable iterable = Splitter.on(",").split(resIds);
            String resId=null;
            RoleRes rr=null;
            Iterator<String> it=iterable.iterator();
            while(it.hasNext()){
                resId=(String)it.next();
                rr=new RoleRes();
                rr.setResId(new Integer(resId));
                rr.setRoleId(roleId);
                rr.save();
            }
        }


        renderSuccessJSON("设置权限成功","");
    }
}

