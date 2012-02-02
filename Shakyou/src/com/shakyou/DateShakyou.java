package com.shakyou;

//import java.util.Date;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.shakyou.TimeZone;

import com.shakyou.BaseCalendar;
import com.shakyou.CalendarSystem;
import com.shakyou.CalendarUtils;

public class DateShakyou implements java.io.Serializable, Cloneable {

	// グレゴリアンカレンダーの取得
	private static final BaseCalendar gcal = CalendarSystem
			.getGregorianCalendar();

	// ユリウス歴カレンダー用
	private static BaseCalendar jcal;

	private transient long fastTime;

	private transient BaseCalendar.Date cdate;

	private static int defaultCenturyStart;

	private static final long serialVersionUID = 1L;

	public DateShakyou() {
		this(System.currentTimeMillis());
	}

	public DateShakyou(long date) {
		fastTime = date;
		System.out.println("date = " + fastTime);
	}

	public DateShakyou(int year, int month, int date) {
		this(year, month, date, 0, 0, 0);
	}

	public DateShakyou(int year, int month, int date, int hours, int min) {
		this(year, month, date, hours, min, 0);
	}

	public DateShakyou(int year, int month, int date, int hours, int min,
			int sec) {
		int y = year + 1900;
		if (month >= 12) {
			month %= 12;
		} else if (month < 0) {
			y += CalendarUtils.floorDivide(month, 12);
			month = CalendarUtils.mod(month, 12);
		}
		BaseCalendar cal = getCalendarSystem(y);
		cdate = (BaseCalendar.Date) cal.newCalendarDate(TimeZone
				.getDefaultRef());
		cdate.setNormalizedDate(y, month + 1, date).setTimeOfDay(hours, min,
				sec, 0);
		getTimeImpl();
		cdate = null;
	}

	@Deprecated
	public DateShakyou(String s) {
		this(parse(s));
	}

	public Object clone() {
		DateShakyou d = null;
		try {
			d = (DateShakyou) super.clone();
			if (cdate != null) {
				d.cdate = (BaseCalendar.Date) cdate.clone();
			}
		} catch (CloneNotSupportedException e) {
		} // Won't happen
		return d;
	}
	
	@Deprecated
	public static long UTC(int year, int month, int date, int hrs, int min,
			int sec) {
		int y = year + 1900;
		// month is 0-based. So we have to normalize month to support
		// Long.MAX_VALUE.
		if (month >= 12) {
			y += month / 12;
			month %= 12;
		} else if (month < 0) {
			y += CalendarUtils.floorDivide(month, 12);
			month = CalendarUtils.mod(month, 12);
		}
		int m = month + 1;
		BaseCalendar cal = getCalendarSystem(y);
		BaseCalendar.Date udate = (BaseCalendar.Date) cal.newCalendarDate(null);
		udate.setNormalizedDate(y, m, date).setTimeOfDay(hrs, min, sec, 0);

		// Use a Date instance to perform normalization. Its fastTime
		// is the UTC value after the normalization.
		DateShakyou d = new DateShakyou(0);
		d.normalize(udate);
		return d.fastTime;
	}
	
