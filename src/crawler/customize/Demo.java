package crawler.customize;

public class Demo {

	public static void main(String[] args){
		DownloadByDifferentCond demo = new DownloadByDifferentCond();
		
		String id = "1277";
		
		//只下载该成员的所有作品
		//demo.downloadAllWorksByMemId(id);
		
		//下载该成员所关注的所有用户的所有作品，不包括成员自己的作品
		//demo.downloadAllFollowersWorksByMemId(id);
		
		//下载该成员所收藏的所有图片
		demo.downloadAllFavoritePicByMemId(id);
	}
}
