package com.dbd.cms.controller.admin.orderinfo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.core.DBDException;
import com.dbd.cms.interceptor.CommonInterceptor;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.DateKit;
import com.dbd.cms.kits.ExcelKit;
import com.dbd.cms.kits.StringKit;
import com.dbd.cms.model.Orderdetail;
import com.dbd.cms.model.Orderinfo;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.plugin.ehcache.EvictInterceptor;
import com.jfinal.upload.UploadFile;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by yuhaihui8913 on 2017/5/27.
 */
public class OrderinfoCtr extends DBDController {

    public static final String ORDERINFO_TREE_JSON="orderinfoTreeJSON";

    public void index(){
        render("/admin/orderinfo/main.html");
    }


    @Clear
    public void listTree(){
        String name=getPara("name");
        String zhName=getPara("title");
        if(StrKit.isBlank(name)) {
            String json = CacheKit.get(Consts.CACHE_NAMES.orderinfo.name(), ORDERINFO_TREE_JSON);
            if (StrKit.isBlank(json)) {
                loadAndCacheOrderinfoTreeJSON();
                json=CacheKit.get(Consts.CACHE_NAMES.orderinfo.name(), ORDERINFO_TREE_JSON);
            }
            renderJson(json);
            return ;
        }else{
            List<Orderinfo> list=null;
            if(!StringKit.isNumeric(name)){
               list=Orderinfo.dao.findOrderYearByZhNameLike(name);
            }else {
                list = Orderinfo.dao.findOrderDateByZhNameAndYear(zhName, name+"-01-01");
            }
            JSONArray ja=new JSONArray();
            JSONObject jo=null;
            for (Orderinfo oi:list){
                jo=new JSONObject();
                if(!StringKit.isNumeric(name)) {
                    jo.put("name", oi.get("year"));
                    jo.put("isParent",true);
                    jo.put("title",name);
                }
                else
                    jo.put("name",DateKit.dateToStr(oi.getOrderDate(),DateKit.yyyy_MM_dd));
                jo.put("id",oi.getId());
                ja.add(jo);
            }
            renderJson(ja.toJSONString());
        }
    }
    @Clear({CommonInterceptor.class, UserInterceptor.class})
    public void orderDetailList(){
        Integer orderinfoId=getParaToInt("orderinfoId");
        Page<Orderdetail> page;
        String serach = getPara("search");
        String catalogName=getPara("catalogName");
        StringBuffer where = new StringBuffer("from f_orderdetail fo where 1=1 and fo.d_at is null and fo.orderinfoId=? and fo.price is not null");
        if(StrKit.notBlank(catalogName))
            where.append(" and fo.catalogName='"+catalogName+"'");
        if (!isParaBlank("search")) {
            where.append(" and (instr(fo.catalogName,?)>0 or instr(fo.zhName,?)>0 or instr(fo.scientificName,?)>0 ) ");
            where.append(" order by fo.scientificName");
            page = Orderdetail.dao.paginateByCache(Consts.CACHE_NAMES.orderinfo.name(),"orderDetailList"+orderinfoId+getPN()+getPS()+where.toString(),getPN(), getPS(), "select * ", where.toString(),orderinfoId,serach, serach, serach);
        } else {
            where.append(" order by fo.scientificName");
            page = Orderdetail.dao.paginateByCache(Consts.CACHE_NAMES.orderinfo.name(),"orderDetailList"+orderinfoId+getPN()+getPS()+where.toString(),getPN(), getPS(), "select * ", where.toString(),orderinfoId);
        }
        rendBootTable(page);
    }

    //查询订单所有大类
    @Clear({CommonInterceptor.class, UserInterceptor.class})
    public void orderdetailCatalogNameJson(){
        Integer orderinfoId=getParaToInt("orderinfoId");
        renderJson(JSON.toJSONString(Orderdetail.dao.findCatalogName(orderinfoId)));
    }

    @Before({EvictInterceptor.class, Tx.class})
    @CacheName("orderinfo")
    public void uploadOrder() throws InvalidFormatException, DBDException, IOException {

        UploadFile uf=getFile();
        if(uf==null){
            renderFailJSON("请上传Excel","");
            return;
        }

        String fileName=uf.getOriginalFileName();
        LogKit.info(fileName.indexOf(".xls")+"");
        if(fileName.indexOf(".xls")<0&&fileName.indexOf(".xlsx")<0){
            renderFailJSON("请上传Excel","");
            return;
        }
        fileName=fileName.substring(0,fileName.indexOf("."));

        if(!Orderinfo.dao.findByName(fileName).isEmpty()){
            renderFailJSON("此订单已经导入!","");
            return;
        }

        String type=getPara("orderType");
        InputStream is=null;
        try {
            is=new FileInputStream(uf.getFile());

            Orderinfo oi=new Orderinfo();
            LogKit.info("读取到的文件的名字");
            oi.setName(fileName);
            String zhName= StringKit.matchResult(Pattern.compile(StringKit.CHINESE_REGEX),fileName);
            String date_str=fileName.replace(zhName,"");
            oi.setZhName(zhName);
            oi.setType(type);
            oi.setOrderDate(DateKit.strToDate(date_str,DateKit.yyyy_MM_dd));
            oi.setCAt(new Date());
            oi.save();
            readExcel(is,oi);
            oi.update();
        }finally {
            if(is!=null)
                try {
                    is.close();
                } catch (IOException e) {
                    LogKit.error("关闭文件输入流错误："+e.getMessage());
                }
        }

        Orderdetail.dao.findOrderdetailWithKbRelation();

        renderSuccessJSON("订单导入成功","");
    }


