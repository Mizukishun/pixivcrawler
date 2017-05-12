package crawler.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil {

	/**
	 * 打印所用的时间<br>
	 * 只要将long型的开始时间传入，就可以打印出从开始时间到此函数执行时所经历的时间<br>
	 * 
	 * @param startTime	程序开始的时间
	 */
	public static void printTime(long startTime){
		
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
	}
}
