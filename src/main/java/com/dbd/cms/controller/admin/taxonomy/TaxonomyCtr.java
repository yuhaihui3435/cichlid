package com.dbd.cms.controller.admin.taxonomy;

import com.alibaba.fastjson.JSON;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.model.Taxonomy;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.Date;
import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.taxonomy]
 * 类名称:    [TaxonomyCtr]
 * 类描述:    [分类，专区，标签ctr]
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/26]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
@Before(UserInterceptor.class)
public class TaxonomyCtr extends DBDController {

    public void index(){
        List<String> modules= CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),Consts.CACHE_NAMES.modules.name());
        setAttr("modules",modules);
        render("/admin/taxonomy/main.html");
    }

    public void listTree(){
        StringBuilder sql=new StringBuilder("select * from c_taxonomy where 1=1 and d_at is null ");
        List<Taxonomy> list=null;
        if(isParaBlank("id")){
            sql.append(" and parent_id=0 order by order_number,title ");
            list=Taxonomy.dao.find(sql.toString());
        }else{
            sql.append("and parent_id=? order by order_number,title");
            list=Taxonomy.dao.find(sql.toString(),getParaToInt("id"));
        }
        LogKit.info(JSON.toJSONString(list));
        renderJson(JSON.toJSONString(list));
    }
    @Before({TaxonomyValidator.class,Tx.class})
    public void save(){
        Taxonomy taxonomy=getModel(Taxonomy.class);
        taxonomy.save();
        clearCache(taxonomy);
        DBDCmsConfig.initCache();
        renderSuccessJSON("保存成功","");
    }
    @Before({TaxonomyValidator.class,Tx.class})
    public void update(){
        Taxonomy taxonomy=getModel(Taxonomy.class);
        if(isParaBlank("taxonomy.menu"))
            taxonomy.setMenu(1);
        if(isParaBlank("taxonomy.show_status"))
            taxonomy.setShowStatus(1);
        taxonomy.update();
        clearCache(taxonomy);
        DBDCmsConfig.initCache();
        renderSuccessJSON("更新成功","");
    }
    @Before(Tx.class)
    public void del(){
        Integer id=getParaToInt("id");
        Taxonomy taxonomy=Taxonomy.dao.findById(id);
        taxonomy.setDat(new Date());
        taxonomy.update();
        clearCache(taxonomy);
        DBDCmsConfig.initCache();
        renderSuccessJSON("删除成功","");
    }

    private void clearCache(Taxonomy taxonomy){
        CacheKit.removeAll(Consts.CACHE_NAMES.taxonomy.name());
//        if(taxonomy.getType().equals("catalog"))
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_CATALOG_CK);
//        else if(taxonomy.getType().equals("area"))
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_AREA_CK);
//        else if(taxonomy.getType().equals("tag"))
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_AREA_CK);
//        else if(taxonomy.getType().equals("species"))
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_SPECIES_CK);
//        else if(taxonomy.getType().equals("subGroup"))
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_SUBGROUP_CK);
//        else if(taxonomy.getType().equals("orderSupport"))
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_ORDERSUPPORT_CK);
//        if(taxonomy.getParentId()!=null&&taxonomy.getParentId().intValue()!=0)
//        CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),"pId"+taxonomy.getParentId());
//        Object o=taxonomy.get("id");
//        if(o instanceof Long)
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),"taxonomy-"+taxonomy.getLong("id"));
//        if(o instanceof BigInteger)
//            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),"taxonomy-"+taxonomy.getBigInteger("id"));

    }
    @Clear({UserInterceptor.class})
    public void getChildrenByPId(){
        int pId=getParaToInt("pId");
        List<Taxonomy> list=Taxonomy.dao.findChildrenByPId(pId);
        renderJson(list);
    }

}
