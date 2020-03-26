package edu.rutgers.RSI;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;

import edu.rutgers.beans.*;


/*
 * Class RSI
 * 
 * When you want to calculate the RSI over a period (a series of
 * prices in day, hour, min, etc.) You need to do following lines:
 * 
 * RSI rsi = RSI.GetInstance();
 * double[] RSIvalues = rsi.myRSI(data, window);
 * 
 * where signal is an array of doubles (each double is a price) and
 * period is an integer which is number of samples. Based on some
 * references, the best choices for period are 9, 14, or 25
 * 
 */

public class RSI {

	public RSI() throws ParseException, IOException {
		
	}
        
	public double calculate(double[] pri_, int periodLength) {
		
		Stack<Averages> avgList;
		ArrayList<Price> prices;
		
		prices = new ArrayList<Price>();
		avgList = new Stack<Averages>();
		for(int i=0; i<periodLength; i++){
			prices.add(new Price(pri_[i]));
		}
		
		double value = 0;
		int pricesSize = prices.size();
		int lastPrice = pricesSize - 1;
		int firstPrice = lastPrice - periodLength + 1;

		double gains = 0;
		double losses = 0;
		double avgUp = 0;
		double avgDown = 0;

		double delta = prices.get(lastPrice).getClose()
			- prices.get(lastPrice - 1).getClose();
		gains = Math.max(0, delta);
		losses = Math.max(0, -delta);

		if (avgList.isEmpty()) {
			for (int bar = firstPrice + 1; bar <= lastPrice; bar++) {
				double change = prices.get(bar).getClose()
					- prices.get(bar - 1).getClose();
				gains += Math.max(0, change);
				losses += Math.max(0, -change);
			}
			avgUp = gains / periodLength;
			avgDown = losses / periodLength;
			avgList.push(new Averages(avgUp, avgDown));

		} else {

			Averages avg = avgList.pop();
			avgUp = avg.getAvgUp();
			avgDown = avg.getAvgDown();
			avgUp = ((avgUp * (periodLength - 1)) + gains) / (periodLength);
			avgDown = ((avgDown * (periodLength - 1)) + losses)
				/ (periodLength);
			avgList.add(new Averages(avgUp, avgDown));
		}
		value = 100 - (100 / (1 + (avgUp / avgDown)));

		return Math.round(value);
	}

	private class Averages {

		private final double avgUp;
		private final double avgDown;

		public Averages(double up, double down) {
			this.avgDown = down;
			this.avgUp = up;
		}

		public double getAvgUp() {
			return avgUp;
		}

		public double getAvgDown() {
			return avgDown;
		}
	}
    
    
	public static class Price {
		private final double close;

		public double getClose() {
			return close;
		}

		public Price(double close) {
			super();

			this.close = close;
		}
	}
	
	public double[] myRSI(List<HistStock> data, int window){
		double[] dataA = new double[data.size()];
		int i=0;
		for(HistStock hs : data) {
			dataA[i++] = hs.close;
		}
		return myRSIarray(dataA, i, window);
	}

	public double[] myRSIarray(double[] data, int size, int period){ //simple moving average
		
		double[] RSI_vector = new double[size-period+1];
		
		for(int window=0; window<size-period+1; window++){
			double[] signal = new double[period];
			int j=0;
			
			for (int i=window; i<window+period; i++){
				signal[j++] = data[i];
			}

			RSI rsi = RSI.GetInstance();
			RSI_vector[window] = rsi.calculate(signal,period);
			//System.out.println(window + "," + RSI_vector[window]);
		}
		
		return RSI_vector;
	}
       
	private static RSI instance;
	
	public static RSI GetInstance() {
		if(RSI.instance == null) {
			try {
				RSI.instance = new RSI();
			} catch (Exception e) {
				System.out.println("FAILED TO START THE RSI");
			}
		}
		return RSI.instance;
	}
    
	//    public static void main(String[] args) {
	//
	//        try {
	//        
	//  /*------------------- CSV File Operation -----------------*/
	//  
	//  String datafile = "/Users/rostami/Developer Tools/Java/workspace/JKalman/histdata.csv"; // the file name has been hard coded
	//  BufferedReader br = null;
	//  String line = "";
	//  String seperator = ",";
	//  String singel_price = "";
	//  double[] price = new double[2000];// Doesn't accept CSV files with more than 2000 lines
	//  int counter = 0;
	//
	//  br = new BufferedReader(new FileReader(datafile));
	//  while ((line = br.readLine()) != null) {
	//  String[] line_ = line.split(seperator);
	//  singel_price = line_[3].replaceAll("^\"|\"$", "");
	//  price [counter++] = Double.parseDouble(singel_price);
	//  }
	//  br.close();
	//  /*------------------- CSV File Operation -----------------*/
	//  
	//  int period = 25;// based on some references 9 and 14 days period are standard periods to calculate RSI
	//  
	//  RSI rsi = RSI.GetInstance();
	//  double[] RSIvalues = rsi.myRSIarray(price, counter, period);
	//  
	//System.out.println(RSIvalues[0] + "," + RSIvalues[counter-period]);
	//        
	//        } catch (Exception ex) {
	//            System.out.println(ex.getMessage());
	//        }
	//    }
}



