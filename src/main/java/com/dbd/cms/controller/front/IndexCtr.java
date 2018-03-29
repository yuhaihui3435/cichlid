package com.dbd.cms.controller.front;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.dbd.cms.Consts;
import com.dbd.cms.core.DBDCmsConfig;
import com.dbd.cms.core.DBDController;
import com.dbd.cms.interceptor.BeetlCacheInterceptor;
import com.dbd.cms.interceptor.UserInterceptor;
import com.dbd.cms.kits.*;
import com.dbd.cms.model.*;
import com.jfinal.aop.Before;
import com.jfinal.aop.Clear;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import com.jfinal.upload.UploadFile;
import com.qq.connect.QQConnectException;
import com.qq.connect.api.OpenID;
import com.qq.connect.api.qzone.UserInfo;
import com.qq.connect.javabeans.AccessToken;
import com.qq.connect.javabeans.qzone.UserInfoBean;
import com.qq.connect.oauth.Oauth;

import javax.servlet.http.HttpServletResponse;
import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/12/1.
 */
public class IndexCtr extends DBDController {
    public static final String QQ_LOGIN_ERROR = "qqLoginError";
    public static final String QQ_INFO_ERROR = "qqInfoError";
    public static final String WX_LOGIN_ERROR = "wxLoginError";


    @Clear(UserInterceptor.class)
    public void index() {
        setAttr("galleryList", Knowledgebase.dao.findByRand(0));
        setAttr("articleList", Content.dao.findNewContent(2));
        if (isParaExists("v")) {
            setAttr("errorCode", getPara("v"));
        }
        render("/front/index.html");
    }

    /**
     * 知识库 首页导向
     */
    public void kl() {
        setAttr("needAdminLTE", "needAdminLTE");//是否需要加载，adminlte样式
        render("/front/kl/search.html");
    }

