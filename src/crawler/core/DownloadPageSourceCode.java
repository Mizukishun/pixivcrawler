package crawler.core;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import crawler.vo.Followers;

import java.net.HttpURLConnection;

public class DownloadPageSourceCode {

	//private String url;			//网页的地址，如：http://www.pixiv.net/member_illust.php?mode=medium&illust_id=61367958
	
	private String thumbnailPicUrl;		//小图的地址
	
	private String worksPageHtml;			//成员所有作品页面的HTML源码
	
	public DownloadPageSourceCode(){
		
	}
	/*public DownloadPageSourceCode(String url){
		this.url = url;
	}*/
	
	/**
	 * 提供给外部调用，以获取网页中的小图地址
	 */
	public String getThumbnailPicUrl(String url){
		if(url == null){
			System.out.println("为提供网页的地址，无法获取小图地址！");
			return "Error";
		}
		String html = getHtml(url);
		return regHtmlForThumbnailPicUrl(html);
	}
	
	/**
	 * 获取页面的源代码，以便后续处理
	 * 
	 * @return
	 */
	private String getHtml(String url){
		
		//urlStr = "http://www.pixiv.net/member_illust.php?mode=medium&illust_id=61367958";
		
		StringBuilder html = new StringBuilder();
		
		try{
			URL htmlUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) htmlUrl.openConnection();
			
			/**
			 * 下面这里只是我在浏览器中手动登录后复制过来的，暂时没有实现程序的模拟登录，之后得修改 //TODO
			 */
			conn.addRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
			conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch");
			conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
			conn.addRequestProperty("Connection", "keep-alive");
			conn.addRequestProperty("Cookie", "p_ab_id=3; p_ab_id_2=4; PHPSESSID=22834429_a7bd4b03f1a5409251074572ec5f4b69; device_token=3ff01dea2c65aad9a03dfe01ab07f3f2; a_type=0; is_sensei_service_user=1; login_ever=yes; __utmt=1; module_orders_mypage=%5B%7B%22name%22%3A%22recommended_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22everyone_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22following_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22mypixiv_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22fanbox%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22featured_tags%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22contests%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22sensei_courses%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22spotlight%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22booth_follow_items%22%2C%22visible%22%3Atrue%7D%5D; _gat_UA-74360115-3=1; __utma=235335808.1102416087.1490788700.1490788700.1490788700.1; __utmb=235335808.7.10.1490788700; __utmc=235335808; __utmz=235335808.1490788700.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=235335808.|2=login%20ever=yes=1^3=plan=normal=1^5=gender=female=1^6=user_id=22834429=1^9=p_ab_id=3=1^10=p_ab_id_2=4=1^12=fanbox_subscribe_button=orange=1^13=fanbox_fixed_otodoke_naiyou=yes=1^14=hide_upload_form=yes=1^15=machine_translate_test=no=1; _ga=GA1.2.1102416087.1490788700");
			conn.addRequestProperty("DNT", "1");
			conn.addRequestProperty("Host", "www.pixiv.net");
			conn.addRequestProperty("Referer", "http://www.pixiv.net/member.php?id=4462245");
			conn.addRequestProperty("Upgrade-Insecure-Request", "1");
			conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
			//conn.addRequestProperty("mode", "medium");
			//conn.addRequestProperty("illust_id", "61367958");
			conn.setConnectTimeout(30000);
			conn.connect();
			
			String contentType = conn.getContentType();
			String responseMessage = conn.getResponseMessage();
			int responseCode = conn.getResponseCode();
			String contentEncoding = conn.getContentEncoding();
			long contentLength = conn.getContentLength();
			
			//conn.setConnectTimeout(3000);
			
			Object content = conn.getContent();
			
			System.out.println("ContentType : " + contentType);
			System.out.println("ResponseMessage : " + responseMessage);
			System.out.println("ResponseCode : " + responseCode);
			System.out.println("contentEncoding : " + contentEncoding);
			System.out.println("ContentLength : " + contentLength);
			System.out.println("Content:" + content);  
			
			try(InputStream in = conn.getInputStream()){
				//BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
				/**
				 * 根据前面的得到的contentEncoding知道，获取到的content是gzip格式的，所以这里采用GZIPInputStream
				 * 来对获取到的InputStream进行解密；
				 */
				GZIPInputStream gzipInput = new GZIPInputStream(in);
				
				
				byte[] tmp = new byte[128];
				int c;
				while((c=gzipInput.read(tmp, 0, 128)) != -1){
					html.append(new String(tmp, 0, c, "UTF-8"));
				}
				
				//System.out.println("========================HTML========================");
				//System.out.println(html.toString());
			}
			
			//String tmpStr = regHtml(html.toString());
			return html.toString();
			
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
		return "Error";

	}
	
