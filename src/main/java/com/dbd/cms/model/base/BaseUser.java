package com.dbd.cms.model.base;

import com.dbd.cms.core.DBDModel;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseUser<M extends BaseUser<M>> extends DBDModel<M> implements IBean {

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}

	public java.math.BigInteger getId() {
		return get("id");
	}

	public void setIdcard(String idcard) {
		set("idcard", idcard);
	}

	public String getIdcard() {
		return get("idcard");
	}

	public void setNickname(String nickname) {
		set("nickname", nickname);
	}

	public String getNickname() {
		return get("nickname");
	}

	public void setScore(Integer score) {
		set("score", score);
	}

	public Integer getScore() {
		return get("score");
	}

	public void setAvatar(String avatar) {
		set("avatar", avatar);
	}

	public String getAvatar() {
		return get("avatar");
	}

	public void setEmail(String email) {
		set("email", email);
	}

	public String getEmail() {
		return get("email");
	}

	public void setSignature(String signature) {
		set("signature", signature);
	}

	public String getSignature() {
		return get("signature");
	}

	public void setThirdId(String thirdId) {
		set("third_id", thirdId);
	}

	public String getThirdId() {
		return get("third_id");
	}

	public void setAccessToken(String accessToken) {
		set("access_token", accessToken);
	}

	public String getAccessToken() {
		return get("access_token");
	}

	public void setReceiveMsg(Boolean receiveMsg) {
		set("receive_msg", receiveMsg);
	}

	public Boolean getReceiveMsg() {
		return get("receive_msg");
	}

	public void setCAt(java.util.Date cAt) {
		set("c_at", cAt);
	}

	public java.util.Date getCAt() {
		return get("c_at");
	}

	public void setMAt(java.util.Date mAt) {
		set("m_at", mAt);
	}

	public java.util.Date getMAt() {
		return get("m_at");
	}

	public void setPhone(String phone) {
		set("phone", phone);
	}

	public String getPhone() {
		return get("phone");
	}

	public void setChannel(String channel) {
		set("channel", channel);
	}

	public String getChannel() {
		return get("channel");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return get("status");
	}

	public void setThirdAccessToken(String thirdAccessToken) {
		set("third_access_token", thirdAccessToken);
	}

	public String getThirdAccessToken() {
		return get("third_access_token");
	}

	public void setLogged(java.util.Date logged) {
		set("logged", logged);
	}

	public java.util.Date getLogged() {
		return get("logged");
	}

	public void setActivated(java.util.Date activated) {
		set("activated", activated);
	}

	public java.util.Date getActivated() {
		return get("activated");
	}

	public void setEmailStatus(Boolean emailStatus) {
		set("email_status", emailStatus);
	}

	public Boolean getEmailStatus() {
		return get("email_status");
	}

	public void setPhoneStatus(Boolean phoneStatus) {
		set("phone_status", phoneStatus);
	}

	public Boolean getPhoneStatus() {
		return get("phone_status");
	}

	public void setContentCount(Integer contentCount) {
		set("content_count", contentCount);
	}

	public Integer getContentCount() {
		return get("content_count");
	}

	public void setCommentCount(Integer commentCount) {
		set("comment_count", commentCount);
	}

	public Integer getCommentCount() {
		return get("comment_count");
	}

	public void setIdcardtype(Integer idcardtype) {
		set("idcardtype", idcardtype);
	}

	public Integer getIdcardtype() {
		return get("idcardtype");
	}

	public void setPassword(String password) {
		set("password", password);
	}

	public String getPassword() {
		return get("password");
	}

	public void setSalt(String salt) {
		set("salt", salt);
	}

	public String getSalt() {
		return get("salt");
	}

	public void setLoginname(String loginname) {
		set("loginname", loginname);
	}

	public String getLoginname() {
		return get("loginname");
	}
	public void setIsAdmin(String isAdmin) {
		set("isAdmin", isAdmin);
	}

	public String getIsAdmin() {
		return get("isAdmin");
	}

	public void setUnionid(String unionid) {
		set("unionid", unionid);
	}

	public String getUnionid() {
		return get("unionid");
	}

	public java.util.Date getAccessTokenOverdue(){
		return getDate("access_token_overdue");
	}

	public void setAccessTokenOverdue(java.util.Date date){
		set("access_token_overdue",date);
	}

}
