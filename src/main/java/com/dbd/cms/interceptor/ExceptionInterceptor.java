package com.dbd.cms.interceptor;

import com.dbd.cms.core.DBDController;
import com.dbd.cms.core.DBDException;
import com.dbd.cms.kits.ReqKit;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.kit.LogKit;
import com.jfinal.kit.PropKit;
import com.qiniu.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by yuhaihui8913 on 2016/12/6.
 */
public class ExceptionInterceptor implements Interceptor {

    public void intercept(Invocation invocation) {
        DBDController controller = (DBDController)invocation.getController();
        HttpServletRequest request = controller.getRequest();

        boolean successed = false;
        try {
            invocation.invoke();
            successed = true;
        } catch (Exception e) {
            doLog(invocation,e);
            String domain= PropKit.use("config.properties").get("wxDomain");
            boolean isAjax = ReqKit.isAjaxRequest(request);
            String msg = formatException(e);
            if(isAjax){
                controller.renderFailJSON("服务器内部错误","");
            }else{
                String redirctUrl = request.getHeader("referer");
                if(StringUtils.isNullOrEmpty(redirctUrl)){
                    redirctUrl = request.getRequestURI();
                }

                String ak=invocation.getActionKey();
                String ck=invocation.getControllerKey();
                controller.setAttr(controller.ERROR_MSG, msg);
                controller.setAttr("redirctUrl", redirctUrl);
                if(ak.contains("admin")) {
                    controller.render("/common/500.html");
                }else if(ck.equals("/wc")){
                    controller.getResponse().setStatus(500);
                    controller.renderFailJSON("系统内部错误","");
                } else{
                    controller.render("/common/f_bns_err.html");
                }
            }
        }finally{
        }
    }

    private void doLog(Invocation ai,Exception e) {
        //开发模式
        if(JFinal.me().getConstants().getDevMode()){
            e.printStackTrace();
        }
        StringBuilder sb =new StringBuilder("\n---Exception Log Begin---\n");
        sb.append("Controller:").append(ai.getController().getClass().getName()).append("\n");
        sb.append("Method:").append(ai.getMethodName()).append("\n");
        sb.append("Exception Type:").append(e.getClass().getName()).append("\n");
        sb.append("Exception Details:");
        LogKit.error(sb.toString(), e);
    }
    private static String formatException(Exception e){
        String message = null;
        Throwable ourCause = e;
        while ((ourCause = e.getCause()) != null) {
            e = (Exception) ourCause;
        }
        String eClassName = e.getClass().getName();
        //一些常见异常提示
        if("java.lang.NumberFormatException".equals(eClassName)){
            message = "请输入正确的数字";
        }else if (e instanceof DBDException ) {
            message = "["+((DBDException) e).getCode()+"]" + e.getMessage();

        }else if(e instanceof RuntimeException){
            message=e.toString();
        }

        //获取默认异常提示
        if (StringUtils.isNullOrEmpty(message)){
            message = "系统繁忙,请稍后再试";
        }
        //替换特殊字符
        message = message.replaceAll("\"", "'");
        return message;
    }
}
