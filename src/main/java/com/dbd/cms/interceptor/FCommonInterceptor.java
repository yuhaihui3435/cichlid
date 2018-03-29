package com.dbd.cms.interceptor;

import com.dbd.cms.Consts;
import com.dbd.cms.kits.CKit;
import com.dbd.cms.kits.CookieKit;
import com.dbd.cms.kits.ReqKit;
import com.dbd.cms.model.*;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.StrKit;

import java.util.List;

/**
 * Created by yuhaihui8913 on 2017/6/21.
 */
public class FCommonInterceptor implements Interceptor {
    public void intercept(Invocation invocation) {
        Controller controller=invocation.getController();
        controller.setAttr("newArticleList5", Content.dao.findNewContent(5));
        controller.setAttr("newKnowledgebase5", Knowledgebase.dao.findNew(5));
        controller.setAttr("newOrderinfo5", Orderinfo.dao.findNew(5));
        String userId= CookieKit.get(controller, Consts.USER_ACCESS_TOKEN);
        String ak=invocation.getActionKey();
        List _l=null;
        Collect collect=null;
        Laud laud=null;
        long viewCount=0L;
        int id=0;
        String tId="";


        controller.setAttr("isWechat",ReqKit.isWechatBrowser(controller.getRequest()));

        LogKit.info("当前用户使用的浏览器类型为:"+ReqKit.getUseragent(controller.getRequest()));

        //修改基于ibeetl缓存的页面中的，内容访问数的数据
        if(controller.isParaExists("id")) {
             id = controller.getParaToInt("id");
             tId = controller.getPara("tId","");
            if (ak.startsWith("/view")) {
                viewCount = viewCount(id, Consts.MODULE_NAMES.knowledgebase.name());
                CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + id,"viewCount", viewCount > 1000 ? (viewCount / 1000) + "K" : viewCount + "");
                CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + id,"isWechat", ReqKit.isWechatBrowser(controller.getRequest()));

            } else if (ak.startsWith("/art/view")) {

                viewCount = viewCount(id, Consts.MODULE_NAMES.content.name());
                if(!StrKit.isBlank(tId)) {
                    CKit.refreshIbeetlViewCache(controller, Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id + "&tId=" + tId, "viewCount", viewCount > 1000 ? (viewCount / 1000) + "K" : viewCount + "");
                    CKit.refreshIbeetlViewCache(controller, Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id + "&tId=" + tId, "isWechat", ReqKit.isWechatBrowser(controller.getRequest()));
                }else {
                    CKit.refreshIbeetlViewCache(controller, Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id, "viewCount", viewCount > 1000 ? (viewCount / 1000) + "K" : viewCount + "");
                    CKit.refreshIbeetlViewCache(controller, Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id, "isWechat", ReqKit.isWechatBrowser(controller.getRequest()));
                }
            } else if (ak.startsWith("/supplier/view")) {
                viewCount = viewCount(id, Consts.MODULE_NAMES.supplier.name());
                CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + id,"viewCount",  viewCount > 1000 ? (viewCount / 1000) + "K" : viewCount + "");
                CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + id,"isWechat",  ReqKit.isWechatBrowser(controller.getRequest()));
            }
        }
        //修改基于ibeetl页面数据缓存中，使用用户已经点过赞和收藏的判断
        if (StrKit.notBlank(userId)) {
            if(ak.startsWith("/view")){

                _l=Collect.dao.findByUserIdAndCIdAndModule(Integer.parseInt(userId),id, Consts.MODULE_NAMES.knowledgebase.name());
                if(!_l.isEmpty())
                collect=(Collect)_l.get(0);

                _l= Laud.dao.findByCIdAndModuleAndUserId(id, Consts.MODULE_NAMES.knowledgebase.name(),Integer.parseInt(userId));
                if(!_l.isEmpty())
                    laud=(Laud) _l.get(0);

                if(collect!=null)
                    CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + id,"collectId",collect.getId());

                if(laud!=null)
                    CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.knowledgebaseView.name(), "/view?id=" + id,"laudId",laud.getId());

            }else if(ak.startsWith("/art/view")){


                _l=Collect.dao.findByUserIdAndCIdAndModule(Integer.parseInt(userId),id, Consts.MODULE_NAMES.content.name());
                if(!_l.isEmpty())
                    collect=(Collect)_l.get(0);

                _l= Laud.dao.findByCIdAndModuleAndUserId(id, Consts.MODULE_NAMES.content.name(),Integer.parseInt(userId));
                if(!_l.isEmpty())
                    laud=(Laud) _l.get(0);

                if(collect!=null)
                    CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id + "&tId=" + tId,"collectId",collect.getId());

                if(laud!=null)
                    CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.article.name(), "/art/view?id=" + id + "&tId=" + tId,"laudId",laud.getId());
            }else if(ak.startsWith("/supplier/view")){

                _l=Collect.dao.findByUserIdAndCIdAndModule(Integer.parseInt(userId),id, Consts.MODULE_NAMES.supplier.name());
                if(!_l.isEmpty())
                    collect=(Collect)_l.get(0);

                _l= Laud.dao.findByCIdAndModuleAndUserId(id, Consts.MODULE_NAMES.supplier.name(),Integer.parseInt(userId));
                if(!_l.isEmpty())
                    laud=(Laud) _l.get(0);

                if(collect!=null)
                    CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + id,"collectId",collect.getId());

                if(laud!=null)
                    CKit.refreshIbeetlViewCache(controller,Consts.CACHE_NAMES.supplierView.name(), "/supplier/view?id=" + id,"laudId",laud.getId());
            }

        }

        invocation.invoke();
    }

    /**
     * 内容查看数统计
     * @param cId
     * @param module
     * @return
     */
    private int viewCount(int cId,String module){
        ReadCount readCount=ReadCount.dao.findByCIdAndModule(cId, module);

        if(readCount==null){
            readCount=new ReadCount();
            readCount.setModule(module);
            readCount.setCId(cId);
            readCount.setCount(1);
            readCount.save();
            return 1;
        }

        ReadCount.dao.countAddOne(cId,module);

        return ReadCount.dao.findByCIdAndModule(cId,module).getCount();

    }


}
