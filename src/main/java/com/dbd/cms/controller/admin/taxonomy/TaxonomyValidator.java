package com.dbd.cms.controller.admin.taxonomy;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDValidator;
import com.dbd.cms.model.Taxonomy;
import com.jfinal.core.Controller;

import java.util.List;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.admin.taxonomy]
 * 类名称:    [taxonomyValidator]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2016/12/26]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class TaxonomyValidator extends DBDValidator {
    protected void validate(Controller controller) {
        String ak=getActionKey();
        Taxonomy taxonomy=controller.getModel(Taxonomy.class);
        List<Taxonomy> list=null;
        if(ak.equals("/adminTaxonomy/save")){
            list=Taxonomy.dao.find("select * from c_taxonomy where title=? and parent_id=? and d_at is null",taxonomy.getName(),taxonomy.getParentId());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),"该标题被占用");
            }
        }else if(ak.equals("/adminTaxonomy/update")){
            list=Taxonomy.dao.find("select * from c_taxonomy where title=? and parent_id=? and id!=? and d_at is null",taxonomy.getName(),taxonomy.getParentId(),taxonomy.getId());
            if(!list.isEmpty()){
                addError(Consts.REQ_JSON_CODE.fail.name(),"该标题被占用");
            }
        }
    }

    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