	@Deprecated
	public static long parse(String s) {
		int year = Integer.MIN_VALUE;
		int mon = -1;
		int mday = -1;
		int hour = -1;
		int min = -1;
		int sec = -1;
		int Shakyou = -1;
		int c = -1;
		int i = 0;
		int n = -1;
		int wst = -1;
		int tzoffset = -1;
		int prevc = 0;
		syntax: {
			if (s == null)
				break syntax;
			int limit = s.length();
			while (i < limit) {
				c = s.charAt(i);
				i++;
				if (c <= ' ' || c == ',')
					continue;
				if (c == '(') { // skip comments
					int depth = 1;
					while (i < limit) {
						c = s.charAt(i);
						i++;
						if (c == '(')
							depth++;
						else if (c == ')')
							if (--depth <= 0)
								break;
					}
					continue;
				}
				if ('0' <= c && c <= '9') {
					n = c - '0';
					while (i < limit && '0' <= (c = s.charAt(i)) && c <= '9') {
						n = n * 10 + c - '0';
						i++;
					}
					if (prevc == '+' || prevc == '-'
							&& year != Integer.MIN_VALUE) {
						// timezone offset
						if (n < 24)
							n = n * 60; // EG. "GMT-3"
						else
							n = n % 100 + n / 100 * 60; // eg "GMT-0430"
						if (prevc == '+') // plus means east of GMT
							n = -n;
						if (tzoffset != 0 && tzoffset != -1)
							break syntax;
						tzoffset = n;
					} else if (n >= 70)
						if (year != Integer.MIN_VALUE)
							break syntax;
						else if (c <= ' ' || c == ',' || c == '/' || i >= limit)
							// year = n < 1900 ? n : n - 1900;
							year = n;
						else
							break syntax;
					else if (c == ':')
						if (hour < 0)
							hour = (byte) n;
						else if (min < 0)
							min = (byte) n;
						else
							break syntax;
					else if (c == '/')
						if (mon < 0)
							mon = (byte) (n - 1);
						else if (mday < 0)
							mday = (byte) n;
						else
							break syntax;
					else if (i < limit && c != ',' && c > ' ' && c != '-')
						break syntax;
					else if (hour >= 0 && min < 0)
						min = (byte) n;
					else if (min >= 0 && sec < 0)
						sec = (byte) n;
					else if (mday < 0)
						mday = (byte) n;
					// Handle two-digit years < 70 (70-99 handled above).
					else if (year == Integer.MIN_VALUE && mon >= 0 && mday >= 0)
						year = n;
					else
						break syntax;
					prevc = 0;
				} else if (c == '/' || c == ':' || c == '+' || c == '-')
					prevc = c;
				else {
					int st = i - 1;
					while (i < limit) {
						c = s.charAt(i);
						if (!('A' <= c && c <= 'Z' || 'a' <= c && c <= 'z'))
							break;
						i++;
					}
					if (i <= st + 1)
						break syntax;
					int k;
					for (k = wtb.length; --k >= 0;)
						if (wtb[k].regionMatches(true, 0, s, st, i - st)) {
							int action = ttb[k];
							if (action != 0) {
								if (action == 1) { // pm
									if (hour > 12 || hour < 1)
										break syntax;
									else if (hour < 12)
										hour += 12;
								} else if (action == 14) { // am
									if (hour > 12 || hour < 1)
										break syntax;
									else if (hour == 12)
										hour = 0;
								} else if (action <= 13) { // month!
									if (mon < 0)
										mon = (byte) (action - 2);
									else
										break syntax;
								} else {
									tzoffset = action - 10000;
								}
							}
							break;
						}
					if (k < 0)
						break syntax;
					prevc = 0;
				}
			}
			if (year == Integer.MIN_VALUE || mon < 0 || mday < 0)
				break syntax;
			// Parse 2-digit years within the correct default century.
			if (year < 100) {
				synchronized (DateShakyou.class) {
					if (defaultCenturyStart == 0) {
						defaultCenturyStart = gcal.getCalendarDate().getYear() - 80;
					}
				}
				year += (defaultCenturyStart / 100) * 100;
				if (year < defaultCenturyStart)
					year += 100;
			}
			if (sec < 0)
				sec = 0;
			if (min < 0)
				min = 0;
			if (hour < 0)
				hour = 0;
			BaseCalendar cal = getCalendarSystem(year);
			if (tzoffset == -1) { // no time zone specified, have to use local
				BaseCalendar.Date ldate = (BaseCalendar.Date) cal
						.newCalendarDate(TimeZone.getDefaultRef());
				ldate.setDate(year, mon + 1, mday);
				ldate.setTimeOfDay(hour, min, sec, 0);
				return cal.getTime(ldate);
			}
			BaseCalendar.Date udate = (BaseCalendar.Date) cal
					.newCalendarDate(null); // no time zone
			udate.setDate(year, mon + 1, mday);
			udate.setTimeOfDay(hour, min, sec, 0);
			return cal.getTime(udate) + tzoffset * (60 * 1000);
		}
		// syntax error
		throw new IllegalArgumentException();
	}

	// perseを動かすため
	private final static String wtb[] = { "am", "pm", "monday", "tuesday",
			"wednesday", "thursday", "friday", "saturday", "sunday", "january",
			"february", "march", "april", "may", "june", "july", "august",
			"september", "october", "november", "december", "gmt", "ut", "utc",
			"est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt" };

