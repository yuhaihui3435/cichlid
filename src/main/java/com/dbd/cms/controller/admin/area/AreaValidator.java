package com.dbd.cms.controller.admin.area;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDValidator;
import com.dbd.cms.model.Area;
import com.jfinal.core.Controller;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/1/18.
 */
public class AreaValidator extends DBDValidator {

    public static final String ENNAME_EXIST = "地域英文名已经存在";

    protected void validate(Controller controller) {
        Area area = controller.getModel(Area.class);
        String ak = getActionKey();
        List<Area> list;
        if (ak.equals("/adminArea/save")) {

            list = Area.dao.find("select * from f_area where enName=?", area.getEnName());
            if (!list.isEmpty()) {
                addError(Consts.REQ_JSON_CODE.fail.name(), ENNAME_EXIST);
                return;
            }
        } else if (ak.equals("/adminArea/update")) {
            list = Area.dao.find("select * from f_area where enName=? and id<>?", area.getEnName(), area.getId());
            if(!list.isEmpty())
            addError(Consts.REQ_JSON_CODE.fail.name(), ENNAME_EXIST);
            return;
        }
    }

    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
