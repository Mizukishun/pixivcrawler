package crawler.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GetRecommend {

	public GetRecommend(){
		
	}
	
	/**
	 * 通过收藏一张美图，从而获得P站推荐的另外500张美图的id<br>
	 * 
	 * <p>注意：这个方法只是获取推荐的500张图片的id，并不是获取500张图片的地址，<br>
	 * 图片地址的获取需要另外的一个方法来根据相应的请求进行获取；<br>
	 * 获取这500张推荐美图ID的请求地址是如下的：<br>
	 * <b>https://www.pixiv.net/rpc/recommender.php?type=illust&sample_illusts=50243983&num_recommendations=500&tt=259ed8be42a041cce88449320b4c557c</b><br>
	 * 其中最后的tt参数可以不用，而sample_illusts=50243983则是起始收藏的那张图片的id
	 * 
	 * @param id 		用来作为引子的起始收藏图片的id
	 * @return
	 */
	public String[] getRecommendIdsFromBookmark(String id){
		String[] ids;
		String urlPre = "https://www.pixiv.net/rpc/recommender.php?type=illust&sample_illusts=";
		String urlSuf = "&num_recommendations=500";
		String url = urlPre + id + urlSuf;
		
		DownloadHtml downloader = new DownloadHtml();
		String html = "";
		
		try {
			html = downloader.getHtml(url);
		} catch (IOException e) {
			System.out.println("======获取收藏推荐IDs失败=======");
			e.printStackTrace();
		}
		
		ids = RegHtml.regRecommenderPageForIds(html);
		
		System.out.println("====================P站推荐的图片id有：==================");
		for(int i = 0; i < ids.length; i++){
			System.out.println(ids[i]);
		}
		
		return ids;
	}
	
	/**
	 * 根据多个图片的id来获取相应图片的小图地址<br>
	 * <p>
	 * 请求的地址如下所示：<br>
	 * <b>https://www.pixiv.net/rpc/illust_list.php?illust_ids=51176125%2C49258389%2C49908880%2C51645544%2C49810303%2C49934602%2C45629088%2C49908488%2C56884511%2C50759967%2C49831074%2C49911367%2C50936724%2C51145109%2C50215790&verbosity=&exclude_muted_illusts=1</b><br>
	 * 这里一次请求了15个图片的地址，所以在这里也一次只请求15个图片的地址
	 * 
	 * @param picIds
	 * @return
	 */
	public Map<String, String> getRecommendPicAddrs(String[] picIds){
		Map<String, String> picAddrs = new HashMap<>();
		String urlPre = "https://www.pixiv.net/rpc/illust_list.php?illust_ids=";
		String urlLast="&verbosity=&exclude_muted_illusts=1";
		
		for(int i = 0; i < picIds.length; i++){
			String urlSuf = "";
			
			//如果从i位置到最后不足15个的时候，就通过外层循环把图片的id参数追加到url后面
			if(i >= picIds.length-15){
				while(i < picIds.length){
					urlSuf += picIds[i++] + "%2C";
				}
			}else{
				//把15个元素放到url的请求参数中
				for(int j = 0; j < 15; j++){
					int tmp = i + j;
					String id = picIds[tmp];
					urlSuf += id + "%2C";
				}
				i += 15;
			}
			
			//因为上面最后都是以"&"号结尾，所以最终的url要去掉那个"&"号
			urlSuf = urlSuf.substring(0, urlSuf.length()-3);
			
			String url = urlPre + urlSuf + urlLast;
			
			DownloadHtml downloader = new DownloadHtml();
			String tagHtml = "";
			try {
				tagHtml = downloader.getHtml(url);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("========================获取的页面==========================");
			System.out.println(tagHtml);
			
			picAddrs.putAll(RegHtml.regTagHtmlForIdAddrs(tagHtml));
			
			
		}
		
		//System.out.println("====================获取到的数量为==============");
		//System.out.println(picAddrs.size());
		
		return picAddrs;
	}
	
	
	public static void main(String[] args){
		GetRecommend demo = new GetRecommend();
		
		//String id = "50243983";
		//demo.getRecommendIdsFromBookmark(id);
		
		String[] ids= {
				"51176125","49258389","49908880","51645544","49810303",
				"49934602","45629088","49908488","56884511","50759967","49831074",
				"49911367","50936724","51145109","50215790","51739759",
				"50099922","50944690","47084218","52025622","52044837","49920423"
		};
		demo.getRecommendPicAddrs(ids);
		
		
	}
}