    private void readExcel(InputStream is,Orderinfo oi) throws DBDException, IOException, InvalidFormatException {
        Workbook workbook = WorkbookFactory.create(is);
        Sheet sheet=workbook.getSheetAt(0);

        Row row=null;
        String cellData=null;
        Cell cell=null;
        boolean dataBegin=false,bl=false;
        Orderdetail orderdetail=null;
        int colIndex=0;
        String _str=null;
        Object obj=null;
        Integer kbId=null;
        Map<String,String> colSetting=new HashMap<String,String>();
        nextGo:
        for (int i = sheet.getFirstRowNum();i<sheet.getLastRowNum()+1 ; i++) {
            row=sheet.getRow(i);
            if(i==0){
              cell=row.getCell(0);
              if(cell==null)cell=row.getCell(1);
              oi.setSummary(cell.getStringCellValue());
            }else {

                for (int j = row.getFirstCellNum(); j <row.getPhysicalNumberOfCells(); j++) {
                    cell=row.getCell(j);
                    if(cell==null)continue ;
                    cellData= ExcelKit.cellValue2Str(cell);
                    //列头
                    if(cellData.contains("学名")||cellData.contains("大类")||cellData.contains("中文名")||cellData.contains("零售")||cellData.equals("尺寸")||cellData.equals("品系")){
                        _str=cell.getStringCellValue();
                        //去掉列名中带有()的内容
                        if(_str.contains("(")){
                            int index=_str.indexOf("(");
                            _str=_str.substring(0,index);
                        }else if(_str.contains("（")){
                            int index=_str.indexOf("（");
                            _str=_str.substring(0,index);
                        }
                        colSetting.put(_str.trim(),j+"");
                        bl=true;

                    }
                    //获取列名后，设置下一行开始读取真正的数据。
                    if(bl&&j+1==row.getPhysicalNumberOfCells()){
                        dataBegin=true;
                        bl=false;
                        continue nextGo;
                    }
                }

                if(dataBegin){
                    orderdetail=new Orderdetail();
                    if(colSetting.containsKey("学名")){
                        colIndex=Integer.parseInt(colSetting.get("学名"));


                        cell=row.getCell(colIndex);
                        if(cell==null)continue nextGo;
                        _str=cell.getStringCellValue();
                        orderdetail.setScientificName(_str);
                        //从缓存中查找已经与知识库建立起来对应关系的订单详细
                        obj=CacheKit.get(Consts.CACHE_NAMES.orderinfoAndKbRelation.name(),_str.trim());
                        if(obj!=null){
                            kbId=(Integer) obj;
                            orderdetail.setKbId(kbId);
                        }
                    }else{
                        LogKit.error("没有找到\"学名\"列");
                        throw  new DBDException("学名列没有找到");
                    }
                    if(colSetting.containsKey("中文名")){
                        colIndex=Integer.parseInt(colSetting.get("中文名"));
                        cell=row.getCell(colIndex);
                        _str=cell.getStringCellValue();
                        if(StrKit.notBlank(_str))_str=_str.trim();
                        orderdetail.setZhName(_str);
                    }else{
                        LogKit.error("没有找到\"中文名\"列");
                        throw  new DBDException("中文列没有找到");
                    }
                    if(colSetting.containsKey("大类名")){
                        colIndex=Integer.parseInt(colSetting.get("大类名"));
                        cell=row.getCell(colIndex);
                        _str=cell.getStringCellValue();
                        if(StrKit.notBlank(_str))_str=_str.trim();
                        orderdetail.setCatalogName(_str);
                    }else{
                        LogKit.error("没有找到\"大类名\"列");
                        throw  new DBDException("大类名列没有找到");
                    }
                    if(colSetting.containsKey("品系")){
                        colIndex=Integer.parseInt(colSetting.get("品系"));
                        cell=row.getCell(colIndex);
                        _str=cell.getStringCellValue();
                        if(StrKit.notBlank(_str))_str=_str.trim();
                        orderdetail.setStrain(_str);
                    }else{
                        LogKit.error("没有找到\"品系\"列");
                        //throw  new DBDException("大类名列没有找到");
                    }
                    if(colSetting.containsKey("尺寸")){
                        colIndex=Integer.parseInt(colSetting.get("尺寸"));
                        cell=row.getCell(colIndex);
                        _str=cell.getStringCellValue();
                        if(StrKit.notBlank(_str))_str=_str.trim();
                        orderdetail.setSize(_str);
                    }else{
                        LogKit.error("没有找到\"尺寸\"列");
                        //throw  new DBDException("大类名列没有找到");
                    }
                    if(colSetting.containsKey("零售")||colSetting.containsKey("零售价")){
                        if(colSetting.get("零售")!=null) {
                            colIndex = Integer.parseInt(colSetting.get("零售"));
                        }else{
                            colIndex = Integer.parseInt(colSetting.get("零售价"));
                        }
                        cell=row.getCell(colIndex);
                        _str=ExcelKit.cellValue2Str(cell);
                        if(StrKit.notBlank(_str))
                        orderdetail.setPrice(new BigDecimal(_str));
                    }else{
                        LogKit.error("没有找到\"零售价格\"列");
                        //throw  new DBDException("大类名列没有找到");
                    }
                }
                //如果读取到详细的数据则进行保存
                if(dataBegin&&orderdetail!=null) {
                    orderdetail.setOrderinfoId(oi.getId().intValue());
                    if(orderdetail.getPrice()!=null)
                    orderdetail.save();
                }

            }


        }
    }



