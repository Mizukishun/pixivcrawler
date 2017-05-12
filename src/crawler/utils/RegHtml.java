package crawler.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 对HTML源码进行正则匹配，获取其中的图片地址、作者信息等
 * 
 * @author Stargazer
 * @date 2017-04-14
 */
public class RegHtml {

	public RegHtml(){
		
	}
	
	/**
	 * 对国际排行榜页面源码进行正则匹配，获取其中的前100名的作品；<br>
	 * 注意：获取的结果中，键为图片的地址，而键值则为该图片的作者ID<br>
	 * 图片的小图 地址再如下的标签中<br>
	 * data-src="https://i.pximg.net/c/150x150/img-master/img/2017/04/08/00/02/29/62303337_p0_master1200.jpg"data-type="illust"data-id="62303337"data-tags="原创 オリジナル クラゲ オリジナル5000users入り"data-user-id="11246082">
	 * 
	 * 
	 * 
	 * @param html
	 * @return
	 */
	public static Map<String, String> regRankingPageForPicAddr(String html){
		Map<String, String> result = new HashMap<>();
		
		//注意下面这个被注释掉的尝试匹配在data-tag中包含的中文和日文，但实际上因为data-tag会包含:()这样的符号，所以其范围很广，
		//下面的这个只能匹配部分没有其它特殊符号的，所以使用[\\S\\s&&[^>]]+这样的正则表达式，匹配除了>的符号之外的所有字符，
		//而>作为标签结束符，用来界定匹配的结尾
		//String reg = "data\\-src=\"http[\\w\\-\\./:]+\"data\\-type=\"[\\w\\-\"=[\\u0800-\\u9fa5]\\s]+>";
		String reg = "data\\-src=\"http[\\w\\-\\./:]+\"data\\-type=\"[\\S\\s&&[^>]]+>";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		//System.out.println("=============正则匹配===========");
		while(matcher.find()){
			//获取到如下的字符串
			//data-src="https://i.pximg.net/c/150x150/img-master/img/2017/04/08/00/02/29/62303337_p0_master1200.jpg"data-type="illust"data-id="62303337"data-tags="原创 オリジナル クラゲ オリジナル5000users入り"data-user-id="11246082">
			//或者是
			//data-src="https://i.pximg.net/c/150x150/img-master/img/2017/04/08/19/25/22/62315243_p0_master1200.jpg"data-type="illust"data-id="62315243"data-tags="ラブライブ! 東條希 絢瀬絵里 南ことり 西木野真姫 ??坂穂乃果 矢澤にこ 星空凛 小泉花陽 園田海未"data-user-id="11387642">
			String tmp = matcher.group(0);
			//System.out.println(tmp);
			
			//对上面的字符串进行字符提取，直接使用String的方法
			String dataSrc = tmp.substring(10, tmp.indexOf("data-type")-1);
			String userId = tmp.substring(tmp.lastIndexOf("=")+2, tmp.length()-2);
			
			result.put(dataSrc, userId);
		}
		
		System.out.println("===========国际排行榜获取到的小图地址有===========");
		for(Map.Entry entry : result.entrySet()){
			System.out.println(entry.getValue() + "==" + entry.getKey());
		}
		System.out.print("总共获取到的国际排行榜上的图片有：");
		System.out.println(result.size());
		
		return result;
	}
	
	
	/**
	 * 请求P站获取推荐的500张图片的id，获取的源码格式如下：<br>
	 * {"recommendations":[51176125,49258389,49908880,51645544,...,47881258,56457301,50007025]}<br>
	 * 其中图片id有500个因为这里的是json格式，所以可以不用正则表达式，直接字符串匹配（或者用json的相关工具类）<br>
	 * 
	 * @param html
	 * @return
	 */
	public static String[] regRecommenderPageForIds(String html) throws IndexOutOfBoundsException {
		String[] ids;
		String idsStr = html.substring(html.indexOf("[") + 1, html.lastIndexOf("]"));
		ids = idsStr.split(",");
		return ids;
	}
	
	
	/**
	 * 在收藏推荐页面，使用多个图片id作为请求参数，获取该多个图片相应的小图地址<br>
	 * <p>
	 * 请求得到的源码格式如下：<br>
	 * "url":"https:\/\/i.pximg.net\/c\/150x150\/img-master\/img\/2015\/08\/02\/14\/00\/26\/51739759_p0_master1200.jpg","user_name":"\u54b2\u826f\u3086\u304d","illust_id":"51739759","illust_title":"Magician girl","illust_user_id":"1661253","illust_restrict"<br>
	 * 所以可以先用正则表达式匹配出上面的字符串，再通过字符串的操作来获取图片的url
	 * 
	 * @param html
	 * @return
	 */
	public static Map<String, String> regTagHtmlForIdAddrs(String html){
		Map<String, String> idAddrs = new HashMap<>();
		
		//System.out.println("=======================正则匹配出来的地址有===============");
		String reg = "url\":\"http[\\w\\W&&[^\"]]+\",\"user_name\":\"[\\w\\W&&[^\"]]+\",\"illust_id\":\"[\\d]+\",\"illust_title\":\"[\\w\\W&&[^\"]]+\",\"illust_user_id\":\"[\\d]+\"";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		while(matcher.find()){
			//得到如下所示的字符串
			//url":"https:\/\/i.pximg.net\/c\/150x150\/img-master\/img\/2015\/04\/19\/16\/29\/41\/49920423_p0_master1200.jpg","user_name":"\u9190\u5473\u5c51","illust_id":"49920423","illust_title":"\u771f\u59eb\u3061\u3083\u3093\u30aa\u30e1\u30b9","illust_user_id":"1815189"
			String uTn = matcher.group(0);
			String url = uTn.substring(uTn.indexOf("http"), uTn.indexOf("1200")+8);
			url = url.replaceAll("\\\\", "");		//替换掉url中的斜杠\
			String authorId = uTn.substring(uTn.lastIndexOf(":")+2, uTn.length()-1);
			
			//System.out.println(uTn + "\n authorId=" + authorId + "\n url=" + url);
			idAddrs.put(url, authorId);
		}
		//System.out.println("==================这一次匹配的数量有===================");
		//System.out.println(idAddrs.size());
		
		return idAddrs;
	}
	
	/**
	 * 根据图片的小图地址获取该图片的id<br>
	 * 图片的地址有如下格式：<br>
	 * http://i3.pixiv.net/c/600x600/img-master/img/2017/01/29/16/20/56/61172838_p0_master1200.jpg<br>
	 * http://i3.pixiv.net/img-original/img/2017/01/29/16/20/56/61172838_p0.jpg
	 * 
	 * 
	 * @param picUrl 图片的小图 地址
	 * @return 图片对应的ID
	 */
	public static String regPicUrlForPicId(String picUrl){
		String picId = "";
		//因为格式固定且相对简单，所以直接使用String的函数进行图片ID的提取
		picId = picUrl.substring(picUrl.lastIndexOf("/")+1, picUrl.indexOf("_p"));
		/*String reg  = "";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(picUrl);
		if(matcher.find()){
			picId = matcher.group(0);
		}*/
		
		return picId;
	}
	
}
