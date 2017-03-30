package crawler.vo;

import java.util.List;
import java.util.Map;

/**
 * 成员所关注的其它用户		//目前只能收集到公开的关注用户
 * 
 * @author Stargazer
 * @date 2017-03-30
 */
public class Followers {
	
	//成员的id
	private String user_id;			
	
	//所关注用户的数量
	private int followers_num;
	
	/**
	 * 关注的用户的id
	 */
	private List<String> followers_id;			
	
	/**
	 * 关注的用户的名称，键为关注用户的id，值为该用户的名称
	 */
	private Map<String, String> followers_name;		
	
	/**
	 * 关注的用户的个人资料地址，键为关注用户的id，值为该用户的链接地址
	 * 注意，这里链接到的是用户的个人资料地址
	 * http://www.pixiv.net/member.php?id=4713
	 */
	private Map<String, String> followers_link;
	
	/**
	 * 关注用户的作品地址，键为关注用户的id,值为该用户的作品地址
	 * http://www.pixiv.net/member_illust.php?id=4713
	 */
	private Map<String, String> followers_works_link;
	
	/**
	 * 关注用户的收藏地址，键为关注用户的id,值为该用户的收藏地址
	 * http://www.pixiv.net/bookmark.php?id=4713
	 */
	private Map<String, String> followers_favorite_link;
	
	
	/**
	 * 与关注的用户是否相互关注，如果是相互关注，则在后面的逐级查询关注用户中时，
	 * 可能陷入死循环，这点要提前注意! 
	 * 其中，键为用户的id，值为是否相互关注
	 * 键值为"1"，表示只是单方向的关注，也即成员关注着用户，但用户没有关注成员；
	 * 键值为"2"，表示是相互关注，两者都关注了对方；
	 * 
	 */
	private Map<String, String> follow_each_other;
	
	public Followers(String user_id){
		this.user_id = user_id;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public int getFollowers_num() {
		return followers_num;
	}

	public void setFollowers_num(int followers_num) {
		this.followers_num = followers_num;
	}

	public List<String> getFollowers_id() {
		return followers_id;
	}

	public void setFollowers_id(List<String> followers_id) {
		this.followers_id = followers_id;
	}

	public Map<String, String> getFollowers_name() {
		return followers_name;
	}

	public void setFollowers_name(Map<String, String> followers_name) {
		this.followers_name = followers_name;
	}

	public Map<String, String> getFollowers_link() {
		return followers_link;
	}

	public void setFollowers_link(Map<String, String> followers_link) {
		this.followers_link = followers_link;
	}

	public Map<String, String> getFollowers_works_link() {
		return followers_works_link;
	}

	public void setFollowers_works_link(Map<String, String> followers_works_link) {
		this.followers_works_link = followers_works_link;
	}

	public Map<String, String> getFollowers_favorite_link() {
		return followers_favorite_link;
	}

	public void setFollowers_favorite_link(Map<String, String> followers_favorite_link) {
		this.followers_favorite_link = followers_favorite_link;
	}

	public Map<String, String> getFollow_each_other() {
		return follow_each_other;
	}

	public void setFollow_each_other(Map<String, String> follow_each_other) {
		this.follow_each_other = follow_each_other;
	}
	

}
