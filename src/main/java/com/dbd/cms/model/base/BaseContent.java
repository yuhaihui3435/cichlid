package com.dbd.cms.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseContent<M extends BaseContent<M>> extends Model<M> implements IBean {

	public void setId(java.math.BigInteger id) {
		set("id", id);
	}

	public java.math.BigInteger getId() {
		return get("id");
	}

	public void setTitle(String title) {
		set("title", title);
	}

	public String getTitle() {
		return get("title");
	}

	public void setText(String text) {
		set("text", text);
	}

	public String getText() {
		return get("text");
	}

	public void setSummary(String summary) {
		set("summary", summary);
	}

	public String getSummary() {
		return get("summary");
	}

	public void setLinkTo(String linkTo) {
		set("link_to", linkTo);
	}

	public String getLinkTo() {
		return get("link_to");
	}

	public void setMarkdownEnable(Boolean markdownEnable) {
		set("markdown_enable", markdownEnable);
	}

	public Boolean getMarkdownEnable() {
		return get("markdown_enable");
	}

	public void setThumbnail(String thumbnail) {
		set("thumbnail", thumbnail);
	}

	public String getThumbnail() {
		return get("thumbnail");
	}

	public void setModule(String module) {
		set("module", module);
	}

	public String getModule() {
		return get("module");
	}

	public void setStyle(String style) {
		set("style", style);
	}

	public String getStyle() {
		return get("style");
	}

	public void setUserId(java.math.BigInteger userId) {
		set("user_id", userId);
	}

	public java.math.BigInteger getUserId() {
		return get("user_id");
	}

	public void setUserIp(String userIp) {
		set("user_ip", userIp);
	}

	public String getUserIp() {
		return get("user_ip");
	}

	public void setUserAgent(String userAgent) {
		set("user_agent", userAgent);
	}

	public String getUserAgent() {
		return get("user_agent");
	}

	public void setParentId(java.math.BigInteger parentId) {
		set("parent_id", parentId);
	}

	public java.math.BigInteger getParentId() {
		return get("parent_id");
	}

	public void setStatus(String status) {
		set("status", status);
	}

	public String getStatus() {
		return get("status");
	}

	public void setVoteUp(Long voteUp) {
		set("vote_up", voteUp);
	}

	public Long getVoteUp() {
		return get("vote_up");
	}

	public void setVoteDown(Long voteDown) {
		set("vote_down", voteDown);
	}

	public Long getVoteDown() {
		return get("vote_down");
	}

	public void setRate(Integer rate) {
		set("rate", rate);
	}

	public Integer getRate() {
		return get("rate");
	}

	public void setRateCount(Long rateCount) {
		set("rate_count", rateCount);
	}

	public Long getRateCount() {
		return get("rate_count");
	}

	public void setCommentStatus(String commentStatus) {
		set("comment_status", commentStatus);
	}

	public String getCommentStatus() {
		return get("comment_status");
	}

	public void setCommentCount(Long commentCount) {
		set("comment_count", commentCount);
	}

	public Long getCommentCount() {
		return get("comment_count");
	}

	public void setCommentTime(java.util.Date commentTime) {
		set("comment_time", commentTime);
	}

	public java.util.Date getCommentTime() {
		return get("comment_time");
	}

	public void setViewCount(Long viewCount) {
		set("view_count", viewCount);
	}

	public Long getViewCount() {
		return get("view_count");
	}

	public void setCAt(java.util.Date cAt) {
		set("c_at", cAt);
	}

	public java.util.Date getCAt() {
		return get("c_at");
	}

	public void setDAt(java.util.Date dAt) {
		set("d_at", dAt);
	}

	public java.util.Date getDAt() {
		return get("d_at");
	}

	public void setSlug(String slug) {
		set("slug", slug);
	}

	public String getSlug() {
		return get("slug");
	}

	public void setMAt(java.util.Date mAt) {
		set("m_at", mAt);
	}

	public java.util.Date getMAt() {
		return get("m_at");
	}

	public void setLng(java.math.BigDecimal lng) {
		set("lng", lng);
	}

	public java.math.BigDecimal getLng() {
		return get("lng");
	}

	public void setLat(java.math.BigDecimal lat) {
		set("lat", lat);
	}

	public java.math.BigDecimal getLat() {
		return get("lat");
	}

	public void setMetaKeywords(String metaKeywords) {
		set("meta_keywords", metaKeywords);
	}

	public String getMetaKeywords() {
		return get("meta_keywords");
	}

	public void setMetaDescription(String metaDescription) {
		set("meta_description", metaDescription);
	}

	public String getMetaDescription() {
		return get("meta_description");
	}

	public void setRemarks(String remarks) {
		set("remarks", remarks);
	}

	public String getRemarks() {
		return get("remarks");
	}

	public void setGood(Boolean good) {
		set("good", good);
	}

	public Boolean getGood() {
		return get("good");
	}

	public void setTop(Boolean top) {
		set("top", top);
	}

	public Boolean getTop() {
		return get("top");
	}

	public void setFlag(String flag) {
		set("flag", flag);
	}

	public String getFlag() {
		return get("flag");
	}

	public void setCollectCount(Integer collectCount) {
		set("collect_count", collectCount);
	}

	public Integer getCollectCount() {
		return get("collect_count");
	}

	public void setLaudCount(Integer laudCount) {
		set("laud_count", laudCount);
	}

	public Integer getLaudCount() {
		return get("laud_count");
	}

}
