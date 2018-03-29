package com.dbd.cms.controller.admin.Knowledgebase;

import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDValidator;
import com.dbd.cms.model.Knowledgebase;
import com.jfinal.core.Controller;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/1/22.
 */
public class KnowledgebaseValidator extends DBDValidator {

    public static final String KNOWLEDGEBASE_EXIST_MSG="同一学名在同一地域下只能出现一次";

    protected void validate(Controller controller) {
        String ak=getActionKey();
        if(ak.equals("/adminKnowledgebase/saveOrUpdate")){
            Knowledgebase knowledgebase=controller.getModel(Knowledgebase.class);
            Integer areaId=knowledgebase.getAreaId();
            if(knowledgebase.getId()==null){
                if(areaId!=null) {
                    List<Knowledgebase> list = Knowledgebase.dao.find("select * from f_knowledgebase where scName=? and areaId=?", knowledgebase.getScName(), knowledgebase.getAreaId());
                    if (!list.isEmpty()) {
                        addError(Consts.REQ_JSON_CODE.fail.name(),KNOWLEDGEBASE_EXIST_MSG );
                    }
                }
            }else{
                if(areaId!=null) {
                    List<Knowledgebase> list = Knowledgebase.dao.find("select * from f_knowledgebase where scName=? and areaId=? and id<>?", knowledgebase.getScName(), knowledgebase.getAreaId(),knowledgebase.getId());
                    if (!list.isEmpty()) {
                        addError(Consts.REQ_JSON_CODE.fail.name(),KNOWLEDGEBASE_EXIST_MSG );
                    }
                }
            }
        }
    }

    protected void handleError(Controller controller) {
        controller.renderJson(getErrorJSON());
    }
}
