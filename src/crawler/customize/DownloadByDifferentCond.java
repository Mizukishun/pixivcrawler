package crawler.customize;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import crawler.core.DownloadOriginalPic;
import crawler.core.DownloadPageSourceCode;
import crawler.utils.DownloadHtml;
import crawler.utils.GetRecommend;
import crawler.utils.RegHtml;
import crawler.utils.TimeUtil;
import crawler.vo.Followers;

public class DownloadByDifferentCond {
	
	/**
	 * 只下载该id成员的所有作品
	 * 
	 * @param id
	 * @throws Exception 
	 */
	public void downloadAllWorksByMemId(String id) throws Exception{
		Logger logger = LogManager.getLogger("mylog");
		
		
		//long startTime = System.currentTimeMillis();
		DownloadPageSourceCode demo = new DownloadPageSourceCode();
		
		String authorName = demo.getAuthorNameById(id);
		System.out.println("该成员的P站昵称为：" + authorName);
		logger.info("该成员的P站昵称为：" + authorName);
		//替换掉名称中不能用于建立文件名的特殊符号
		//authorName = authorName.re
		
		List<String> allUrls = demo.getWorksUrlByMemId(id);
		
		DownloadOriginalPic download = new DownloadOriginalPic();
		
		String filename =  id + "Works/" + id + "N";
		logger.info("未加图片id之前的保存文件名为：" + filename);
		for(String url : allUrls){
			logger.info("开始下载图片：" + url);
			try{
				//System.out.println("**************************************************进度显示***************************************");
				//System.out.println("图片地址：" + url);
				download.download(url, filename);
				logger.info("=============图片下载成功============");
				logger.info("=============开始图片下载============");
			}catch(IndexOutOfBoundsException e){
				//System.out.println("=========================出错了============================");
				//System.out.println("出错的图片为：" + url);
				logger.error("===========================图片下载出错=========================");
				logger.error("下载出错的图片地址为：" + url);
				logger.error(e.getMessage());
				e.printStackTrace();
				continue;
			}catch(IOException ex){
				System.out.println("=========================出错了============================");
				System.out.println("出错的图片为：" + url);
				logger.error("===========================图片下载出错=========================");
				logger.error("下载出错的图片地址为：" + url);
				logger.error(ex.getMessage());
				ex.printStackTrace();
				continue;
			}
		}
		//TimeUtil.printTime(startTime);
	}
	
	/**
	 * 下载该成员所关注的所有的用户的所有作品，不包含该成员自己的作品
	 * @throws Exception 
	 */
	public void downloadAllFollowersWorksByMemId(String id) throws Exception{
		//long startTime = System.currentTimeMillis();
		DownloadPageSourceCode demo = new DownloadPageSourceCode();
		DownloadOriginalPic download = new DownloadOriginalPic();
		
		Followers followers = demo.getFollowersById(id);
		
		/**
		 * 该成员所关注的所有的用户的所有作品都放在id+“followers"的文件夹下，图片命名统一用各用户各自的id+”N"
		 */
		String folder = id + "Followers/";
		
		List<String> followersId = followers.getFollowers_id();
		
		//followers中本就不包括成员自己的id
		/*if(followersId.contains(id)){
			int self_id_index = followersId.indexOf(id);
			followersId.remove(self_id_index);
		}*/
		
		for(String followerId : followersId){
			List<String> followerAllUrl = demo.getWorksUrlByMemId(followerId);
			String filename = folder + followerId + "N";
			for(String url : followerAllUrl){
				try{
					download.download(url, filename);
				}catch(IOException e){
					System.out.println("=============================出错了===================");
					//System.out.println(e.getMessage());
					e.printStackTrace();
					continue;
				}
				
			}
			
		}
		
		//TimeUtil.printTime(startTime);
		
	}
	
	/**
	 * 下载该成员所收藏的所有图片
	 * 图片存放在以该成员id+"favorites"命名的文件夹中，图片以用户id+"N"统一命名
	 * @param id
	 * @throws Exception 
	 */
	public void downloadAllFavoritePicByMemId(String id) throws Exception{
		long startTime = System.currentTimeMillis();
		DownloadPageSourceCode dpsc = new DownloadPageSourceCode();
		DownloadOriginalPic download = new DownloadOriginalPic();
		
		String folder = id + "Favorites/";
		
		Map<String, String> linkAndId = dpsc.getFavoriteWorksUrlByMemId(id);
		for(Map.Entry entry : linkAndId.entrySet()){
			String userId = (String) entry.getValue();
			String filename = folder + userId + "N";
			String url = (String)entry.getKey();
			try{
				download.download(url, filename);
			}catch(IOException ex){
				System.out.println("=========================出错了==========================");
				System.out.println("出错的图片地址为：" + url);
				ex.printStackTrace();
				continue;
			}
		}
		
		//TimeUtil.printTime(startTime);
	}
	
