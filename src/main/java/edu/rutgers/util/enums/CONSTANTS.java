package edu.rutgers.util.enums;

public enum CONSTANTS {
	
	
	AVAILABLE_SYMBOLS("available_symbols"),
	USER_ID("userId"),
	USER_AUTHENTICATION_FAILED("user_authentication_failed"),
	USER_REGISTRATION_FAILED("user_registration_failed"),
	USER_REGISTRATION_SUCCESS("registration_successful");
	
	private String gValue;
	
	CONSTANTS(String pVal) {
		gValue = pVal;
	}
	
	public String toString() {
		return gValue;
	}

}