	/**
	 * 对请求得到的页面源码HTML，进行正则匹配，找出小图的地址；经分析，发现小图位于如何的div中
	 * <div class="_layout-thumbnail ui-modal-trigger">
	 * <img src="http://i3.pixiv.net/c/600x600/img-master/img/2017/02/10/00/03/04/61367958_p0_master1200.jpg" alt="Colours">
	 * </div>
	 * 
	 * 所以，首先要匹配出"_layout-thumbnail ui-modal-trigger"这个，然后找出其中的img标签，
	 * 从而得到其src属性值，也即小图的地址
	 * 
	 * 
	 * @param args
	 */
	private String regHtmlForThumbnailPicUrl(String sourceCode){
		String result = "";
		
		
		//先把小图所在的DIV标签匹配出来
		String reg = "class=\"_layout-thumbnail ui-modal-trigger\"><img src=\"[a-zA-Z0-9:/\\.\\-_]*\"";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(sourceCode);
		if(matcher.find()){
			 result = matcher.group(0);
		}
		
		System.out.println("=========================小图地址DIV============================");
		System.out.println(result);
		
		//再把小图地址匹配出来
		String reg2 = "http[\\w:/\\.\\-]+";
		pattern = Pattern.compile(reg2);
		matcher = pattern.matcher(result);
		if(matcher.find()){
			thumbnailPicUrl = matcher.group(0);
		}else{
			thumbnailPicUrl = "Error";
		}
		System.out.println("小图地址：");
		System.out.println(thumbnailPicUrl);
		
		return thumbnailPicUrl;
	}
	
	/**
	 * 根据会员的id，获取会员的所有作品的小图地址
	 * 如http://www.pixiv.net/member_illust.php?id=4462245
	 * 这个页面上便是id=4462245这个P站会员的个人所有作品，在这个页面上边有这些作品的地址
	 * 如：http://i4.pixiv.net/c/150x150/img-master/img/2016/12/25/14/21/03/60547339_p0_master1200.jpg
	 * <del>如：http://www.pixiv.net/member_illust.php?mode=medium&illust_id=60680252</del>
	 * 
	 * 
	 * @param args
	 * @throws IOException
	 */
	public List<String> getWorksUrlByMemId(String id){
		String memUrlPrefix = "http://www.pixiv.net/member_illust.php?id=";
		String memUrl = memUrlPrefix + id;
		List<String> allWorksUrl = new ArrayList<>();
		
		/*if(worksPageHtml == null || worksPageHtml == ""){
			//获取该页面源码
			worksPageHtml = getHtml(memUrl);
		}*/
		//获取该页面源码
		worksPageHtml = getHtml(memUrl);
		//获取该成员作品的页数，也即该成员总共有多少页的作品，并把每页的地址放到worksPages中
		List<String> worksPages = regHtmlForWorksPages(worksPageHtml);
		//提取该成员第一页上的所有作品地址
		List<String> worksUrl = regHtmlForMemWorksUrl(worksPageHtml);
		//获取该成员的P站昵称
		String pixivName = regHtmlForAuthorName(worksPageHtml);
		
		allWorksUrl.addAll(worksUrl);
		for(String worksPage : worksPages){
			String sourceCodeHtml = getHtml(worksPage);
			worksUrl = regHtmlForMemWorksUrl(sourceCodeHtml);
			allWorksUrl.addAll(worksUrl);
		}
		int worksCounts = allWorksUrl.size();
		System.out.println("==========================================================");
		System.out.println(pixivName + "(id=" + id + ")的成员共有" + worksCounts + "幅作品！");
		System.out.println("所有作品的小图地址是：");
		for(String small : allWorksUrl){
			System.out.println(small);
		}
		
		return allWorksUrl;
	}
	/**
	 * 根据P站成员作品集地址页面的源码，提取出该成员的所有作品地址
	 * 。。。
	 * 经分析发现，在这个源码中就能获取到P站成员在该也中的所有作品的小图地址
	 * 如：data-src="http://i2.pixiv.net/c/150x150/img-master/img/2017/03/17/00/00/43/61945597_p0_master1200.jpg"
	 * 也即都在data-src这一属性中，所以只要提取出所有data-src属性的值就是所有小图的地址了
	 * 
	 * @param args
	 * @throws IOException
	 */
	private List<String> regHtmlForMemWorksUrl(String memHtml){
		List<String> firstPageUrls = new ArrayList<>();
		//System.out.println("=================该id会员作品集第一页所有作品的地址data-src=====================");
		
		String reg = "data\\-src=\"http[\\w\\.\\-/:]+\"";		//http[\\w:/\\.\\-]+
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(memHtml);
		while(matcher.find()){
			String tmp = matcher.group(0);
			//System.out.println(tmp);
			
			//再从含有data-src的字符串中提取出小图的地址
			//如：data-src="http://i1.pixiv.net/c/150x150/img-master/img/2016/06/10/00/12/30/57313892_p0_master1200.jpg"
			//提取出：http://i1.pixiv.net/c/150x150/img-master/img/2016/06/10/00/12/30/57313892_p0_master1200.jpg
			String uReg = "http[\\w\\.\\-/:]+";
			Pattern uPattern = Pattern.compile(uReg);
			Matcher uMatcher = uPattern.matcher(tmp);
			String imgUrl = "";
			if(uMatcher.find()){
				imgUrl = uMatcher.group(0);
			}
			firstPageUrls.add(imgUrl);
		}
		/*System.out.println("=======================上面对应的具体地址为========================");
		for(String url : firstPageUrls){
			System.out.println(url);
		}*/

		return firstPageUrls;
	}
	
