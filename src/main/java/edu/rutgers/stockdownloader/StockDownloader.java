package edu.rutgers.stockdownloader;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.histquotes.Interval;
import yahoofinance.quotes.stock.StockQuote;
import edu.rutgers.util.LoggerFactory;
import edu.rutgers.util.LoggerFactory.Logger;
import edu.rutgers.util.enums.LOG_TYPE;

public class StockDownloader {
	
	private final Semaphore tickersSem;
	private List <String> tickers;
	private boolean dirty;
	private Map<String, Stock> stocks;
	private Logger gLogger;
	
	public StockDownloader() {
		tickersSem = new Semaphore(1);
		try {
			tickersSem.acquire();
			gLogger = LoggerFactory.getInstance();
			gLogger.log("StockDownloader initialized", LOG_TYPE.DEBUG);
		} catch (java.lang.InterruptedException ignore) {
			System.err.println("ERROR could not acquire semaphore");
		} catch (Exception e) {
			gLogger.log("Something went wrong initializing StockDownloader");
		}
		tickers = new ArrayList<String>();
		dirty = true;
		tickersSem.release();
	};

	public void addTicker(String ticker) {
		try {
			tickersSem.acquire();
		} catch (java.lang.InterruptedException ignore) {
			System.err.println("ERROR could not acquire semaphore");
		};
		dirty = true;
		tickers.add(ticker);
		tickersSem.release();
	};

	public Map<String,StockQuote> updateStocks() {
		try {
			tickersSem.acquire();
		} catch (java.lang.InterruptedException ignore) {
			System.err.println("ERROR could not acquire semaphore");
		};
		if(dirty) {
			String[] symbols = new String[tickers.size()];
			tickers.toArray(symbols);
			stocks = YahooFinance.get(symbols);
			dirty = false;
		}
		tickersSem.release();

		Map<String,StockQuote> map = new HashMap<String,StockQuote>();
		for (Map.Entry<String, Stock> entry : stocks.entrySet()) {
			String key = entry.getKey();
			Stock value = entry.getValue();
			StockQuote q = value.getQuote(true);
			if(q==null) {
				System.err.println("ERROR could not get quote "+key);
			} else {
				map.put(key,q);
			}
		}
		return map;
	};

	public List<HistoricalQuote> getHistoricalQuotes(String ticker, Calendar from, Calendar to) {
		gLogger.log("Ticker "+ticker);
		Stock stock = YahooFinance.get(ticker);
		return stock.getHistory(from, to, Interval.DAILY);
	};
};
