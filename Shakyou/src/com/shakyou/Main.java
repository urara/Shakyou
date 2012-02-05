package com.shakyou;

import java.security.AccessController;

import sun.security.action.GetPropertyAction;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// DateShakyou date = new DateShakyou(1L);
		java.util.Date dateorg = new java.util.Date();
		Date dateb = new Date();
		Date date = new Date();

		
		
		String country = AccessController.doPrivileged(new GetPropertyAction(
				"user.country"));
		String javaHome = AccessController.doPrivileged(new GetPropertyAction(
				"java.home"));
		//String zoneID = TimeZone.getSystemTimeZoneID(javaHome, country);

		System.out.println(country + " " + javaHome + " ");
		
		//System.out.println(AccessController.doPrivileged(new GetPropertyAction("java.home")));

		// System.out.println(date.after(dateb) + " "+
		// System.currentTimeMillis());
		System.out.println(date.toString());
		System.out.println(dateorg.toString());
	}

	public void testpubv() {
	}

	public String testpubS() {
		String a = "";
		return a;
	};

	void testppp() {
	}

	protected void testpro() {
	}

	private void testpri() {
	}

	private int aaa;
	public int bbb;
	public static int ccc;
}
