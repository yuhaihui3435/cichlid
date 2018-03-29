package com.dbd.cms.kits;

import com.dbd.cms.Consts;
import com.jfinal.kit.LogKit;
import com.jfinal.plugin.ehcache.CacheKit;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yuhaihui8913 on 2017/5/27.
 */
public class StringKit {
    public static final String CHINESE_REGEX = "[\\u4e00-\\u9fa5]";


    public static String matchResult(Pattern p, String str)
    {
        StringBuilder sb = new StringBuilder();
        Matcher m = p.matcher(str);
        while (m.find())
            for (int i = 0; i <= m.groupCount(); i++)
            {
                sb.append(m.group());
            }
        return sb.toString();
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        if( !isNum.matches() ){
            return false;
        }
        return true;
    }
    public static String getUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    /**暂时未使用,使用WordFilter**/
    public static String filterMgc(String target){
        StringBuilder sb=new StringBuilder();
        List<String> mgcLib=(List<String>)CacheKit.get(Consts.CACHE_NAMES.mgc.name(),"mgcAll");
        List<String> targetLib=new ArrayList<String>();
        try {
            fenci(target,targetLib);
            for(int i=0;i<targetLib.size();i++) {
                for(int j=0;j<mgcLib.size();j++) {
                    if (targetLib.get(i).equals(mgcLib.get(j))) {
                        targetLib.set(i, "*");
                    }
                }
            }
            Iterator<String> iterator = targetLib.iterator();

            while (iterator.hasNext()) {
                sb.append(iterator.next());
            }

        } catch (IOException e) {
            LogKit.error("分词处理出现错误:"+e.getMessage());
        }
        return sb.toString();
    }

    public static void fenci(String text,List<String> list) throws IOException {

        StringReader re = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(re,true);
        String _s=null;
        Lexeme beforeWord = null;
        Lexeme currentWord = null;
        while((currentWord=ik.next())!=null){
            _s=appendSymbol(text, beforeWord, currentWord);
            list.add(_s);
            if(!_s.equals(currentWord.getLexemeText()))
                list.add(currentWord.getLexemeText());
            beforeWord = currentWord;
        }
        list.add(appendSymbol(text, beforeWord, currentWord));
    }

    public static String appendSymbol(String line, Lexeme before, Lexeme cur) {
        String res = "";
        if (before == null) {// 第一个词前边的符号
            res = cur.getLexemeText() + "";
            int start = cur.getBegin();
            if (start > 0) {
                String left =line.substring(0, start);
                res = left ;
            }
        } else if (cur == null) {// 最后一个词后边的符号
            int end = before.getEndPosition();
            if (end < line.length()) {
                String right = line.substring(before.getEndPosition());
                res = right;
            }
        } else { // 和前一个词之间的符号
            res = cur.getLexemeText() + "";
            int beforeEnd = before.getEndPosition();
            if (cur.getBegin() > beforeEnd) {
                String mid = line.substring(beforeEnd, cur.getBegin());
                res = mid ;
            }
        }
        return res;
    }

    public static String getRandomString(int length) { //length表示生成字符串的长度
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }



    public static void main(String[] args) throws IOException {
//        System.out.print(StringKit.matchResult(Pattern.compile(CHINESE_REGEX),"2017-1-9你好啊佳佳"));
        String text="约炮，姐姐阿姨和少妇满意 ，不影响家庭，做得私聊谈，喜欢被亲吻，被口活的姐姐加我，本人。可长期相处，也可就一次高兴。单身寂寞的，经常空虚的，老公无法满足你的，就想让男人跪下给你舔，泄压满足心理的等等，只要你愿意都可以私聊我。";
        //创建分词对象
        StringReader re = new StringReader(text);
        IKSegmenter ik = new IKSegmenter(re,true);
        Lexeme lex = null;
        while((lex=ik.next())!=null){
            System.out.print(lex.getLexemeText()+"|");
        }
    }


}
