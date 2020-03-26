package edu.rutgers.beans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.rutgers.model.impl.DataManager;

public class User {
	
	private String userName;
	private String firstName;
	private String middleName;
	private String lastName;
	private String userId;
	private String userEmail;
	private String userPassword;
	private HashMap<String, Byte> userSymbols;
	
	public User() {
		userSymbols = new HashMap<String, Byte>();
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public void setFirstName(String firstname) {
		this.firstName = firstname;
	}
	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public void setSymbol(String symbol) {
		if(!userSymbols.containsKey(symbol)) {
			userSymbols.put(symbol,null);
		}
	}
	
	public List<Ticker> getFavoriteSymbols() {
		List<Ticker> syms = new ArrayList<Ticker>();
		for (String s : userSymbols.keySet()) {
			try {
				Ticker t = new Ticker();
				t=DataManager.getInstance().tickerExists(s);
				syms.add(t);
			} catch (Exception e) { }
			
		}
		return syms;
	}
	
	public void setFavoriteSymbols(List<Ticker> pSyms) {
		for(Ticker t : pSyms) {
			if(!this.userSymbols.containsKey(t.getTickerSymbol())) {
				this.userSymbols.put(t.getTickerSymbol(), null);
			}
		}
	}
	
	public String getUserName() { return this.userName ; }
	public String getFirstName() { return this.firstName; }
	public String getMiddleName() { return this.middleName; }
	public String getLastName() { return this.lastName; }
	public String getUserId() { return this.userId; }
	public String getUserEmail() { return this.userEmail; }
	public String getUserPassword() { return this.userPassword; }
	
	public void addFavoriteTicker(Ticker t) {
		if(!userSymbols.containsKey(t.getTickerSymbol())) {
			userSymbols.put(t.getTickerSymbol(), null);
		}
	}
	
	public void removeFavoriteTicker(Ticker t) {
		if(userSymbols.containsKey(t.getTickerSymbol())) {
			userSymbols.remove(t.getTickerSymbol());
		}
	}
	
}