	// perseを動かすため
	private final static int ttb[] = { 14, 1, 0, 0, 0, 0, 0, 0, 0, 2, 3, 4, 5,
			6, 7, 8, 9, 10, 11, 12, 13, 10000 + 0, 10000 + 0, 10000 + 0, // GMT/UT/UTC
			10000 + 5 * 60, 10000 + 4 * 60, // EST/EDT
			10000 + 6 * 60, 10000 + 5 * 60, // CST/CDT
			10000 + 7 * 60, 10000 + 6 * 60, // MST/MDT
			10000 + 8 * 60, 10000 + 7 * 60 // PST/PDT
	};

	@Deprecated
	public int getYear() {
		return normalize().getYear() - 1900;
	}

	/**
	 * Sets the year of this <tt>Date</tt> object to be the specified value plus
	 * 1900. This <code>Date</code> object is modified so that it represents a
	 * point in time within the specified year, with the month, date, hour,
	 * minute, and second the same as before, as interpreted in the local time
	 * zone. (Of course, if the date was February 29, for example, and the year
	 * is set to a non-leap year, then the new date will be treated as if it
	 * were on March 1.)
	 * 
	 * @param year
	 *            the year value.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.set(Calendar.YEAR, year + 1900)</code>.
	 */
	@Deprecated
	public void setYear(int year) {
		getCalendarDate().setNormalizedYear(year + 1900);
	}

	/**
	 * Returns a number representing the month that contains or begins with the
	 * instant in time represented by this <tt>Date</tt> object. The value
	 * returned is between <code>0</code> and <code>11</code>, with the value
	 * <code>0</code> representing January.
	 * 
	 * @return the month represented by this date.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.get(Calendar.MONTH)</code>.
	 */
	@Deprecated
	public int getMonth() {
		return normalize().getMonth() - 1; // adjust 1-based to 0-based
	}

	/**
	 * Sets the month of this date to the specified value. This <tt>Date</tt>
	 * object is modified so that it represents a point in time within the
	 * specified month, with the year, date, hour, minute, and second the same
	 * as before, as interpreted in the local time zone. If the date was October
	 * 31, for example, and the month is set to June, then the new date will be
	 * treated as if it were on July 1, because June has only 30 days.
	 * 
	 * @param month
	 *            the month value between 0-11.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.set(Calendar.MONTH, int month)</code>.
	 */
	@Deprecated
	public void setMonth(int month) {
		int y = 0;
		if (month >= 12) {
			y = month / 12;
			month %= 12;
		} else if (month < 0) {
			y = CalendarUtils.floorDivide(month, 12);
			month = CalendarUtils.mod(month, 12);
		}
		BaseCalendar.Date d = getCalendarDate();
		if (y != 0) {
			d.setNormalizedYear(d.getNormalizedYear() + y);
		}
		d.setMonth(month + 1); // adjust 0-based to 1-based month numbering
	}

	/**
	 * Returns the day of the month represented by this <tt>Date</tt> object.
	 * The value returned is between <code>1</code> and <code>31</code>
	 * representing the day of the month that contains or begins with the
	 * instant in time represented by this <tt>Date</tt> object, as interpreted
	 * in the local time zone.
	 * 
	 * @return the day of the month represented by this date.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.get(Calendar.DAY_OF_MONTH)</code>.
	 * @deprecated
	 */
	@Deprecated
	public int getDate() {
		return normalize().getDayOfMonth();
	}

	/**
	 * Sets the day of the month of this <tt>Date</tt> object to the specified
	 * value. This <tt>Date</tt> object is modified so that it represents a
	 * point in time within the specified day of the month, with the year,
	 * month, hour, minute, and second the same as before, as interpreted in the
	 * local time zone. If the date was April 30, for example, and the date is
	 * set to 31, then it will be treated as if it were on May 1, because April
	 * has only 30 days.
	 * 
	 * @param date
	 *            the day of the month value between 1-31.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.set(Calendar.DAY_OF_MONTH, int date)</code>.
	 */
	@Deprecated
	public void setDate(int date) {
		getCalendarDate().setDayOfMonth(date);
	}

	/**
	 * Returns the day of the week represented by this date. The returned value
	 * (<tt>0</tt> = Sunday, <tt>1</tt> = Monday, <tt>2</tt> = Tuesday,
	 * <tt>3</tt> = Wednesday, <tt>4</tt> = Thursday, <tt>5</tt> = Friday,
	 * <tt>6</tt> = Saturday) represents the day of the week that contains or
	 * begins with the instant in time represented by this <tt>Date</tt> object,
	 * as interpreted in the local time zone.
	 * 
	 * @return the day of the week represented by this date.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.get(Calendar.DAY_OF_WEEK)</code>.
	 */
	@Deprecated
	public int getDay() {
		return normalize().getDayOfWeek() - gcal.SUNDAY;
	}

