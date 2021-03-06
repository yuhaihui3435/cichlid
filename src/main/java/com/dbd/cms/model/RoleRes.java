package com.dbd.cms.model;

import com.dbd.cms.Consts;
import com.dbd.cms.model.base.BaseRoleRes;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class RoleRes extends BaseRoleRes<RoleRes> {
	public static final RoleRes dao = new RoleRes();

	public  void delByRoleId(int roleId){
		List<RoleRes> roleResList=dao.find("select id from s_role_res where roleId=?",roleId);
		for(RoleRes rr:roleResList){
			rr.delete();
			List<UserRole> userRoles=UserRole.dao.find("select * from s_user_role where rid=?",rr.getRoleId());
			for(UserRole userRole:userRoles){
				CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(),userRole.getUid());
				CacheKit.remove(Consts.CACHE_NAMES.userReses.name(),userRole.getUid());
			}
		}
	}

	public Res getRes(){
		return Res.dao.findById(getResId());
	}


}
