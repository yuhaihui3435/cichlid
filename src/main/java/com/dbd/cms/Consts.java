package com.dbd.cms;

import org.jsoup.safety.Whitelist;

/**
 * Created by 于海慧（125227112@qq.com） on 2016/11/30.
 */
public class Consts {

    public static final Whitelist basicWithImages=Whitelist.basicWithImages();

    public static final String USER_ACCESS_TOKEN="ccId";

    public static final String ENCRYPT_KEY="yuhaihui3435-cichlid";

    public static final int COOKIE_TIMEOUT=24*60*60*6;

    public static final int COOKIE_FOREVER=24*60*60*6*365*50;

    public static final String CURR_USER="currUser";

    public static final String CURR_USER_ROLES="currUserRoles";

    public static final String CURR_USER_RESES="currUserReses";

    public static final String T_AREA_CK="tArea";

    public static final String T_CATALOG_CK="tCatalog";

    public static final String T_TAG_CK="tTag";

    public static final String T_SUBGROUP_CK="tSubgroup";

    public static final String T_ORDERSUPPORT_CK="tOrdersupport";

    public static final String T_SPECIES_CK="tSpecies";

    public static final String T_ALL_SPECIES_CK="AllTSpecies";

    public static final String DFT_AVATAR="images/sys/avatar.jpg";

    public static final String DFT_COVER="images/sys/dft-cover.jpg";

    public static final String USER_UPLOAD_MAX_SIZE_CK="userUploadMaxSize";

    public static final String WX_OAUTH2_TOKEN="wx_oauth2_token";




    public enum YORN {
        yes(true), no(false);
        boolean val;

        private YORN(boolean val) {
            this.val = val;
        }

        public String getLabel() {
            return (val) ? "否" : "是";
        }

        public boolean isVal() {
            return val;
        }
    }

    public enum YORN_STR {
        yes("0"), no("1");
        String val;

        private YORN_STR(String val) {
            this.val = val;
        }

        public String getLabel() {
            return (val.equals("0")) ? "是" : "否";
        }

        public String getVal() {
            return val;
        }
    }

    /**
     * @param
     * @author: 于海慧  2016/12/10
     * @Description:  状态枚举
     * @return void
     * @throws
     **/
    public enum STATUS {
        enable("0"), forbidden("1");
        String val;

        private STATUS(String val) {
            this.val = val;
        }
        public String getVal(){
            return this.val;
        }
        public String getValTxt(){
            return (val.equals("0")?"正常":"禁用");
        }
    }

    public enum REQ_JSON_CODE {
        success, fail,unauthorized;
    }


    public enum CACHE_NAMES {
        paramCache,modules,article,user,userRoles,userReses,taxonomy,fArea,knowledge,knowledgebaseView,habitat,ssq,supplierView,orderinfo,orderinfoAndKbRelation,comment
        ,sx,follow,collect,mgc,notification
    }

    public enum MODULE_NAMES{
        knowledgebase,content,supplier
    }

    public enum CHECK_STATUS{
        normal("00"), waitingCheck("01"),revokeCheck("02");
        String val;

        private CHECK_STATUS(String val) {
            this.val = val;
        }
        public String getVal(){
            return this.val;
        }
        public String getValTxt(){
            if(val.equals("00")){
                return "正常";
            }else if(val.equals("01")){
                return "等待审批";
            }else if(val.equals("02")){
                return "未通过审批";
            }
            return "";
        }
    }

    /**
     *
     * 01:草稿
     * 00:正常
     */
    public enum CONTENT_FLAG{
        drfat("01"),normal("00");
        String val;
        private CONTENT_FLAG(String val) {
            this.val = val;
        }
        public String getVal(){
            return this.val;
        }
    }

    public enum LAKE_NAME{
        mlwh("00"),tgnkh("01"),wdlyh("02");
        String val;
        private LAKE_NAME(String val) {
            this.val = val;
        }
        public String getVal(){
            return this.val;
        }
        public String getValTxt(){
            if(val.equals("00")){
                return "马拉维湖";
            }else if(val.equals("01")){
                return "坦噶尼喀湖";
            }else if(val.equals("02")){
                return "维多利亚湖";
            }
            return "";
        }
    }


    public enum SOURCE{
        online("00"),unline("01");
        String val;
        private SOURCE(String val){
            this.val=val;
        }

        public String getVal() {
            return val;
        }

        public String getValTxt(){
            if(val.equals("00")){
                return "线上";
            }else if(val.equals("01")){
                return "线下";
            }
            return "";
        }
    }

}
