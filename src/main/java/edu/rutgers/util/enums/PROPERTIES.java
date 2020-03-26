package edu.rutgers.util.enums;

/**
 * Application's global constant variables defined in app.properties
 */
public enum PROPERTIES {
	
	SVM_TRAIN("svm_train"),
	DEBUG_DROP_DB("wipe_db"),
	INITIALIZE_HISTORIC("initialize_historic"),
	HISTORIC_START("historic_from"),
	STOCK_UPDATE("stock_update"),
	DEBUG_LOG("debug_log"),
	DB_SCHEMA_FILE("db_schema"),
	LOGGER("logger"),
	DB_CONNECTOR("db_connector"),
	DB_CONNECTION("db_connection"),
	DB_NAME("db_name"),
	DB_USER("db_user"),
	DB_PASSWORD("db_password");
	
	private String strVal;
	private PROPERTIES(final String pstrVal) {
		strVal = pstrVal;
	}
	
	public String toString() {
		return strVal;
	}
}
