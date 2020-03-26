package edu.rutgers.util.enums;

/**
 * Type of message to be logged.
 */
public enum LOG_TYPE {
	DEBUG("DEBUG: "),
	ERROR("ERROR: "),
	FATAL_ERROR("FATAL ERROR: "),
	GRAL("GENERAL: ");
	
	private String strVal;
	
	private LOG_TYPE(final String pstrMessage) {
		strVal = pstrMessage;
	}
	
	public String toString() {
		return strVal;
	}
}
