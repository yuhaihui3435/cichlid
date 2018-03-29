package com.dbd.cms.controller.front.article;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.BeetlCacheInterceptor;
import com.dbd.cms.model.Content;
import com.dbd.cms.model.Taxonomy;
import com.jfinal.aop.Before;
import com.jfinal.plugin.ehcache.CacheName;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/6/21.
 */
public class ArtCtr extends DBDController {

    public void index(){
        Integer tId=null;
        if(isParaExists("tId")){
            tId=getParaToInt("tId");
        }

        if(tId!=null)
            setAttr("taxonomy", Taxonomy.dao.findById(tId));
        List<Taxonomy> taxonomyList=(List<Taxonomy>) DBDCmsConfig.getRf().groupTemplate.getSharedVars().get("catalogs");
        for(Taxonomy taxonomy:taxonomyList){
            taxonomy.setContentCount(Content.dao.findCountInTaxonomy(taxonomy.getId().intValue()));
        }

        setAttr("page", Content.dao.findContentByTIdAndType(tId,getPN(),getPS(),"catalog"));
        render("/front/article/list.html");
    }
    @Before(BeetlCacheInterceptor.class)
    @CacheName("article")
    public void view(){
        int id=getParaToInt("id");
        int tId=0;
        if(isParaExists("tId")&&!isParaBlank("tId")){
            tId=getParaToInt("tId");
        }
        Content c=Content.dao.findFirst("select * from c_content where id=?",id);

        if(c==null||c.getDAt()!=null){
            renderError(404);
            return ;
        }

        setAttr("taxonomy",Taxonomy.dao.findByContentId(id));
        setAttr("art",c);
        setAttr("next",Content.dao.findAdjoinNext(id,tId));
        setAttr("pre",Content.dao.findAdjoinPre(id,tId));
        setAttr("contentModule", Consts.CACHE_NAMES.article.name());
        setAttr("contentId",c.getId().toString());
        setAttr("authorId",c.getUserId().toString());
        if(tId!=0)
        setAttr("tId",tId);
        render("/front/article/view.html");
    }


    public void queryByTag(){
        Integer tag=getParaToInt("tId");

    }
}