	/**
	 * 根据成员的作品集页面的源码，找出该成员的作品总共有几页，并把每页的地址塞到List中返回;
	 * 经分析页面源码，发现页数在class="pager-container"的DIV中
	 * 
	 * @param html
	 * @return
	 */
	private List<String> regHtmlForWorksPages(String html){
		System.out.println("===============匹配出的页数DIV为===================");
		ArrayList<String> worksPagesUrl = new ArrayList<>();
		String worksPagePrefix = "http://www.pixiv.net/member_illust.php";
		
		String reg = "class=\"pager\\-container\"[\\w\\s\\?\\.\\-/=\"<>;&]+class=\"next\"";		 //[\\w/<>;\"=\\s\\?&]+class=\"next\"
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		if(matcher.find()){
			String divStr = matcher.group(0);
			
			System.out.println(divStr);
			
			//从提取的DIV字符串中再提取出具体的页数的地址
			String numReg = "\\?id=\\d+&amp;type=all&amp;p=\\d";
			Pattern numPattern = Pattern.compile(numReg);
			Matcher numMatcher = numPattern.matcher(divStr);
			System.out.println("========================该成员作品的各页地址(不包含第一页）=====================");
			while(numMatcher.find()){
				String pageUrlSuffix = numMatcher.group(0);
				String pageUrl = worksPagePrefix + pageUrlSuffix;
				System.out.println(pageUrl);
				worksPagesUrl.add(pageUrl);
			}
		}
		
		return worksPagesUrl;
	}
	
	/**
	 * 根据成员关注者页面的源码找出该成员总共关注了多少用户
	 * 该数子在<span class="count-badge">252</span>标签中
	 * 
	 * @param html
	 * @return
	 */
	private String regFollowersPageForFollowersCounts(String html){
		String counts = "";
		
		String reg = "class=\"count\\-badge\">\\d+";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		if(matcher.find()){
			String tmp = matcher.group(0);
			reg = "\\d+";
			pattern = Pattern.compile(reg);
			matcher = pattern.matcher(tmp);
			if(matcher.find()){
				counts = matcher.group(0);
			}
		}
		System.out.println("===================================================");
		System.out.println("该成员总共关注了 " + counts + " 名其它用户");
		
		return counts;
	}
	
