package com.dbd.cms.controller.admin.user;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDValidator;
import com.dbd.cms.model.User;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;

import java.math.BigInteger;
import java.util.List;

/**
 *       简介:用户信息验证类
 *
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.userinfo]
 * 类名称:    [UserValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/4]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class UserValidator extends DBDValidator {

    public static final String LOGINNAME_EXIST="登录名已被占用";
    public static final String NICKNAME_EXIST="昵称已被占用";
    public static final String EMAIL_EXIST="邮箱地址已被占用";
    public static final String PHONE_EXIST="手机号已被占用";

    protected void validate(Controller controller) {
        User user=controller.getModel(User.class);
        String ak=getActionKey();
        LogKit.debug("ActionKey ="+ak);
        if(ak.equals("/adminUser/save")){

            List _list=User.dao.find("select id from s_user where loginname=?",user.getLoginname());
            if(!_list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),LOGINNAME_EXIST);
                return ;
            }
            _list=User.dao.find("select id from s_user where nickname=?",user.getNickname());
            if(!_list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),NICKNAME_EXIST);
                return ;
            }
            _list=User.dao.find("select id from s_user where email=?",user.getEmail());
            if(!_list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),EMAIL_EXIST);
                return ;
            }
            _list=User.dao.find("select id from s_user where phone=?",user.getPhone());
            if(!_list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),PHONE_EXIST);
                return ;
            }


        }else if(ak.equals("/adminUser/update")){
            BigInteger id=user.getId();
            User _user=User.dao.findFirst("select id from s_user where loginname=? and id<>?",user.getLoginname(),user.getId());
            if(_user!=null){
                addError(Consts.REQ_JSON_CODE.fail.name(),LOGINNAME_EXIST);
                return ;
            }
            _user=User.dao.findFirst("select id from s_user where nickname=? and id<>?",user.getNickname(),user.getId());
            if(_user!=null){
                addError(Consts.REQ_JSON_CODE.fail.name(),NICKNAME_EXIST);
                return ;
            }
            _user=User.dao.findFirst("select id from s_user where email=? and id<>?",user.getEmail(),user.getId());
            if(_user!=null){
                addError(Consts.REQ_JSON_CODE.fail.name(),EMAIL_EXIST);
                return ;
            }
            _user=User.dao.findFirst("select id from s_user where phone=? and id<>?",user.getPhone(),user.getId());
            if(_user!=null){
                addError(Consts.REQ_JSON_CODE.fail.name(),PHONE_EXIST);
                return ;
            }
        }
    }

    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
