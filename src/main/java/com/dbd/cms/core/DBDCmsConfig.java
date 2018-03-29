package com.dbd.cms.core;

import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dbd.cms.Consts;
import com.dbd.cms.controller.CommonCtr;
import com.dbd.cms.controller.admin.Knowledgebase.KnowledgebaseCtr;
import com.dbd.cms.controller.admin.area.AreaCtr;
import com.dbd.cms.controller.admin.content.ContentCtr;
import com.dbd.cms.controller.admin.invitecode.InvitecodeCtr;
import com.dbd.cms.controller.admin.orderinfo.OrderinfoCtr;
import com.dbd.cms.controller.admin.res.ResCtr;
import com.dbd.cms.controller.admin.role.RoleCtr;
import com.dbd.cms.controller.admin.setting.SettingCtr;
import com.dbd.cms.controller.admin.supplier.SupplierCtr;
import com.dbd.cms.controller.admin.taxonomy.TaxonomyCtr;
import com.dbd.cms.controller.admin.user.UserCtr;
import com.dbd.cms.controller.front.IndexCtr;
import com.dbd.cms.controller.front.WeChatAPICtr;
import com.dbd.cms.controller.front.article.ArtCtr;
import com.dbd.cms.controller.front.supplier.SupplierInfoCtr;
import com.dbd.cms.controller.front.userinfo.UserInfoCtr;
import com.dbd.cms.interceptor.CommonInterceptor;
import com.dbd.cms.interceptor.ExceptionInterceptor;
import com.dbd.cms.interceptor.FCommonInterceptor;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.FileKit;
import com.dbd.cms.kits.WordFilter;
import com.dbd.cms.model.*;
import com.google.common.collect.Lists;
import com.jfinal.config.*;
import com.jfinal.kit.PropKit;
import com.jfinal.log.Log4jLogFactory;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.template.Engine;
import com.jfplugin.mail.MailPlugin;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.jfinal3.JFinal3BeetlRenderFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//import com.dbd.cms.controller.front.article.ArtCtr;

/**
 * Created by yuhaihui on 2016/11/28.
 */
public class DBDCmsConfig extends JFinalConfig {


    private  static JFinal3BeetlRenderFactory rf;

    public void configConstant(Constants constants) {

        PropKit.use("config.properties");

        constants.setDevMode(PropKit.getBoolean("devMode", false));
        //constants.setMainRenderFactory(new BeetlRenderFactory());
        constants.setError500View("/common/500.html");
        constants.setError404View("/common/404.html");
        constants.setError403View("/common/403.html");
        constants.setEncoding("UTF-8");
        constants.setLogFactory(new Log4jLogFactory());
        //constants.setViewType(ViewType.OTHER);
        rf = new JFinal3BeetlRenderFactory();
        rf.config();
        constants.setRenderFactory(rf);

    }

    public void configRoute(Routes routes) {
        final Routes adminR=new Routes() {
            @Override
            public void config() {
                addInterceptor(new UserInterceptor());
                add("/admincc", com.dbd.cms.controller.admin.IndexCtr.class);
                add("/adminUser", UserCtr.class);
                add("/adminRes", ResCtr.class);
                add("/adminRole",RoleCtr.class);
                add("/adminTaxonomy",TaxonomyCtr.class);
                add("/adminSetting",SettingCtr.class);
                add("/adminContent",ContentCtr.class);
                add("/adminArea", AreaCtr.class);
                add("/adminKnowledgebase", KnowledgebaseCtr.class);
                add("/adminSupplier", SupplierCtr.class);
                add("/adminOrderinfo", OrderinfoCtr.class);
                add("/adminInvitecode", InvitecodeCtr.class);
            }
        };


        final Routes forntR=new Routes() {
            @Override
            public void config() {
                //addInterceptor(new CommonInterceptor());
                addInterceptor(new FCommonInterceptor());
                add("/", IndexCtr.class);
                add("/userinfo", UserInfoCtr.class);
                add("/supplier", SupplierInfoCtr.class);
                add("/orderinfo", com.dbd.cms.controller.front.orderinfo.OrderinfoCtr.class);
                add("/art", ArtCtr.class);

            }
        };

        final Routes cR=new Routes() {
            @Override
            public void config() {
                addInterceptor(new FCommonInterceptor());add("/c", CommonCtr.class);
            }
        };
        routes.add(adminR);
        routes.add(cR);
        routes.add(forntR);
        final Routes wxR=new Routes() {
            @Override
            public void config() {
                addInterceptor(new UserInterceptor());
                add("/wc", WeChatAPICtr.class);
            }
        };
        routes.add(wxR);//微信公众账号路由设置

    }

    public void configEngine(Engine engine) {

    }