	/**
	 * 获取该页面中该成员所关注的所有的用户的包含id链接及其用户名的li标签部分
	 * <li><div class="usericon"><a href="member.php?id=465133" class="ui-profile-popup" data-user_id="465133" data-user_name="天三月"
	 * 标签所包围，其中及包括该关注用户的ID ,还包括该用户的名称alt
	 * 
	 * 
	 * 得到的结果是List,每条记录是如下
	 * <li><div class="usericon"><a href="member.php?id=484261" class="ui-profile-popup" data-user_id="484261" data-user_name="irua"
	 * <li><div class="usericon"><a href="member.php?id=36" class="ui-profile-popup" data-user_id="36" data-user_name="虫麻呂"
	 * 
	 * @param html
	 * @return  
	 */
	private List<String> regFollowersPageForFollowersLiTag(String html){
		List<String> followersLiTag = new ArrayList<>();
		
		String tmp;
		//下面这个正则表达式中，如何匹配多语种（日语、中文、英语甚至包括数字）的名称是个问题
		//这里暂时用 [\\S&&[^\"]]+ 来匹配一个或多个非空白字符，并排除掉引号，很有可能会把后面众多的其它无关字符都匹配出来
		//竟然还有  桝石きのと◆コミ１【て35a】   这样的名字。。。
		String reg = "<li><div class=\"usericon\"><a href=\"member\\.php\\?id=\\d+\" class=\"ui\\-profile\\-popup\" data\\-user_id=\"\\d+\" data\\-user_name=\"[\\S\\s&&[^\"]]+\"";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		//System.out.println("========================关注者<li>标签===================");
		while(matcher.find()){
			tmp = matcher.group(0);
			//System.out.println(tmp);
			followersLiTag.add(tmp);
			/*
			//找出包含用户ID的链接，也即匹配member.php?id=465133
			String tmpReg = "member\\.php\\?id=\\d+";
			Pattern tmpPattern = Pattern.compile(tmpReg);
			Matcher tmpMatcher = tmpPattern.matcher(tmp);
			if(tmpMatcher.find()){
				followersLinkSuffix.add(tmpMatcher.group(0));
			}
			
			//找出相应用户的名称，也即匹配出data-user_name="天三月"
			tmpReg = "data\\-user_name=\"[\\S\\s&&[^\"]]+\"";
			tmpPattern = Pattern.compile(tmpReg);
			tmpMatcher = tmpPattern.matcher(tmp);
			if(tmpMatcher.find()){
				followersName.add(tmpMatcher.group(0));
			}
			*/
		}
		
		/*System.out.println("======================关注用户的链接后缀======================");
		for(String Link : followersLinkSuffix){
			System.out.println(Link);
		}
		System.out.println("======================关注用户的名称======================");
		for(String name : followersName){
			System.out.println(name);
		}
		*/
		return followersLiTag;
	}
	
	/**
	 * 从<li><div class="usericon"><a href="member.php?id=36" class="ui-profile-popup" data-user_id="36" data-user_name="虫麻呂"
	 * 这样的先匹配出
	 * data-user_id="484261"
	 * 在匹配出484261，即是关注用户的ID
	 * @param html
	 * @return
	 */
	private List<String> regFollowersPageForFollowersId(String html){
		List<String> followersId = new ArrayList<>();
		
		//先从每一个li标签中匹配出data-user_id="36"
		String reg = "data\\-user_id=\"\\d+\"";
		Pattern pattern = Pattern.compile(reg);
		
		List<String> liTagList = regFollowersPageForFollowersLiTag(html);
		for(String tag : liTagList){
			//每一个tag都是诸如
			//<li><div class="usericon"><a href="member.php?id=36" class="ui-profile-popup" data-user_id="36" data-user_name="虫麻呂"
			//的标签
			Matcher matcher = pattern.matcher(tag);
			
			if(matcher.find()){
				String tmp = matcher.group(0);
				//从data-user_id="36"匹配出36，这个就是关注用户的ID
				String tmpReg = "\\d+";
				Pattern tmpPattern = Pattern.compile(tmpReg);
				Matcher tmpMatcher = tmpPattern.matcher(tmp);
				if(tmpMatcher.find()){
					followersId.add(tmpMatcher.group(0));
				}
			}
			
		}

		/*System.out.println("======================关注用户的ID=======================");
		for(String id : followersId){
			System.out.println(id);
		}*/
		
		
		return followersId;
	}
	private Map<String, String> regFollowersPageForFollowersName(String html){
		Map<String, String> followersName = new HashMap<>();
		
		//先从每一个li标签中匹配出data-user_name="虫麻呂"，注意用户名的字符很杂，还有可能包含空格，特殊字符等等，
		//但其中不会有的就是引号“，所以可以在其中用排除法来进行正则匹配
		String reg = "data\\-user_name=\"[\\S\\s&&[^\"]]+\"";
		Pattern pattern = Pattern.compile(reg);
		
		//先从每一个li标签中匹配出data-user_id="36"
		String idreg = "data\\-user_id=\"\\d+\"";
		Pattern idpattern = Pattern.compile(idreg);
		
		List<String> liTagList = regFollowersPageForFollowersLiTag(html);
		for(String tag : liTagList){
			//每一个tag都是诸如
			//<li><div class="usericon"><a href="member.php?id=36" class="ui-profile-popup" data-user_id="36" data-user_name="虫麻呂"
			//的标签
			Matcher matcher = pattern.matcher(tag);
			
			Matcher idmatcher = idpattern.matcher(tag);
			
			if(matcher.find() && idmatcher.find()){
				String tmp = matcher.group(0);
				//从data-user_name="虫麻呂"找出出 虫麻呂 ，这个就是关注用户的名称
				//因为tmp的格式是固定的data-user_name="用户名"，所以可以直接用字符串的方法进行切割
				String name = tmp.substring(16, tmp.length()-1);
				
				String followersId = idmatcher.group(0);
				followersId = followersId.substring(14, followersId.length()-1);
				
				followersName.put(followersId, name);
			}
			
		}

		/*System.out.println("======================关注用户的名称=======================");
		for(String name : followersName){
			System.out.println(name);
		}	*/	
		
		
		return followersName;
	}
	private Map<String, String> regFollowersPageForFollowersLink(String html){
		Map<String, String> followersLink = new HashMap<>();
		
		
		//先从每一个li标签中匹配出member.php?id=36
		String reg = "member\\.php\\?id=\\d+";
		Pattern pattern = Pattern.compile(reg);
		
		//先从每一个li标签中匹配出data-user_id="36"
		String idreg = "data\\-user_id=\"\\d+\"";
		Pattern idpattern = Pattern.compile(idreg);
		
		List<String> liTagList = regFollowersPageForFollowersLiTag(html);
		for(String tag : liTagList){
			//每一个tag都是诸如
			//<li><div class="usericon"><a href="member.php?id=36" class="ui-profile-popup" data-user_id="36" data-user_name="虫麻呂"
			//的标签
			Matcher matcher = pattern.matcher(tag);
			
			Matcher idmatcher = idpattern.matcher(tag);
			
			if(matcher.find()&&idmatcher.find()){
				String suffix = matcher.group(0);
				String link = "http://www.pixiv.net/" + suffix;
				
				String followersId = idmatcher.group(0);
				followersId = followersId.substring(14, followersId.length()-1);
				
				
				followersLink.put(followersId, link);
				
			}
			
		}

		/*System.out.println("======================关注用户的链接地址=======================");
		for(String link : followersLink){
			System.out.println(link);
		}*/
		return followersLink;
	}
	
