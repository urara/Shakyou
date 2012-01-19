package com.shakyou;
import java.util.Date;

import com.shakyou.TimeZone;

import com.shakyou.BaseCalendar;
import com.shakyou.CalendarSystem;
import com.shakyou.CalendarUtils;

public class DateShakyou implements java.io.Serializable, Cloneable{

	

	//グレゴリアンカレンダーの取得
	private static final BaseCalendar gcal = CalendarSystem.getGregorianCalendar();
	
	//ユリウス歴カレンダー用
	private static BaseCalendar jcal;
	
	private transient long fastTime;
	
	private transient BaseCalendar.Date cdate;
	
	private static int defaultCenturyStart;
		
	private static final long serialVersionUID = 1L;
	
	public DateShakyou(){
		this(System.currentTimeMillis());
	}

	public DateShakyou(long date) {
		fastTime = date;
		System.out.println("date = " + fastTime);
	}
	
	public DateShakyou(int year, int month, int date){
		this(year, month, date, 0,0,0);
	}

	public DateShakyou(int year, int month, int date, int hours, int min) {
		this(year, month, date, hours, min, 0);
	}

	public DateShakyou(int year, int month, int date, int hours, int min, int sec) {
		int y = year + 1900;
		if(month >= 12){
			month %= 12;
		}else if(month < 0){
			y += CalendarUtils.floorDivide(month,12);
			month = CalendarUtils.mod(month,12);
		}
		BaseCalendar cal = getCalendarSystem(y);
//		cdate = (BaseCalendar.Date) cal.newCalendarDate(TimeZone.getDefaultRef());
		cdate.setNormalizedDate(y, month + 1, date).setTimeOfDay(hours, min, sec, 0 );
		getTimeImpl();
		cdate = null;
	}

	
	
	
	
	

	

	
	private long getTimeImpl() {
		if (cdate != null && !cdate.isNormalized()){
//ore			normalize();
		}
		return fastTime;
	}

	//ここから
	/*ore
	private final BaseCalendar.Date normalize() {
		if(cdate == null){
			BaseCalendar cal = getCalendarSystem(fastTime);
		}
		
	}

ore*/
	//ユリウス歴,グレゴリアン歴の適切なカレンダーを取得する
	private static BaseCalendar getCalendarSystem(int year) {
		if (year >= 1582){
			return gcal;
		}
		return getJulianCalendar();
	}

	private static BaseCalendar getJulianCalendar() {
		if (jcal == null){
			jcal = (BaseCalendar) CalendarSystem.forName("julian");
		}
		return jcal;
	}
	
}
