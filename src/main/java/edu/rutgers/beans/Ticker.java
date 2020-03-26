package edu.rutgers.beans;

public class Ticker {
	
	private String tickerSymbol;
	private String tickerName;
	
	
	public Ticker(){
	}

	public Ticker(String tsymbol, String tname){
		this.tickerName=tname;
		this.tickerSymbol=tsymbol;
	}

	public String getTickerSymbol() {
		return tickerSymbol;
	}


	public void setTickerSymbol(String tickerSymbol) {
		this.tickerSymbol = tickerSymbol;
	}


	public String getTickerName() {
		return tickerName;
	}


	public void setTickerName(String tickerName) {
		this.tickerName = tickerName;
	}
	
	public String toString() {
		return this.tickerSymbol+" - "+this.tickerName;
	}
	

}