	/**
	 * 从关注者第一个页面获取其它页面的链接，以便后面获取所有的关注者用户信息
	 * 其中，其它页面的链接在如下的标签中
	 * <a href="bookmark.php?type=user&amp;id=27517&amp;rest=show&amp;p=2">2</a>
	 * @param html
	 * @return
	 */
	private List<String> regFollowersPageForPagesLink(String html){
		List<String> links = new ArrayList<>();
		
		String reg = "bookmark\\.php\\?[\\w=;&]+\">\\d</a>";
		
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		System.out.println("====================该成员关注的用户的其它页面的链接=======================");
		while(matcher.find()){
			//获取到 bookmark.php?type=user&amp;id=27517&amp;rest=show&amp;p=6">
			String tmp = matcher.group(0);
			//System.out.println(tmp);
			
			String prefix = "http://www.pixiv.net/";
			tmp = tmp.replaceAll("&amp;", "&");
			tmp = tmp.substring(0, tmp.length()-7);
			String link = prefix + tmp;
			
			links.add(link);
		}
		
		for(String l : links){
			System.out.println(l);
		}
		return links;
	}
	
	/**
	 * 根据会员Id，得到其所关注的所有用户id
	 * 会员所关注的用户的页面在诸如
	 * http://www.pixiv.net/bookmark.php?type=user&id=27517
	 * http://www.pixiv.net/bookmark.php?type=user&id=27517&rest=show&p=2
	 * 这些页面中，分为了不同页
	 * 
	 * 
	 * @param id
	 * @return
	 */
	public Followers getFollowersById(String id){
		Followers followers = new Followers(id);
		
		List<String> followersID = new ArrayList<>();
		Map<String, String> followersName = new HashMap<>();
		Map<String, String> followersLink = new HashMap<>();
		Map<String, String> followersWorksLink = new HashMap<>();
		Map<String, String> followersFavoriteLink = new HashMap<>();
		
		//所关注的用户第一页
		String followersUrlPrefix = "http://www.pixiv.net/bookmark.php?type=user&id=";
		String followersUrl = followersUrlPrefix + id;
		
		//获取第一页的源代码，从中找出第一页所有关注的用户的id，以及是否有多页的关注用户
		String firstPage = getHtml(followersUrl);
		
		//获取关注的用户数
		String followersCounts = regFollowersPageForFollowersCounts(firstPage);
		
		//获取其它页的链接
		List<String> moreLinks =  regFollowersPageForPagesLink(firstPage);
		List<String> pagesLinks = new ArrayList<>();
		//该成员所关注的用户共有多少页
		pagesLinks.add(followersUrl);
		pagesLinks.addAll(moreLinks);
		
		for(String pageUrl : pagesLinks){
			String html = getHtml(pageUrl);
			
			//获取该页中所列出来的关注的用户的id
			followersID.addAll(regFollowersPageForFollowersId(html));
			
			//获取该页中所列出来的关注的用户名称
			followersName.putAll(regFollowersPageForFollowersName(html));
			
			//获取改业中所列出来的关注用户的链接地址
			followersLink.putAll(regFollowersPageForFollowersLink(html));
		}
		/*
		//获取该页中所列出来的关注的用户的id
		List<String> firstPageFollowersID = regFollowersPageForFollowersId(firstPage);
		
		//获取该页中所列出来的关注的用户名称
		List<String> firstPageFollowersName = regFollowersPageForFollowersName(firstPage);
		
		//获取改业中所列出来的关注用户的链接地址
		List<String> firstPageFollowersLink = regFollowersPageForFollowersLink(firstPage);
		*/
		System.out.println("==============================所有关注用户的ID======================");
		for(String tmp : followersID){
			System.out.println(tmp);
		}
		System.out.println("==============================所有关注用户的名称======================");
		for(Map.Entry tmp : followersName.entrySet()){
			System.out.println(tmp.getKey() + "==" + tmp.getValue());
		}
		System.out.println("==============================所有关注用户的链接地址====================");
		for(Map.Entry tmp : followersLink.entrySet()){
			System.out.println(tmp.getKey() + "==" + tmp.getValue());
		}
		
		/*
		 * 因为用户的个人资料地址、作品地址、收藏地址很相似，所以直接在个人资料地址基础上添加用户的
		 * 作品地址和收藏地址
		 * 个人资料地址：http://www.pixiv.net/member.php?id=27517
		 * 作品地址：http://www.pixiv.net/member_illust.php?id=27517
		 * 收藏地址：http://www.pixiv.net/bookmark.php?id=27517
		 */
		for(Map.Entry entry : followersLink.entrySet()){
			String followerUserId = (String) entry.getKey();
			String followerLink = (String)entry.getValue();
			String followerWorksLink = followerLink.replaceAll("member", "member_illust");
			String followerFavoriteLink = followerLink.replaceAll("member", "bookmark");
			followersWorksLink.put(followerUserId, followerWorksLink);
			followersFavoriteLink.put(followerUserId, followerFavoriteLink);
		}
		
		if(followersCounts != "" && followersCounts != null){
			
			followers.setFollowers_num(Integer.parseInt(followersCounts));
		}
		followers.setFollowers_id(followersID);
		followers.setFollowers_link(followersLink);
		followers.setFollowers_works_link(followersWorksLink);
		followers.setFollowers_favorite_link(followersFavoriteLink);

		return followers;
	}
	
