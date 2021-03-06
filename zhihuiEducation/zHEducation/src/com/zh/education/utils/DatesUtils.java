package com.zh.education.utils;

import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.os.StrictMode;
import android.text.TextUtils;
import android.util.Log;

/**
 * 日期类工具类
 * 
 * @author jun
 * 
 */
public class DatesUtils {
	private static DatesUtils mUtils;

	public synchronized static DatesUtils getInstance() {
		if (mUtils == null) {
			mUtils = new DatesUtils();
		}
		return mUtils;
	}

	/**
	 * 时间戳转换成--日期
	 * 
	 * @param time
	 * @return
	 */
	public String getTimeStampToDate(long timestamp, String format) {
		String day = null;
		try {
			SimpleDateFormat sf = new SimpleDateFormat(format);
			long timestamp_ = timestamp;
			Date d = new Date(timestamp_);
			day = sf.format(d);
		} catch (Exception e) {
		}
		return day;
	}

	/**
	 * 将日期转为--时间戳
	 * 
	 * @param day
	 * @return
	 */
	public long getDateToTimeStamp(String day, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = new Date();

		try {
			date = sdf.parse(day);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long timestamp = date.getTime();
		return timestamp;
	}

	/**
	 * 返回同一天两个时间相差的时间（tian）
	 * 
	 * @param starttime
	 *            例如： 2013-12-11
	 * @param endtime
	 *            例如： 2013-12-11
	 * @param format
	 *            例如： HH:mm yyyy-MM-dd HH:mm
	 * @return
	 */
	public long timeDiff(String starttime, String endtime, String format) {
		SimpleDateFormat sdweek = new SimpleDateFormat(format);
		Date date = null;
		Date date2 = null;
		long day = 0;
		try {
			date = sdweek.parse(starttime);
			date2 = sdweek.parse(endtime);
			long time = (date2.getTime() - date.getTime()) / 1000; // 相差秒数
			day = time / (24 * 60 * 60);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return day;
	}
	/**
	 * 返回同一天两个时间相差的时间（hours）
	 * 
	 * @param starttime
	 *            例如：8:30 2013-12-11 8:30
	 * @param endtime
	 *            例如： 9:00 2013-12-11 9:00
	 * @param format
	 *            例如： HH:mm yyyy-MM-dd HH:mm
	 * @return
	 */
	public int timeDiffhours(String starttime, String endtime, String format) {
		SimpleDateFormat sdweek = new SimpleDateFormat(format);
		Date date = null;
		Date date2 = null;
		int hoursStr = 0;
		try {
			date = sdweek.parse(starttime);
			date2 = sdweek.parse(endtime);
			long time = (date2.getTime() - date.getTime()) / 1000; // 相差秒数
			long day = time / (24 * 60 * 60);
			long hours = (time % (24 * 60 * 60)) / (60 * 60);
			long minutes = ((time % (24 * 60 * 60)) % (60 * 60)) / 60;
			long second = ((time % (24 * 60 * 60)) % (60 * 60)) % 60;
			hoursStr = (int) (day * 24 * 60 * 60 + hours * 60 * 60);
			System.out.println("starttime 与  endtime 相差" + hoursStr + "hour");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return hoursStr;
	}
	/**
	 * 注意的是在对日期进行加减的时候请使用DAY_OF_YEAR 不要使用DAY_OF_MONTH
	 * 
	 * @param time
	 *            例如：13:11 2014-5-8 13:11
	 * @param format
	 *            例如：HH:mm yyyy-MM-dd HH:mm
	 * @param adddays
	 *            所加的分钟
	 * @return
	 */
	public String addTime(String time, int adddays, String format) {
		String newtime = "";
		try {
			SimpleDateFormat sdweek = new SimpleDateFormat(format);
			Date date = sdweek.parse(time);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			cal.add(Calendar.DAY_OF_YEAR, adddays);
			newtime = sdweek.format(cal.getTime());
			System.out.println("newtime ==" + newtime);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return newtime;
	}
	public String getNowTime(String format) {
		SimpleDateFormat nowsdf = new SimpleDateFormat(format);
		Date date = new Date();
		String time = nowsdf.format(date);
		return time;
	}
	/**
	 * 周一为当月的第几周
	 * @param day
	 * @return
	 */
	public   int getWeekIndex(String day){
		int weekOfMonth = 1;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdf.parse(day);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			  weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
			System.out.println(weekOfMonth); 
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return weekOfMonth;
	}
	/**
	 * 得到本周的周一到周日
	 * 
	 * @return
	 */
	public long[] getWeekDay(long realnowday) {
		Calendar calendar = Calendar.getInstance();
//		long realnowday = syncCurrentTime();
		if (realnowday != 0) {
			calendar.setTime(new Date(realnowday));// 把当前时间赋给日历
		} 
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, -1);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date[] dates = new Date[7];
		long[] daysStamp = new long[dates.length];
		for (int i = 0; i < 7; i++) {
			dates[i] = calendar.getTime();
			String day = dateFormat.format(dates[i]);
			daysStamp[i] = getDateToTimeStamp(day, "yyyy-MM-dd");
			calendar.add(Calendar.DATE, 1);
		}
		return daysStamp;
	}
	/**
	 * 时间格式的转换,转换显示类型
	 * 
	 * @param day
	 *            2014-11-11
	 * @param fromformat
	 *            yyyy-MM-dd
	 * @param toformat
	 *            MM/dd EE
	 * @return
	 */
	public String getDateGeShi(String day, String fromformat, String toformat) {
		SimpleDateFormat sdweek = new SimpleDateFormat(fromformat);
		SimpleDateFormat formatter = new SimpleDateFormat(toformat);
		String datetime = "";
		try {
			if (TextUtils.isEmpty(day)) {
				return datetime;
			}
			Date date = sdweek.parse(day);
			datetime = formatter.format(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return datetime;
	}
	/**
	 * 判断两个时间是否是同一天
	 * 
	 * @param sdate
	 * @param odate
	 * @return
	 */
	public boolean doDateEqual(String sdate, String odate, String format) {
		boolean status = false;
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		Date date = null;
		Date date1 = null;
		try {
			if (!TextUtils.isEmpty(sdate) && !TextUtils.isEmpty(odate)) {
				date = sdf.parse(sdate);
				date1 = sdf.parse(odate);
				if (date.compareTo(date1) == 0) {
					status = true;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return status;
	}
	/**
	 * 得到本周的周一到周日
	 * 
	 * @return
	 */
	public String[] getWeekDayString(long realnowday) {
		Calendar calendar = Calendar.getInstance();
		if (realnowday != 0) {
			calendar.setTime(new Date(realnowday));// 把当前时间赋给日历
		} else{
//			realnowday = syncCurrentTime();
//			calendar.setTime(new Date(realnowday));
		}
		while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
			calendar.add(Calendar.DAY_OF_WEEK, -1);
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date[] dates = new Date[7];
		String[] daysStamp = new String[dates.length];
		for (int i = 0; i < 7; i++) {
			dates[i] = calendar.getTime();
			String day = dateFormat.format(dates[i]);
			daysStamp[i] = day;
			calendar.add(Calendar.DATE, 1);
		}
		return daysStamp;
	}
	/**
	 * 得到网络当期时间
	 * 
	 * @return
	 */
	public  long syncCurrentTime()   {
	       int DEFAULT_PORT = 37;//NTP服务器端口
	       String DEFAULT_HOST = "time-nw.nist.gov";//NTP服务器地址
     // The time protocol sets the epoch at 1900,
     // the java Date class at 1970. This number
     // converts between them.
     long differenceBetweenEpochs = 2208988800L;
     // If you'd rather not use the magic number uncomment
     // the following section which calculates it directly.
     /*
      * TimeZone gmt = TimeZone.getTimeZone("GMT"); Calendar epoch1900 =
      * Calendar.getInstance(gmt); epoch1900.set(1900, 01, 01, 00, 00, 00);
      * long epoch1900ms = epoch1900.getTime().getTime(); Calendar epoch1970
      * = Calendar.getInstance(gmt); epoch1970.set(1970, 01, 01, 00, 00, 00);
      * long epoch1970ms = epoch1970.getTime().getTime();
      * 
      * long differenceInMS = epoch1970ms - epoch1900ms; long
      * differenceBetweenEpochs = differenceInMS/1000;
      */

     InputStream raw = null;
     try {
         Socket theSocket = new Socket(DEFAULT_HOST, DEFAULT_PORT);
         raw = theSocket.getInputStream();
    
         long secondsSince1900 = 0;
         for (int i = 0; i < 4; i++) {
             secondsSince1900 = (secondsSince1900 << 8) | raw.read();
         }
         if (raw != null)
             raw.close();
         long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;
         long msSince1970 = secondsSince1970 * 1000;
         return msSince1970;
     } catch (Exception e) {
     	 return 0;
     }
 }
	/**
     * 通过年份和月份 得到当月的日子
     * 
     * @param year
     * @param month
     * @return
     */
	public int getMonthDays(int year, int month) {
		month++;
		switch (month) {
		case 1:
		case 3:
		case 5:
		case 7:
		case 8:
		case 10:
		case 12: 
		    return 31;
		case 4:
		case 6:
		case 9:
		case 11: 
		    return 30;
		case 2:
			if (((year % 4 == 0) && (year % 100 != 0)) || (year % 400 == 0)){
				return 29;
			}else{
				return 28;
			}
		default:
			return  -1;
		}
    }
	/**
     * 返回当前月份1号位于周几
     * @param year
     * 		年份
     * @param month
     * 		月份，传入系统获取的，不需要正常的
     * @return
     * 	日：1		一：2		二：3		三：4		四：5		五：6		六：7
     */
	public int getFirstDayWeek(int mSelYear, int mSelMonth) {
    	Calendar calendar = Calendar.getInstance();
    	calendar.set(mSelYear, mSelMonth, 1);
    	Log.d("DateView", "DateView:First:" + calendar.getFirstDayOfWeek());
    	return calendar.get(Calendar.DAY_OF_WEEK);
    }

}
