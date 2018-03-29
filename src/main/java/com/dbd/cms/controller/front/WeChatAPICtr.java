package com.dbd.cms.controller.front;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.CookieKit;
import com.dbd.cms.kits.DateKit;
import com.dbd.cms.kits.QiNiuKit;
import com.dbd.cms.kits.StringKit;
import com.dbd.cms.model.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.*;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import com.jfinal.weixin.sdk.api.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 微信端API
 *
 * Created by yuhaihui8913 on 2017/9/30.
 */
public class WeChatAPICtr extends DBDController {
    /**
     * 查询知识库全部分类
     */
    @Clear(UserInterceptor.class)
    public  void  querySpecies(){
        renderJson(DBDCmsConfig.getRf().groupTemplate.getSharedVars().get("allSpecies"));
    }

    /**
     * 查询知识库详细
     *
     */
    //@Clear(UserInterceptor.class)
    public  void queryKbView(){
        Integer id = getParaToInt("id");
        Knowledgebase knowledgebase = Knowledgebase.dao.findFirst("select fk.*,(select enName from f_area where id=fk.areaId) as areaTxt,(select title from c_taxonomy where id=fk.sgId ) as sgTxt,(select title from c_taxonomy where id=fk.speciesId ) as speciesTxt,(select val from s_param where id=fk.fh) as fhTxt,(select val from s_param where id=fk.rt) as rtTxt,(select val from s_param where id=fk.mf) as mfTxt,(select val from s_param where id=fk.breed) as breedTxt,(select val from s_param where id=fk.ha) as haTxt,(select val from s_param where id=fk.ca) as caTxt,(select val from s_param where id=fk.rare) as rareTxt,(select val from s_param where id=fk.reproduce) as reproduceTxt,(select nickname from s_user where id=fk.oper) as operTxt from f_knowledgebase fk where id=?", id);
       renderJson(JSON.toJSONString(knowledgebase, SerializerFeature.WriteNullStringAsEmpty));
    }

    /**
     * 查询文章列表
     *
     */
    @Clear(UserInterceptor.class)
    public void queryArt(){
        Integer tId=null;
        if(isParaExists("tId")){
            tId=getParaToInt("tId");
        }
        renderJson(JSON.toJSONString(Content.dao.findContentByTIdAndType(tId,getPN(),getPS(),"catalog"), SerializerFeature.WriteNullStringAsEmpty));
    }

    /**
     * 查询文章分类列表
     *
     */
    @Clear(UserInterceptor.class)
    public void queryArtCatalog(){
        List<Taxonomy> taxonomyList=(List<Taxonomy>) DBDCmsConfig.getRf().groupTemplate.getSharedVars().get("catalogs");
        for(Taxonomy taxonomy:taxonomyList){
            taxonomy.setContentCount(Content.dao.findCountInTaxonomy(taxonomy.getId().intValue()));
        }
        renderJson(JSON.toJSONString(taxonomyList));
    }

    /**
     * 查询文章详细
     */
    @Clear(UserInterceptor.class)
    public void queryArtView(){
        int id=getParaToInt("id");
        int tId=0;
        Content c=Content.dao.findFirst("select * from c_content where id=?",id);
        renderJson(JSON.toJSONString(c));
    }

