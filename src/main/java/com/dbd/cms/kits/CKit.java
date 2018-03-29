package com.dbd.cms.kits;

import com.jfinal.core.Controller;
import com.jfinal.plugin.ehcache.CacheKit;

import java.util.HashMap;

/**
 * Created by yuhaihui8913 on 2017/9/20.
 */
public class CKit {

    /**
     * 刷新缓存中数据，如果缓存数据为空，则保存到reqest上下文中
     * @param controller
     * @param cName
     * @param cKey
     * @param key
     * @param val
     */
    public static void refreshIbeetlViewCache(Controller controller, String cName, String cKey, String key, Object val){
        HashMap data=(HashMap) CacheKit.get(cName,cKey);
        if(data==null)controller.setAttr(key,val);//第一次如果没有缓存，则直接放到attr中
        if(data!=null) {
            data.put(key, val);
            CacheKit.put(cName, cKey, data);
        }
    }
}
