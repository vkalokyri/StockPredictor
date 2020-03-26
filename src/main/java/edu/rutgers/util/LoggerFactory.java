package edu.rutgers.util;

import edu.rutgers.util.enums.LOGGER_TYPE;
import edu.rutgers.util.enums.LOG_TYPE;
import edu.rutgers.util.enums.PROPERTIES;

/**
 * Provides factory method to retrieve the requested type of Logger.
 */

public class LoggerFactory {
	
	private static Logger instance;
	
	/**
	 * Factory method which returns the specified logger's instance.
	 * @param penmLoggerType
	 * @return
	 */
	public static Logger getLogger(final LOGGER_TYPE penmLoggerType) {
		switch(penmLoggerType) {
		case CONSOLE:
			LoggerFactory.instance = ConsoleLogger.getInstance();
			return LoggerFactory.instance;
		case TEXT:
			LoggerFactory.instance = TextLogger.getInstance();
			return LoggerFactory.instance;
		default:
			return null;
		}
	}
	
	/**
	 * If the logger was instantiated, then this will return the current application's logger, if not, null.
	 * @return Logger or null
	 */
	public static Logger getInstance() {
		return LoggerFactory.instance;
	}
	
	/**
	 * Implements Logger, logs to a text file specified in the app.properties file.
	 */
	private static class TextLogger extends Logger {

		private static Logger instance;
		
		public static Logger getInstance() {
			if(TextLogger.instance == null) {
				TextLogger.instance = new TextLogger();
			}
			return TextLogger.instance;
		}
		
		protected TextLogger() {
			// TODO - implement initialization of text properties here
		}
		
	}
	
	/**
	 * Implements Logger. Simply logs to console during the application's runtime.
	 */
	private static class ConsoleLogger extends Logger {

		private static Logger instance;
		
		public static Logger getInstance() {
			if(ConsoleLogger.instance == null) {
				ConsoleLogger.instance = new ConsoleLogger();
			}
			return ConsoleLogger.instance;
		}
		
		protected ConsoleLogger() {
			System.out.println(LOG_TYPE.GRAL.toString()+"Console log initialized");
		}
	}
	
	/**
	 * Permits logging messages to different sources depending on the instance invoked which implements this interface. 
	 */
	public static abstract class Logger {
		
		
		
		/**
		 * Logs a message with GENERAL default type.
		 * @param pstrMessage
		 */
		public void log(String pstrMessage) {
			System.out.println(Utilities.getDataStampString()+" - "+LOG_TYPE.GRAL.toString()+pstrMessage);
		}

		/**
		 * Logs a messages with the specified type.
		 * @param pstrMessage
		 * @param penmType
		 */
		public void log(String pstrMessage, LOG_TYPE penmType) {
			String header;
			switch(penmType) {
			case DEBUG:
				if(!(ConfigReader.getInstance().getBool(PROPERTIES.DEBUG_LOG))) return;
				header = LOG_TYPE.DEBUG.toString();
				break;
			case ERROR:
				header = LOG_TYPE.ERROR.toString();
				break;
			case FATAL_ERROR:
				header = LOG_TYPE.FATAL_ERROR.toString();
				break;
			default:
				header = LOG_TYPE.GRAL.toString();
			}
			System.out.println(Utilities.getDataStampString()+" - "+header+pstrMessage);
		}
	}
}
