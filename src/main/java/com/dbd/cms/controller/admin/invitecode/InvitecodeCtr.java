package com.dbd.cms.controller.admin.invitecode;

import com.dbd.cms.core.DBDController;
import com.dbd.cms.kits.StringKit;
import com.dbd.cms.model.InviteCode;
import com.jfinal.aop.Before;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;

import java.util.Date;

/**
 * Created by yuhaihui8913 on 2017/7/5.
 */
public class InvitecodeCtr extends DBDController{

    public void index(){
        render("/admin/invitecode/list.html");
    }


    public void list(){
        Page<InviteCode> page;
        String serach = getPara("search");
        StringBuilder where=new StringBuilder();
        where.append(" from s_invite_code sic,s_user su  where (sic.userId=su.id or sic.u_userId=su.id) ");
        if(!isAdmin()){
            where.append(" and sic.userId="+currUser().getId());
        }
        if(StrKit.notBlank(serach)){
            where.append(" and (instr(su.nickname,?)>0 or instr(sic.code,?)>0 )");
            page= InviteCode.dao.paginate(getPN(),getPS(),"select sic.* ",where.toString(),serach,serach);
        }else{
            page=InviteCode.dao.paginate(getPN(),getPS(),"select sic.* ",where.toString());
        }
        rendBootTable(page);
    }


    @Before(Tx.class)
    public void gen(){
        InviteCode inviteCode=new InviteCode();
        inviteCode.setCode(StringKit.getUUID());
        inviteCode.setCAt(new Date());
        inviteCode.setState("00");
        inviteCode.setUserId(currUser().getId().intValue());
        inviteCode.save();
        renderSuccessJSON("邀请码申请成功","");
    }

}
