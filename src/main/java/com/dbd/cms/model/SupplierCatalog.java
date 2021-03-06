package com.dbd.cms.model;

import com.dbd.cms.Consts;
import com.dbd.cms.model.base.BaseSupplierCatalog;

import java.util.List;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class SupplierCatalog extends BaseSupplierCatalog<SupplierCatalog> {
	public static final SupplierCatalog dao = new SupplierCatalog().dao();

	public void delBySupplierId(Integer supplierId){
		List<SupplierCatalog> supplierCatalogs=dao.find("select * from f_supplier_catalog where supplierId=?",supplierId);
		for (SupplierCatalog sc:supplierCatalogs){
			sc.delete();
		}
	}

	public String getTaxonomyTxt(){
		Taxonomy taxonomy=Taxonomy.dao.findFirstByCache(Consts.CACHE_NAMES.taxonomy.name(),"SupplierCatalog"+getTaxonomyId(),"select * from c_taxonomy where id=?",getTaxonomyId());
		return (taxonomy==null)?"":taxonomy.getTitle();
	}

}
