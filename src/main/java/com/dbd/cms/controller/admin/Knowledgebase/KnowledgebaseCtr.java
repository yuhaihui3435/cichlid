package com.dbd.cms.controller.admin.Knowledgebase;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.BeetlCacheInterceptor;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.PinyinKit;
import com.dbd.cms.kits.QiNiuKit;
import com.dbd.cms.model.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinal.upload.UploadFile;

import java.util.Date;

/**
 * Created by yuhaihui8913 on 2017/1/22.
 */
@Before(UserInterceptor.class)
public class KnowledgebaseCtr extends DBDController {

    public void index(){
        render("/admin/knowledgebase/list.html");
    }
    @Clear({UserInterceptor.class})
    public void list(){
        User user=currUser();
        Page<Knowledgebase> page;
        String serach = getPara("search");
        StringBuffer where = new StringBuffer("from f_knowledgebase fk where 1=1 and fk.d_at is null ");
        if (!isParaBlank("search")) {



            serach=optimizeSearch(serach);
            where.append(" and (instr(fk.enName,'" + serach + "')>0 or instr(fk.zhName,'" + serach + "')>0 or instr(fk.scName,'" + serach + "')>0 or instr(fk.bName,'" + serach + "')>0 or instr(fk.py,'" + serach + "')>0)");
            page = Knowledgebase.dao.paginate(getPN(), getPS(), "select * ", where.toString());
            if(!page.getList().isEmpty()){
                rendBootTable(page);
                return ;
            }
            where.delete(0,where.length());
            where.append("from f_knowledgebase fk where 1=1 and fk.d_at is null ");
            String [] _search=serach.split(" ");
            int i=0;
            nextgo:
            for(String s:_search) {
//                s=optimizeSearch(s);
                if(s.equals("sp.")||s.contains("sp."))
                    s=s.replace("sp.","");
                if(StrKit.isBlank(s))continue;

                Character c=null;
                if(StrKit.notBlank(s))
                 c=s.charAt(0);

//                if(i==0||(c!=null&&Character.isLowerCase(c))) {
                if(i<=1){
                    where.append(" and (instr(fk.enName,'" + s + "')>0 or instr(fk.zhName,'" + s + "')>0 or instr(fk.scName,'" + s + "')>0 or instr(fk.bName,'" + s + "')>0 or instr(fk.py,'" + s + "')>0)");
//                    else
//                    where.append(" or (instr(fk.enName,'" + s + "')>0 or instr(fk.zhName,'" + s + "')>0 or instr(fk.scName,'" + s + "')>0 or instr(fk.bName,'" + s + "')>0 or instr(fk.py,'" + s + "')>0)");
                }else{
                    break ;
                }

                i++;
            }
            page = Knowledgebase.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        } else {
            page = Knowledgebase.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }
        rendBootTable(page);
    }
    @Before({EvictInterceptor.class,KnowledgebaseValidator.class,Tx.class})
    @CacheName("knowledgebaseView")
    public void saveOrUpdate(){
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
        Integer[] habitats=getParaValuesToInt("habitats");
        Knowledgebase knowledgebase=getModel(Knowledgebase.class);
        if(StrKit.isBlank(knowledgebase.getRemark())||knowledgebase.getRemark().equals("<p><br></p>")){
            knowledgebase.setRemark("相关资料、图片征集中，如果您愿意提供相关内容可以发送邮件至<a href=\"mailto:"+CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"siteEmail")+"\">"+CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),"siteEmail")+"</a>，期待您的来信。");
        }
        if(knowledgebase.getId()==null)//add
        {
            if(StrKit.notBlank(knowledgebase.getBLen()))
            knowledgebase.setBLen(knowledgebase.getBLen().replace(",","-"));
            knowledgebase.setCAt(new Date());
            knowledgebase.setOper(currUser().getId().intValue());
            if(StrKit.notBlank(knowledgebase.getZhName()))
                knowledgebase.setPy(PinyinKit.getFirstSpell(knowledgebase.getZhName()));
            knowledgebase.setThumbnail((fileName==null)? Consts.DFT_COVER:fileName);
            knowledgebase.save();

            Habitat habitat=null;
            if(habitats!=null){
                for(Integer i:habitats) {
                    habitat = new Habitat();
                    habitat.setFId(knowledgebase.getId().intValue());
                    habitat.setPId(i);
                    habitat.save();
                }
            }

        }else{
            if(StrKit.notBlank(knowledgebase.getBLen()))
            knowledgebase.setBLen(knowledgebase.getBLen().replace(",","-"));
            knowledgebase.setMAt(new Date());
            knowledgebase.setOper(currUser().getId().intValue());
            if(StrKit.notBlank(knowledgebase.getZhName()))
                knowledgebase.setPy(PinyinKit.getFirstSpell(knowledgebase.getZhName()));
            else
                knowledgebase.setPy("");
            knowledgebase.setThumbnail((fileName==null)? Consts.DFT_COVER:fileName);
            knowledgebase.update();
            Habitat.dao.delByFId(knowledgebase.getId().intValue());
            Habitat habitat=null;
            if(habitats!=null){
                for(Integer i:habitats) {
                    habitat = new Habitat();
                    habitat.setFId(knowledgebase.getId().intValue());
                    habitat.setPId(i);
                    habitat.save();
                }
            }
            CacheKit.remove(Consts.CACHE_NAMES.habitat.name(), Consts.CACHE_NAMES.habitat.name()+knowledgebase.getId());
        }

        CacheKit.removeAll(Consts.CACHE_NAMES.knowledge.name());
        renderSuccessJSON("知识库信息保存成功","");
    }
    @Before({EvictInterceptor.class,Tx.class})
    @CacheName("knowledgebaseView")
    public void del(){
        String ids_Str=getPara("ids");
        String[] ids_array=ids_Str.split(",");

        Integer[] ids=new Integer[ids_array.length];
        int i=0;
        for(String s:ids_array){
            ids[i]=Integer.parseInt(s);
            i++;
        }
        if(ids!=null) {
            Knowledgebase knowledgebase=null;
            for(Integer id:ids) {
                knowledgebase = Knowledgebase.dao.findById(id);
                knowledgebase.setDAt(new Date());
                knowledgebase.setOper(currUser().getId().intValue());
                knowledgebase.update();
            }
        }
        CacheKit.removeAll(Consts.CACHE_NAMES.knowledge.name());
        renderSuccessJSON("知识库信息删除成功","");
    }

    public void get(){
        Integer id=getParaToInt("id");
        Knowledgebase knowledgebase=Knowledgebase.dao.findById(id);
        JSONObject jo=(JSONObject) JSON.toJSON(knowledgebase);
        jo.put("subGroupPId",Knowledgebase.dao.findSubGroupPId(knowledgebase.getSgId()));
        jo.put("speciesPId",Knowledgebase.dao.findSpeciesPId(knowledgebase.getSpeciesId()));
        //jo.put("currSubGroups",Knowledgebase.dao.findCurrSubGroups(knowledgebase.getSgId()));
        //jo.put("currSpecies",Knowledgebase.dao.findCurrSpecies(knowledgebase.getSpeciesId()));
        renderJson(jo.toJSONString());
    }
    @Clear(UserInterceptor.class)
    @Before(BeetlCacheInterceptor.class)
    @CacheName("knowledgebaseView")
    public void view(){
        Integer id=getParaToInt("id");
        Knowledgebase knowledgebase=Knowledgebase.dao.findById(id);
        setAttr("knowledgebase",knowledgebase);
        setAttr("contentId",knowledgebase.getId().toString());
        setAttr("authorId",knowledgebase.getOper());
        setAttr("contentModule", Consts.CACHE_NAMES.knowledge.name());
        setAttr("odId",getParaToInt("odId"));
        render("/admin/knowledgebase/view.html");
    }


    private String optimizeSearch(String s){
        if(s.contains("'"))
            s=s.replace("'","");
         if(s.contains("("))
            s=s.replace("("," ");
         if(s.contains(")"))
            s=s.replace(")"," ");
         if(s.contains("-"))
            s=s.replace("-"," ");
         if(s.contains("（"))
            s=s.replace("（"," ");
         if(s.contains("）")){
            s=s.replace("）"," ");
        } if(s.contains("spec")||s.contains("Spec")){
            s=s.toLowerCase();
            s=s.replace("spec","sp");
        }
        return s;
    }
}
