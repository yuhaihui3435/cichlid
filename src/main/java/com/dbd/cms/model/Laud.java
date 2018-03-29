package com.dbd.cms.model;

import com.dbd.cms.model.base.BaseLaud;
import com.jfinal.plugin.activerecord.Db;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Laud extends BaseLaud<Laud> {
	public static final Laud dao = new Laud().dao();

	public List findByCIdAndModule(Integer cId, String module){
		return Laud.dao.find("select * from s_laud where cId=? and module=?",cId,module);
	}

	public long countByCIdAndModule(int cId,String module){
		return Db.queryLong("select count(1) from s_laud where cId=? and module=?",cId,module);
	}

	public List findByCIdAndModuleAndUserId(int cId,String module,int useId){
		return Laud.dao.find("select * from s_laud where cId=? and module=? and userId=?",cId,module,useId);
	}

}
