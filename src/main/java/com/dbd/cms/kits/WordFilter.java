package com.dbd.cms.kits;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 思路：	创建一个FilterSet，枚举了0~65535的所有char是否是某个敏感词开头的状态
 * 			
 * 			判断是否是 敏感词开头
 * 			|			|
 * 			是			不是
 * 		获取头节点			OK--下一个字
 * 	然后逐级遍历，DFA算法
 * 
 * @author pangjs ~ 2015-4-29 下午06:30:29
 */
public class WordFilter {
	
//	private static WordNode[] nodes = new WordNode[65536];省6W个句柄的空间呗，测试发现，相比使用65536长度数组方式，过滤速度也提高了
	private static final FilterSet set = new FilterSet();
	private static final Map<Integer, WordNode> nodes = new HashMap<Integer, WordNode>(1024, 1);
	private static final String encoding = "UTF-8";
	
//	static{
//		try {
//			long a = System.nanoTime();
//			init();
//			a = System.nanoTime()-a;
//			System.out.println("加载时间 : "+a+"ns");
//			System.out.println("加载时间 : "+a/1000000+"ms");
//		} catch (Exception e) {
//			throw new RuntimeException("初始化过滤器失败");
//		}
//	}
	
	public static void init(List<String> words){
//		List<String> words;
//		BufferedReader br = null;
//		try {
//			br = new BufferedReader(new InputStreamReader(WordFilter.class.getClassLoader().getResourceAsStream("CensorWords.txt"),encoding));
//			words = new ArrayList<String>(1200);
//			for(String buf="";(buf = br.readLine())!=null;){
//				if(StrKit.isBlank(buf))
//					continue;
//				words.add(buf);
//			}
//		} catch (Exception e) {
//			throw new RuntimeException(e);
//		} finally{
//			try {
//				if(br != null)
//					br.close();
//			} catch (IOException e) {
//			}
//		}
		//获取敏感词
//		words= CacheKit.get(Consts.CACHE_NAMES.mgc.name(),"mgcAll");
		addSensitiveWord(words);
	}
	
	private static void addSensitiveWord(final List<String> words){
		char[] chs;
		int fchar;
		int lastIndex;
		WordNode fnode;
		for(String curr : words){
			chs = curr.toCharArray();
			fchar = chs[0];
			if(!set.contains(fchar)){//没有首字定义
				set.add(fchar);//首字标志位	可重复add,反正判断了，不重复了
				fnode = new WordNode(fchar, chs.length==1);
				nodes.put(fchar, fnode);
			}else{
				fnode = nodes.get(fchar);
				if(!fnode.isLast() && chs.length==1)
					fnode.setLast(true);
			}
			lastIndex = chs.length-1;
			for(int i=1; i<chs.length; i++){
				fnode = fnode.addIfNoExist(chs[i], i==lastIndex);
			}
		}
	}
	
	private static final char SIGN = '*';
	public static final String doFilter(final String src){
		char[] chs = src.toCharArray();
		int length = chs.length;
		int currc;
		int k;
		WordNode node;
		for(int i=0;i<length;i++){
			currc = chs[i];
			if(!set.contains(currc)){
				continue;
			}
//			k=i;//日	2
			node = nodes.get(currc);//日	2
			if(node == null)//其实不会发生，习惯性写上了
				continue;
			boolean couldMark = false;
			int markNum = -1;
			if(node.isLast()){//单字匹配（日）
				couldMark = true;
				markNum = 0;
			}
			//继续匹配（日你/日你妹），以长的优先
			// 你-3	妹-4		夫-5
			k=i;
			for( ; ++k<length; ){
				
				node = node.querySub(chs[k]);
				if(node==null)//没有了
					break;
				if(node.isLast()){
					couldMark = true;
					markNum = k-i;//3-2
				}
			}
			if(couldMark){
				for(k=0;k<=markNum;k++){
					chs[k+i] = SIGN;
				}
				i = i+markNum;
			}
		}
		
		return new String(chs);
	}
	
	public static void main(String[] args) {
		
		String s = "约炮，姐姐阿姨和少妇满意 ，不影响家庭，做得私聊谈，喜欢被亲吻，被口活的姐姐加我，本人。可长期相处，也可就一次高兴。单身寂寞的，经常空虚的，老公无法满足你的，就想让男人跪下给你舔，泄压满足心理的等等，只要你愿意都可以私聊我。";
		System.out.println("解析字数 : "+s.length());
		String re;
		long nano = System.nanoTime();
		re=WordFilter.doFilter(s);
		nano = (System.nanoTime()-nano);
		System.out.println("解析时间 : " + nano + "ns");
		System.out.println("解析时间 : " + nano/1000000 + "ms");
		System.out.println(re);
		System.out.println(re.length()==s.length());
	}
	
}