    /**
     *
     *
     * 知识库搜索
     *
     */
    @Clear(UserInterceptor.class)
    public void searchKB(){
        Page<Knowledgebase> page;
        String serach = getPara("search");
        Knowledgebase knowledgebase = getModel(Knowledgebase.class);
        StringBuffer where = new StringBuffer("from f_knowledgebase fk where 1=1 and fk.d_at is null ");
        if (knowledgebase.getAreaId() != null)
            where.append(" and fk.areaId=").append(knowledgebase.getAreaId());
        if (knowledgebase.getSgId() != null)
            where.append(" and fk.sgId=").append(knowledgebase.getSgId());
        if (knowledgebase.getSpeciesId() != null)
            where.append(" and fk.speciesId=").append(knowledgebase.getSpeciesId());
        if (!isParaBlank("search")) {
            where.append(" and (instr(fk.enName,?)>0 or instr(fk.zhName,?)>0 or instr(fk.scName,?)>0 or instr(fk.bName,?)>0 or instr(fk.py,?)>0)");
            where.append(" order by fk.id");
            page = Knowledgebase.dao.paginateByCache(Consts.CACHE_NAMES.knowledge.name(), where.toString() + getPN() + getPS() + serach, getPN(), getPS(), "select fk.*,(select title from c_taxonomy where id=fk.sgId ) as sgTxt,(select title from c_taxonomy where id=fk.speciesId ) as speciesTxt,(select val from s_param where id=fk.fh) as fhTxt,(select val from s_param where id=fk.rt) as rtTxt,(select val from s_param where id=fk.mf) as mfTxt,(select val from s_param where id=fk.breed) as breedTxt,(select val from s_param where id=fk.ha) as haTxt,(select val from s_param where id=fk.ca) as caTxt,(select val from s_param where id=fk.rare) as rareTxt,(select val from s_param where id=fk.reproduce) as reproduceTxt,(select nickname from s_user where id=fk.oper) as operTxt ", where.toString(), serach, serach, serach, serach, serach);
        } else {
            where.append(" order by fk.id");
            page = Knowledgebase.dao.paginateByCache(Consts.CACHE_NAMES.knowledge.name(), where.toString() + getPN() + getPS() + serach, getPN(), getPS(), "select fk.*,(select title from c_taxonomy where id=fk.sgId ) as sgTxt,(select title from c_taxonomy where id=fk.speciesId ) as speciesTxt,(select val from s_param where id=fk.fh) as fhTxt,(select val from s_param where id=fk.rt) as rtTxt,(select val from s_param where id=fk.mf) as mfTxt,(select val from s_param where id=fk.breed) as breedTxt,(select val from s_param where id=fk.ha) as haTxt,(select val from s_param where id=fk.ca) as caTxt,(select val from s_param where id=fk.rare) as rareTxt,(select val from s_param where id=fk.reproduce) as reproduceTxt,(select nickname from s_user where id=fk.oper) as operTxt ", where.toString());
        }
//        LogKit.info(JSON.toJSONString(page, SerializerFeature.WriteNullStringAsEmpty));
        renderJson(JSON.toJSONString(page, SerializerFeature.WriteNullStringAsEmpty));
    }

