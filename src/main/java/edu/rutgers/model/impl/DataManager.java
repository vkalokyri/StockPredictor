package edu.rutgers.model.impl;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import yahoofinance.histquotes.HistoricalQuote;
import yahoofinance.quotes.stock.StockQuote;
import edu.rutgers.beans.*;
import edu.rutgers.model.api.ModelManager;
import edu.rutgers.util.ConfigReader;
import edu.rutgers.util.LoggerFactory;
import edu.rutgers.util.LoggerFactory.Logger;
import edu.rutgers.util.Utilities;
import edu.rutgers.util.enums.LOG_TYPE;
import edu.rutgers.util.enums.PROPERTIES;
import edu.rutgers.util.exceptions.UserException.IllegalUserEmail;
import edu.rutgers.util.exceptions.UserException.IllegalUserName;


/**
 * Singleton implementation of ModelManager for data read/write
 */
public class DataManager implements ModelManager {
	
	private Logger gLogger;
	private ConfigReader confReader;
	
	private static String gstrConnector;
	private static String gstrConnection;
	private static String gstrDBName;
	private static String gstrDBUser;
	private static String gstrDBPassword;
	private static String gstrFullConnection;
	
	private static HashMap<String,Ticker> mAvailableSymbols;
	
	private DataManager() throws Exception {
		initializeManager();
	}
	
	private static DataManager instance;
	
	public void initializeManager() throws Exception {
		DataManager.mAvailableSymbols = new HashMap<String,Ticker>();
		gLogger = LoggerFactory.getInstance();
		confReader = ConfigReader.getInstance();
		Connection con = null;
		gstrConnector = confReader.getStr(PROPERTIES.DB_CONNECTOR);
		gstrConnection = confReader.getStr(PROPERTIES.DB_CONNECTION);
		gstrDBName = confReader.getStr(PROPERTIES.DB_NAME);
		gstrDBUser = confReader.getStr(PROPERTIES.DB_USER);
		gstrDBPassword = confReader.getStr(PROPERTIES.DB_PASSWORD);
		gstrFullConnection = gstrConnection + "/" + gstrDBName;
		try {
			Class.forName(gstrConnector);
			con = DriverManager.getConnection(gstrConnection,gstrDBUser,gstrDBPassword);
		} catch (Exception e) {
			gLogger.log("FAILED TO INITIALIZE DataManager - "+e.getMessage(), LOG_TYPE.FATAL_ERROR);
		} finally {
			if(con != null) {
				con.close();
				gLogger.log("DB Connection successfully tested and closed on startup", LOG_TYPE.DEBUG);
			}
			else {
				gLogger.log("FAILED TO INITIALIZE DataManager", LOG_TYPE.FATAL_ERROR);
			}
		}
		gLogger.log("Initializing model...", LOG_TYPE.DEBUG);
		initializeModel();
	}
	
	/**
	 * Returns a single instance of DataManager
	 * @return DataManager singleton instance
	 * @throws Exception 
	 */
	public static DataManager getInstance() throws Exception {
		if(DataManager.instance == null) {
			DataManager.instance = new DataManager();
		}
		return DataManager.instance;
	}
	
	/**
	 * Returns a single connection to the MySQL instance specified in the app.properties file
	 * @return
	 */
	public Connection borrowConnection() {
		try {
			return DriverManager.getConnection(gstrFullConnection,gstrDBUser,gstrDBPassword);
		} catch (Exception e) {
			gLogger.log("Connection couldn't be borrowed, unavailable.", LOG_TYPE.FATAL_ERROR);
			return null;
		}
	}

	public void initializeModel() throws Exception {
		boolean dropDB = ConfigReader.getInstance().getBool(PROPERTIES.DEBUG_DROP_DB);
		String schemaFile = confReader.getStr(PROPERTIES.DB_SCHEMA_FILE);
		List<String> commands = Utilities.getSqlFromFile(schemaFile);
		Connection c = DriverManager.getConnection(gstrConnection, gstrDBUser, gstrDBPassword);
		Iterator<String> it = commands.iterator();
		String curStatement = "";
		// THIS IS SOOO DANGEROUS IT HURTS, but in he end it is a project so be careful
		if(dropDB) {
			try {
				Statement s = c.createStatement();
				s.execute("DROP DATABASE STOCKPRED");
				s.close();
			} catch (Exception e) {
				gLogger.log("Dropping database failed: "+e.getMessage(), LOG_TYPE.ERROR);
			}
		}
		while(it.hasNext()) {
			try {
			curStatement = (String) it.next();
			Statement statement = c.createStatement();
			statement.execute(curStatement);
			statement.close();
			} catch (SQLException e) {
				gLogger.log("SQL EXCEPTION ON STARTUP - "+e.getMessage(), LOG_TYPE.ERROR);
			}
		}
		c.close();
	}

