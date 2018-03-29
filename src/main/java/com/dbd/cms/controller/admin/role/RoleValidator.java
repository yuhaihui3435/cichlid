package com.dbd.cms.controller.admin.role;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDValidator;
import com.dbd.cms.model.Role;
import com.jfinal.core.Controller;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.role]
 * 类名称:    [RoleValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/6]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class RoleValidator extends DBDValidator {
    public static final String ROLENAME_EXIST="角色名称被占用";
    protected void validate(Controller controller) {
        String ak=getActionKey();
        Role role=controller.getModel(Role.class);
        List<Role> roleList;
        if(ak.equals("/adminRole/save")){
            roleList=Role.dao.find("select * from s_role where name=?",role.getName());
            if(!roleList.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),ROLENAME_EXIST);
            }

        }else if(ak.equals("/adminRole/update")){
            roleList=Role.dao.find("select * from s_role where name=? and id<>?",role.getName(),role.getId());
            if(!roleList.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),ROLENAME_EXIST);
            }
        }
    }

    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
