package com.dbd.cms.controller.admin.supplier;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.BeetlCacheInterceptor;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.QiNiuKit;
import com.dbd.cms.model.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinal.upload.UploadFile;

import java.util.Date;
import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/5/22.
 */
public class SupplierCtr extends DBDController{

    public void index(){
        render("/admin/supplier/list.html");
    }

    public void list(){
        String sheng=getPara("sheng");
        String shi=getPara("shi");
        String qu=getPara("qu");
        String state=getPara("state");
        int id=0;
        boolean isAdmin=false;
        List<Role> roleList=currUserRoles();
        for(Role role:roleList){
            if(role.getName().equals("admin")){
                isAdmin=true;
            }
        }

        if(!isAdmin)id=currUser().getId().intValue();
        Page<Supplier> page;
        String search = getPara("search");
        StringBuffer where = new StringBuffer("from f_supplier fs where 1=1 and fs.d_at is null ");
        if(StrKit.notBlank(sheng))
            where.append(" and fs.sheng='"+sheng+"'");
        if(StrKit.notBlank(shi))
            where.append(" and fs.shi='"+shi+"'");
        if(StrKit.notBlank(qu))
            where.append(" and fs.qu='"+qu+"'");
        if(StrKit.notBlank(state))
            where.append(" and fs.state='"+state+"'");
        if(id!=0)
            where.append(" and fs.userId="+id);
        if (!isParaBlank("search")) {
            if(search.contains(" ")){
                String[] strs=search.split(" ");
                where.append(" and (");
                for(String s : strs){
                    where.append(" instr(fs.name,'"+s+"')>0 or instr(fs.tel,'"+s+"')>0 ");
                }
                where.append(")");
                LogKit.info(where.toString());
            }else {
                where.append(" and (instr(fs.name,'"+search+"')>0 or instr(fs.tel,'"+search+"')>0 )");
            }
            page = Supplier.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        } else {
            page = Supplier.dao.paginate(getPN(), getPS(), "select * ", where.toString());
        }
        rendBootTable(page);
    }
    @Before({EvictInterceptor.class,SupplierValidator.class,Tx.class})
    @CacheName("supplierView")
    public void saveOrUpdate(){
        User user=currUser();
        List<UploadFile> ufs = getFiles();
        UploadFile uf=null;
        String pName=null, fileName = null,s = null,qrFileName=null;
        //不支持 缩略图的多图，这段处理缩略图+微信二维码图
        if(!ufs.isEmpty()) {
            for (int i = 0; i < ufs.size(); i++) {

                uf=ufs.get(i);
                pName = uf.getParameterName();
                if(pName.equals("thumbnail")) {
                    s = "images/" + System.currentTimeMillis() + uf.getFileName();
                    if (fileName == null)
                        fileName = s;
                    else
                        fileName = fileName + "|" + s;
                    QiNiuKit.upload(uf.getFile(), s);
                }else{
                    qrFileName="images/" + System.currentTimeMillis() + uf.getFileName();
                    QiNiuKit.upload(uf.getFile(), qrFileName);
                }
            }
            if(!StrKit.notBlank(qrFileName))
                qrFileName=getPara("qrCode" + "_bak");
            if(!StrKit.notBlank(s))
                fileName=getPara("thumbnail" + "_bak");
        }else{
            fileName=getPara("thumbnail" + "_bak");
            qrFileName=getPara("qrCode" + "_bak");
        }
        //封装数据模型
        Supplier supplier=getModel(Supplier.class);
        //经营品类
        Integer[] catalogIds = null;
        if (isParaExists("catalog")) {
            catalogIds = getParaValuesToInt("catalog");
        }
        //进口订单类型
        Integer[] orderTypeIds = null;
        if (isParaExists("orderType")) {
            orderTypeIds = getParaValuesToInt("orderType");
        }
        //设置微信二维码图片
        supplier.setQRCodePic(qrFileName);
        //处理 是否置顶
        String isTop=getPara("isTop");
        if(isTop.equals("00")){
            //Record record= Db.findFirst("select max(isTop) as isTop from f_supplier ");
            //int max=record.get("isTop",0);
            //max=max+1;
            supplier.setIsTop(1);
        }else{
            supplier.setIsTop(0);
        }
        supplier.setUserId(user.getId().intValue());
        //开始新增或者更新的逻辑，使用数据模型中的id作为判断的依据
        if(supplier.getId()==null){
            supplier.setCAt(new Date());
            supplier.setState(Consts.STATUS.enable.getVal());
            supplier.setSource(Consts.SOURCE.unline.getVal());
            supplier.save();

        }else{
            SupplierPics.dao.delBySupplierId(supplier.getId().intValue());
            supplier.setMAt(new Date());
            supplier.update();
            SupplierOrdertype.dao.delBySupplierId(supplier.getId().intValue());
            SupplierCatalog.dao.delBySupplierId(supplier.getId().intValue());
        }
        //缩略图处理开始，此处代码按照多个缩略图的目的处理，但目前无法同时上传多个缩略图
        if(StrKit.notBlank(fileName)) {
            String pic=null;
            SupplierPics supplierPics = new SupplierPics();
            if(fileName.contains("|")){
                String[] strings=fileName.split("\\|");
                for (int i = 0; i < strings.length; i++) {
                    pic=strings[i];
                    supplierPics.setSupplierId(supplier.getId().intValue());
                    supplierPics.setPic(pic);
                    supplierPics.save();
                }
            }else{
                supplierPics.setSupplierId(supplier.getId().intValue());
                supplierPics.setPic(fileName);
                supplierPics.save();
            }

        }
        //鱼商 经营品类关系保存处理
        SupplierCatalog sc=null;
        if(catalogIds!=null){
            for (Integer cId:catalogIds){
                sc=new SupplierCatalog();
                sc.setTaxonomyId(cId);
                sc.setSupplierId(supplier.getId().intValue());
                sc.save();
            }
        }
        //鱼商 支持的订单类型保存处理,如果不支持进口订单则不需要处理
        if(supplier.getOrderSupport().equals("00")) {
            SupplierOrdertype so = null;
            if (orderTypeIds != null) {
                for (Integer otId : orderTypeIds) {
                    so = new SupplierOrdertype();
                    so.setTaxonomyId(otId);
                    so.setSupplierId(supplier.getId().intValue());
                    so.save();
                }
            }
        }

        renderSuccessJSON("鱼商信息保存成功!","");

    }
    public void get(){
        Long id=getParaToLong("id");
        Supplier supplier=Supplier.dao.findById(id);
        JSONObject jo=JSON.parseObject(supplier.toJson());
        jo.put("pics",Supplier.dao.getPics(supplier.getId().intValue()));
        jo.put("catalogs",Supplier.dao.getCatalogs(supplier.getId().intValue()));
        jo.put("ordertypes",Supplier.dao.getOrdertypes(supplier.getId().intValue()));
        renderJson(jo.toJSONString());
    }
    @Before({EvictInterceptor.class,Tx.class})
    @CacheName("supplierView")
    public void del(){
        String ids_Str=getPara("ids");
        String[] ids_array=ids_Str.split(",");

        Long[] ids=new Long[ids_array.length];
        int i=0;
        for(String s:ids_array){
            ids[i]=Long.parseLong(s);
            i++;
        }
        if(ids!=null) {
            Supplier supplier=null;
            for(Long id:ids) {
                supplier = Supplier.dao.findById(id);
                supplier.setDAt(new Date());
                supplier.update();
            }
        }
        //CacheKit.removeAll(Consts.CACHE_NAMES.supplierView.name());
        renderSuccessJSON("鱼商信息删除成功","");
    }

    @Clear(UserInterceptor.class)
    @Before(BeetlCacheInterceptor.class)
    @CacheName("supplierView")
    public void view(){
        Integer id=getParaToInt("id");
        Supplier supplier=Supplier.dao.findById(id);
        setAttr("supplier",supplier);
        render("/front/supplierinfo/view.html");
    }

    @Before({EvictInterceptor.class,Tx.class})
    @CacheName("supplierView")
    public void stop(){
        Integer id=getParaToInt("id");
        Supplier supplier=Supplier.dao.findById(id);
        supplier.setState(Consts.STATUS.forbidden.getVal());
        supplier.update();
        renderSuccessJSON("鱼商停用成功","");
    }

    @Before({EvictInterceptor.class,Tx.class})
    @CacheName("supplierView")
    public void reStart(){
        Integer id=getParaToInt("id");
        Supplier supplier=Supplier.dao.findById(id);
        supplier.setState(Consts.STATUS.enable.getVal());
        supplier.update();
        renderSuccessJSON("鱼商启用成功","");
    }
}
