package com.dbd.cms.core;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.jfinal.validate.Validator;

 /**
   *         简介
   *
   * 包:        [com.dbd.cms.core]
   * 类名称:    [DBDValidator]
   * 类描述:    [验证核心类]
   * 创建人:    [于海慧]
   * 创建时间:  [2016/12/4]
   * 修改人:    []
   * 修改时间:  []
   * 修改备注:  []
   * 版本:     [v1.0]
   *
   */
public abstract class DBDValidator extends Validator{

    public String getErrorJSON(){
        String msg=getController().getAttrForStr(Consts.REQ_JSON_CODE.fail.name());
        JSONObject jo=new JSONObject();
        jo.put("resCode", Consts.REQ_JSON_CODE.fail.name());
        jo.put("resMsg",msg);
        return jo.toJSONString();
    }
}
