package com.dbd.cms.core;

import com.dbd.cms.Consts;
import com.jfinal.plugin.activerecord.Model;
import org.jsoup.Jsoup;

import java.util.Map;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/11/30.
 */
public abstract class DBDModel<M extends DBDModel<M>> extends Model<M> {

    /**
     * 防止xss攻击处理
     * @return
     */
    @Override
    public boolean save(){
        for(Map.Entry me : getAttrs().entrySet()){
            if(me.getValue() instanceof String) {
                set((String) me.getKey(), Jsoup.clean((String) me.getValue(), Consts.basicWithImages));
            }
        }
        return super.save();
    }

    public boolean saveWithoutClean(){
        return super.save();
    }

    /**
     * 防止xss攻击处理
     * @return
     */
    @Override
    public boolean update(){
        for(Map.Entry me : getAttrs().entrySet()){
            if(me.getValue() instanceof String)
                set((String)me.getKey(), Jsoup.clean((String)me.getValue(),Consts.basicWithImages));
        }
        return super.update();
    }

    public boolean updateWithoutClean(){
        return super.update();
    }

    public String getYOrNTxt(boolean val){
        return (val)? Consts.YORN.yes.getLabel(): Consts.YORN.no.getLabel();
    }

    public String getStatusTxt(String val){
        if(val==null)return "";
        return (val.equals(Consts.STATUS.enable.getVal())? Consts.STATUS.enable.getValTxt(): Consts.STATUS.forbidden.getValTxt());
    }
}
