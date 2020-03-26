package edu.rutgers.config;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

import yahoofinance.histquotes.HistoricalQuote;
import edu.rutgers.SVM.mymain;
import edu.rutgers.beans.Ticker;
import edu.rutgers.model.api.ModelManager;
import edu.rutgers.model.impl.DataManager;
import edu.rutgers.stockdownloader.StockDownloaderTask;
import edu.rutgers.util.ConfigReader;
import edu.rutgers.util.LoggerFactory;
import edu.rutgers.util.LoggerFactory.Logger;
import edu.rutgers.util.Utilities;
import edu.rutgers.util.enums.LOGGER_TYPE;
import edu.rutgers.util.enums.LOG_TYPE;
import edu.rutgers.util.enums.PROPERTIES;

/**
 * Will run on app initialization in web container. Main objective is to instantiate all singletons which will provide facilities
 * across the applications.
 *
 */
public class Initializer implements WebApplicationInitializer {
	
	private static ModelManager modelManager;
	private static ConfigReader config;
	private static StockDownloaderTask stockDownloader;
	private static Logger gLogger;
	private static mymain myMain;
	
	/**
	 * Initializes singleton instances for properties, logging and data access.
	 */
	public void onStartup(ServletContext arg0) throws ServletException {
		try {
			config = ConfigReader.getInstance();
			LOGGER_TYPE loggerType;
			if(config.getStr(PROPERTIES.LOGGER).equalsIgnoreCase("console")) {
				loggerType = LOGGER_TYPE.CONSOLE;
			} else {
				loggerType = LOGGER_TYPE.TEXT;
			}
			// initialize logging
			gLogger = LoggerFactory.getLogger(loggerType);
			// initialize model manager
			modelManager = DataManager.getInstance();
			
			if(config.getBool(PROPERTIES.SVM_TRAIN)) {
				// start SVM
				myMain = mymain.GetInstance();
			}
			
			// start stock downloader
			stockDownloader = StockDownloaderTask.getInstance();
			
			if(config.getBool(PROPERTIES.INITIALIZE_HISTORIC)) {
				gLogger.log("INITIALIZING DB, please wait.... sit down, relax, put some music on....", LOG_TYPE.GRAL);
				initHistoricData();
			}
			
		} catch (Exception e) { // temporary catch all for initializer 
			System.out.println("FATAL ERROR: onStartup of Initializer: "+e.getMessage());
		}
	}
	
	private void initHistoricData() throws Exception {
		Calendar from = Utilities.getDateFromString(config.getStr(PROPERTIES.HISTORIC_START));
		Calendar to = Calendar.getInstance(); to.add(Calendar.DATE, -1); // yesterdays date
		List<Ticker> tickers = modelManager.getTickers();
		Iterator<Ticker> ticIter = tickers.iterator();
		HashMap<String,List<HistoricalQuote>> historicData = new HashMap<String,List<HistoricalQuote>>();
		while(ticIter.hasNext()) {
			Ticker t = ticIter.next();
			if(!modelManager.checkHistoricDataExists(t.getTickerName())) {
				historicData.put(t.getTickerSymbol(), stockDownloader.getHistoricQuote(t.getTickerSymbol(), from, to));
			}
		}
		for(String t : historicData.keySet()) {
			gLogger.log("Downloading historic data for: "+t, LOG_TYPE.GRAL);
			try {
				modelManager.saveHistoricalData(historicData.get(t));
			} catch (Exception e) {
				gLogger.log("Something went wrong downloading historic data for "+t, LOG_TYPE.ERROR);
			}
		}
	}

}
