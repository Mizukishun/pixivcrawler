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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.net.HttpURLConnection;

public class DownloadPageSourceCode {

	private String url;			//网页的地址，如：http://www.pixiv.net/member_illust.php?mode=medium&illust_id=61367958
	
	private String thumbnailPicUrl;		//小图的地址
	
	public DownloadPageSourceCode(String url){
		this.url = url;
	}
	
	/**
	 * 提供给外部调用，以获取网页中的小图地址
	 */
	public String getThumbnailPicUrl(){
		if(url == null){
			System.out.println("为提供网页的地址，无法获取小图地址！");
			return "Error";
		}
		String html = getHtml();
		return regHtml(html);
	}
	
	/**
	 * 获取一名P站会员的一幅图片页面的源码，以便获取小图地址
	 * 如：http://www.pixiv.net/member_illust.php?mode=medium&illust_id=59512854
	 * 请求得到相应的HTML源码，其中包含了小图的地址
	 * @return
	 */
	private String getHtml(){
		
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
			conn.connect();
			
			String contentType = conn.getContentType();
			String responseMessage = conn.getResponseMessage();
			int responseCode = conn.getResponseCode();
			String contentEncoding = conn.getContentEncoding();
			long contentLength = conn.getContentLength();
			
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
				
				System.out.println("========================HTML========================");
				System.out.println(html.toString());
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
	private String regHtml(String sourceCode){
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
	
	public static void main(String[] args) throws IOException{
		String url = "http://www.pixiv.net/member_illust.php?mode=medium&illust_id=59665229";
		
		DownloadPageSourceCode demo = new DownloadPageSourceCode(url);
		DownloadOriginalPic downloadDemo = new DownloadOriginalPic();
		String filename = "secondPhase";
		String picUrl = demo.getThumbnailPicUrl();
		downloadDemo.download(picUrl, filename);
		//demo.getHtml();
		
	}
}
