package crawler.utils;

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
		System.out.println(result.size());
		for(Map.Entry entry : result.entrySet()){
			System.out.println(entry.getValue() + "==" + entry.getKey());
		}
		System.out.print("总共获取到的国际排行榜上的图片有：");
		System.out.println(result.size());
		
		return result;
	}
	
}
