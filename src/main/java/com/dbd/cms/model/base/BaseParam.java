package com.dbd.cms.model.base;

import com.dbd.cms.core.DBDModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseParam<M extends BaseParam<M>> extends DBDModel<M> implements IBean {

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}

	public java.math.BigInteger getId() {
		return get("id");
	}

	public void setK(java.lang.String key) {
		set("k", key);
	}

	public java.lang.String getK() {
		return get("k");
	}

	public void setVal(java.lang.String val) {
		set("val", val);
	}

	public java.lang.String getVal() {
		return get("val");
	}

	public void setNote(String note){set("note",note);}

	public String getNote(){return get("note");}

}
