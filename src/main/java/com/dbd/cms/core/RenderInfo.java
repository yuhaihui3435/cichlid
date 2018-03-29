package com.dbd.cms.core;

import com.jfinal.render.*;
import org.beetl.core.GroupTemplate;
import org.beetl.ext.jfinal.BeetlRender;
import org.beetl.ext.jfinal.BeetlRenderFactory;
import org.beetl.ext.jfinal3.JFinal3BeetlRender;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuhaihui8913 on 2017/2/8.
 */
public class RenderInfo implements Serializable {
    private static final long serialVersionUID = -7299875545092102194L;

    private String view;
    private Integer renderType;
    private Map<String, Object> otherPara = null;

    public RenderInfo(Render render) {
        if (render == null)
            throw new IllegalArgumentException("Render can not be null.");

        view = render.getView();
        if (render instanceof FreeMarkerRender)
            renderType = RenderType.FREE_MARKER_RENDER;
        else if (render instanceof JspRender)
            renderType = RenderType.JSP_RENDER;
        else if (render instanceof VelocityRender)
            renderType = RenderType.VELOCITY_RENDER;
        else if (render instanceof XmlRender)
            renderType = RenderType.XML_RENDER;
        else if (render instanceof JsonRender) {
            JsonRender jr = (JsonRender)render;
            renderType = RenderType.JSON_RENDER;
            otherPara = new HashMap<String, Object>();
            otherPara.put("jsonText", jr.getJsonText());
            otherPara.put("attrs", jr.getAttrs());
            otherPara.put("forIE", jr.getForIE());
        }
        else if (render instanceof BeetlRender) {
            renderType = RenderType.BEETL_RENDER;
        }
        else if (render instanceof JFinal3BeetlRender) {
            renderType = RenderType.JFINAL3_BEETL_RENDER;
        }
        else
            throw new IllegalArgumentException("CacheInterceptor can not support the render of the type : " + render.getClass().getName());
    }

    public Render createRender() {
        if (renderType == RenderType.FREE_MARKER_RENDER)
            return new FreeMarkerRender(view);
        else if (renderType == RenderType.JSP_RENDER)
            return new JspRender(view);
        else if (renderType == RenderType.VELOCITY_RENDER)
            return new VelocityRender(view);
        else if (renderType == RenderType.XML_RENDER)
            return new XmlRender(view);
        else if (renderType == RenderType.JSON_RENDER) {
            JsonRender jr;
            if (otherPara.get("jsonText") != null)
                jr = new JsonRender((String)otherPara.get("jsonText"));
            else if (otherPara.get("attrs") != null)
                jr = new JsonRender((String[])otherPara.get("attrs"));
            else
                jr = new JsonRender();

            if (Boolean.TRUE.equals(otherPara.get("forIE")))
                jr.forIE();

            return jr;
        }
        else if (renderType == RenderType.BEETL_RENDER) {
            GroupTemplate gt = BeetlRenderFactory.groupTemplate;
            return new BeetlRender(gt, view);
        }
        else if (renderType == RenderType.JFINAL3_BEETL_RENDER) {
            GroupTemplate gt = DBDCmsConfig.getRf().groupTemplate;
            return new BeetlRender(gt, view);
        }
        throw new IllegalArgumentException("CacheInterceptor can not support the renderType of the value : " + renderType);
    }

    /**
     * 扩展部分
     */
    class RenderType extends com.jfinal.plugin.ehcache.RenderType {
        public static final int BEETL_RENDER = 6;
        public static final int JFINAL3_BEETL_RENDER=7;
    }
}