    private void loadAndCacheOrderinfoTreeJSON(){
        List<Orderinfo> orderinfoList=Orderinfo.dao.find("select distinct(zhName) as zhName from f_orderinfo where d_at is null order by orderDate");
        JSONArray ja=new JSONArray();
        JSONObject jo=null;
        for (int i = 0; i < orderinfoList.size(); i++) {
            jo=new JSONObject();
            jo.put("name",orderinfoList.get(i).getZhName());
            jo.put("isParent",true);
            ja.add(jo);
        }
        CacheKit.put(Consts.CACHE_NAMES.orderinfo.name(),ORDERINFO_TREE_JSON, JSON.toJSONString(ja));
    }

    /**
     * 绑定订单条目与知识库关系
     */
    @Before({EvictInterceptor.class, Tx.class})
    @CacheName("orderinfo")
    public void removeBindOdAndKb(){
        Integer orderdetailId=getParaToInt("orderdetailId");
        Orderdetail od=Orderdetail.dao.findById(orderdetailId);
        od.setKbId(null);
        od.update();
        String sname=od.getScientificName();
        List<Orderdetail> list=Orderdetail.dao.findByScientificNameTrim(sname.trim());
        for (Orderdetail orderdetail:list){
            orderdetail.setKbId(null);
            orderdetail.setMAt(new Date());
            orderdetail.update();
        }
        Orderdetail.dao.findOrderdetailWithKbRelation();
        renderSuccessJSON("订单详细与知识库解除绑定成功！","");
    }

    /**
     * 绑定订单条目与知识库关系
     */
    @Before({EvictInterceptor.class, Tx.class})
    @CacheName("orderinfo")
    public void bindOdAndKb(){
        Integer orderdetailId=getParaToInt("orderdetailId");
        Integer kbId=getParaToInt("kbId");
        Orderdetail od=Orderdetail.dao.findById(orderdetailId);
        od.setKbId(kbId);
        od.update();
        String sname=od.getScientificName();
        List<Orderdetail> list=Orderdetail.dao.findByScientificNameTrim(sname.trim());
        for (Orderdetail orderdetail:list){
            orderdetail.setKbId(kbId);
            orderdetail.setMAt(new Date());
            orderdetail.update();
        }
        Orderdetail.dao.findOrderdetailWithKbRelation();
        renderSuccessJSON("订单详细与知识库绑定成功！","");
    }

    @Clear
    public void countStatistics(){
        Integer odId=getParaToInt("odId");
        List<JSONObject> list=Orderdetail.dao.statisticsCount(odId);
        LogKit.info(JSON.toJSONString(list));
        renderJson(JSON.toJSONString(list));
    }
    @Clear
    public void priceStatistics(){
        Integer odId=getParaToInt("odId");
        Orderdetail orderdetail=Orderdetail.dao.findById(odId);
        JSONObject jo=new JSONObject();
        jo.put("orderZhname",orderdetail.getOrderinfoZhname());
        StringBuilder sb=new StringBuilder();
        sb.append(orderdetail.getScientificName());
        if(StrKit.notBlank(orderdetail.getSize())){
            sb.append("|").append(orderdetail.getSize());
        }
        if(StrKit.notBlank(orderdetail.getStrain())){
            sb.append("|").append(orderdetail.getStrain());
        }
        jo.put("orderdetailName",sb.toString());
        JSONArray ja=Orderdetail.dao.statisticsPrice(odId);
        jo.put("statisticsData",ja);

        LogKit.info(jo.toJSONString());
        renderJson(jo.toJSONString());
    }

}
