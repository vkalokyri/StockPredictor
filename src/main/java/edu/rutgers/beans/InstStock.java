package edu.rutgers.beans;

import java.util.Date;

public class InstStock {
	
	public Ticker ticker;
	public Date entryDate;
	public double instPrice;
	public long volume;

	public Ticker getTicker() {
		return ticker;
	}
	public void setTicker(Ticker _Ticker) {
		ticker = _Ticker;
	}
	
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date _EntryDate) {
		entryDate = _EntryDate;
	}

	
	public double getInstPrice() {
		return instPrice;
	}
	public void setInstPrice(double _InstPrice) {
		instPrice = _InstPrice;
	}

	public long getVolume() {
		return volume;
	}
	public void setVolume(long _Volume) {
		volume = _Volume;
	}

	public String toString() {
		return "InstStock "+ticker.getTickerSymbol()+
			" "+entryDate+" "+instPrice+" "+volume;
	}

}
