package edu.rutgers.util.enums;

/**
 * All available views.
 *
 */
public enum PAGES {
	
	PORTAL("portal"),
	MAINPAGE("mainPage"),
	GETPRICE("getPrice");
	
	private final String strVal;
	
	PAGES(String pstrVal) {
		strVal = pstrVal;
	}
	
	public String toString() {
		return strVal;
	}
}
