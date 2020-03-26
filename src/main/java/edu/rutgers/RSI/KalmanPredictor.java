package edu.rutgers.RSI;

import jama.Matrix;
import java.util.Random;
import jkalman.JKalman;

public class KalmanPredictor {
	
	public KalmanPredictor(){
	}
	
	/*
	 * Kalman Filter Class
	 * 
	 * This class will get an array of doubles, find the anomalies,
	 * Interpolates them, then applies the Kalman filter. The Kalman 
	 * filter is used to predict the next DAYS_TO_PREDICT samples as
	 * an array of doubles. Here we predict 10 samples (hardcoded)
	 * Number of input samples should be 32 here (otherwise an 
	 * exception error will be generated).
	 *
	 * USAGE:
	 *
	 *	KalmanPredictor kp = KalmanPredictor.GetInstance();
	 *		
	 *		double[] u = kp.DWaveletT(signal, 32);
	 *		double[] inter_signal = kp.interpolator(signal, u);
	 *		double[] pre = kp.KalmanFilter(inter_signal, 32);
	 * 
	 * Input is signal array of size "size"
	 * Output is predicted_samples which is an array of length DAYS_TO_PREDICT
	 * 
	 */
	
    public double[] KalmanFilter(double[] price, int size) {

    	int DAYS_TO_PREDICT = 10;
        double[] predictedP = new double[DAYS_TO_PREDICT];

		try {
    			
	        JKalman kalman = new JKalman(4, 2);
	
	        Random rand = new Random(System.currentTimeMillis() % 2011);
	        double x = 0;
	        double y = price[0];
	        
	        // constant velocity for time
	        double dx = 1;
	        double dy = rand.nextDouble();
	        
	        // initialization
	        Matrix s = new Matrix(4, 1); // state [x, y, dx, dy]        
	        Matrix c = new Matrix(4, 1); // corrected state [x, y, dx, dy]                
	        
	        Matrix m = new Matrix(2, 1); // measurement [x]
	        m.set(0, 0, x);
	        m.set(1, 0, y);
	
	        // transitions for x, y, dx, dy
	        double[][] tr = { {1, 0, 1, 0}, 
	                          {0, 1, 0, 1}, 
	                          {0, 0, 1, 0}, 
	                          {0, 0, 0, 1} };
	        kalman.setTransition_matrix(new Matrix(tr));
	        kalman.setError_cov_post(kalman.getError_cov_post().identity());
	
	        // Train loop
	        for (int i = 0; i < size; ++i) {
	                       
	            s = kalman.Predict();
	            
	            x = rand.nextGaussian();
	            y = rand.nextGaussian();
	            
	            m.set(0, 0, i);
	            m.set(1, 0, price[i]);
	
	            c = kalman.Correct(m);
	        }
	        
	        double y1 = price[size-1];
	        double y2 = price[size-1];
	                
	        for (int i = 0; i <DAYS_TO_PREDICT ; i++) {
	        	
	        	System.out.println("");
	            
	            // check state before
	            s = kalman.Predict();
	            
	            x = rand.nextGaussian();
	            y = rand.nextGaussian();
	            
	            // not sure if we should tune dy before correction
	            y1 = y2;
	            y2 = s.get(1, 0);
	            dy = y2 - y1;
	            
	            m.set(0, 0, m.get(0, 0) + dx);
	            m.set(1, 0, m.get(1, 0) + dy + rand.nextGaussian());
	
	            c = kalman.Correct(m);
	            
	            int toBeRounded = (int) (s.get(1, 0) * 100);
	            double rounded = (double) toBeRounded;
	            predictedP[i] = rounded/100;
	            
	        }
        
        	return predictedP;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return price;
    }
    

	/*
	 * Performing Discrete Wavelet Transform on input signal
	 * 
	 * Important: the length of signal array should be 2^n
	 * This piece of code has been written for n = 5 (size = 5)
	 */

    public double [] DWaveletT(double[] signal, int size){
    	
		double[] w = new double[size];
		
		try{
			int pow = (int) (Math.log(size) / Math.log(2));

			double[] dwt = DWT.forwardDwt(signal, Wavelet.Daubechies, 4, pow-1);	
			int[] dyad = DWT.dyad(pow-1);
			for (int k = dyad[0]; k <= dyad[dyad.length - 1]; k++) {
				w[k - 1] = dwt[k - 1];
			}
			w = DWT.inverseDwt(w, Wavelet.Daubechies, 4, pow-1);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return w;
	}
	
	public double [] interpolator(double[] signal, double[] w){
		
		double Threshold = 8.0;
		for(int i=5; i<30; i++){
			if(Math.abs(w[i]) > Threshold){
				signal[i] = (signal[i]+signal[i+2]) / 2;
			}
		}		
		return signal;
	}
	
	private static KalmanPredictor instance;
	
	public static KalmanPredictor GetInstance() {
		if(KalmanPredictor.instance == null) {
			try {
				KalmanPredictor.instance = new KalmanPredictor();
			} catch (Exception e) {
				System.out.println("FAILED TO START THE RSI");
			}
		}
		return KalmanPredictor.instance;
	}

//    public static void main(String[] args) {
//
//        try {
//        	
//			//------------------- CSV File Operation -----------------//
//			
//			String datafile = "/Users/rostami/Developer Tools/Java/workspace/JKalman/histdata.csv"; // the file name has been hard coded
//			BufferedReader br = null;
//			String line = "";
//			String seperator = ",";
//			String singel_price = "";
//			double[] price = new double[2000];		// Doesn't accept CSV files with more than 2000 lines
//			int counter = 0;
//
//			br = new BufferedReader(new FileReader(datafile));
//			while ((line = br.readLine()) != null) {
//				String[] line_ = line.split(seperator);
//				singel_price = line_[3].replaceAll("^\"|\"$", "");
//				price [counter++] = Double.parseDouble(singel_price);
//			}
//			br.close();
//			//------------------- CSV File Operation -----------------//
//			
//			double[] signal = new double[32];
//			int j=0;
//			for (int i=counter-32; i<counter; i++){
//				signal[j++] = price[i];
//			}
//			
//			double[] u = DWaveletT(signal, 32);
//			double[] inter_signal = interpolator(signal, u);
//			double[] pre = KalmanFilter(inter_signal, 32);
//			
//			for(j=0; j<10; j++){
//				System.out.println(pre[j]);
//			}
//        	
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//    }
}


