package com.dbd.cms.model;

import com.dbd.cms.model.base.BaseParam;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Param extends BaseParam<Param> {
	public static final String TABLE="s_param";
	public static final Param dao = new Param();

	public Param findByKey(String key){
		return dao.findFirst("select * from "+TABLE+" where k=?",key);
	}

}