	/**
	 * 从成员的所有作品页面中获取该成员在P站的昵称
	 * 
	 * @param worksPageSourceCode 该成员所有作品页面的HTML字符串
	 * @return
	 */
	private String regHtmlForAuthorName(String html){
		String name = "";
		
		
		String reg = "class=\"user\">[\\s\\S&&[^</h1>]]+</h1>";
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		if(matcher.find()){
			String tmp = matcher.group(0);
			int index = tmp.indexOf(">");
			name = tmp.substring(index+1, tmp.length()-5);
		}
		
		return name;
	}
	
	public String getAuthorNameById(String id){
		String name = "";
		String memUrlPrefix = "http://www.pixiv.net/member_illust.php?id=";
		String memUrl = memUrlPrefix + id;
		
		/*if(worksPageHtml == null || worksPageHtml == ""){
			//先获取该id成员所有作品页面的html源码
			worksPageHtml = getHtml(memUrl);
		}*/
		//先获取该id成员所有作品页面的html源码
		worksPageHtml = getHtml(memUrl);
		
		//在获取该id所对应的P站昵称
		name = regHtmlForAuthorName(worksPageHtml);
		
		return name;
	}
	
	/*********************************************************收藏图片页面**************************************************************/
	
	/**
	 * 获取收藏图片页面中总共有多少件图片
	 * 图片的总数量在如下的标签中
	 * <span class="count-badge">516件</span>
	 * 
	 * @param html
	 * @return
	 */
	private String regFavoritePageForPicCounts(String html){
		String pageCounts = "";
		
		String reg = "class=\"count\\-badge\">\\d+";
		
		Pattern pattern = Pattern.compile(reg);
		Matcher matcher = pattern.matcher(html);
		if(matcher.find()){
			String tmp = matcher.group(0);
			pageCounts = tmp.substring(tmp.lastIndexOf(">")+1, tmp.length());
		}
		
		return pageCounts;
	}
	