	/**
	 * Returns the hour represented by this <tt>Date</tt> object. The returned
	 * value is a number (<tt>0</tt> through <tt>23</tt>) representing the hour
	 * within the day that contains or begins with the instant in time
	 * represented by this <tt>Date</tt> object, as interpreted in the local
	 * time zone.
	 * 
	 * @return the hour represented by this date.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.get(Calendar.HOUR_OF_DAY)</code>.
	 */
	@Deprecated
	public int getHours() {
		return normalize().getHours();
	}

	/**
	 * Sets the hour of this <tt>Date</tt> object to the specified value. This
	 * <tt>Date</tt> object is modified so that it represents a point in time
	 * within the specified hour of the day, with the year, month, date, minute,
	 * and second the same as before, as interpreted in the local time zone.
	 * 
	 * @param hours
	 *            the hour value.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.set(Calendar.HOUR_OF_DAY, int hours)</code>.
	 */
	@Deprecated
	public void setHours(int hours) {
		getCalendarDate().setHours(hours);
	}

	/**
	 * Returns the number of minutes past the hour represented by this date, as
	 * interpreted in the local time zone. The value returned is between
	 * <code>0</code> and <code>59</code>.
	 * 
	 * @return the number of minutes past the hour represented by this date.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.get(Calendar.MINUTE)</code>.
	 */
	@Deprecated
	public int getMinutes() {
		return normalize().getMinutes();
	}

	/**
	 * Sets the minutes of this <tt>Date</tt> object to the specified value.
	 * This <tt>Date</tt> object is modified so that it represents a point in
	 * time within the specified minute of the hour, with the year, month, date,
	 * hour, and second the same as before, as interpreted in the local time
	 * zone.
	 * 
	 * @param minutes
	 *            the value of the minutes.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.set(Calendar.MINUTE, int minutes)</code>.
	 */
	@Deprecated
	public void setMinutes(int minutes) {
		getCalendarDate().setMinutes(minutes);
	}

	/**
	 * Returns the number of seconds past the minute represented by this date.
	 * The value returned is between <code>0</code> and <code>61</code>. The
	 * values <code>60</code> and <code>61</code> can only occur on those Java
	 * Virtual Machines that take leap seconds into account.
	 * 
	 * @return the number of seconds past the minute represented by this date.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.get(Calendar.SECOND)</code>.
	 */
	@Deprecated
	public int getSeconds() {
		return normalize().getSeconds();
	}

	/**
	 * Sets the seconds of this <tt>Date</tt> to the specified value. This
	 * <tt>Date</tt> object is modified so that it represents a point in time
	 * within the specified second of the minute, with the year, month, date,
	 * hour, and minute the same as before, as interpreted in the local time
	 * zone.
	 * 
	 * @param seconds
	 *            the seconds value.
	 * @see java.util.Calendar
	 * @deprecated As of JDK version 1.1, replaced by
	 *             <code>Calendar.set(Calendar.SECOND, int seconds)</code>.
	 */
	@Deprecated
	public void setSeconds(int seconds) {
		getCalendarDate().setSeconds(seconds);
	}

	/**
	 * Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
	 * represented by this <tt>Date</tt> object.
	 * 
	 * @return the number of milliseconds since January 1, 1970, 00:00:00 GMT
	 *         represented by this date.
	 */
	public long getTime() {
		return getTimeImpl();
	}

	private final long getTimeImpl() {
		if (cdate != null && !cdate.isNormalized()) {
			normalize();
		}
		return fastTime;
	}

	public void setTime(long time) {
		fastTime = time;
		cdate = null;
	}
	
	public boolean before(DateShakyou when) {
		return getMillisOf(this) < getMillisOf(when);
	}
	
	public boolean after(DateShakyou when) {
		return getMillisOf(this) > getMillisOf(when);
	}
	
	public boolean equals(Object obj) {
		return obj instanceof DateShakyou && getTime() == ((DateShakyou) obj).getTime();
	}
	
	static final long getMillisOf(DateShakyou date) {
		if (date.cdate == null || date.cdate.isNormalized()) {
			return date.fastTime;
		}
		BaseCalendar.Date d = (BaseCalendar.Date) date.cdate.clone();
		return gcal.getTime(d);
	}
	