    /**
     *
     * 图片上传，图片格式为base64编码的字符串
     *
     *
     */
//    @Clear(UserInterceptor.class)
    public void uploadPic(){
        UploadFile uf=getFile("file");
        int uploadMaxSize= CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),Consts.USER_UPLOAD_MAX_SIZE_CK)!=null?Integer.parseInt(CacheKit.get(Consts.CACHE_NAMES.paramCache.name(),Consts.USER_UPLOAD_MAX_SIZE_CK).toString()):5;
        String userKey=CookieKit.get(this,Consts.USER_ACCESS_TOKEN);
        String base64Str=getPara("base64Str");
        String module=getPara("module");
        String moduleId=getPara("moduleId");
        long uploadedCount=Pic.dao.findCountByUserKey(Integer.parseInt(userKey),module,moduleId);
        LogKit.info("上传最大数为："+uploadMaxSize+",已经上传个数:"+uploadedCount);
        if(uploadMaxSize<=uploadedCount){
            renderFailJSON("每种物种可以最多上传"+uploadMaxSize+"张照片，您已经为该物种上传了"+uploadedCount+"张照片","");
            return ;
        }

        String imgKey= "/upload/"+ DateKit.dateToStr(new Date(),DateKit.yyyyMMdd)+"/"+StringKit.getUUID()+".jpg";
        String qnRs=null;
        try {
             qnRs=QiNiuKit.put64image(base64Str,imgKey);
        } catch (IOException e) {
            LogKit.error("七牛上传base64图片失败:"+e.getMessage());
            renderFailJSON("图片上传失败","");
            return ;
        }


        if(qnRs==null) {
            renderFailJSON("图片上传失败", "");
            return ;
        }else{
            if(qnRs.equals(Consts.YORN_STR.yes.name())){
                Pic pic=new Pic();
                pic.setCAt(new Date());
                pic.setModule(module);
                pic.setModuleId(Integer.parseInt(moduleId));
                pic.setStatus(Consts.CHECK_STATUS.waitingCheck.getVal());
                pic.setPic(imgKey);
                pic.setUploadUser(StrKit.isBlank(userKey)?null:Integer.parseInt(userKey));
                pic.save();
                renderSuccessJSON("图片上传成功","");
                return ;
            }else{
                LogKit.error("七牛base64上传失败:"+qnRs);
                renderFailJSON("图片上传失败", "");
            }
        }
    }

    /**
     *
     * 查询内容下对应的，用户上传的图片
     *
     */
    @Clear(UserInterceptor.class)
    public void loadUploadPics(){

        String module=getPara("module");
        String moduleId=getPara("moduleId");
        Page<Pic> page;
        StringBuffer where=null;
        if(currUser()!=null&&isAdmin()){
            where = new StringBuffer("from f_pic fp where 1=1 and fp.d_at is null  and fp.module=? and fp.moduleId=?");
            page=Pic.dao.paginate(getPN(),getPS(),"select fp.*,(select nickname from s_user where id=fp.uploadUser) as userNickname ",where.toString(),module,moduleId);
        }else{
            where = new StringBuffer("from f_pic fp where 1=1 and fp.d_at is null and fp.status=? and fp.module=? and fp.moduleId=?");
            page=Pic.dao.paginate(getPN(),getPS(),"select fp.*,(select nickname from s_user where id=fp.uploadUser) as userNickname ",where.toString(),Consts.CHECK_STATUS.normal.getVal(),module,moduleId);
        }

        renderJson(JSON.toJSONString(page, SerializerFeature.WriteNullStringAsEmpty));
    }


    public static final String WXGZ_OAUTH_ERROR_NOCODE="9999";
    public static final String WXGZ_OAUTH_ERROR_GETTOKEN="9998";
    public static final String WXGZ_OAUTH_ERROR_GETUSERINFO="9997";
    public static final String WXGZ_OAUTH_ERROR_FALSIFY="9996";
    public static final String WXGZ_OAUTH_SUCCESS="1000";

    /**
     * 微信账号登录回调
     *
     *
     */
    @Clear(UserInterceptor.class)
    public void wxCallback(){
        LogKit.info("微信公众账号登录回调处理开始");
        String appId = PropKit.use("wxconnectconfig.properties").get("WXGZ_AppID");
        String secret = PropKit.use("wxconnectconfig.properties").get("WXGZ_AppSecret");
        String domain=PropKit.use("config.properties").get("domain");
        String wxDomain=PropKit.use("config.properties").get("wxDomain");


        //9999
        if (!isParaExists("code")) {
            LogKit.error("微信公众账号登录处理失败,CODE="+WXGZ_OAUTH_ERROR_NOCODE);
            redirect(wxDomain+"?woe="+WXGZ_OAUTH_ERROR_NOCODE);
            return;
        }

        String code = getPara("code");
        String state = getPara("state");
        Map param = new HashMap<String, String>();
        param.put("code", code);
        param.put("grant_type", "authorization_code");
        param.put("appid", appId);
        param.put("secret", secret);
//        String wx_oauth2_token=(String)getSession().getAttribute(Consts.WX_OAUTH2_TOKEN);
//        LogKit.info("应用session中wx_oauth2_token="+wx_oauth2_token);
        //9996
//        if(!wx_oauth2_token.equals(state)){
//            LogKit.error("微信回调疑似被篡改,返回的state="+state+",CODE="+WXGZ_OAUTH_ERROR_FALSIFY);
//            redirect(wxDomain+"?woe="+WXGZ_OAUTH_ERROR_FALSIFY);
//            return;
//        }

        String rs = HttpKit.get("https://api.weixin.qq.com/sns/oauth2/access_token", param);

        LogKit.info("获取TOKEN" + rs);

        JSONObject jsonObject = JSON.parseObject(rs);

        if (jsonObject.containsKey("errcode")) {
            LogKit.error("微信公众账号登录处理失败信息:" + jsonObject.toJSONString()+",CODE="+WXGZ_OAUTH_ERROR_GETTOKEN);
            redirect(wxDomain+"?woe="+WXGZ_OAUTH_ERROR_GETTOKEN);
            return;
        }
        String openid = jsonObject.getString("openid");
        String access_token = jsonObject.getString("access_token");
//        int expires_in=jsonObject.getIntValue("expires_in");

        param.clear();
        param.put("access_token", access_token);
        param.put("openid", openid);

        rs = HttpKit.get("https://api.weixin.qq.com/sns/userinfo", param);

        LogKit.info("获取用户信息" + rs);

        jsonObject = JSON.parseObject(rs);

        if (jsonObject.containsKey("errcode")) {
            LogKit.error("微信公众账号获取用户信息处理失败:" + jsonObject.toJSONString());
            redirect(wxDomain+"?woe="+WXGZ_OAUTH_ERROR_GETUSERINFO);
            return;
        }

        String nickname = jsonObject.getString("nickname");
        String avatar = jsonObject.getString("headimgurl");
        String unionid = jsonObject.getString("unionid");
        User user = User.dao.addThirdUserInfo(nickname, avatar, access_token, openid, unionid, "WeChatGZPT");
        if (user.get("id") instanceof Long)
            CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getLong("id").toString(), Consts.COOKIE_FOREVER);
        else
            CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), Consts.COOKIE_FOREVER);
        LogKit.info("微信公众账号登录处理结束");
        redirect(wxDomain+"?woe="+WXGZ_OAUTH_SUCCESS);
    }

    /**
     *
     * 进行微信公众账号的三方登录认证
     *
     *
     */
    @Clear(UserInterceptor.class)
    public void toWXOAuth2(){
        String callbackUrl=null;
        try {
            callbackUrl= URLEncoder.encode(PropKit.use("config.properties").get("domain")+"/wc/wxCallback","UTF-8");
            LogKit.info("微信公众平台回调地址:"+callbackUrl);
        } catch (UnsupportedEncodingException e) {
            LogKit.error("微信Autho2认证前，转换callbackURL时候，encode转码失败："+e.getMessage());
            e.printStackTrace();
        }
        String uuid= StrKit.getRandomUUID();
        getSession().setAttribute(Consts.WX_OAUTH2_TOKEN,uuid);
        String wxgz_appid=PropKit.use("wxconnectconfig.properties").get("WXGZ_AppID");
        String wx_oauth2_api="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+wxgz_appid+"&redirect_uri="+callbackUrl+"&response_type=code&scope=snsapi_userinfo&state="+uuid+"#wechat_redirect";
        redirect(wx_oauth2_api);
    }

    /**
     *
     * 删除图片
     *
     */
    @Before({Tx.class})
    @Clear(UserInterceptor.class)
    public void delKbPic(){
        int id=getParaToInt("id");
        Pic pic=Pic.dao.findById(id);
        String picUrl=pic.getPic();
        pic.setDAt(new Date());
        QiNiuKit.del(picUrl);
        pic.update();
        renderSuccessJSON("图片删除成功","");
    }

    /**
     *
     * 图片审核通过
     *
     */
    @Before({Tx.class})
    @Clear(UserInterceptor.class)
    public void checkKbPic(){
        int id=getParaToInt("id");
        Pic pic=Pic.dao.findById(id);
        pic.setStatus(Consts.CHECK_STATUS.normal.getVal());
        pic.update();
        renderSuccessJSON("审批通过","");
    }


    /**
     *
     * 查询微信APPID,APPSECRET
     *
     */
    @Clear(UserInterceptor.class)
    public void queryWXAPPINFO(){
        ApiConfig ac = new ApiConfig();
        ac.setAppId(PropKit.use("wxconnectconfig.properties").get("WXGZ_AppID"));
        ac.setAppSecret(PropKit.use("wxconnectconfig.properties").get("WXGZ_AppSecret"));
        ApiConfigKit.setThreadLocalApiConfig(ac);
        JsTicket jsApiTicket = JsTicketApi.getTicket(JsTicketApi.JsApiType.jsapi);
        String jsapi_ticket = jsApiTicket.getTicket();
        String nonce_str = create_nonce_str();
        String shareUrl=getPara("shareUrl");
//        // 注意 URL 一定要动态获取，不能 hardcode.
//        String url = "http://" + getRequest().getServerName() // 服务器地址
//                // + ":"
//                // + getRequest().getServerPort() //端口号
//                + getRequest().getContextPath() // 项目名称
//                + getRequest().getServletPath();// 请求页面或其他地址
//        String qs = getRequest().getQueryString(); // 参数
//        if (qs != null) {
//            url = url + "?" + (getRequest().getQueryString());
//        }
        //String wxDomain=PropKit.use("config.properties").get("wxDomain");
        //shareUrl=wxDomain+shareUrl.substring(1);
        System.out.println("shareUrl>>>>" + shareUrl);
        String timestamp = create_timestamp();
        // 这里参数的顺序要按照 key 值 ASCII 码升序排序
        //注意这里参数名必须全部小写，且必须有序
        String  str = "jsapi_ticket=" + jsapi_ticket +
                "&noncestr=" + nonce_str +
                "&timestamp=" + timestamp +
                "&url=" + shareUrl;

        String signature = HashKit.sha1(str);
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("appId",ApiConfigKit.getApiConfig().getAppId());
        jsonObject.put("nonceStr",nonce_str);
        jsonObject.put("timestamp",timestamp);
        jsonObject.put("signature",signature);
        renderJson(jsonObject);
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }


}
