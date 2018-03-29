package com.dbd.cms.controller.admin.res;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDValidator;
import com.dbd.cms.model.Res;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2016/12/6.
 */
public class ResValidator extends DBDValidator {
    public static final String RES_NAME_EXIST="菜单名称被占用";

    protected void validate(Controller controller) {
        LogKit.debug(getActionKey());
        Res res=controller.getModel(Res.class);
        List<Res> l;
        if(getActionKey().equals("/res/save")){
            l=Res.dao.find("select * from s_res where name=? and pid=?", res.getName(),res.getPid());
            if(!l.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),RES_NAME_EXIST);
            }
        }else if(getActionKey().equals("/res/update")){
            l=Res.dao.find("select * from s_res where name=? and pid=? and id<>?", res.getName(),res.getPid(),res.getId());
            if(!l.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),RES_NAME_EXIST);
            }
        }

    }

    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
