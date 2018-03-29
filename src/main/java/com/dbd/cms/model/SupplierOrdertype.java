package com.dbd.cms.model;

import com.dbd.cms.Consts;
import com.dbd.cms.model.base.BaseSupplierOrdertype;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class SupplierOrdertype extends BaseSupplierOrdertype<SupplierOrdertype> {
	public static final SupplierOrdertype dao = new SupplierOrdertype().dao();

	public void delBySupplierId(Integer supplierId){
		List<SupplierOrdertype> supplierOrdertypes=dao.find("select * from f_supplier_ordertype where supplierId=?",supplierId);
		for (SupplierOrdertype so:supplierOrdertypes){
			so.delete();
		}
	}

	public String getTaxonomyTxt(){
		Taxonomy taxonomy=Taxonomy.dao.findFirstByCache(Consts.CACHE_NAMES.taxonomy.name(),"SupplierOrdertype"+getTaxonomyId(),"select * from c_taxonomy where id=?",getTaxonomyId());
		return (taxonomy==null)?"":taxonomy.getTitle();
	}

}