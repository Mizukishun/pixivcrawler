package crawler.customize;

public class Demo {

	public static void main(String[] args){
		DownloadByDifferentCond demo = new DownloadByDifferentCond();
		
		//String id = "418969";
		//String id = "5206721";
		
		//只下载该成员的所有作品
		//demo.downloadAllWorksByMemId(id);
		
		//下载该成员所关注的所有用户的所有作品，不包括成员自己的作品
		//demo.downloadAllFollowersWorksByMemId(id);
		
		//下载该成员所收藏的所有图片
		//demo.downloadAllFavoritePicByMemId(id);
		
		//下载今日国际排行榜的前100名的作品
		//demo.downloadInternationalRankingPicForToday(6);
		
		//下载2017年3月的所有的今日数据，也即30天*50张
		demo.downloadRankingByDay();
	}
}
