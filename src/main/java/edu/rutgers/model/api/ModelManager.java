package edu.rutgers.model.api;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.stock.StockQuote;
import edu.rutgers.beans.*;


/**
 * API for data access. To be implemented by any DAO despite of the architecture.
 */
public interface ModelManager {
	
	/**
	 * Initial contact to DB system for health of connection and schema check.
	 * @throws Exception
	 */
	public void initializeManager() throws Exception;
	
	/**
	 * TO be run on app's startup, will create or check schema's integrity
	 * @throws Exception
	 */
	public void initializeModel() throws Exception;
	
	/**
	 * Retrieves a unique instance of a user identified either by email or user name
	 * @param pId
	 * @return User instance
	 * @throws Exception
	 */
	public User getUserById(final String pId) throws Exception;
	
	/**
	 * Adds a new ticker in the db by given its ticker symbol and its ticker name
	 * @param tsymbol
	 * @param tname
	 * @throws Exception
	 */
	public void addTicker(String tsymbol, String tname) throws Exception;
	
	/**
	 * Gets all the tickers stored in the db
	 * @return list of tickers
	 * @throws Exception
	 */
	public List<Ticker> getTickers() throws Exception;
	
	public List<InstStock> getStockQuotes(Ticker t, Date begin, Date end) throws Exception;
	public List<HistStock> getHistQuotes(Ticker t, Date begin, Date end) throws Exception;

	/**
	 * Saves the real-time stock data in the database
	 * @param stockData
	 * @throws Exception
	 */
	public void saveStockQuote(Map<String, StockQuote> stockData) throws Exception;
	
	/**
	 * Saves the historical data of the given list of quotes
	 * @param histQuoteList
	 * @throws Exception
	 */
	public void saveHistoricalData(List<HistoricalQuote> histQuoteList) throws Exception;
	
	/**
	 * Check if data exist in the historic set for the specified ticker.
	 * @param pTicker
	 * @return
	 * @throws Exception
	 */
	public boolean checkHistoricDataExists(String pTicker) throws Exception;
	
	
	/**
	 * Registers a new user in the database
	 * @param pUser
	 * @return
	 * @throws Exception
	 */
	public void registerNewUser(User pUser) throws Exception;
	
	/**
	 * Adds user bean to DB
	 * @param pUser
	 * @throws Exception
	 */
	public void saveUser(User pUser) throws Exception;
	
	/**
	 * Matches a ticker's name or symbol against a search query
	 * @param pParam
	 * @return
	 */
	public Ticker tickerExists(String pParam);
	
	
	/**
	 * Retrieves a list of prices for the specified symbol - all information available
	 * @param pTicker
	 * @return
	 */
	public List<InstStock> getDailyClosingPrices(Ticker pTicker) throws Exception;
	
	/**
	 * Retrieves a list of prices for the specified symbol within a ranged dates
	 * @param pTicker
	 * @return
	 */
	public List<InstStock> getDailyClosingPrices(Ticker pTicker, Calendar pFrom, Calendar pTo) throws Exception;

	public Ticker getTicker(String tickerSymbol) throws Exception;
	
	/**
	 * Returns a list of all tickers added by a user
	 * @param pUserId
	 * @return
	 * @throws Exception
	 */
	public List<Ticker> getUserFavoriteTickers(String pUserId) throws Exception;
	
	/**
	 * Add a symbol to the user's favorite list
	 * @param u
	 * @param t
	 * @throws Exception
	 */
	public void addUserFavoriteTicker(User u, Ticker t) throws Exception;
	
	/**
	 * Removes a symbol from the user's favs list
	 * @param u
	 * @param t
	 * @throws Exception
	 */
	public void removeUserFavoriteTicker(User u, Ticker t) throws Exception;
	
}
