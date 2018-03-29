package com.dbd.cms.controller.admin.content;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.QiNiuKit;
import com.dbd.cms.kits.ReqKit;
import com.dbd.cms.model.Content;
import com.dbd.cms.model.Mapping;
import com.dbd.cms.model.Taxonomy;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinal.upload.UploadFile;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.content]
 * 类名称:    [ContentCtr]
 * 类描述:    [内容管理]
 * 创建人:    [于海慧]
 * 创建时间:  [2017/1/3]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
@Before(UserInterceptor.class)
public class ContentCtr extends DBDController {

    public void index() {
        render("/admin/content/list.html");
    }

    public void list() {
        Page<Content> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from c_content c left join s_user u on c.user_id=u.id where 1=1 and c.d_at is null ");
        if (!isParaBlank("search")) {
            where.append(" and (instr(c.title,?)>0 or instr(c.text,?)>0 or instr(c.summary,?)>0 or instr(c.meta_keywords,?)>0 or instr(u.nickname,?)>0)");
            where.append(" order by c.c_at desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString(), serach, serach, serach, serach, serach);
        } else {
            where.append(" order by c.c_at desc");
            page = Content.dao.paginate(getPN(), getPS(), "select c.* ", where.toString());
        }
        rendBootTable(page);
    }

    public void toA() {
        render("/admin/content/add.html");
    }
    public void toE() {
        int id=getParaToInt("id");
        setAttr("content",Content.dao.findById(id));
        render("/admin/content/edit.html");
    }

    @Before({Tx.class})
    public void saveOrUpdate() {

        UploadFile uf = getFile();
        String pName, fileName = null;
        if(uf!=null) {
            pName = uf.getParameterName();
            fileName = "images/" + System.currentTimeMillis() + uf.getFileName();
            if (!isParaBlank(pName + "_bak")) {
                QiNiuKit.del(getPara(pName + "_bak"));
            }
            QiNiuKit.upload(uf.getFile(), fileName);
        }else{
            fileName=getPara("thumbnail" + "_bak");
        }

        Content content = getModel(Content.class);
        Integer areaId = getParaToInt("area");
        Integer catalogId = getParaToInt("catalog");
        String tags_str = getPara("tags");
        String tags[]=null;
        if(StrKit.notBlank(tags_str))
            tags=tags_str.split(",");
        Mapping mapping = null;
        Taxonomy taxonomy=null;

        //没有保存过的草稿
        if (content.getId() == null) {

            content.setCAt(new Date());
            //content.setFlag(Consts.CONTENT_FLAG.drfat.getVal());
            content.setRate(0);
            content.setRateCount(new Long(0));
            content.setUserIp(ReqKit.getRemortIP(getRequest()));
            String contentCheckState = CacheKit.get(Consts.CACHE_NAMES.paramCache.name(), "contentCheckState");
            if (contentCheckState != null && contentCheckState.equals(Consts.YORN_STR.yes.getVal()))
                content.setStatus(Consts.CHECK_STATUS.waitingCheck.getVal());
            else
                content.setStatus(Consts.CHECK_STATUS.normal.getVal());
            content.setUserAgent(ReqKit.getUseragent(getRequest()));
            content.setUserId(currUser().getId());
            content.setThumbnail(fileName);
            content.save();
            mapping = new Mapping();
            mapping.setContentId(BigInteger.valueOf(content.getLong("id")));
            mapping.setTaxonomyId(BigInteger.valueOf(catalogId));
            mapping.save();
            if(!isParaBlank("area")) {
                mapping = new Mapping();
                mapping.setContentId(BigInteger.valueOf(content.getLong("id")));
                mapping.setTaxonomyId(BigInteger.valueOf(areaId));
                mapping.save();

            }

            if(tags!=null) {

                for (String i : tags) {
                    taxonomy=Taxonomy.dao.findByTitleByCache(i);
                    if(taxonomy==null){
                        taxonomy=new Taxonomy();
                        taxonomy.setType("tag");
                        taxonomy.setContentModule("artice");
                        taxonomy.setTitle(i);
                        taxonomy.setParentId(BigInteger.valueOf(2));
                        taxonomy.save();

                    }

                    mapping = new Mapping();
                    mapping.setContentId(BigInteger.valueOf(content.getLong("id")));
                    if(taxonomy.get("id") instanceof Long)
                        mapping.setTaxonomyId(BigInteger.valueOf(taxonomy.getLong("id")));
                    else
                        mapping.setTaxonomyId(taxonomy.getBigInteger("id"));
                    mapping.save();
                }
            }

            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_TAG_CK);
            DBDCmsConfig.getRf().groupTemplate.getSharedVars().put("tags",Taxonomy.dao.findTag());

        }
        else {
            CacheKit.remove(Consts.CACHE_NAMES.article.name(),"findCatalogByArticle"+content.getId());
            CacheKit.remove(Consts.CACHE_NAMES.article.name(),"findAreaByArticle"+content.getId());
            CacheKit.remove(Consts.CACHE_NAMES.article.name(),"findTagsByArticle"+content.getId());
            content.setMAt(new Date());
            content.setUserIp(ReqKit.getRemortIP(getRequest()));
            content.setThumbnail(fileName);
            content.setUserAgent(ReqKit.getUseragent(getRequest()));
            if(fileName!=null)content.setThumbnail(fileName);
            Mapping.dao.delByContentId(content.getId());

            mapping = new Mapping();
            mapping.setContentId(content.getBigInteger("id"));
            mapping.setTaxonomyId(BigInteger.valueOf(catalogId));
            mapping.save();
            if(!isParaBlank(    "area")) {
                mapping = new Mapping();
                mapping.setContentId(content.getBigInteger("id"));
                mapping.setTaxonomyId(BigInteger.valueOf(areaId));
                mapping.save();
            }
            if(tags!=null) {

                for (String i : tags) {
                    taxonomy=Taxonomy.dao.findByTitleByCache(i);
                    if(taxonomy==null){
                        taxonomy=new Taxonomy();
                        taxonomy.setType("tag");
                        taxonomy.setContentModule("artice");
                        taxonomy.setTitle(i);
                        taxonomy.setParentId(BigInteger.valueOf(2));
                        taxonomy.save();
                    }

                    mapping = new Mapping();
                    mapping.setContentId(content.getBigInteger("id"));
                    if(taxonomy.get("id") instanceof Long)
                        mapping.setTaxonomyId(BigInteger.valueOf(taxonomy.getLong("id")));
                    else
                        mapping.setTaxonomyId(taxonomy.getBigInteger("id"));
                    mapping.save();
                }
            }
            content.update();

            CacheKit.remove(Consts.CACHE_NAMES.taxonomy.name(),Consts.T_TAG_CK);
            DBDCmsConfig.getRf().groupTemplate.getSharedVars().put("tags",Taxonomy.dao.findTag());
        }
        CacheKit.removeAll(Consts.CACHE_NAMES.article.name());
        renderSuccessJSON("保存成功","");

    }

    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void setTop(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setTop(false);
        c.update();
        renderSuccessJSON("置顶设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void cancelTop(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setTop(true);
        c.update();
        renderSuccessJSON("置顶取消设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void setGood(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setGood(false);
        c.update();
        renderSuccessJSON("精华设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void cancelGood(){
        int id=getParaToInt("content.id");
        Content c=Content.dao.findById(id);
        c.setGood(true);
        c.update();
        renderSuccessJSON("精华取消设置成功","");
    }
    @Before({Tx.class, EvictInterceptor.class})
    @CacheName("article")
    public void del(){

        String ids=getPara("ids");
        List<Integer> idsList=new ArrayList<Integer>();
        if(ids==null){
            renderFailJSON("没有找到要删除的文章","");
            return ;
        }else{
            String[] array=ids.split(",");
            for(String s:array){
                idsList.add(Integer.parseInt(s));
            }
        }


        for(Integer i:idsList){
            Content c=Content.dao.findById(i);
            c.setDAt(new Date());
            c.update();
        }
        renderSuccessJSON("删除成功","");

    }

    public void view(){
        int id=getParaToInt("id");
        Content c=Content.dao.findById(id);

        if(c==null||c.getDAt()!=null){
            renderError(404);
            return ;
        }
        setAttr("content",c);
        render("/admin/content/view.html");
    }


    public void get(){
        int id=getParaToInt("id");
        renderJson(Content.dao.findById(id));
        //renderCaptcha();
    }


}
