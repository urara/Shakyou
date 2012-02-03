package com.shakyou;

import java.util.Date;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// DateShakyou date = new DateShakyou(1L);
		Date dateorg = new java.util.Date();
		DateShakyou date = new DateShakyou();
		System.out.println(date.toString() + " " + dateorg.toString() + " "
				+ System.currentTimeMillis());
		// System.out.println(date.toString());
	}

	public void testpubv() {
	}

	public String testpubS() {
		String a = "";
		return a;
	};

	void testppp(){}
	
	protected void testpro(){}
	
	private void testpri(){}
	
	private int aaa;
	public  int bbb;
	public static int ccc;
}
