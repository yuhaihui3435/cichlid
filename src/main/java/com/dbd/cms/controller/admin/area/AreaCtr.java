package com.dbd.cms.controller.admin.area;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.PinyinKit;
import com.dbd.cms.model.Area;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

/**
 * Created by yuhaihui8913 on 2017/1/18.
 */
@Before({UserInterceptor.class})
public class AreaCtr extends DBDController{

    public void index(){
        render("/admin/area/list.html");
    }

    public void list(){
        Page<Area> page;
        String serach = getPara("search");
        StringBuilder where=new StringBuilder();
        where.append(" from f_area where 1=1 ");
        if(isParaExists("zhFilter")){
            where.append(" and zhName ='' ");
        }
        if(StrKit.notBlank(serach)){
            where.append(" and (instr(enName,?)>0 or instr(zhName,?)>0 or instr(py,?)>0 )");
            page=Area.dao.paginate(getPN(),getPS(),"select * ",where.toString(),serach,serach,serach);
        }else{
            page=Area.dao.paginate(getPN(),getPS(),"select * ",where.toString());
        }
        rendBootTable(page);
    }
    @Before({AreaValidator.class,Tx.class})
    public void save(){
        Area area=getModel(Area.class);
        if(StrKit.notBlank(area.getZhName()))
        area.setPy(PinyinKit.getFirstSpell(area.getZhName()));
        area.save();
        CacheKit.remove(Consts.CACHE_NAMES.fArea.name(),Consts.CACHE_NAMES.fArea.name()+area
        .getId());
        CacheKit.remove(Consts.CACHE_NAMES.fArea.name(),Consts.CACHE_NAMES.fArea.name());
        DBDCmsConfig.initCache();
        renderSuccessJSON("新增地域成功","");
    }
    @Before({AreaValidator.class, Tx.class})
    public void update(){
        Area area=getModel(Area.class);
        if(StrKit.notBlank(area.getZhName())){
            area.setPy(PinyinKit.getFirstSpell(area.getZhName()));
        }
        area.update();
        CacheKit.remove(Consts.CACHE_NAMES.fArea.name(),Consts.CACHE_NAMES.fArea.name()+area.getId());
        CacheKit.remove(Consts.CACHE_NAMES.fArea.name(),Consts.CACHE_NAMES.fArea.name());
        DBDCmsConfig.initCache();
        renderSuccessJSON("修改地域信息成功","");
    }
}
