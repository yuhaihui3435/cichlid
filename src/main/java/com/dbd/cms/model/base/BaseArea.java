package com.dbd.cms.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseArea<M extends BaseArea<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return get("id");
	}

	public void setEnName(String enName) {
		set("enName", enName);
	}

	public String getEnName() {
		return get("enName");
	}

	public void setZhName(String zhName) {
		set("zhName", zhName);
	}

	public String getZhName() {
		return get("zhName");
	}

	public void setState(String state) {
		set("state", state);
	}

	public String getState() {
		return get("state");
	}

	public void setRegion(String region) {
		set("region", region);
	}

	public String getRegion() {
		return get("region");
	}

	public void setOwner(String owner) {
		set("owner", owner);
	}

	public String getOwner() {
		return get("owner");
	}

	public void setPy(String py) {
		set("py", py);
	}

	public String getPy() {
		return get("py");
	}

}
