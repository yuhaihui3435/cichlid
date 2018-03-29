package com.dbd.cms.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseNotification<M extends BaseNotification<M>> extends Model<M> implements IBean {

	public void setId(Integer id) {
		set("id", id);
	}

	public Integer getId() {
		return get("id");
	}

	public void setRead(String read) {
		set("read", read);
	}

	public String getRead() {
		return get("read");
	}

	public void setFromUserId(Integer fromUserId) {
		set("fromUserId", fromUserId);
	}

	public Integer getFromUserId() {
		return get("fromUserId");
	}

	public void setToUserId(Integer toUserId) {
		set("toUserId", toUserId);
	}

	public Integer getToUserId() {
		return get("toUserId");
	}

	public void setCAt(java.util.Date cAt) {
		set("c_at", cAt);
	}

	public java.util.Date getCAt() {
		return get("c_at");
	}

	public void setAction(String action) {
		set("action", action);
	}

	public String getAction() {
		return get("action");
	}

	public void setContent(String content) {
		set("content", content);
	}

	public String getContent() {
		return get("content");
	}

	public void setRId(Integer rId){
		set("rId",rId);
	}
	public Integer getRId(){return get("rId");}

}