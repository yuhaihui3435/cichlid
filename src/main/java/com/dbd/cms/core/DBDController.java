package com.dbd.cms.core;

import com.alibaba.druid.util.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.ValueFilter;
import com.dbd.cms.Consts;
import com.dbd.cms.model.Role;
import com.dbd.cms.model.User;
import com.jfinal.core.Controller;
import com.jfinal.plugin.activerecord.Page;

import java.util.List;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/12/1.
 */
public class DBDController extends Controller {
    public static final String PAGENUMBER = "offset";
    public static final String PAGESIZE = "limit";
    public static final String ORDER="order";
    public static final String ERROR_MSG="_err_msg";
    public static final String SUCCESS_MSG="_suc_msg";


    /**
     * @param @return 参数说明
     * @return int    返回类型
     * @throws
     * @Title: getPN
     * @Description: 获取当前第几页
     */
    public int getPN() {
        int pagenumber = getParaToInt(PAGENUMBER, 0);

        pagenumber = (pagenumber == 0) ? 1 : pagenumber/getPS()+1;
        return pagenumber;
    }

    /**
     * @param @return 参数说明
     * @return int    返回类型
     * @throws
     * @Title: getPS
     * @Description: 每页显示多少条，如果页面上没有设置默认显示15条。
     */
    public int getPS() {
        return getParaToInt(PAGESIZE, 15);
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description:获取表单的排序
     **/
    public String getOrder(){
        String order=getPara("order");
        return (StringUtils.isEmpty(order))?"asc":"desc";
    }

    /**
     * @param page 查询结果
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description: 渲染bootstrap table
     **/
    public void rendBootTable(Page page) {
        int totalRows = page.getTotalRow();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("total", totalRows );
        if (!page.getList().isEmpty()) {
            Object list_json = JSON.toJSON(page.getList());
            jsonObject.put("rows", list_json);
        }

//        LogKit.debug("rendBootTable result:" + JSON.toJSONString(jsonObject,filter,SerializerFeature.WriteMapNullValue,SerializerFeature.WriteNullStringAsEmpty));
        renderJson(JSON.toJSONString(jsonObject,filter,SerializerFeature.WriteMapNullValue, SerializerFeature.WriteNullStringAsEmpty));
    }

    /**
     * @param msg  自定义消息
     * @param data 数据
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description: 请求成功的JSON返回结果
     **/
    public void renderSuccessJSON(String msg, Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resCode", Consts.REQ_JSON_CODE.success.name());
        jsonObject.put("resMsg", (StringUtils.isEmpty(msg) ? "操作成功" : msg));
        jsonObject.put("resData", data);
        renderJson(jsonObject.toJSONString());
    }

    /**
     * @param msg  自定义消息
     * @param data 数据
     * @return void
     * @throws
     * @author: 于海慧  2016/12/4
     * @Description: 请求失败的JSON返回结果
     **/
    public void renderFailJSON(String msg, Object data) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("resCode", Consts.REQ_JSON_CODE.fail.name());
        jsonObject.put("resMsg", (StringUtils.isEmpty(msg) ? "操作失败" : msg));
        jsonObject.put("resData", data);
        renderJson(jsonObject.toJSONString());
    }

    /**
     * @param
     * @return void
     * @throws
     * @author: 于海慧  2017/1/16
     * @Description:用户未通过认证返回结果
     **/
    public void renderUnauthorizedJSON(String s) {
        getResponse().setHeader("sessionstatus", "timeout-"+s);
        renderFailJSON("请先身份认证后，再进行操作。","");
        return;
    }




    private static  ValueFilter filter = new ValueFilter() {
        public Object process(Object obj, String s, Object v) {
            if(v==null)
                return "";
            return v;
        }
    };

    protected User currUser(){
        return (User)getAttr(Consts.CURR_USER);
    }

    protected List<Role> currUserRoles(){return getAttr(Consts.CURR_USER_ROLES);}

    protected boolean isAdmin(){
        List<Role> roles=currUserRoles();
        boolean bl=false;
        for (int i = 0; i < roles.size(); i++) {
            if(roles.get(i).getName().equals("admin")){
                bl=true;
                break;
            }
        }
        return bl;
    }


}
