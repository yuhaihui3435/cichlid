package com.dbd.cms.controller.admin.setting;

import com.alibaba.fastjson.JSON;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.QiNiuKit;
import com.dbd.cms.model.Param;
import com.jfinal.aop.Before;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.upload.UploadFile;

import java.util.Enumeration;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2016/12/28.
 */
@Before(UserInterceptor.class)
public class SettingCtr extends DBDController {

    public void index() {
        render("/admin/setting/main.html");
    }

    public void getSettingJSON() {
        List<Param> paramList = Param.dao.find("select * from " + Param.TABLE);
        String str = JSON.toJSONString(paramList);
        LogKit.info(str);
        renderJson(str);
    }

    @Before(Tx.class)
    public void save() {
        List<UploadFile> fileList = getFiles();
        String pName, fileName = null;
        Param param = null;
        for (UploadFile uf : fileList) {
            pName = uf.getParameterName();

            fileName = "images/" + System.currentTimeMillis() + uf.getFileName();
            if(!isParaBlank(pName+"_bak")){
                QiNiuKit.del(getPara(pName+"_bak"));
            }
            QiNiuKit.upload(uf.getFile(), fileName);
            param = Param.dao.findByKey(pName);
            if (param == null) {
                param = new Param();
                param.setK(pName);
                param.setVal(fileName);
                param.setNote(uf.getFile().length()+"");
                param.save();
            } else {
                param.setVal(fileName);
                param.setNote(uf.getFile().length()+"");
                param.update();
            }
        }
        Enumeration<String> stringEnumeration = getParaNames();
        String key;
        String val = null;
        while (stringEnumeration.hasMoreElements()) {
            key = stringEnumeration.nextElement();
            if(key.contains("_bak"))continue;
            param = Param.dao.findByKey(key);
            val = getPara(key);
            if (isParaExists(key + "_bak")) {
                if (!isParaBlank(key + "_bak") && isParaBlank(key)) {
                    val = getPara(key + "_bak");
                }
            }
            if (param == null) {
                param = new Param();
                param.setK(key);
                param.setVal(val);
                param.save();
            } else {
                param.setVal(val);
                param.update();
            }

        }
        DBDCmsConfig.initCache();
        renderSuccessJSON("设置成功", "");
    }

}
