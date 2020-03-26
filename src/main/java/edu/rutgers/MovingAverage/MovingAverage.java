package edu.rutgers.MovingAverage;

import edu.rutgers.beans.*;
import java.util.*;

//import java.io.FileReader;
//import java.io.IOException;
//import com.opencsv.CSVReader	
public class MovingAverage{	
/*	public static int countLines(String filename) throws IOException {
			int o = 0;
			    try {
			    	CSVReader reader = new CSVReader(new FileReader(filename));
					String [] nextLine;
					
					while((nextLine = reader.readNext()) != null) {
				    	 o = o+1;
					} 
			    }catch(Exception e)
					{
						e.printStackTrace();
					}
			    return o;
		}
		*/
	/*double[] ProcessFile(String filename) throws IOException
		{
		int NumLines = countLines(filename);
		double[] AllData = new double[NumLines];
		int tmp = 0;
			try
			{
				CSVReader reader = new CSVReader(new FileReader(filename));
				String [] nextLine;
			     while ((nextLine = reader.readNext()) != null) {		    	 
			         AllData[tmp]= Double.parseDouble(nextLine[3]);
			         tmp = tmp+1;

			     }
			} catch(Exception e)
			{
				e.printStackTrace();
			}
	return AllData;
	}
	*/

	public static double[] myAverage(List<HistStock> data, int window) //simple moving average
	{
		double[] dataA = new double[data.size()];
		int i=0;
		for(HistStock hs : data) {
			dataA[i++] = hs.close;
		}
		return myAverage(dataA,window);
	}
	public static double[] myEMAverage(List<HistStock> data, int window) //simple moving average
	{
		double[] dataA = new double[data.size()];
		int i=0;
		for(HistStock hs : data) {
			dataA[i++] = hs.close;
		}
		return myEMAverage(dataA,window);
	}

	public static double[] myAverage(double[] data, int window) //simple moving average
	{
		int range = data.length;
		double[] MA = new double[range-window+1];
		for (int j=window-1 ; j<range ; j++)
			{
				double tmp = 0;
				for (int i=j-window+1; i<j+1 ; i++)
					{
						tmp = tmp+data[i];        
					}
				MA[j-window+1] = tmp/window;
        
			}
		return MA;
    
	}

	public static double[] myEMAverage(double[] data, int window) //exponential moving average
	{
		double[] EMA = new double[data.length-window+1];
		double sum = 0;
		double multiplier = 2/(double)(window+1);
		for(int i=0 ; i< window ; i++)
			{
				sum = sum+data[i];        
			}
		EMA[0] = sum/window; //first term of EMA is basically the moving average for the first N terms
		for(int j=window ; j<data.length ; j++)
			{
				int index = j-window+1;
				EMA[index] = data[j] * multiplier + EMA[index-1] *(1-multiplier); 
			}
        
        
		return EMA;
	}

		
	/*public static void main(String [] args) throws IOException
		{
			//reading from the file:
			MovingAverage MA = new MovingAverage();
			String file = "/Users/parishad/term4/SoftwareEngineering/HW3/histdataYAHOO.csv";
			int size = countLines(file);
			double[] Data = new double[size];
			Data = MA.ProcessFile("/Users/parishad/term4/SoftwareEngineering/HW3/histdataYAHOO.csv");
			
			//Computing the Moving Average:
			double[] myMA = new double[size];
			myMA = MA.myAverage(Data);
			//Exponential Moving Average:
			double[] myEMA = new double[Data.length];
			myEMA = MA.myEMAverage(Data);			
				

	}
	*/
}
