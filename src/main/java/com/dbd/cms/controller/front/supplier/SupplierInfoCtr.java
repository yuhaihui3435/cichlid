package com.dbd.cms.controller.front.supplier;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.BeetlCacheInterceptor;
import com.dbd.cms.model.Supplier;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.ehcache.CacheName;

/**
 * Created by yuhaihui8913 on 2017/5/26.
 */
public class SupplierInfoCtr extends DBDController {


    public  void index(){
        String sheng=getPara("sheng");
        String shi=getPara("shi");
        String qu=getPara("qu");
        String state=getPara("state");
        Page<Supplier> page;
        String search = getPara("search");
        StringBuffer where = new StringBuffer("from f_supplier fs where 1=1 and fs.d_at is null and fs.state='0'");
        if(StrKit.notBlank(sheng))
            where.append(" and fs.sheng='"+sheng+"'");
        if(StrKit.notBlank(shi))
            where.append(" and fs.shi='"+shi+"'");
        if(StrKit.notBlank(qu))
            where.append(" and fs.qu='"+qu+"'");
        if(StrKit.notBlank(state))
            where.append(" and fs.state='"+state+"'");
        if (!isParaBlank("search")) {
            if(search.contains(" ")){
                String[] strs=search.split(" ");
                where.append(" and (");
                for(String s : strs){
                    where.append(" instr(fs.name,'"+s+"')>0 or instr(fs.tel,'"+s+"')>0 ");
                }
                where.append(")");
                LogKit.info(where.toString());
            }else {
                where.append(" and (instr(fs.name,'"+search+"')>0 or instr(fs.tel,'"+search+"')>0 )");
            }
            where.append(" order by fs.isTop desc");
            page = Supplier.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        } else {
            where.append(" order by fs.isTop desc");
            page = Supplier.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }

//        if(StrKit.notBlank(shi)){
//            String shengName=(String)DBDCmsConfig.getRf().groupTemplate.getSharedVars().get("sheng"+sheng);
//            String shiStr= CacheKit.get(Consts.CACHE_NAMES.ssq.name(),shengName+sheng);
//            List<SSQ> shiList= JSON.parseArray(shiStr,SSQ.class);
//            setAttr("shiList",shiList);
//        }
//        if(StrKit.notBlank(qu)){
//            String shiName=(String)DBDCmsConfig.getRf().groupTemplate.getSharedVars().get("shi"+shi);
//            String quStr= CacheKit.get(Consts.CACHE_NAMES.ssq.name(),shiName+shi);
//            List<SSQ> quList=JSON.parseArray(quStr,SSQ.class);
//            setAttr("quList",quList);
//        }

        setAttr("page",page);
        setAttr("sheng",sheng);
        setAttr("shi",shi);
        setAttr("qu",qu);
        setAttr("search",search);
        render("/front/supplierinfo/list.html");
    }

    @Before(BeetlCacheInterceptor.class)
    @CacheName("supplierView")
    public void view(){
        String sheng=null,shi=null,qu=null,search=null;
        if(isParaExists("sheng"))
            sheng=getPara("sheng");
        if(isParaExists("shi"))
            shi=getPara("shi");
        if(isParaExists("qu"))
            qu=getPara("qu");
        if(isParaExists("search"))
            search=getPara("search");
        Integer id=getParaToInt("id");
        Supplier supplier=Supplier.dao.findById(id);
        setAttr("supplier",supplier);
        setAttr("next",Supplier.dao.findAdjoinNext(id,sheng,shi,qu,search));
        setAttr("pre",Supplier.dao.findAdjoinPre(id,sheng,shi,qu,search));
        setAttr("sheng",sheng);
        setAttr("shi",shi);
        setAttr("qu",qu);
        setAttr("contentId",supplier.getId().toString());
        setAttr("authorId",supplier.getUserId());
        setAttr("contentModule", Consts.CACHE_NAMES.supplierView.name());
        render("/front/supplierinfo/view.html");
    }

}