	public int compareTo(DateShakyou anotherDate) {
		long thisTime = getMillisOf(this);
		long anotherTime = getMillisOf(anotherDate);
		return (thisTime < anotherTime ? -1 : (thisTime == anotherTime ? 0 : 1));
	}
	
	public int hashCode() {
		long ht = this.getTime();
		return (int) ht ^ (int) (ht >> 32);
	}
	
	public String toString() {
		// "EEE MMM dd HH:mm:ss zzz yyyy";
		BaseCalendar.Date date = normalize();
		StringBuilder sb = new StringBuilder(28);
		int index = date.getDayOfWeek();
		if (index == gcal.SUNDAY) {
			index = 8;
		}
		convertToAbbr(sb, wtb[index]).append(' '); // EEE
		convertToAbbr(sb, wtb[date.getMonth() - 1 + 2 + 7]).append(' '); // MMM
		CalendarUtils.sprintf0d(sb, date.getDayOfMonth(), 2).append(' '); // dd

		CalendarUtils.sprintf0d(sb, date.getHours(), 2).append(':'); // HH
		CalendarUtils.sprintf0d(sb, date.getMinutes(), 2).append(':'); // mm
		CalendarUtils.sprintf0d(sb, date.getSeconds(), 2).append(' '); // ss
		TimeZone zi = date.getZone();
		if (zi != null) {
			sb.append(zi.getDisplayName(date.isDaylightTime(), zi.SHORT,
					Locale.US)); // zzz
		} else {
			sb.append("GMT");
		}
		sb.append(' ').append(date.getYear()); // yyyy
		return sb.toString();
	}
	
	private static final StringBuilder convertToAbbr(StringBuilder sb,
			String name) {
		sb.append(Character.toUpperCase(name.charAt(0)));
		sb.append(name.charAt(1)).append(name.charAt(2));
		return sb;
	}
	
	@Deprecated
	public String toLocaleString() {
		DateFormat formatter = DateFormat.getDateTimeInstance();
		return formatter.format(this);
	}
	
	@Deprecated
	public String toGMTString() {
		// d MMM yyyy HH:mm:ss 'GMT'
		long t = getTime();
		BaseCalendar cal = getCalendarSystem(t);
		BaseCalendar.Date date = (BaseCalendar.Date) cal.getCalendarDate(
				getTime(), (TimeZone) null);
		StringBuilder sb = new StringBuilder(32);
		CalendarUtils.sprintf0d(sb, date.getDayOfMonth(), 1).append(' '); // d
		convertToAbbr(sb, wtb[date.getMonth() - 1 + 2 + 7]).append(' '); // MMM
		sb.append(date.getYear()).append(' '); // yyyy
		CalendarUtils.sprintf0d(sb, date.getHours(), 2).append(':'); // HH
		CalendarUtils.sprintf0d(sb, date.getMinutes(), 2).append(':'); // mm
		CalendarUtils.sprintf0d(sb, date.getSeconds(), 2); // ss
		sb.append(" GMT"); // ' GMT'
		return sb.toString();
	}
	
	@Deprecated
	public int getTimezoneOffset() {
		int zoneOffset;
		if (cdate == null) {
			TimeZone tz = TimeZone.getDefaultRef();
			if (tz instanceof ZoneInfo) {
				zoneOffset = ((ZoneInfo) tz).getOffsets(fastTime, null);
			} else {
				zoneOffset = tz.getOffset(fastTime);
			}
		} else {
			normalize();
			zoneOffset = cdate.getZoneOffset();
		}
		return -zoneOffset / 60000; // convert to minutes
	}
	
	
	
	// ここから
	/*
	 * ore private final BaseCalendar.Date normalize() { if(cdate == null){
	 * BaseCalendar cal = getCalendarSystem(fastTime); }
	 * 
	 * }
	 * 
	 * ore
	 */
	// ユリウス歴,グレゴリアン歴の適切なカレンダーを取得する
	private static BaseCalendar getCalendarSystem(int year) {
		if (year >= 1582) {
			return gcal;
		}
		return getJulianCalendar();
	}

	private static BaseCalendar getJulianCalendar() {
		if (jcal == null) {
			jcal = (BaseCalendar) CalendarSystem.forName("julian");
		}
		return jcal;
	}

