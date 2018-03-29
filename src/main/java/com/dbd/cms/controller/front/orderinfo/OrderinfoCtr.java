package com.dbd.cms.controller.front.orderinfo;

import com.dbd.cms.core.DBDController;
import com.dbd.cms.model.Orderinfo;

/**
 * 简介
 * <p>
 * 项目名称:   [cms]
 * 包:        [com.dbd.cms.controller.front.orderinfo]
 * 类名称:    [OrderinfoCtr]
 * 类描述:    []
 * 创建人:    [于海慧]
 * 创建时间:  [2017/6/16]
 * 修改人:    []
 * 修改时间:  []
 * 修改备注:  []
 * 版本:     [v1.0]
 */
public class OrderinfoCtr extends DBDController{

    public void index(){
        setAttr("needAdminLTE","needAdminLTE");//引入AdminLTE样式表变量。
        if(isParaExists("orderinfoId")) {
            int orderinfoId = getParaToInt("orderinfoId");
            setAttr("orderinfoId", orderinfoId);
            setAttr("orderinfo", Orderinfo.dao.findById(orderinfoId));
        }
        render("/front/orderinfo/main.html");
    }

}
