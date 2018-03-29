package com.dbd.cms.controller;

import com.alibaba.fastjson.JSON;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.kits.QiNiuKit;
import com.dbd.cms.model.Knowledgebase;
import com.dbd.cms.model.Orderdetail;
import com.dbd.cms.model.SSQ;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/1/4.
 */
public class CommonCtr extends DBDController {

    public void uptoken(){
        renderJson("uptoken", QiNiuKit.getUpToken());
    }

    /**
     * 根据省查询下级市
     */
    public void queryShiList(){
        String shengName=getPara("shengName");
        String shengId=getPara("shengId");
        String ck=shengName+shengId;
        //LogKit.info(JSON.toJSONString(CacheKit.getKeys(Consts.CACHE_NAMES.ssq.name())));

        String json=CacheKit.get(Consts.CACHE_NAMES.ssq.name(),ck);
        List<SSQ> shiList=JSON.parseArray(json,SSQ.class);
        renderJson(shiList);
    }

    /**
     * 根据市查询下级区
     */
    public void queryQuList(){
        String shiName=getPara("shiName");
        String shiId=getPara("shiId");
        String ck=shiName+shiId;
        String json=CacheKit.get(Consts.CACHE_NAMES.ssq.name(),ck);
        List<SSQ> quList=JSON.parseArray(json,SSQ.class);
        renderJson(quList);
    }

    @Before(Tx.class)
    public void synZh(){
        Orderdetail orderdetail=null;
        Knowledgebase knowledgebase=null;
        String zhName=null;
        String[] zhNames=null;
        boolean append=true;
        List<Orderdetail> list=Orderdetail.dao.find("select * from f_orderdetail where d_at is null ");
        for (int i = 0; i < list.size(); i++) {
            append=true;
            orderdetail=list.get(i);
            if(orderdetail.getKbId()!=null){
                knowledgebase=Knowledgebase.dao.findById(orderdetail.getKbId());
                zhName=knowledgebase.getZhName();
                if(StrKit.notBlank(zhName)) {
                    zhNames = zhName.split(",");
                    if (zhNames != null && zhNames.length > 0) {
                        for (String s : zhNames) {
                            if (orderdetail.getZhName().equals(s)) {
                                append = false;
                                break;
                            }
                        }
                    }
                }


                if (append) {
                    if(StrKit.notBlank(zhName)) {
                        if(StrKit.notBlank(orderdetail.getZhName())&&!orderdetail.getZhName().equals(zhName))
                        knowledgebase.setZhName(zhName + "," + orderdetail.getZhName());
                    }else {
                        knowledgebase.setZhName( orderdetail.getZhName());
                    }
                    LogKit.info(knowledgebase.getZhName());
                    knowledgebase.update();
                }
            }
        }

        renderSuccessJSON("知识库中文同步成功","");

    }




}