	private final BaseCalendar.Date normalize() {
		if (cdate == null) {
			BaseCalendar cal = getCalendarSystem(fastTime);
			cdate = (BaseCalendar.Date) cal.getCalendarDate(fastTime,
					TimeZone.getDefaultRef());
			return cdate;
		}

		// Normalize cdate with the TimeZone in cdate first. This is
		// required for the compatible behavior.
		if (!cdate.isNormalized()) {
			cdate = normalize(cdate);
		}

		// If the default TimeZone has changed, then recalculate the
		// fields with the new TimeZone.
		TimeZone tz = TimeZone.getDefaultRef();
		if (tz != cdate.getZone()) {
			cdate.setZone(tz);
			CalendarSystem cal = getCalendarSystem(cdate);
			cal.getCalendarDate(fastTime, cdate);
		}
		return cdate;
	}

	
	private final BaseCalendar.Date normalize(BaseCalendar.Date date) {
		int y = date.getNormalizedYear();
		int m = date.getMonth();
		int d = date.getDayOfMonth();
		int hh = date.getHours();
		int mm = date.getMinutes();
		int ss = date.getSeconds();
		int ms = date.getMillis();
		TimeZone tz = date.getZone();

		// If the specified year can't be handled using a long value
		// in milliseconds, GregorianCalendar is used for full
		// compatibility with underflow and overflow. This is required
		// by some JCK tests. The limits are based max year values -
		// years that can be represented by max values of d, hh, mm,
		// ss and ms. Also, let GregorianCalendar handle the default
		// cutover year so that we don't need to worry about the
		// transition here.
		if (y == 1582 || y > 280000000 || y < -280000000) {
			if (tz == null) {
				tz = TimeZone.getTimeZone("GMT");
			}
			GregorianCalendar gc = new GregorianCalendar(tz);
			gc.clear();
			gc.set(gc.MILLISECOND, ms);
			gc.set(y, m - 1, d, hh, mm, ss);
			fastTime = gc.getTimeInMillis();
			BaseCalendar cal = getCalendarSystem(fastTime);
			date = (BaseCalendar.Date) cal.getCalendarDate(fastTime, tz);
			return date;
		}

		BaseCalendar cal = getCalendarSystem(y);
		if (cal != getCalendarSystem(date)) {
			date = (BaseCalendar.Date) cal.newCalendarDate(tz);
			date.setNormalizedDate(y, m, d).setTimeOfDay(hh, mm, ss, ms);
		}
		// Perform the GregorianCalendar-style normalization.
		fastTime = cal.getTime(date);

		// In case the normalized date requires the other calendar
		// system, we need to recalculate it using the other one.
		BaseCalendar ncal = getCalendarSystem(fastTime);
		if (ncal != cal) {
			date = (BaseCalendar.Date) ncal.newCalendarDate(tz);
			date.setNormalizedDate(y, m, d).setTimeOfDay(hh, mm, ss, ms);
			fastTime = ncal.getTime(date);
		}
		return date;
	}
	
	private final BaseCalendar.Date getCalendarDate() {
		if (cdate == null) {
			BaseCalendar cal = getCalendarSystem(fastTime);
			cdate = (BaseCalendar.Date) cal.getCalendarDate(fastTime,
					TimeZone.getDefaultRef());
		}
		return cdate;
	}
	
	private static final BaseCalendar getCalendarSystem(long utc) {
		// Quickly check if the time stamp given by `utc' is the Epoch
		// or later. If it's before 1970, we convert the cutover to
		// local time to compare.
		if (utc >= 0
				|| utc >= GregorianCalendar.DEFAULT_GREGORIAN_CUTOVER
						- TimeZone.getDefaultRef().getOffset(utc)) {
			return gcal;
		}
		return getJulianCalendar();
	}

	private static final BaseCalendar getCalendarSystem(BaseCalendar.Date cdate) {
		if (jcal == null) {
			return gcal;
		}
		if (cdate.getEra() != null) {
			return jcal;
		}
		return gcal;
	}
	
	private void writeObject(ObjectOutputStream s) throws IOException {
		s.writeLong(getTimeImpl());
	}

	/**
	 * Reconstitute this object from a stream (i.e., deserialize it).
	 */
	private void readObject(ObjectInputStream s) throws IOException,
			ClassNotFoundException {
		fastTime = s.readLong();
	}
}