	public User getUserById(String pId) throws Exception {
		User user = null;
		String query = "SELECT * FROM users WHERE ";
		if(pId.contains("@")) {
			query += "email='"+pId+"';";
		} else {
			query += "userID='"+pId+"';";
		}	
		Connection c = null;
		try {
			c = borrowConnection();
			Statement s = c.createStatement();
			s.execute(query);
			ResultSet results = s.getResultSet();
			if(results.next()) {
				user = new User();
				user.setUserName(results.getString("userID"));
				user.setUserPassword(results.getString("password"));
				user.setUserEmail(results.getString("email"));
				user.setFirstName(results.getString("fname"));
				user.setLastName(results.getString("lname"));
				user.setFavoriteSymbols(this.getUserFavoriteTickers(user.getUserName()));
			}
		} catch(Exception e) { 
			gLogger.log("Failed retrieving user for: "+pId+" - " + e.getMessage(), LOG_TYPE.ERROR);
		} 
		finally {
			if(c!=null) c.close();
		}
		return user;
	}

	public void saveStockQuote(Map<String, StockQuote> stockData)
			throws Exception {
		
		Connection c = null;
		Date dt = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currentTime = sdf.format(dt);
		for (Map.Entry<String, StockQuote> entry : stockData.entrySet()) {
			String key = entry.getKey();
			StockQuote v = entry.getValue();
			String query = "INSERT INTO instData (tsymbol, entryDate, instPrice, volume) VALUES ('"+ key +"', '" + currentTime +"', '"+ v.getPrice()+ "', '"+ v.getVolume()+ "');";
			System.out.println(query);
			try {
				c = borrowConnection();
				Statement s = c.createStatement();
				s.execute(query);
			}catch (Exception e){
				gLogger.log("Failed storing real-time data for: "+key+" stock - " + e.getMessage(), LOG_TYPE.ERROR);
			} 
			finally {
				if(c!=null) c.close();
			}
		}
	}

	public void saveHistoricalData(List<HistoricalQuote> histQuoteList)
			throws Exception {
		Connection c = null;

		for (HistoricalQuote quote : histQuoteList) {
			String tsymbol = quote.getSymbol();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			String date = sdf.format(quote.getDate().getTime());
			String openPrice = quote.getOpen().toPlainString();
			String closePrice = quote.getClose().toPlainString();
			String minPrice = quote.getLow().toPlainString();
			String maxPrice = quote.getHigh().toPlainString();
			long volume = quote.getVolume();
			String query = "INSERT INTO histData (tsymbol, entryDate, openPrice, closePrice, minPrice, maxPrice, volume) VALUES ('"+ tsymbol +"', '" + date +"', '"+ openPrice + "', '"+ closePrice + "', '"+ minPrice + "', '"+ maxPrice + "', '"+ volume + "');";
			System.out.println(query);
			try {
				c = borrowConnection();
				Statement s = c.createStatement();
				s.execute(query);
			}catch (Exception e){
				gLogger.log("Failed storing historical data for: "+tsymbol+" stock - " + e.getMessage(), LOG_TYPE.ERROR);
			} 
			finally {
				if(c!=null) c.close();
			}
		}
	}
	
	public static List<String> getAvailableSymbols() {
		List<String> syms = new ArrayList<String>();
		for(String s : DataManager.mAvailableSymbols.keySet()) {
			syms.add(DataManager.mAvailableSymbols.get(s).getTickerSymbol()+" - "+DataManager.mAvailableSymbols.get(s).getTickerName());
		}
		return syms;
	}

	public static Collection<Ticker> getAvailableTickers() {
		return DataManager.mAvailableSymbols.values();
	}


	public List<InstStock> getStockQuotes(Ticker t, Date beginT, Date endT)
			throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginTime = sdf.format(beginT);
		String endTime = sdf.format(endT);

		String query = "SELECT * FROM instData i WHERE i.tSymbol='"+t.getTickerSymbol()+"' AND i.entryDate between \""+beginTime+"\" AND \""+endTime+"\"";