	/**
	 * 获取一个收藏页面中的所有图片的链接地址及其对应的id
	 * 
	 * 其实一个页面20张图片都是在如下的标签中的
	 * 
	 * class="_layout-thumbnail"><img src="http://source.pixiv.net/www/images/common/transparent.gif"
	 * class="ui-scroll-view"data-filter="thumbnail-filter lazy-image"
	 * data-src="https://i.pximg.net/c/150x150/img-master/img/2016/02/09/20/24/38/55184464_p0_master1200.jpg"
	 * data-type="illust"data-id="55184464"
	 * data-tags="女の子 オリ???ナル 制服 セーラー服 バレンタイン 百合 黒タイツ 差し出す 百合1000users入り"
	 * data-user-id="1765103">
	 * 
	 * 注意：
	 * 1、一个成员有可能收藏同一名用户的多张图片，所以绝对不能用用户id来作为Map的键，而应该用图片的地址来
	 * 		作为Map的键，而对应的用户id则最为值
	 * 
	 * @param favoritePage
	 * @return 
	 */
	private Map<String, String> regFavoritePageForAllPicLink(String favoritePage){
		Map<String, String> allPicLinkFromSinglePage = new HashMap<>();
		/**
		 * data-src="https://i.pximg.net/c/150x150/img-master/img/2016/02/09/20/24/38/55184464_p0_master1200.jpg"data-type="illust"data-id="55184464"data-tags="女の子 オリ???ナル 制服 セーラー服 バレンタイン 百合 黒タイツ 差し出す 百合1000users入り"data-user-id="1765103"
		 */
		//首先提炼出该上面所示的标签内容
		//String firstReg = "data\\-src=\"http[\\w\\-\\./:]+\"data\\-type=\"[\\w]+\" data\\-id=\"[\\w]+\"data\\-tags=\"[\\s\\S&&[^\"]+\"data\\-user\\-id=\"[\\d]+\"";
		
		String firstReg = "data\\-src=\"http[\\w\\-\\./:]+\"data\\-type=\"[\\w]+\"data\\-id=\"[\\w]+\"data\\-tags=\"[\\s\\S&&[^\"]]+\"data\\-user\\-id=\"[\\d]+\"";
		Pattern firstPattern = Pattern.compile(firstReg);
		
		Matcher firstMatcher = firstPattern.matcher(favoritePage);
		while(firstMatcher.find()){
			String divStr = firstMatcher.group(0);
			
			String link = "";			//该图片的链接
			String id = "";				//该图片的主人id
			
			//获取data-src部分，从中提取出该图片的链接地址
			link = divStr.substring(10, divStr.indexOf("data-type")-1);
			//获取data-user-id部分，从中提取出该图片的主人id
			id = divStr.substring(divStr.lastIndexOf("=")+2, divStr.length()-1);
			allPicLinkFromSinglePage.put(link, id);
		}
		
		for(Map.Entry tmp : allPicLinkFromSinglePage.entrySet()){
			System.out.println("id=" + tmp.getValue() + "==" + tmp.getKey());
		}
		System.out.println("===========================================================");
		
		return allPicLinkFromSinglePage;
	}
	
