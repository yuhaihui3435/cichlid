package com.dbd.cms.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.template.ext.directive.Str;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BasePic<M extends BasePic<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return get("id");
	}

	public void setModule(String module) {
		set("module", module);
	}

	public String getModule() {
		return get("module");
	}

	public void setModuleId(Integer moduleId) {
		set("moduleId", moduleId);
	}

	public Integer getModuleId() {
		return get("moduleId");
	}

	public void setPic(String pic) {
		set("pic", pic);
	}

	public String getPic() {
		return get("pic");
	}

	public void setUploadUser(Integer uploadUser) {
		set("uploadUser", uploadUser);
	}

	public Integer getUploadUser() {
		return get("uploadUser");
	}

	public void setCAt(java.util.Date cAt) {
		set("c_at", cAt);
	}

	public java.util.Date getCAt() {
		return get("c_at");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return get("status");
	}

	public void setDAt(java.util.Date dAt) {
		set("d_at", dAt);
	}

	public java.util.Date getDAt() {
		return get("d_at");
	}

}
