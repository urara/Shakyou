package com.shakyou;
import sun.util.calendar.BaseCalendar.Date;


public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		DateShakyou date = new DateShakyou();
		java.util.Date dateorg = new java.util.Date();
		System.out.println(date.toString() + " " + dateorg.toString() + " " + System.currentTimeMillis());
	}

}
