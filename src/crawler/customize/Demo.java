package crawler.customize;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import crawler.utils.TimeUtil;

public class Demo {

	public static void main(String[] args){
		long startTime = System.currentTimeMillis();
		Logger logger = LogManager.getLogger("mylog");
		logger.trace("程序启动");
		
		
		DownloadByDifferentCond demo = new DownloadByDifferentCond();
		
		//String id = "2174196";
		String id = args[0];
		//String id = "5206721";
		
		//只下载该成员的所有作品
		/*logger.trace("==============================下载指定成员的所有作品============================");
		logger.trace("开始下载成员id为" + id + "的所有图片");
		demo.downloadAllWorksByMemId(id);*/
	
		
		//下载该成员所收藏的所有图片
		//demo.downloadAllFavoritePicByMemId(id);
		
		//下载该成员所关注的所有用户的所有作品，不包括成员自己的作品
		//demo.downloadAllFollowersWorksByMemId(id);
		
		
		//下载今日国际排行榜的前100名的作品
		//demo.downloadInternationalRankingPicForToday(6);
		
		//下载2017年3月的所有的今日数据，也即30天*50张
		//demo.downloadRankingByDay();
		
		//图片id
		/*String[] picids ={};
		for(int i = 0; i < picids.length; i++){
			try{
				logger.info("图片id=" + picids[i]);
				demo.downloadRecommendPicByPicId(picids[i]);
			}catch(Exception e){
				e.printStackTrace();
				logger.error("===================推荐图片下载不完全============================");
				logger.error("此图片id=" + picids[i] + "下载推荐图片出错，没有将所有推荐的图片下载下来");
				continue;
			}
		}*/
		
		//根据成员id找出其所有作品id,再根据作品id下载所有推荐的图片
		demo.downloadRecommendPicByAuthorId(id);
		
		TimeUtil.printTime(startTime);
	}
}