    /**
     * 知识库条件搜索
     */
    public void searchK() {
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
     * 知识库 详细查看
     */
    @Before(BeetlCacheInterceptor.class)
    @CacheName("knowledgebaseView")
    public void view() {
        Integer id = getParaToInt("id");
        Knowledgebase knowledgebase = Knowledgebase.dao.findFirst("select fk.*,(select enName from f_area where id=fk.areaId) as areaTxt,(select title from c_taxonomy where id=fk.sgId ) as sgTxt,(select title from c_taxonomy where id=fk.speciesId ) as speciesTxt,(select val from s_param where id=fk.fh) as fhTxt,(select val from s_param where id=fk.rt) as rtTxt,(select val from s_param where id=fk.mf) as mfTxt,(select val from s_param where id=fk.breed) as breedTxt,(select val from s_param where id=fk.ha) as haTxt,(select val from s_param where id=fk.ca) as caTxt,(select val from s_param where id=fk.rare) as rareTxt,(select val from s_param where id=fk.reproduce) as reproduceTxt,(select nickname from s_user where id=fk.oper) as operTxt from f_knowledgebase fk where id=?", id);
        setAttr("knowledgebase", knowledgebase);
        setAttr("contentId", knowledgebase.getId().toString());
        setAttr("authorId", knowledgebase.getOper());
        setAttr("contentModule", Consts.CACHE_NAMES.knowledge.name());
        render("/front/kl/view.html");
    }

    /**
     * 内容评论查询
     */
    public void replyList() {
        Integer contentId = getParaToInt("contentId");
        String module = getPara("contentModule");
        String sql = " from c_comment cc where cc.content_id=? and cc.content_module=? and cc.status='00' order by cc.created desc ";
        Page<Comment> page = Comment.dao.paginate(getPN(), getPS(), "select cc.*,(select nickname from s_user where id=cc.user_id) as user_nickname,(select avatar from s_user where id=cc.user_id) as user_avatar,(CASE cc.content_module WHEN 'article'THEN (select thumbnail from c_content where id=cc.content_id) WHEN 'knowledge' THEN (select thumbnail from f_knowledgebase where id=cc.content_id) WHEN 'supplierView' THEN (select pic from f_supplier_pics where supplierId=cc.content_id limit 1 ) END) as content_thumbnail," +
                "(CASE cc.content_module WHEN 'article'THEN (select title from c_content where id=cc.content_id) WHEN 'knowledge' THEN (select scName from f_knowledgebase where id=cc.content_id) WHEN 'supplierView' THEN (select name from f_supplier where id=cc.content_id  ) END) as content_title, " +
                "(CASE cc.content_module WHEN 'article'THEN (select summary from c_content where id=cc.content_id) WHEN 'knowledge' THEN (select zhName from f_knowledgebase where id=cc.content_id) WHEN 'supplierView' THEN (select summary from f_supplier where id=cc.content_id  ) END) as content_summary  ", sql, contentId, module);
        renderJson(JSON.toJSONString(page, SerializerFeature.DisableCircularReferenceDetect));
    }

    /**
     * 新增评论
     */
    @Before({UserInterceptor.class, Tx.class})

    public void addReply() {
        Integer contentId = getParaToInt("contentId");
        String contentModule = getPara("contentModule");
        Integer authorId = null;
        if (isParaExists("authorId") && !isParaBlank("authorId")) {
            authorId = getParaToInt("authorId");
        }
        String txt = getPara("replyContent");
        if (StrKit.notBlank(txt)) {
            txt = WordFilter.doFilter(txt);
            LogKit.info(txt);
        }
        Pattern pattern = Pattern.compile("@[^\\s]+\\s?");
        Matcher m = pattern.matcher(txt);
        ArrayList<String> strs = new ArrayList<String>();
        while (m.find()) {
            //LogKit.info(m.groupCount()+"");
            //if(m.groupCount()>1)

            if (!strs.contains(m.group()))
                strs.add(m.group());
        }
        User user = null;
        At at = null;
        String _s = null;
        String _txt = txt;
        String author = null;
        for (String s : strs) {
            _s = s;
            s = s.replace("@", "");
            s = s.trim();
            user = User.dao.findByNickname(s);
            _txt = _txt.replaceAll(_s, "<a href=\"" + getRequest().getContextPath() + "/userInfo?id=" + user.getId().intValue() + "\" target=\"_blank\">" + _s + "</a>");
            if (user != null && user.getId().intValue() != authorId) {
                at = new At();
                at.setCAt(new Date());
                at.setContentId(contentId);
                at.setFromUserId(currUser().getId().intValue());
                at.setToUserId(user.getId().intValue());
                at.setText(m.replaceAll(""));
                at.save();
            } else {
                author = _s;
            }


        }
//        if(StrKit.notBlank(author))
//        txt=txt.replaceAll(author,"");
//        if(currUser().getId().intValue()!=authorId) {
//            at = new At();
//            at.setCAt(new Date());
//            at.setContentId(contentId);
//            at.setFromUserId(currUser().getId().intValue());
//            at.setToUserId(authorId);
//            at.setText(txt);
//            at.save();
//        }

        Comment comment = new Comment();
        comment.setContentModule(contentModule);
        comment.setContentId(BigInteger.valueOf(contentId));
        comment.setText(_txt);
        comment.setUserId(currUser().getId());
        comment.setCreated(new Date());
        comment.setIp(ReqKit.getRemortIP(getRequest()));
        comment.setStatus(Consts.CHECK_STATUS.normal.getVal());
        comment.setAgent(ReqKit.getUseragent(getRequest()));
        comment.saveWithoutClean();
        renderSuccessJSON("回复成功", "");

    }

    /**
     * 删除评论
     */
    @Before({UserInterceptor.class, Tx.class})

    public void delReply() {
        Integer commentId = getParaToInt("id");
        Comment.dao.deleteById(commentId);
        At.dao.delByContentId(commentId);
        renderSuccessJSON("删除回复成功", "");
    }

    /**
     * 导向我的设置页面
     */
    @Before({UserInterceptor.class})
    public void my() {
        if (isParaExists("active")) {
            String active = getPara("active");
            if (StrKit.isBlank(active)) {
                setAttr("myReplyActive", "active");
            } else {
                setAttr(active, "active");
            }
        } else {
            setAttr("myReplyActive", "active");
        }

        int userId = 0;
        if (isParaExists("seeUId")) {
            userId = getParaToInt("seeUId");//如果是查看其它人的用户信息
            if (userId != currUser().getId().intValue()) {//并且不能查看自己
                setAttr("seeUId", userId);
                setAttr("seeUser", User.dao.findById(userId));
                int currUserId = currUser().getId().intValue();
                Follow follow = Follow.dao.findFirst("select * from s_follow where fromUserId=? and followUserId=?", currUserId, userId);
                if (follow != null) {
                    setAttr("isFollow", "yes");
                } else {
                    setAttr("isFollow", "no");
                }
                PriLetter priLetter = PriLetter.dao.findByFromUserAndToUser(userId, currUserId);

                setAttr("priLetter", priLetter);
            } else {
                userId = currUser().getId().intValue();
            }

        } else {
            userId = currUser().getId().intValue();
        }


        //查询 我的收藏，我的关注，我的粉丝的数量


        Record record = Db.findFirst("select count(*) as followers from s_follow where followUserId=?", userId);
        setAttr("followers", record.getLong("followers"));
        record = Db.findFirst("select count(*) as collects from s_collect where userId=?", userId);
        setAttr("collects", record.getLong("collects"));
        record = Db.findFirst("select count(*) as ifollow from s_follow where fromUserId=?", userId);
        setAttr("ifollow", record.getLong("ifollow"));
        setAttr("needAdminLTE", "needAdminLTE");
        render("/front/userinfo/my.html");

    }

    /**
     * 我的评论
     */
    @Before({UserInterceptor.class})
    public void myCommnets() {
        Integer userId = -1;
        if (isParaExists("seeUId") && !isParaBlank("seeUId")) {
            userId = getParaToInt("seeUId");
        } else {
            User user = currUser();
            userId = user.getId().intValue();
        }
        String sql = " from c_comment cc  where cc.status='00' and cc.user_id=? order by cc.created desc";
        Page<Comment> page = Comment.dao.paginate(getPN(), getPS(), "select *,(select nickname from s_user where id=cc.user_id) as user_nickname,(select avatar from s_user where id=cc.user_id) as user_avatar,(CASE cc.content_module WHEN 'article'THEN (select thumbnail from c_content where id=cc.content_id) WHEN 'knowledge' THEN (select thumbnail from f_knowledgebase where id=cc.content_id) WHEN 'supplierView' THEN (select pic from f_supplier_pics where supplierId=cc.content_id limit 1 ) END) as content_thumbnail," +
                "(CASE cc.content_module WHEN 'article'THEN (select title from c_content where id=cc.content_id) WHEN 'knowledge' THEN (select scName from f_knowledgebase where id=cc.content_id) WHEN 'supplierView' THEN (select name from f_supplier where id=cc.content_id  ) END) as content_title, " +
                "(CASE cc.content_module WHEN 'article'THEN (select summary from c_content where id=cc.content_id) WHEN 'knowledge' THEN (select zhName from f_knowledgebase where id=cc.content_id) WHEN 'supplierView' THEN (select summary from f_supplier where id=cc.content_id  ) END) as content_summary ", sql, userId);
        LogKit.info(JSON.toJSONString(page));
        renderJson(JSON.toJSONString(page, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty));
    }

    /**
     * @我列表
     */
    @Before({UserInterceptor.class})
    public void myAt() {
        User user = currUser();
        Integer userId = user.getId().intValue();
        String sql = " from s_at sat where sat.toUserId=? order by sat.c_at desc";
        Page<At> page = At.dao.paginate(getPN(), getPS(), "select sat.*,(select nickname from s_user where id=sat.fromUserId) as fromUser_nickname,(select avatar from s_user where id=sat.fromUserId) as fromUser_avatar ,(CASE sat.contentModule WHEN 'article'THEN (select thumbnail from c_content where id=sat.contentId) WHEN 'knowledge' THEN (select thumbnail from f_knowledgebase where id=sat.contentId) WHEN 'supplierView' THEN (select pic from f_supplier_pics where supplierId=sat.contentId limit 1 ) END) as content_thumbnail," +
                "(CASE sat.contentModule WHEN 'article'THEN (select title from c_content where id=sat.contentId) WHEN 'knowledge' THEN (select scName from f_knowledgebase where id=sat.contentId) WHEN 'supplierView' THEN (select name from f_supplier where id=sat.contentId  ) END) as content_title, " +
                "(CASE sat.contentModule WHEN 'article'THEN (select summary from c_content where id=sat.contentId) WHEN 'knowledge' THEN (select zhName from f_knowledgebase where id=sat.contentId) WHEN 'supplierView' THEN (select summary from f_supplier where id=sat.contentId  ) END) as content_summary", sql, userId);
        LogKit.info(JSON.toJSONString(page));
        renderJson(JSON.toJSONString(page, SerializerFeature.DisableCircularReferenceDetect, SerializerFeature.WriteNullStringAsEmpty));
    }

    /**
     * 我的私信
     */
    @Before({UserInterceptor.class})
    public void mySx() {
        User user = currUser();
        Integer userId = user.getId().intValue();
        String sql = " from s_pri_letter where (fromUserId=? or toUserId=?) and dAt is null order by cAt desc";
        Page<PriLetter> page = PriLetter.dao.paginate(getPN(), getPS(), "select * ", sql, userId, userId);
        LogKit.info(JSON.toJSONString(page));
        renderJson(JSON.toJSONString(page, SerializerFeature.DisableCircularReferenceDetect));
    }

    /**
     * 查看我的私信详细
     */
    @Before({UserInterceptor.class})
    public void mySxDetail() {
        Integer plId = getParaToInt("plId");
        String sql = " select splr.* from s_pri_letter_record splr   where splr.splId=? and splr.dAt is null order by splr.cAt desc";
        List<PriLetterRecord> list = PriLetterRecord.dao.find(sql, plId);
        renderJson(JSON.toJSONString(list, SerializerFeature.DisableCircularReferenceDetect));
    }

    /**
     * 新增私信
     */
    @Before({UserInterceptor.class, Tx.class})
    public void sx() {
        Integer userId = currUser().getId().intValue();
        String content = getPara("content");
        content = WordFilter.doFilter(content);
        Integer toUserId = getParaToInt("toUserId");
        PriLetter priLetter = PriLetter.dao.findByFromUserAndToUser(userId, toUserId);
        if (priLetter == null) {
            priLetter = new PriLetter();
            priLetter.setFromUserId(userId);
            priLetter.setToUserId(toUserId);
            priLetter.setCAt(new Date());
            priLetter.save();
        }

        PriLetterRecord priLetterRecord = new PriLetterRecord();
        priLetterRecord.setSplId(priLetter.getId().intValue());
        priLetterRecord.setTxt(content);
        priLetterRecord.setCAt(new Date());
        priLetterRecord.setSenderId(userId);
        priLetterRecord.setReceiverId(toUserId);
        priLetterRecord.save();
//        CacheKit.removeAll(Consts.CACHE_NAMES.sx.name());
        renderSuccessJSON("私信发送成功", priLetter);
    }

    /**
     * 删除私信
     */
    @Before({UserInterceptor.class, Tx.class})
    public void delSx() {
        Integer id = getParaToInt("priLetterId");
        PriLetter priLetter = PriLetter.dao.findById(id);
        priLetter.setDAt(new Date());
        priLetter.update();
        renderSuccessJSON("私信删除成功", "");
    }

    /**
     * 删除私信详细，未用
     */
    @Before({UserInterceptor.class, Tx.class})
    public void delSxDetail() {
        Integer id = getParaToInt("priLetterRecordId");
        PriLetterRecord priLetterRecord = PriLetterRecord.dao.findById(id);
        priLetterRecord.setDAt(new Date());
        priLetterRecord.update();
        renderSuccessJSON("私信删除成功", "");

    }

    /**
     * 更新头像
     */
    @Before({UserInterceptor.class, Tx.class})
    public void uploadAvatar() {
        User user = currUser();
        UploadFile uf = getFile();
        String fileName = null;

        if (uf != null) {
            fileName = "images/avatar/" + System.currentTimeMillis() + "_" + user.getId().intValue() + "_" + uf.getFileName();
            QiNiuKit.upload(uf.getFile(), fileName);
        }

        if (fileName != null) {
            user.setAvatar(fileName);
            user.update();
        }
        CacheKit.remove(Consts.CACHE_NAMES.user.name(), user.getId());
        renderSuccessJSON("头像信息更新成功", "");
    }

    /**
     * 我的关注
     */
    @Before({UserInterceptor.class})
    public void myFollow() {
        Integer userId = -1;
        if (isParaExists("seeUId") && !isParaBlank("seeUId")) {
            userId = getParaToInt("seeUId");
        } else {
            User user = currUser();
            userId = user.getId().intValue();
        }
        List<Follow> list = Follow.dao.find("select * from s_follow where fromUserId=? order by id desc", userId);
        renderJson(JSON.toJSONString(list));
    }

    /**
     * 我的收藏
     */
    @Before({UserInterceptor.class})
    public void myCollect() {
        Integer userId = currUser().getId().intValue();
        String sql = " from s_collect where userId=?  order by id desc";
        Page<Collect> page = Collect.dao.paginate(getPN(), getPS(), "select * ", sql, userId);
        LogKit.info(JSON.toJSONString(page));
        renderJson(JSON.toJSONString(page));
    }

    /**
     * 关注
     */
    @Before({UserInterceptor.class, Tx.class})
    public void follow() {
        Integer userId = currUser().getId().intValue();
        Integer followUserId = getParaToInt("followUserId");
        Follow follow = new Follow();
        follow.setFromUserId(userId);
        follow.setFollowUserId(followUserId);
        follow.save();
        renderSuccessJSON("关注成功", "");
    }

    /**
     * 取消关注
     */
    @Before({UserInterceptor.class, Tx.class})
    public void cancelFollow() {
        Integer userId = currUser().getId().intValue();
        Integer followId = getParaToInt("followUserId");

        Follow follow = Follow.dao.findFirst("select * from s_follow where fromUserId=? and followUserId=?", userId, followId);
        follow.delete();
        renderSuccessJSON("取消关注成功", "");
    }

    /**
     * 收藏
     */
    @Before({UserInterceptor.class, Tx.class})
    public void collect() {
        Integer userId = currUser().getId().intValue();
        Integer id = getParaToInt("cId");
        String module = getPara("module");

        List list = Collect.dao.findByUserIdAndCIdAndModule(userId, id, module);
        if (!list.isEmpty()) {
            renderFailJSON("此内容已收藏，请刷新页面。", "");
            return;
        }
        Collect collect = new Collect();
        collect.setCId(id);
        collect.setModule(module);
        collect.setUserId(userId);
        Knowledgebase knowledgebase = null;
        Content content = null;
        Supplier supplier = null;
        if (module.equals(Consts.MODULE_NAMES.knowledgebase.name())) {
            knowledgebase = Knowledgebase.dao.findById(id);
            collect.setTitle(knowledgebase.getScName());
            collect.setUrl("/view?id=" + id);
            CacheKit.remove(Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + id);
        } else if (module.equals(Consts.MODULE_NAMES.content.name())) {
            content = Content.dao.findById(id);
            collect.setTitle(content.getTitle());
            collect.setUrl("/art/view?id=" + id);
            CacheKit.remove(Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id + "&tId=" + content.getCatalog().getBigInteger("id"));
        } else if (module.equals(Consts.MODULE_NAMES.supplier.name())) {
            supplier = Supplier.dao.findById(id);
            collect.setTitle(supplier.getName());
            collect.setUrl("/supplier/view?id=" + id);
            CacheKit.remove(Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + id);
        }
        collect.save();
        long count = 0L;
        count = Db.queryLong("select count(1) from s_collect where cId=?", id);
        if (module.equals("knowledgebase")) {
            knowledgebase.setCollectCount(Integer.parseInt(count + ""));
            knowledgebase.update();
        } else if (module.equals("content")) {
            content.setCollectCount(Integer.parseInt(count + ""));
            content.update();
        } else if (module.equals("supplier")) {
            supplier.setCollectCount(Integer.parseInt(count + ""));
            supplier.update();
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("collectCount", count);
        jsonObject.put("collectId", collect.getId());
        CacheKit.removeAll(Consts.CACHE_NAMES.collect.name());
        renderSuccessJSON("收藏成功", jsonObject);
    }

    /**
     * 取消收藏
     */
    @Before({UserInterceptor.class, Tx.class})
    public void cancelCollect() {
        //1.查询得到被收藏内容的id
        //2.然后在删掉收藏的流水记录
        //3.重新计算被收藏内容的收藏量
        //4.更新被收藏内容的收藏量
        Integer collectId = getParaToInt("collectId");
        Collect collect = Collect.dao.findById(collectId);
        if (collect == null) {
            renderFailJSON("出错了，请刷新页面", "");
            return;
        }
        Integer id = collect.getCId();
        collect.delete();
        long count = 0L;
        count = Db.queryLong("select count(1) from s_collect where cId=?", id);
        String module = collect.getModule();
        Knowledgebase knowledgebase = null;
        Content content = null;
        Supplier supplier = null;
        if (module.equals(Consts.MODULE_NAMES.knowledgebase.name())) {
            knowledgebase = Knowledgebase.dao.findById(id);
            CacheKit.remove(Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + id);
            knowledgebase.setCollectCount(Integer.parseInt(count + ""));
            knowledgebase.update();
        } else if (module.equals(Consts.MODULE_NAMES.content.name())) {
            content = Content.dao.findById(id);
            content.setCollectCount(Integer.parseInt(count + ""));
            CacheKit.remove(Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id + "&tId=" + content.getCatalog().getBigInteger("id"));
            content.update();
        } else if (module.equals(Consts.MODULE_NAMES.supplier.name())) {
            supplier = Supplier.dao.findById(id);
            supplier.setCollectCount(Integer.parseInt(count + ""));
            CacheKit.remove(Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + id);
            supplier.update();
        }
//        CacheKit.removeAll(Consts.CACHE_NAMES.collect.name());

        Record record = Db.findFirst("select count(*) as collects from s_collect where userId=?", currUser().getId());


        JSONObject jsonObject = new JSONObject();
        jsonObject.put("collectCount", count);
        jsonObject.put("cId", id);
        jsonObject.put("collects", record.getLong("collects"));
        renderSuccessJSON("取消收藏成功", jsonObject);
    }


    /**
     * 微信互联登录，未实现完
     */
    public void wxLogin() {
        String appId = PropKit.use("wxconnectconfig.properties").get("WX_AppID");
        HttpServletResponse response = getResponse();
        response.setContentType("text/html;charset=utf-8");
        String state = StringKit.getUUID();
        getSession().setAttribute("wxLoginStateVal", state);
        redirect("https://open.weixin.qq.com/connect/qrconnect?appid=" + appId + "&redirect_uri=http://www.cichlid.cc/wxCallBack&response_type=code&scope=snsapi_login&state=" + state + "#wechat_redirect");

        return;

    }

    /**
     * qq互联登录成功后，的回调处理
     */
    public void wxCallBack() {

        LogKit.info("微信第三方web登录处理开始");
        String appId = PropKit.use("wxconnectconfig.properties").get("WX_AppID");
        String secret = PropKit.use("wxconnectconfig.properties").get("WX_AppSecret");


        if (!isParaExists("code")) {
            LogKit.error("微信第三方登录处理失败");
            setAttr("errorCode", WX_LOGIN_ERROR);
            render("/front/wxCallback.html");
            return;
        }

        String code = getPara("code");
        String state = getPara("state");

        String stateInSession = (String) getSession().getAttribute("wxLoginStateVal");

        if (!state.equals(stateInSession)) {
            LogKit.error("微信第三方登录处理失败:登录回调疑似被篡改");
            setAttr("errorCode", WX_LOGIN_ERROR);
            setAttr("errorMsg", "微信授权登录失败");
            render("/front/wxCallback.html");
            return;
        }


        Map param = new HashMap<String, String>();
        param.put("code", code);
        param.put("grant_type", "authorization_code");
        param.put("appid", appId);
        param.put("secret", secret);


        String rs = HttpKit.get("https://api.weixin.qq.com/sns/oauth2/access_token", param);

        LogKit.info("获取TOKEN" + rs);

        JSONObject jsonObject = JSON.parseObject(rs);

        if (jsonObject.containsKey("errcode")) {
            LogKit.error("微信第三方登录处理失败:" + jsonObject.toJSONString());
            setAttr("errorCode", jsonObject.get("errcode"));
            setAttr("errorMsg", jsonObject.get("errmsg"));
            render("/front/wxCallback.html");
            return;
        }


        String openid = jsonObject.getString("openid");
        String access_token = jsonObject.getString("access_token");
        param.clear();
        param.put("access_token", access_token);
        param.put("openid", openid);

        rs = HttpKit.get("https://api.weixin.qq.com/sns/userinfo", param);

        LogKit.info("获取用户信息" + rs);

        jsonObject = JSON.parseObject(rs);

        if (jsonObject.containsKey("errcode")) {
            LogKit.error("微信第三方登录处理失败:" + jsonObject.toJSONString());
            setAttr("errorCode", jsonObject.get("errcode"));
            setAttr("errorMsg", jsonObject.get("errmsg"));
            render("/front/wxCallback.html");
            return;
        }

        String nickname = jsonObject.getString("nickname");
        String avatar = jsonObject.getString("headimgurl");
        String unionid = jsonObject.getString("unionid");
        User user = User.dao.addThirdUserInfo(nickname, avatar, access_token, openid, unionid, "WeChat");
        if (user.get("id") instanceof Long)
            CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getLong("id").toString(), Consts.COOKIE_TIMEOUT);
        else
            CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), Consts.COOKIE_TIMEOUT);
        CacheKit.remove(Consts.CACHE_NAMES.user.name(), user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(), user.getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(), user.getId());
        LogKit.info("微信web第三方登录处理结束");
        render("/front/wxCallback.html");
    }

    /**
     * qq互联登录处理
     */
    public void qqLogin() {
        HttpServletResponse response = getResponse();
        response.setContentType("text/html;charset=utf-8");
        try {
            redirect(new Oauth().getAuthorizeURL(getRequest()));
            return;
        } catch (QQConnectException e) {
            LogKit.error("qq登录连接错误[" + e.getMessage() + "]");
            setAttr("errorCode", QQ_LOGIN_ERROR);

        }
    }


    /**
     * qq互联登录成功后，的回调处理
     */
    public void qqCallBack() {
//        1.回调先获得qq返回的相关数据
//        2.检查该qq用户是否已经登录过本应用，登录过：获取已有的用户信息，未登录：新增用户信息

        String site = (String) DBDCmsConfig.getRf().groupTemplate.getSharedVars().get("siteDomain");
        AccessToken accessTokenObj = null;
        String accessToken = null,
                openID = null;
        long tokenExpireIn = 0L;
        try {
            accessTokenObj = (new Oauth()).getAccessTokenByRequest(getRequest());
        } catch (QQConnectException e) {
            LogKit.error("qq登录连接错误[" + e.getMessage() + "]");
            setAttr("errorCode", QQ_LOGIN_ERROR);
            render("/front/loginCallback.html");
            return;

        }
        if (accessTokenObj.getAccessToken().equals("")) {
            LogKit.error("没有获取到响应参数");
            setAttr("errorCode", QQ_LOGIN_ERROR);
        } else {
            LogKit.info("开始处理QQ互联登录后返回");
            accessToken = accessTokenObj.getAccessToken();
            tokenExpireIn = accessTokenObj.getExpireIn();
            OpenID openIDObj = new OpenID(accessToken);
            try {
                openID = openIDObj.getUserOpenID();
                LogKit.info("userOpenID:" + openID);
            } catch (QQConnectException e) {
                LogKit.error("qq登录连接错误[" + e.getMessage() + "]");
                redirect(site + "?v=" + QQ_LOGIN_ERROR);
            }
            UserInfo qzoneUserInfo = new UserInfo(accessToken, openID);
            try {
                UserInfoBean userInfoBean = qzoneUserInfo.getUserInfo();
                if (userInfoBean.getRet() == 0) {
                    String nickname = userInfoBean.getNickname();


                    String avatar = userInfoBean.getAvatar().getAvatarURL30();
                    User user = User.dao.addThirdUserInfo(nickname, avatar, accessToken, openID, "", "QQ");
                    if (user.get("id") instanceof Long)
                        CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getLong("id").toString(), Consts.COOKIE_TIMEOUT);
                    else
                        CookieKit.put(this, Consts.USER_ACCESS_TOKEN, user.getId().toString(), Consts.COOKIE_TIMEOUT);
                    CacheKit.remove(Consts.CACHE_NAMES.user.name(), user.getId());
                    LogKit.info("QQ互联登录处理结束");
                } else {
                    LogKit.error("很抱歉，我们没能正确获取到您的信息，原因是： " + userInfoBean.getMsg());
                    setAttr("errorCode", QQ_LOGIN_ERROR);
                }
            } catch (QQConnectException e) {
                LogKit.error("qq登录连接错误[" + e.getMessage() + "]");
                setAttr("errorCode", QQ_LOGIN_ERROR);
            }
        }
        render("/front/loginCallback.html");
    }

    /**
     * 点赞处理
     */
    @Before({UserInterceptor.class, Tx.class})
    public void laud() {
        int cId = getParaToInt("cId");
        String module = getPara("module");
        List _l = Laud.dao.findByCIdAndModule(cId, module);
        if (!_l.isEmpty()) {
            renderFailJSON("您已经赞过该内容", "");
            return;
        }
        Laud laud = new Laud();
        laud.setCId(cId);
        laud.setModule(module);
        laud.setUserId(currUser().getId().intValue());
        laud.save();

        Long count = Laud.dao.countByCIdAndModule(cId, module);
        String sql = null;
        if (module.equals(Consts.MODULE_NAMES.knowledgebase.name())) {

            Knowledgebase knowledgebase = Knowledgebase.dao.findById(cId);
            knowledgebase.setLaudCount(count.intValue());
            knowledgebase.update();
//            sql="update s_knowledgebase set laud_count=laud_count+1 where id=?";
//            Db.update(sql,cId);
            CacheKit.remove(Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + cId);
        } else if (module.equals(Consts.MODULE_NAMES.content.name())) {
            Content content = Content.dao.findById(cId);
            content.setLaudCount(count.intValue());
            content.update();
            CacheKit.remove(Consts.CACHE_NAMES.article.name(), "/art/view?id=" + cId + "&tId=" + content.getCatalog().getBigInteger("id"));
            CacheKit.remove(Consts.CACHE_NAMES.article.name(), "/art/view?id=" + cId + "&tId=");
        } else if (module.equals(Consts.MODULE_NAMES.supplier.name())) {
            Supplier supplier = Supplier.dao.findById(cId);
            supplier.setLaudCount(count.intValue());
            supplier.update();
//            sql="update s_supplier set laud_count=laud_count+1 where id=?";
//            Db.update(sql,cId);
            CacheKit.remove(Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + cId);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("laudId", laud.getId());
        jsonObject.put("laudCount", count);

        renderSuccessJSON("", jsonObject);
    }

    /**
     * 取消点赞
     */
    @Before({UserInterceptor.class, Tx.class})
    public void cancelLaud() {
        int laudId = getParaToInt("laudId");
        Laud laud = Laud.dao.findById(laudId);
        if (laud == null) {
            renderFailJSON("点赞已取消，请刷新重试", "");
            return;
        }
        int cId = laud.getCId();
        String module = laud.getModule();
        laud.delete();
        Long count = Laud.dao.countByCIdAndModule(cId, module);

        if (module.equals(Consts.MODULE_NAMES.knowledgebase.name())) {
            Knowledgebase knowledgebase = Knowledgebase.dao.findById(cId);
            knowledgebase.setLaudCount(count.intValue());
            knowledgebase.update();
            CacheKit.remove(Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + cId);
        } else if (module.equals(Consts.MODULE_NAMES.content.name())) {
            Content content = Content.dao.findById(cId);
            content.setLaudCount(count.intValue());
            content.update();
            CacheKit.remove(Consts.CACHE_NAMES.article.name(), "/art/view?id=" + cId + "&tId=" + content.getCatalog().getBigInteger("id"));
        } else if (module.equals(Consts.MODULE_NAMES.supplier.name())) {
            Supplier supplier = Supplier.dao.findById(cId);
            supplier.setLaudCount(count.intValue());
            supplier.update();
            CacheKit.remove(Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + cId);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("cId", laud.getCId());
        jsonObject.put("laudCount", count);
        renderSuccessJSON("", jsonObject);
    }

    @Before({UserInterceptor.class})
    public void logout() {

        String prevUrl = getPara("prevUrl");
        if (StrKit.isBlank(prevUrl)) prevUrl = "/";
        CookieKit.remove(this, Consts.USER_ACCESS_TOKEN);
        CacheKit.remove(Consts.CACHE_NAMES.user.name(), currUser().getId());
        CacheKit.remove(Consts.CACHE_NAMES.userReses.name(), currUser().getId());
        CacheKit.remove(Consts.CACHE_NAMES.userRoles.name(), currUser().getId());
        redirect(prevUrl);
    }

}
