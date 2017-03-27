package crawler.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 直接复制浏览器中的那些请求数据来对页面进行请求，看这里是否能够请求成功
 * 
 * @author Stargazer
 * @date 2017-03-27
 */
public class DownloadOriginalPic {

	private File file;					//将下载的图片数据保存到这个文件中
	
	public DownloadOriginalPic(){
		
	}
	
	public void setFile(File file){
		this.file = file;
	}

	/**
	 * 根据原始图片的地址，将图片下载到以filename为文件名的文件中
	 * 
	 * 注意：这里请求中的Cookie等信息是我在浏览器登录P站并手动F12查看某一张原始图片是的请求信息，
	 * 			 这里可能会失效！
	 * 
	 * @param urlStr			原始图片的地址
	 * @param filename		图片下载保存到磁盘上时的文件名
	 */
	public void getPicture(String urlStr){
		
		try {
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection)url.openConnection();
			
			
			conn.addRequestProperty("Accept", "image/webp,image/*,*/*;q=0.8");
			conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
			conn.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4,ja;q=0.2");
			conn.addRequestProperty("Connection", "keep-alive");
			conn.addRequestProperty("Cookie", "p_ab_id=1; p_ab_id_2=5; PHPSESSID=22834429_dd1ee52a1c5fe902f87592004b3beb52; device_token=a5e1f2587fe13ad2ba0974ea10b8c4ae; module_orders_mypage=%5B%7B%22name%22%3A%22recommended_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22everyone_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22following_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22mypixiv_new_illusts%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22fanbox%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22featured_tags%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22contests%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22sensei_courses%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22spotlight%22%2C%22visible%22%3Atrue%7D%2C%7B%22name%22%3A%22booth_follow_items%22%2C%22visible%22%3Atrue%7D%5D; __utma=235335808.300421734.1490616508.1490616508.1490616508.1; __utmc=235335808; __utmz=235335808.1490616508.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); __utmv=235335808.|2=login%20ever=yes=1^3=plan=normal=1^5=gender=female=1^6=user_id=22834429=1^9=p_ab_id=1=1^10=p_ab_id_2=5=1^12=fanbox_subscribe_button=orange=1^13=fanbox_fixed_otodoke_naiyou=no=1^14=hide_upload_form=no=1^15=machine_translate_test=no=1; _ga=GA1.2.300421734.1490616508");
			conn.addRequestProperty("DNT", "1");
			conn.addRequestProperty("Host", "i3.pixiv.net");
			conn.addRequestProperty("Referer", "http://www.pixiv.net/member_illust.php?mode=medium&illust_id=6193441");
			conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 Safari/537.36");
			
			
			String contentType = conn.getContentType();
			System.out.println("contentType=" + contentType);
			
			
			
			FileOutputStream fileStream = new FileOutputStream(file);
			
			try(InputStream in = new BufferedInputStream(conn.getInputStream())){
				Reader reader = new InputStreamReader(in, "UTF-8");
				
				long length = conn.getContentLength();
				int tmp;
				while(in.available() >=0){
					tmp = in.read();
					fileStream.write(tmp);
					
					long fileLength = file.length();
					if(fileLength >= length){
						break;
					}
				}
			}
			
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}catch(IOException ex){
			ex.printStackTrace();
		}
		
	}
	
	public static void main(String[] args){
		DownloadOriginalPic demo = new DownloadOriginalPic();
		String picUrl = "http://i4.pixiv.net/img-original/img/2016/08/01/00/31/01/58186979_p0.jpg";
		String filename = "F:\\crawlerTest\\pixiv\\pixiv05.jpg";
		File file = new File(filename);
		demo.setFile(file);
		demo.getPicture(picUrl);
	}
}




	