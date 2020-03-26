package edu.rutgers.stockdownloader;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.stock.StockQuote;
import edu.rutgers.beans.Ticker;
import edu.rutgers.model.api.ModelManager;
import edu.rutgers.model.impl.DataManager;
import edu.rutgers.util.LoggerFactory;
import edu.rutgers.util.LoggerFactory.Logger;
import edu.rutgers.util.enums.LOG_TYPE;

/**
 * This class extends TimerTask and is supposed to be run as a separate thread, since it implements the Runnable interface. 
 * This class will trigger the update of stock values from the Yahoo! Finance website.
 * 
 */
public class StockDownloaderTask extends TimerTask {
	
	private StockDownloader sd;
	private static StockDownloaderTask instance;
	private ModelManager modelManager;
	private Logger gLogger;
	
	public static StockDownloaderTask getInstance() {
		if(StockDownloaderTask.instance == null)
			StockDownloaderTask.instance = new StockDownloaderTask();
		return StockDownloaderTask.instance;
	}
	
	private StockDownloaderTask() {

		try {
			gLogger = LoggerFactory.getInstance();
			modelManager = DataManager.getInstance();
			gLogger.log("StockDownloaderTask initialized", LOG_TYPE.DEBUG);
			sd = new StockDownloader();
			
			List<Ticker> tickerList = modelManager.getTickers();
			for (Ticker t: tickerList){
				sd.addTicker(t.getTickerSymbol());
			}
			
		} catch (Exception e) {
			gLogger.log("Error initializing StockDownloaderTask - "+e.getMessage(), LOG_TYPE.FATAL_ERROR);
		}
	};

	public void run() {
		try {
			Map<String, StockQuote> stockData = sd.updateStocks();
			modelManager.saveStockQuote(stockData);
		} catch (Exception e) {
			gLogger.log("Couldn't go online to fetch stock data", LOG_TYPE.ERROR);
		}
	};
	
	/**
	 * Returns a List of historicquotes for the specified ticker within the range dates
	 * @param pTicker
	 * @param pFrom
	 * @param pTo
	 * @return list
	 */
	public List<HistoricalQuote> getHistoricQuote(String pTicker, Calendar pFrom, Calendar pTo) {
		return this.sd.getHistoricalQuotes(pTicker, pFrom, pTo);
	}
}