	/**
	 * 根据成员id查询出该成员所收藏的所有图片的地址
	 * 
	 * 注意，返回的Map中，键是小图的链接地址，而值则是对应用户的id
	 * 
	 * @param id		成员的id
	 * @return 			返回的小图的地址，它在DownloadOriginalPic.java类里会自动转换成相应的原始大图地址
	 */
	public Map<String, String> getFavoriteWorksUrlByMemId(String id){
		//成员收藏图片的页面地址
		String prefix_url = "http://www.pixiv.net/bookmark.php?id=";
		String url = prefix_url + id;
		
		//根据该地址获取收藏图片页面的源码
		String favoritePageHtml = getHtml(url);
		//System.out.println("==========================收藏图片页面源码======================");
		//System.out.println(favoritePageHtml);
		
		//获取该id的P站昵称
		
		//分析源码页面，获取时收藏图片的数量
		String count_badge = regFavoritePageForPicCounts(favoritePageHtml);
		System.out.println("==========================收藏的图片数量======================");
		System.out.println("id=" + id +"的成员总共收藏了" + count_badge + "张图片");
		
		/**
		 * 获取收藏图片的页面总共有多少页。
		 * 这里要注意，收藏页面中，每一页显示20张图片，每次最多只能显示9页，如果收藏超过了180张图片
		 * 那第9页之后的页数在页面源码是无法得出的；所以这里采用直接用总的收藏图片树，除以每页显示的图片数，
		 * 这样就获取到了收藏图片的总的页数，从而拼凑得出每页的相应链接地址
		 */
		int picCounts = Integer.valueOf(count_badge);
		int pageCounts;
		if(picCounts > 20){
			pageCounts = picCounts / 20 +1;
		}else{
			pageCounts = 1;
		}
		
		//收藏图片页面的每页链接都是如下格式的
		//http://www.pixiv.net/bookmark.php?id=512849&rest=show&p=2
		List<String> pageUrls = new ArrayList<>();
		for(int i = 1; i <= pageCounts; i++){
			pageUrls.add("http://www.pixiv.net/bookmark.php?id=" + id + "&rest=show&p=" + i);
		}
		
		
		//从第一页开始，到最后一页，分别获取对应的的源码,并分析获取其中每页的所有图片链接地址
		Map<String, String> allPicUrls = new HashMap<>();
		for(String pageUrl : pageUrls){
			String page = getHtml(pageUrl);
			//System.out.println("这个页面内的图片有这些：" + pageUrl);
			//获取该页面中所有图片的链接地址以及图片对应的id
			allPicUrls.putAll(regFavoritePageForAllPicLink(page));
		}
		
		
		System.out.println("===========================所有收藏的图片地址为==================");
		for(Map.Entry entry : allPicUrls.entrySet()){
			System.out.println(entry.getValue() + "==" + entry.getKey());
		}
		
		System.out.println("============================收藏统计==========================");
		System.out.println("共有" + pageUrls.size() + "页收藏");
		System.out.println("该用户共收藏了" + picCounts + "张图片");
		if(allPicUrls.size() != picCounts){
			System.out.println("但获取到的图片地址只有" + allPicUrls.size() + "张图片");
		}
		
		
		return allPicUrls;

		
	}
	
	public static void main(String[] args) throws IOException{
		//String url = "http://www.pixiv.net/member_illust.php?mode=medium&illust_id=59665229";
		//String id = "4462245";		//幻像黒兎
		//String id="27517";				//藤原
		//String id = "490219";			//Hiten
		//String id	= "152142";			//すいひ
		String id = "512849";			//刃天
		//String id = "1584611";			//卑しい人间
		//String id = "548883";			//タロ
		//String id = "4754550";
		DownloadPageSourceCode demo = new DownloadPageSourceCode();
		demo.getFavoriteWorksUrlByMemId(id);
		
		
		
		/*long start = System.currentTimeMillis();
		Date startTime = new Date(start);
		DownloadPageSourceCode demo = new DownloadPageSourceCode();
		
		Followers followers = demo.getFollowersById(id);
		List<String> AllFollowersId = followers.getFollowers_id();
		for(String user_id : AllFollowersId){
			
			//根据id获取该成员的所有作品
			List<String> urls = demo.getWorksUrlByMemId(user_id);
			DownloadOriginalPic downloadDemo = new DownloadOriginalPic();
			String filename = "id_" +user_id + "N";
			for(String picUrl : urls){
				try{
					
					downloadDemo.download(picUrl, filename);
				}catch(IOException ex){
					System.out.println("==========================出错了==========================");
					System.out.println(ex.getMessage());
					ex.printStackTrace();
					continue;
				}
			}
			
		}*/
		
		/*
		//根据id获取该成员的所有作品
		List<String> urls = demo.getWorksUrlByMemId(id);
		DownloadOriginalPic downloadDemo = new DownloadOriginalPic();
		String filename = "id_" + id + "N";
		for(String picUrl : urls){
			try{
				
				downloadDemo.download(picUrl, filename);
			}catch(IOException ex){
				System.out.println("==========================出错了==========================");
				System.out.println(ex.getMessage());
				ex.printStackTrace();
				continue;
			}
		}
		*/
		/*String picUrl = demo.getThumbnailPicUrl(url);
		DownloadOriginalPic downloadDemo = new DownloadOriginalPic();
		String filename = "secondPhase";
		downloadDemo.download(picUrl, filename);*/
		//demo.getHtml();
		
		
		/*long end = System.currentTimeMillis();
		Date endTime = new Date(end);
		long dif = end-start;
		//String difTime = en
		System.out.println("开始时间：" + startTime.toString());
		System.out.println("结束时间：" + endTime.toString());*/
		//System.out.println("所用时间：" + difTime.toString());
	}
}