    public void configPlugin(Plugins plugins) {
        //开启druid数据库连接池
        DruidPlugin druidPlugin = createDruidPlugin();
        // StatFilter提供JDBC层的统计信息
        druidPlugin.addFilter(new StatFilter());
        // WallFilter的功能是防御SQL注入攻击
        WallFilter wallDefault = new WallFilter();
        wallDefault.setDbType("mysql");
        druidPlugin.addFilter(wallDefault);
        druidPlugin.setInitialSize(PropKit.getInt("db.default.poolInitialSize"));
        druidPlugin.setMaxPoolPreparedStatementPerConnectionSize(PropKit.getInt("db.default.poolMaxSize"));
        druidPlugin.setTimeBetweenConnectErrorMillis(PropKit.getInt("db.default.connectionTimeoutMillis"));
        plugins.add(druidPlugin);
        //开启DB+record 映射关系插件
        ActiveRecordPlugin arp = new ActiveRecordPlugin(druidPlugin);
        _MappingKit.mapping(arp);
        arp.setShowSql(true);
        plugins.add(arp);
        //开启eheache缓存
        plugins.add(new EhCachePlugin());
        plugins.add(new MailPlugin(PropKit.use("config.properties").getProperties()));

    }

    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new ExceptionInterceptor());
        interceptors.add(new CommonInterceptor());

    }

    public void configHandler(Handlers handlers) {

    }

    public static DruidPlugin createDruidPlugin() {
        DruidPlugin druidDefault = new DruidPlugin(PropKit.get("db.default.url"), PropKit.get("db.default.user"),
                PropKit.get("db.default.password"), PropKit.get("db.default.driver"));
        return druidDefault;
    }


    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        initCache();
        Consts.basicWithImages.addAttributes(":all", "style");

    }

    public static void initCache(){
        CacheKit.removeAll(Consts.CACHE_NAMES.paramCache.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.user.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.fArea.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.userRoles.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.userReses.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.knowledge.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.article.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.knowledgebaseView.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.ssq.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.orderinfo.name());
        //CacheKit.removeAll(Consts.CACHE_NAMES.orderinfoAndKbRelation.name());
//        CacheKit.removeAll(Consts.CACHE_NAMES.modules.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.taxonomy.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.knowledge.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.habitat.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.supplierView.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.comment.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.mgc.name());
        CacheKit.removeAll(Consts.CACHE_NAMES.sx.name());

        List<String> moduleList= Lists.newArrayList();
        List<Param> fhList=Lists.newArrayList();
        List<Param> rtList=Lists.newArrayList();
        List<Param> mfList=Lists.newArrayList();
        List<Param> breedList=Lists.newArrayList();
        List<Param> haList=Lists.newArrayList();
        List<Param> caList=Lists.newArrayList();
        List<Param> rareList=Lists.newArrayList();
        List<Param> reproduceList=Lists.newArrayList();
        List<Param> perchList=Lists.newArrayList();
        List<Param> transportList=Lists.newArrayList();
        //模板全局变量
        GroupTemplate groupTemplate = rf.groupTemplate;

        Map<String, Object> sharedVars = new HashMap<String, Object>();
        //缓存参数表数据
        List<Param> paramList=Param.dao.find("select * from s_param");
        for(Param p:paramList){
            sharedVars.put(p.getK(), p.getVal());
            if(p.getK().contains("module")){
                moduleList.add(p.getVal());
            }
            else if(p.getK().indexOf("rt-")==0){
                rtList.add(p);
            }
            else if(p.getK().indexOf("fh-")==0){
                fhList.add(p);
            }
            else if(p.getK().indexOf("mf-")==0){
                mfList.add(p);
            }
            else if(p.getK().indexOf("breed-")==0){
                breedList.add(p);
            }
            else if(p.getK().indexOf("ha-")==0){
                haList.add(p);
            }
            else if(p.getK().indexOf("ca-")==0){
                caList.add(p);
            }
            else if(p.getK().indexOf("rare-")==0){
                rareList.add(p);
            }
            else if(p.getK().startsWith("reproduce-")){
                reproduceList.add(p);
            }
            else if(p.getK().startsWith("perch-")){
                perchList.add(p);
            }
            else if(p.getK().startsWith("transport-")){
                transportList.add(p);
            }
            CacheKit.put(Consts.CACHE_NAMES.paramCache.name(),p.getK(),p.getVal());
        }

        List<Area> areaList= Area.dao.findByCache(Consts.CACHE_NAMES.fArea.name(),Consts.CACHE_NAMES.fArea.name(),"select * from f_area");
        groupTemplate.setSharedVars(sharedVars);
        CacheKit.put(Consts.CACHE_NAMES.paramCache.name(), Consts.CACHE_NAMES.modules.name(),moduleList);
        sharedVars.put("areas",Taxonomy.dao.findArea());
        sharedVars.put("catalogs",Taxonomy.dao.findCatalog());
        sharedVars.put("tags",Taxonomy.dao.findTag());
        sharedVars.put("species",Taxonomy.dao.findSpecies());
        sharedVars.put("subGroup",Taxonomy.dao.findSubGroup());
        sharedVars.put("orderSupport",Taxonomy.dao.findOrderSupport());
        sharedVars.put("f_areas",areaList);
        sharedVars.put("rtList",rtList);
        sharedVars.put("fhList",fhList);
        sharedVars.put("mfList",mfList);
        sharedVars.put("breedList",breedList);
        sharedVars.put("haList",haList);
        sharedVars.put("caList",caList);
        sharedVars.put("rareList",rareList);
        sharedVars.put("reproduceList",reproduceList);
        sharedVars.put("perchList",perchList);
        sharedVars.put("transportList",transportList);
        sharedVars.put("allSpecies",Taxonomy.dao.findAllSpecies());

        initSSQ(sharedVars);
        initOrderinfoAndKbRelation();
        initMgc();
    }

    private static void initSSQ(Map<String,Object> sharedVars){
        String shengStr=FileKit.loadFile2Str("jsonData/shengData.json");
        String shiStr=FileKit.loadFile2Str("jsonData/shiData.json");
        String quStr=FileKit.loadFile2Str("jsonData/quData.json");
        JSONArray shengJA= JSON.parseArray(shengStr);
        JSONArray shiJA=JSON.parseArray(shiStr);
        JSONArray quJA=JSON.parseArray(quStr);
        List<SSQ> shengList=new ArrayList<SSQ>();
        List<SSQ> shiList=new ArrayList<SSQ>();
        List<SSQ> quList=new ArrayList<SSQ>();
        JSONObject jo=null;
        SSQ ssq=null;
        String shengCk,shiCk=null;
        int proID;
        int shiID;
        for (int i = 0; i <shengJA.size() ; i++) {
            jo=shengJA.getJSONObject(i);
            ssq=new SSQ();
            ssq.setId(jo.getIntValue("ProID"));
            ssq.setName(jo.getString("name"));
            ssq.setSort(jo.getIntValue("ProSort"));
            sharedVars.put("sheng"+ssq.getId(),ssq.getName());
            shengList.add(ssq);
            shengCk=ssq.getName()+ssq.getId();
            proID=ssq.getId();
            for (int j = 0; j < shiJA.size(); j++) {
                jo=shiJA.getJSONObject(j);
                if(jo.getIntValue("ProID")==proID) {
                    ssq = new SSQ();
                    ssq.setId(jo.getIntValue("CityID"));
                    ssq.setName(jo.getString("name"));
                    ssq.setSort(jo.getIntValue("CitySort"));
                    ssq.setPid(jo.getIntValue("ProID"));
                    sharedVars.put("shi"+ssq.getId(),ssq.getName());
                    shiList.add(ssq);
                    shiCk=ssq.getName()+ssq.getId();
                    shiID=ssq.getId();
                    for (int k = 0; k < quJA.size(); k++) {
                        jo=quJA.getJSONObject(k);
                        if(jo.getIntValue("CityID")==shiID){
                            ssq = new SSQ();
                            ssq.setId(jo.getIntValue("Id"));
                            ssq.setName(jo.getString("DisName"));
                            ssq.setSort(jo.getIntValue("DisSort"));
                            ssq.setPid(jo.getIntValue("CityID"));
                            sharedVars.put("qu"+ssq.getId(),ssq.getName());
                            quList.add(ssq);
                        }
                    }
//                    LogKit.info(JSON.toJSONString(quList));
                    CacheKit.put(Consts.CACHE_NAMES.ssq.name(),shiCk,JSON.toJSONString(quList));
                    quList.clear();
                }
            }
//            LogKit.info(JSON.toJSONString(shiList));
            CacheKit.put(Consts.CACHE_NAMES.ssq.name(),shengCk,JSON.toJSONString(shiList));
            shiList.clear();

        }
        sharedVars.put("shengList",shengList);
    }

    public static void initOrderinfoAndKbRelation(){
        Orderdetail.dao.findOrderdetailWithKbRelation();
    }


    public static JFinal3BeetlRenderFactory getRf() {
        return rf;
    }

    public static void initMgc(){
        List<String> mgcList=new ArrayList<String>();
        List<Mgc> list=Mgc.dao.find("select DISTINCT(txt) as txt from s_mgc ");
        for (int i = 0; i < list.size(); i++) {
            mgcList.add(list.get(i).getTxt());
        }
//        CacheKit.put(Consts.CACHE_NAMES.mgc.name(),"mgcAll",mgcList);
        WordFilter.init(mgcList);
    }
}