		gLogger.log("Running "+query);
		List<InstStock> stockList = new ArrayList<InstStock>();
		Connection c = null;
		try {
			c = borrowConnection();
			Statement s = c.createStatement();
			s.execute(query);
			ResultSet results = s.getResultSet();
			gLogger.log("Resultset obtained");
			while(results.next()) {
				InstStock sq = new InstStock();
				sq.ticker = t;
				sq.instPrice = results.getDouble("instPrice");
				sq.volume = results.getLong("volume");
				sq.entryDate = results.getDate("entryDate");
				stockList.add(sq);
			}
		} catch(Exception e) { 
			gLogger.log("Failed retrieving stocks from the database: - " + e.getMessage(), LOG_TYPE.ERROR);
		}
		finally {
			if(c!=null) c.close();
		}
		return stockList;
	}

	public List<HistStock> getHistQuotes(Ticker t, Date beginT, Date endT) throws Exception {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String beginTime = sdf.format(beginT);
		String endTime = sdf.format(endT);

		String query = "SELECT * FROM histData i WHERE i.tsymbol='"+t.getTickerSymbol()+"' AND i.entryDate between \""+beginTime+"\" AND \""+endTime+"\"";

		gLogger.log("Running "+query);
		List<HistStock> stockList = new ArrayList<HistStock>();
		Connection c = null;
		try {
			c = borrowConnection();
			Statement s = c.createStatement();
			s.execute(query);
			ResultSet results = s.getResultSet();
			gLogger.log("Resultset obtained");
			while(results.next()) {
				HistStock sq = new HistStock();
				sq.ticker = t;
				sq.open = results.getDouble("openPrice");
				sq.close = results.getDouble("closePrice");
				sq.max = results.getDouble("maxPrice");
				sq.min = results.getDouble("minPrice");
				sq.volume = results.getLong("volume");
				sq.entryDate = results.getDate("entryDate");
				stockList.add(sq);
			}
		} catch(Exception e) { 
			gLogger.log("Failed retrieving stocks from the database: - " + e.getMessage(), LOG_TYPE.ERROR);
		}
		finally {
			if(c!=null) c.close();
		}
		return stockList;
	}


	public void addTicker(String tsymbol, String tname) throws Exception {
		Connection c = null;
		String query = "INSERT IGNORE INTO ticker (tsymbol,tname) VALUES ('"+ tsymbol +"','"+ tname +"');";
		try {
			c = borrowConnection();
			Statement s = c.createStatement();
			s.execute(query);
			if(!DataManager.mAvailableSymbols.containsKey(tsymbol)) {
				DataManager.mAvailableSymbols.put(tsymbol, new Ticker(tsymbol,tname));
			}
		} catch(Exception e) { 
			gLogger.log("Failed inserting new ticker: "+tname+" - " + e.getMessage(), LOG_TYPE.ERROR);
		} 
		finally {
			if(c!=null) c.close();
		}
		gLogger.log("New ticket "+tname+" added. ", LOG_TYPE.DEBUG);
	}

	public Ticker getTicker(String symbol) throws SQLException{
		String query = "SELECT * FROM ticker where tsymbol='"+ symbol+"'";
		Connection c = null;
		Ticker ticker = new Ticker();
		try {
			c = borrowConnection();
			Statement s = c.createStatement();
			s.execute(query);
			ResultSet results = s.getResultSet();
			while(results.next()) {
				ticker.setTickerName(results.getString("tname"));
				ticker.setTickerSymbol(results.getString("tsymbol"));
				DataManager.mAvailableSymbols.put(ticker.getTickerSymbol(),ticker);
			}
		} catch(Exception e) { 
			gLogger.log("Failed retrieving tickers from the database: - " + e.getMessage(), LOG_TYPE.ERROR);
		} 
		finally {
			if(c!=null) c.close();
		}
		return ticker;	
	}
	
	public List<Ticker> getTickers() throws Exception {
		List<Ticker> tickerList = new ArrayList<Ticker>();
		String query = "SELECT * FROM ticker;";
		Connection c = null;
		try {
			c = borrowConnection();
			Statement s = c.createStatement();
			s.execute(query);
			ResultSet results = s.getResultSet();
			while(results.next()) {
				Ticker ticker = new Ticker();
				ticker.setTickerName(results.getString("tname"));
				ticker.setTickerSymbol(results.getString("tsymbol"));
				tickerList.add(ticker);
				DataManager.mAvailableSymbols.put(ticker.getTickerSymbol(),ticker);
			}
		} catch(Exception e) { 
			gLogger.log("Failed retrieving tickers from the database: - " + e.getMessage(), LOG_TYPE.ERROR);
		} 
		finally {
			if(c!=null) c.close();
		}
		return tickerList;	
	}

	public boolean checkHistoricDataExists(String pTicker) throws Exception {
		String query = "SELECT * FROM histData WHERE tsymbol='"+pTicker+"';";
		Connection c = borrowConnection();
		Statement s = c.createStatement();
		ResultSet set = s.executeQuery(query);
		boolean exists = false;
		if(set.next()) {
			exists = true;
		}
		s.close(); c.close();
		return exists;
	}

	public void registerNewUser(User pUser) throws Exception {
		if(getUserById(pUser.getUserEmail()) != null) {
			throw new IllegalUserEmail();
		} else if (getUserById(pUser.getUserName()) != null) {
			throw new IllegalUserName();
		} else {
			saveUser(pUser);
		}
	}

	public void saveUser(User pUser) throws Exception {
		Connection c = borrowConnection();
		String query = "INSERT INTO users (userId,fname,lname,email,password) VALUES (?,?,?,?,?)";
		PreparedStatement s = c.prepareStatement(query);
		s.setString(1, pUser.getUserName());
		s.setString(2, pUser.getFirstName());
		s.setString(3, pUser.getLastName());
		s.setString(4, pUser.getUserEmail());
		s.setString(5, pUser.getUserPassword());    
		s.execute();
		s.close();
		c.close();
	}

	
	public Ticker tickerExists(String pParam) {
		try {
			Ticker pT=null;
			if(pParam == null || pParam.length() == 0) return null;
			List<Ticker> tickers = getTickers();
			Iterator<Ticker> it = tickers.iterator();
			while(it.hasNext()) {
				Ticker t = it.next();
				gLogger.log("TickerInDataManager:"+t.getTickerName());
				if(t.getTickerName().equalsIgnoreCase(pParam) || t.getTickerSymbol().equalsIgnoreCase(pParam)) {
					pT = t;
					gLogger.log("tickerExists in DataManager", LOG_TYPE.ERROR);
					return pT;
				}
			}
		} catch (Exception e) {
			gLogger.log("tickerExists in DataManager", LOG_TYPE.ERROR);
		}
		return null;
	}

	public List<InstStock> getDailyClosingPrices(Ticker pTicker) throws Exception {
		Connection c = borrowConnection();
		String query = "select entryDate, closePrice from histData where tsymbol='"+pTicker.getTickerSymbol()+"' ORDER BY entryDate DESC";
		gLogger.log(query);
		List<InstStock> prices = new ArrayList<InstStock>();
		try {
		PreparedStatement s = c.prepareStatement(query);
		//s.setString(1, pTicker.getTickerSymbol());
		ResultSet res = s.executeQuery();
		while(res.next()) {
			InstStock stock = new InstStock();
			stock.setTicker(pTicker);
			stock.setEntryDate(res.getDate("entryDate"));
			stock.setInstPrice(res.getDouble("closePrice"));
			prices.add(stock);
		}
		} catch (Exception e) { }
		finally { if(c != null) c.close(); }
		return prices;
	}

	public List<InstStock> getDailyClosingPrices(Ticker pTicker,
			Calendar pFrom, Calendar pTo) {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Ticker> getUserFavoriteTickers(String pUserId) throws Exception {
		Connection c = borrowConnection();
		List<Ticker> tickers = new ArrayList<Ticker>();
		try {
			String query = "select tsymbol from userTickers where userID=?";
			PreparedStatement s = c.prepareStatement(query);
			s.setString(1, pUserId);
			ResultSet res = s.executeQuery();
			while(res.next()) {
				tickers.add(new Ticker(res.getString("tsymbol"), ""));
			}
		} catch (Exception e) { }
		finally { if(c != null) c.close(); }
		
		return tickers;
	}

	public void addUserFavoriteTicker(User u, Ticker t) throws Exception {
		Connection c = borrowConnection();
		String query = "insert into userTickers values (?,?);";
		try {
			PreparedStatement s = c.prepareStatement(query);
			s.setString(1, u.getUserName());
			s.setString(2, t.getTickerSymbol());
			s.execute();
			u.addFavoriteTicker(t);
		} catch (Exception e) { gLogger.log("Couldn't add ticker to user's favs", LOG_TYPE.ERROR); }
		finally { if(c != null) c.close(); }
		
	}

	public void removeUserFavoriteTicker(User u, Ticker t) throws Exception {
		Connection c = borrowConnection();
		String query = "delete from userTickers where userID=? and tsymbol=?";
		try {
			PreparedStatement s = c.prepareStatement(query);
			s.setString(1, u.getUserName());
			s.setString(2, t.getTickerSymbol());
			s.execute();
			u.removeFavoriteTicker(t);
		} catch (Exception e) { gLogger.log("Couldn't remove ticker from user's favs", LOG_TYPE.ERROR); }
		finally { if(c != null) c.close(); }
	}
	
}
