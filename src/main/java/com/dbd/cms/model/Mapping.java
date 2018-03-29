package com.dbd.cms.model;

import com.dbd.cms.model.base.BaseMapping;

import java.math.BigInteger;
import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Mapping extends BaseMapping<Mapping> {
	public static final Mapping dao = new Mapping();

	public void delByContentId(BigInteger bi){
		String sql="select * from c_mapping where content_id=?";
		List<Mapping> mappings=dao.find(sql,bi);
		for(Mapping m:mappings){
			m.delete();
		}
	}
}