	/**
	 * 把今日国际排行榜上的100张图片下载下来<br>
	 * https://www.pixiv.net/ranking_area.php?type=detail&no=6<br>
	 * 如果把最后的no=6修改成0-5的数字，则分别表示日本6个地区的当日排行榜，但是这些区域的只有前50的作品<br>
	 * 0对应北海道/东北<br>
	 * 1对应关东<br>
	 * 2对应中部<br>
	 * 3对应近畿<br>
	 * 4对应中国/四国<br>
	 * 5对应九州/冲绳<br>
	 * 6对应国际<br>
	 * @throws Exception 
	 */
	public void downloadInternationalRankingPicForToday(int regionCode) throws Exception{
		//long startTime = System.currentTimeMillis();
		
		String url = "https://www.pixiv.net/ranking_area.php?type=detail&no=" + regionCode;
		
		DownloadHtml download = new DownloadHtml();
		Map<String, String> picAddrs = new HashMap<>();
		try {
			picAddrs = RegHtml.regRankingPageForPicAddr(download.getHtml(url));
		} catch (IOException e) {
			System.out.println("==========下载网页源码出错,需要重试===========");
			e.printStackTrace();
			
		}
		DownloadOriginalPic down = new DownloadOriginalPic();
		
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		String today = format.format(new Date());
		String folder = today + "Ranking/";
		
		for(Map.Entry entry : picAddrs.entrySet()){
			String authorId = (String)entry.getValue();
			String filename = folder + authorId + "N";
			String picUrl = (String)entry.getKey();
			try{
				down.download(picUrl, filename);
			}catch(IOException ex){
				System.out.println("========下载图片是出错了=======");
				System.out.println("图片地址：" + picUrl);
				ex.printStackTrace();
				continue;
			}
		}
		//TimeUtil.printTime(startTime);
		
	}
	
	/**
	 * 下载2017年3月每天的当日最受男性欢迎的50张图片<br>
	 * 地址如下：<br>
	 * https://www.pixiv.net/ranking.php?mode=male&date=20170409
	 * @throws Exception 
	 */
	public void downloadRankingByDay() throws Exception{
		//long startTime = System.currentTimeMillis();
		
		DownloadOriginalPic download = new DownloadOriginalPic();
		DownloadHtml downloadHtml = new DownloadHtml();
		String folder = "201612DayRanking/";
		String urlSuffix;
		for(int i = 1; i <= 31; i++){
			urlSuffix = String.valueOf(i);
			if(i<10){
				urlSuffix = "0" + urlSuffix;
			}
			String urlPre = "https://www.pixiv.net/ranking.php?mode=male&date=201612";
			String url = urlPre + urlSuffix;
			try{
				Map<String, String> picAddrs = RegHtml.regRankingPageForPicAddr(downloadHtml.getHtml(url));
				
				for(Map.Entry entry : picAddrs.entrySet()){
					String authorId = (String)entry.getValue();
					String filename = folder + authorId + "N";
					String picUrl = (String)entry.getKey();
					try{
						download.download(picUrl, filename);
					}catch(IOException e){
						System.out.println("===========下载图片出错===========");
						System.out.println("出错图片地址：" + picUrl);
						e.printStackTrace();
						continue;
					}
					
				}
			}catch(IOException ex){
				System.out.println("======下载网页源码出错，忽略此日的数据====");
				System.out.println("这天为2017年3月的" + i + "日");
				ex.printStackTrace();
				continue;
			}			
			
		}

	}
	
	/**
	 * 通过收藏一张图片，再根据该图片的id来获取P站推荐的500张相关的图片
	 * 
	 * @param id 图片的id
	 * @throws Exception 
	 */
	public void downloadRecommendPicByPicId(String id) throws Exception{
		
		Logger logger = LogManager.getLogger("mylog");
		
		long startTime = System.currentTimeMillis();
		GetRecommend recommender = new GetRecommend();
		String folder = id + "Recommend/";
		
		String[] ids = recommender.getRecommendIdsFromBookmark(id);
		Map<String, String> picUrls = recommender.getRecommendPicAddrs(ids);
		System.out.println("==================总共获取到的推荐图片数量为======================");
		System.out.println(picUrls.size() + "张图片");
		logger.info("===============总共获取到的推荐图片数量为======================");
		logger.info(picUrls.size() + "张图片");
		logger.info("===============获取到的图片链接地址有=========================");
		
		for(Map.Entry entry : picUrls.entrySet()){
			System.out.println(entry.getValue() + "=" + entry.getKey());
		}
		
		DownloadOriginalPic download = new DownloadOriginalPic();
		
		for(Map.Entry entry : picUrls.entrySet()){
			String authorId = (String)entry.getValue();
			String filename = folder + authorId + "N";
			String picUrl = (String)entry.getKey();
			logger.info("图片作者id=" + authorId);
			logger.info(picUrl);
			
			try{
				download.download(picUrl, filename);
			}catch(IndexOutOfBoundsException iex){
				System.out.println("==================字符串匹配出错================");
				iex.printStackTrace();
				continue;
			}catch(IOException ex){
				System.out.println("==================下载图片出错,该图片地址为==================");
				System.out.println(picUrl);
				ex.printStackTrace();
				continue;
			
			}
		}
		
		
		//TimeUtil.printTime(startTime);
		
	}
	
	//下面的打印时间函数统一放到TimeUtil.java中
	/**
	 * 打印所用的时间
	 * 
	 * @param startTime	程序开始的时间
	 */
	/*private void printTime(long startTime){
		
		long endTime = System.currentTimeMillis();
		Date start = new Date(startTime);
		Date end = new Date(endTime);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String startStr = format.format(start);
		String endStr = format.format(end);
		long difTime = endTime - startTime;
		int difDay = (int) (difTime/(1000*60*60*24));
		int difHour = (int) ((difTime-difDay*1000*60*60*24)/(1000*60*60));
		int difMinute = (int)((difTime-((difDay*24+difHour)*1000*60*60))/(1000*60));
		int difSecond = (int)((difTime-(difDay*24*60+difHour*60+difMinute)*1000*60)/1000);
		System.out.println("============================图片下载结束=======================");
		System.out.println("开始于：" + startStr);
		System.out.println("结束于：" + endStr);
		System.out.println("共用了：" + difDay + "天" + difHour + "小时" + difMinute + "分钟" + difSecond + "秒");
	}*/

}
