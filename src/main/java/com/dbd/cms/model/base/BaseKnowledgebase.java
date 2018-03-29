package com.dbd.cms.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseKnowledgebase<M extends BaseKnowledgebase<M>> extends Model<M> implements IBean {

	public void setId(Long id) {
		set("id", id);
	}

	public Long getId() {
		return get("id");
	}

	public void setSgId(Integer sgId) {
		set("sgId", sgId);
	}

	public Integer getSgId() {
		return get("sgId");
	}

	public void setSpeciesId(Integer speciesId) {
		set("speciesId", speciesId);
	}

	public Integer getSpeciesId() {
		return get("speciesId");
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

	public void setScName(String scName) {
		set("scName", scName);
	}

	public String getScName() {
		return get("scName");
	}

	public void setBName(String bName) {
		set("bName", bName);
	}

	public String getBName() {
		return get("bName");
	}

	public void setAreaId(Integer areaId) {
		set("areaId", areaId);
	}

	public Integer getAreaId() {
		return get("areaId");
	}

	public void setFh(Integer fh) {
		set("fh", fh);
	}

	public Integer getFh() {
		return get("fh");
	}

	public void setBLen(String bLen) {
		set("bLen", bLen);
	}

	public String getBLen() {
		return get("bLen");
	}

	public void setRt(Integer rt) {
		set("rt", rt);
	}

	public Integer getRt() {
		return get("rt");
	}

	public void setMf(Integer mf) {
		set("mf", mf);
	}

	public Integer getMf() {
		return get("mf");
	}

	public void setBreed(Integer breed) {
		set("breed", breed);
	}

	public Integer getBreed() {
		return get("breed");
	}

	public void setHa(Integer ha) {
		set("ha", ha);
	}

	public Integer getHa() {
		return get("ha");
	}

	public void setCa(Integer ca) {
		set("ca", ca);
	}

	public Integer getCa() {
		return get("ca");
	}

	public void setRare(Integer rare) {
		set("rare", rare);
	}

	public Integer getRare() {
		return get("rare");
	}

	public void setRemark(String remark) {
		set("remark", remark);
	}

	public String getRemark() {
		return get("remark");
	}

	public void setPy(String py) {
		set("py", py);
	}

	public String getPy() {
		return get("py");
	}

	public void setOper(Integer oper) {
		set("oper", oper);
	}

	public Integer getOper() {
		return get("oper");
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

	public void setDAt(java.util.Date dAt) {
		set("d_at", dAt);
	}

	public java.util.Date getDAt() {
		return get("d_at");
	}

	public void setThumbnail(String thumbnail) {
		set("thumbnail", thumbnail);
	}

	public String getThumbnail() {
		return get("thumbnail");
	}

	public void setReproduce(Integer reproduce) {
		set("reproduce", reproduce);
	}

	public Integer getReproduce() {
		return get("reproduce");
	}

	public void setLaudCount(Integer laudCount) {
		set("laud_count", laudCount);
	}

	public Integer getLaudCount() {
		return get("laud_count");
	}

	public void setCollectCount(Integer collectCount) {
		set("collect_count", collectCount);
	}

	public Integer getCollectCount() {
		return get("collect_count");
	}



}
