package com.dbd.cms.model;

import com.dbd.cms.model.base.BaseCollect;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Collect extends BaseCollect<Collect> {
	public static final Collect dao = new Collect().dao();


	public List findByUserIdAndCId(int userid, int cid){
		return Collect.dao.find("select * from s_collect where userId=? and cId=?",userid,cid);
	}
	public List findByUserIdAndCIdAndModule(int userid, int cid,String module){
		return Collect.dao.find("select * from s_collect where userId=? and cId=? and module=?",userid,cid,module);
	}

	public String getModuleTxt(){
		return getModule().equals("content")?"杂七杂八":(getModule().equals("supplier"))?"鱼商":"知识库";
	}

	public String getModuleToken(){
		return getModule().equals("content")?"1":(getModule().equals("supplier"))?"2":"3";
	}
}
