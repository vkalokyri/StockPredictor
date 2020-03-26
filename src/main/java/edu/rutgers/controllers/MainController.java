package edu.rutgers.controllers;

import java.io.IOException;

import edu.rutgers.MovingAverage.*;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import libsvm.svm_model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import edu.rutgers.RSI.RSI;
import edu.rutgers.RSI.KalmanPredictor;
import edu.rutgers.RSI.RSI;
import edu.rutgers.SVM.mymain;
import edu.rutgers.beans.*;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;
import yahoofinance.histquotes.HistoricalQuote;
import edu.rutgers.model.api.ModelManager;
import edu.rutgers.model.impl.DataManager;
import edu.rutgers.stockdownloader.StockDownloaderTask;
import edu.rutgers.util.ConfigReader;
import edu.rutgers.util.LoggerFactory;
import edu.rutgers.util.Utilities;
import edu.rutgers.util.LoggerFactory.Logger;
import edu.rutgers.util.enums.CONSTANTS;
import edu.rutgers.util.enums.LOG_TYPE;
import edu.rutgers.util.enums.PAGES;
import edu.rutgers.util.enums.PROPERTIES;
import edu.rutgers.util.exceptions.UserException;
import edu.rutgers.util.exceptions.UserException.IllegalUserEmail;

/**
 * Applications main controller. Its main function is to redirect traffic of requests to proper handlers and manage model for setting up views.
 *
 */
@Controller
public class MainController {
	
	HashMap<String,User> gLoggedSessions;
	
	Logger gLogger;
	ModelManager modelManager;
	StockDownloaderTask stockDownloaderTask;
	
	public MainController() {
		gLogger = LoggerFactory.getInstance();
		try {
			
			gLoggedSessions = new HashMap<String,User>();
			
			modelManager = DataManager.getInstance();
			stockDownloaderTask = StockDownloaderTask.getInstance();
		} catch (Exception e) {
			gLogger.log("Failed to initialized model manager for controller", LOG_TYPE.FATAL_ERROR);
		}
		gLogger.log("MainController initialized", LOG_TYPE.DEBUG);
	}
	
	/**
	 * Temporary test method for application mapped to the root of the app.
	 * @return ModelAndView
	 */
	@RequestMapping(value = "/")
	public void handleEntry(HttpServletRequest req, HttpServletResponse res) {
		// handle authentication, if fails, redirect to /portal
		try {
			if(req.getSession().getAttribute(CONSTANTS.USER_ID.toString()) != null) {
				// do something
			} else {
				req.getRequestDispatcher("/portal").forward(req, res);
			}
		} catch (Exception e) {
			gLogger.log("Failed to handle root request - "+e.getMessage());
		}
	}
	
	@RequestMapping(value="/logout", method=RequestMethod.POST)
	public ModelAndView doLogout(HttpSession pSession) {
		ModelAndView model = new ModelAndView(PAGES.PORTAL.toString());
		gLoggedSessions.remove(pSession.getId());
		return model;
	}
	
	/**
	 * Handles authentication request
	 * @param req
	 * @return ModelAndView for spring
	 */
	@RequestMapping(value = "/authenticate", method=RequestMethod.POST)
	public ModelAndView doAuthentication(User pUser, HttpSession pSession) {
		ModelAndView model = new ModelAndView();
		gLogger.log("Handling POST request from /portal", LOG_TYPE.DEBUG);
		/* Handle Authentication/Registration here */
		try {
			
			model.addObject(CONSTANTS.AVAILABLE_SYMBOLS.toString(), DataManager.getAvailableSymbols());
			if (gLoggedSessions.containsKey(pSession.getId())) {
				model.setViewName(PAGES.MAINPAGE.toString());
				addUserInformation(pSession, model);
			} else if(pUser != null) {
				User user = modelManager.getUserById(pUser.getUserName());
				// TODO this should redirect to user home page
				if(pUser.getUserPassword().equals(user.getUserPassword())) {

					// add user to session
					handleSession(pSession, user);
					addUserInformation(pSession, model);
					
					// redirect to home
					model.setViewName(PAGES.MAINPAGE.toString());
					
				} else {	
					model.setViewName(PAGES.PORTAL.toString());
					gLogger.log("Authentication failed for: "+pUser.getUserName());	
					model.setViewName(PAGES.PORTAL.toString());
					model.addObject(CONSTANTS.USER_AUTHENTICATION_FAILED.toString(), "Authentication failed");
				}
			}
			model.addObject("dashboard_data", doGetListCompanies());
			
		} catch (Exception e) {
			gLogger.log("Authentication failed due to system problem - " + e.getMessage(), LOG_TYPE.FATAL_ERROR);
		}
		return model;
	}
	
	private void handleSession(HttpSession pSession, User pUser) {
		
		// record session
		if(!gLoggedSessions.containsKey(pSession.getId())) {
		} else {
			gLoggedSessions.remove(pSession.getId());
		}
		gLoggedSessions.put(pSession.getId(),pUser);
		gLogger.log("Session "+pSession.getId()+" successfully recorded for user: "+pUser.getUserName());
		// TODO - other logic goes here (for example, check that a session exists for  user and clean it up, etc.)	
	}
	
	/**
	 * Handle registration request
	 * @param pUser
	 * @return ModelAndView for spring
	 */
	@RequestMapping(value = "/register", method=RequestMethod.POST)
	public ModelAndView doRegistration(User pUser, HttpSession pSession) {
		ModelAndView model = new ModelAndView(PAGES.PORTAL.toString());
		try {
			modelManager.registerNewUser(pUser);
			model.addObject(CONSTANTS.USER_REGISTRATION_SUCCESS.toString(), "Registration complete! Please log in");
			handleSession(pSession, pUser);
			addUserInformation(pSession, model);
			model.setViewName(PAGES.PORTAL.toString());
		} catch (UserException e) {
			String msg = "";
			if(e instanceof IllegalUserEmail) {
				msg = "Invalid user email, please try another one";
			} else msg = "User name taken, please try another one";
			model.addObject(CONSTANTS.USER_REGISTRATION_FAILED.toString(), msg);
		} catch (Exception e) {
			gLogger.log("System failure on registration", LOG_TYPE.FATAL_ERROR);
		}
		return model;
	}
	
	/**
	 * Main login interface for any unidentified sessions in a request
	 * @return
	 */
	@RequestMapping(value = "/portal", method=RequestMethod.GET)
	public ModelAndView doPortal(HttpSession pSession) {
		ModelAndView model = new ModelAndView(PAGES.PORTAL.toString());
		addUserInformation(pSession, model);
		model.addObject(CONSTANTS.AVAILABLE_SYMBOLS.toString(), DataManager.getAvailableSymbols());
		return model;
	}

	@RequestMapping(value = "/main", method=RequestMethod.GET)
	public ModelAndView doMain(HttpSession pSession) {
		ModelAndView model = new ModelAndView(PAGES.MAINPAGE.toString());
		addUserInformation(pSession, model);
		model.addObject(CONSTANTS.AVAILABLE_SYMBOLS.toString(), DataManager.getAvailableSymbols());
		return model;
	}

	private PredictionResults doPrediction(List<InstStock> allData) throws java.io.IOException
	{
		PredictionResults pr = new PredictionResults();

		double[] allClosingPrices=new double[allData.size()];
		for(int i=0; i<allData.size(); i++){
			allClosingPrices[i]=allData.get(i).instPrice;
		}
			
		if (allData.size()>32){
			double[] prices = new double[32];
			int indx=0;
			for(int i=31; i>=0; i--){
				prices[indx]=allData.get(i).instPrice;
				indx++;
			}

			KalmanPredictor kp = KalmanPredictor.GetInstance();
			double[] u = kp.DWaveletT(prices, 32);
			double[] inter_signal = kp.interpolator(prices, u);
			double[] pre = kp.KalmanFilter(inter_signal, 32);
			pr.predictionPrices = pre;//model.addObject("predictionPrices", pre); 

			RSI rsi = RSI.GetInstance();
			double[] RSIvalues = rsi.myRSIarray(allClosingPrices, allClosingPrices.length, 25);
			double predict = RSIvalues[RSIvalues.length-1];
			gLogger.log("RSI IS===="+predict);

			pr.predict=predict;
			
			//get the difference
			double difference = pre[0] - allData.get(0).instPrice;
			int toBeRounded = (int) (difference * 100);
			double rounded = (double) toBeRounded;
			difference = rounded/100;
		            
			if (difference>0) pr.goesUpOrDownForDay="1";//model.addObject("goesUpOrDownForDay", "1"); 
			else pr.goesUpOrDownForDay="0";//model.addObject("goesUpOrDownForDay", "0"); 
					
			double percentageDifference = (Math.abs(allData.get(0).instPrice-pre[0])/((allData.get(0).instPrice+pre[0])/2))*100;
			toBeRounded = (int) (percentageDifference * 100);
			rounded = (double) toBeRounded;
			percentageDifference = rounded/100;
					
					
			pr.nextDayDifference = difference;//model.addObject("nextDayDifference", difference); 
			pr.nextDayPercentageDifference = percentageDifference;//model.addObject("nextDayPercentageDifference", percentageDifference); 
					
			difference = pre[4] - allData.get(0).instPrice;
			int toBeRounded2 = (int) (difference * 100);
			double rounded2 = (double) toBeRounded2;
			difference = rounded2/100;
		            
			if (difference>0) pr.goesUpOrDownFor5Day="1";//model.addObject("goesUpOrDownFor5Day", "1"); 
			else pr.goesUpOrDownFor5Day="0";//model.addObject("goesUpOrDownFor5Day", "0"); 
			
			percentageDifference = (Math.abs(allData.get(0).instPrice-pre[4])/((allData.get(0).instPrice+pre[4])/2))*100;
			toBeRounded = (int) (percentageDifference * 100);
			rounded = (double) toBeRounded;
			percentageDifference = rounded/100;
					
			pr.difference=difference;//model.addObject("fiveDayDifference", difference); 
			pr.fiveDayPercentageDifference=percentageDifference;//model.addObject("fiveDayPercentageDifference", percentageDifference); 
					
			//Pari's code
			mymain svmPredict = mymain.GetInstance();
			int[] patternsHappening=new int[7];
			int pointer=0;
					
			Iterator it = svmPredict.getGmodels().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry pair = (Map.Entry)it.next();
				int range=150;
				if (allClosingPrices.length<range) range=allClosingPrices.length;
				double isThePattern = svmPredict.mySvmTest(allClosingPrices, (svm_model)pair.getValue() ,21, range);
				        
				if (isThePattern==1){
					patternsHappening[pointer]=Integer.parseInt(pair.getKey().toString());
					gLogger.log("I DETECTED"+pair.getKey());
					pointer++;
				}    
			}
				    
			double buyConfidence = 0;
			double sellConfidence = 0;
			double holdConfidence = 0;
				    
			for (int j=0; j< patternsHappening.length; j++){
				gLogger.log("IM="+patternsHappening[j]);
				switch (patternsHappening[j]){
				case 1:
					sellConfidence=sellConfidence+1;
					break;
					//"sell"
				case 2:
					buyConfidence=buyConfidence+1;
					break;
					//"buy"
				case 3:
					//"sell"
					sellConfidence=sellConfidence+1;
					break;
				case 4:
					//"hold"
					holdConfidence=holdConfidence+1;
					break;
				case 5:
					//"buy"
					buyConfidence=buyConfidence+1;
					break;
				case 6:
					gLogger.log("IM sixxxxxxxx");
					holdConfidence=holdConfidence+1;
					break;
					//"hold"
				case 7:
					holdConfidence=holdConfidence+1;
					break;
					//"hold"
				}
			}
				    
			double totalConfidence=buyConfidence+sellConfidence+holdConfidence;
			double buy = (buyConfidence/totalConfidence)*100;
			double sell = (sellConfidence/totalConfidence)*100;
			double hold = (holdConfidence/totalConfidence)*100;

			pr.buy=buy;//model.addObject("buy", buy);
			pr.sell=sell;//model.addObject("sell",sell);
			pr.hold=hold;//model.addObject("hold", hold);

		}

		return pr;
	}

	@RequestMapping(value = "/search", method=RequestMethod.POST)
	public ModelAndView doSearch(Ticker pTicker, HttpSession pSession) throws Exception {
		ModelAndView model = new ModelAndView(PAGES.MAINPAGE.toString());
		addUserInformation(pSession, model);
		String symbol = pTicker.getTickerName();
		gLogger.log("IM HERE1"+symbol);
		
		try{
			symbol = pTicker.getTickerSymbol();
			if(symbol == null || symbol.isEmpty()) {
				symbol = pTicker.getTickerName().split(" - ")[1];
			}
		}catch(Exception e){
			symbol = pTicker.getTickerName();
		}

		Ticker t = modelManager.tickerExists(symbol);

		if(t!=null) {
			// if the ticker exists, then t will be filled up.
			//try {
			gLogger.log("IM HERE2"+t.getTickerSymbol());
			gLogger.log("tickerExists");
			List<InstStock> allData=null;
				
			allData = modelManager.getDailyClosingPrices(t);

			PredictionResults pr = doPrediction(allData);
			model.addObject("predictionPrices",pr.predictionPrices);
			model.addObject("goesUpOrDownForDay",pr.goesUpOrDownForDay);
			model.addObject("nextDayDifference",pr.nextDayDifference);
			model.addObject("nextDayPercentageDifference",pr.nextDayPercentageDifference);
			model.addObject("goesUpOrDownFor5Day",pr.goesUpOrDownFor5Day);
			model.addObject("fiveDayDifference",pr.difference);
			model.addObject("fiveDayPercentageDifference",pr.fiveDayPercentageDifference);
			model.addObject("buy",pr.buy);
			model.addObject("sell",pr.sell);
			model.addObject("hold",pr.hold);
			model.addObject("predict", pr.predict); 

			model.addObject("current_ticker", t);
			model.addObject("available_data",allData);
			gLogger.log("IM HERE2");
			gLogger.log("Got size : "+allData.size());
		} else {
			t=new Ticker();
			t.setTickerSymbol(symbol);
			Stock stock = YahooFinance.get(symbol);

			if(stock!=null){
				try {
					modelManager.addTicker(stock.getSymbol(), stock.getName());
				} catch (Exception e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				ConfigReader config = ConfigReader.getInstance();
	
				StockDownloaderTask stockDownloader = StockDownloaderTask.getInstance();
				Calendar from = Utilities.getDateFromString(config.getStr(PROPERTIES.HISTORIC_START));
				Calendar to = Calendar.getInstance(); to.add(Calendar.DATE, -1); // yesterdays date
	
				HashMap<String,List<HistoricalQuote>> historicData = new HashMap<String,List<HistoricalQuote>>();
				try {
					if(!modelManager.checkHistoricDataExists(symbol)) {
						gLogger.log("Symbol : "+symbol);
						historicData.put(symbol, stockDownloader.getHistoricQuote(symbol, from, to));
					}
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				for(String tk : historicData.keySet()) {
					gLogger.log("Downloading historic data for: "+tk, LOG_TYPE.GRAL);
					try {
						modelManager.saveHistoricalData(historicData.get(tk));
					} catch (Exception e) {
						gLogger.log("Something went wrong downloading historic data for "+t, LOG_TYPE.ERROR);
					}
				}
				
				List<InstStock> allData;
				try {					
					allData = modelManager.getDailyClosingPrices(t);
					double[] allClosingPrices=new double[allData.size()];
					for(int i=0; i<allData.size(); i++){
						allClosingPrices[i]=allData.get(i).instPrice;
					}
					
					if (allData.size()>32){
						double[] prices = new double[32];
						int indx=0;
						for(int i=31; i>=0; i--){
							gLogger.log("INDEX  : "+indx);
							prices[indx]=allData.get(i).instPrice;
							indx++;
						}
						
						for(int i=0; i<prices.length; i++){
							gLogger.log("Got  : "+prices[i]);
						}
						
						KalmanPredictor kp = KalmanPredictor.GetInstance();
						double[] u = kp.DWaveletT(prices, 32);
						double[] inter_signal = kp.interpolator(prices, u);
						double[] pre = kp.KalmanFilter(inter_signal, 32);
						model.addObject("predictionPrices", pre); 
						
						RSI rsi = RSI.GetInstance();
						double[] RSIvalues = rsi.myRSIarray(allClosingPrices, allClosingPrices.length, 25);
						double predict = RSIvalues[RSIvalues.length-1];
						model.addObject("predict", predict); 
						
						//get the difference
						double difference = pre[0] - allData.get(0).instPrice;
						int toBeRounded = (int) (difference * 100);
						double rounded = (double) toBeRounded;
						difference = rounded/100;
			            
						if (difference>0) model.addObject("goesUpOrDownForDay", "1"); 
						else model.addObject("goesUpOrDownForDay", "0"); 
						
						double percentageDifference = (Math.abs(allData.get(0).instPrice-pre[0])/((allData.get(0).instPrice+pre[0])/2))*100;
						toBeRounded = (int) (percentageDifference * 100);
						rounded = (double) toBeRounded;
						percentageDifference = rounded/100;
						
						
						model.addObject("nextDayDifference", difference); 
						model.addObject("nextDayPercentageDifference", percentageDifference); 
						
						difference = pre[4] - allData.get(0).instPrice;
						int toBeRounded2 = (int) (difference * 100);
						double rounded2 = (double) toBeRounded2;
						difference = rounded2/100;
			            
						if (difference>0) model.addObject("goesUpOrDownFor5Day", "1"); 
						else model.addObject("goesUpOrDownFor5Day", "0"); 
			            
						
						percentageDifference = (Math.abs(allData.get(0).instPrice-pre[4])/((allData.get(0).instPrice+pre[4])/2))*100;
						toBeRounded = (int) (percentageDifference * 100);
						rounded = (double) toBeRounded;
						percentageDifference = rounded/100;
						
						model.addObject("fiveDayDifference", difference); 
						model.addObject("fiveDayPercentageDifference", percentageDifference); 
						
						//Pari's code
						mymain svmPredict = mymain.GetInstance();
						int[] patternsHappening=new int[7];
						int pointer=0;
						
						Iterator it = svmPredict.getGmodels().entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry pair = (Map.Entry)it.next();
							int range=150;
							if (allClosingPrices.length<range) range=allClosingPrices.length;
							double isThePattern = svmPredict.mySvmTest(allClosingPrices, (svm_model)pair.getValue() ,21, range);
					        
							if (isThePattern==1){
								patternsHappening[pointer]=Integer.parseInt(pair.getKey().toString());
								gLogger.log("I DETECTED"+pair.getKey());
								pointer++;
							}    
						}
					    
						double buyConfidence = 0;
						double sellConfidence = 0;
						double holdConfidence = 0;
					    
						for (int j=0; j< patternsHappening.length; j++){
							gLogger.log("IM="+patternsHappening[j]);
							switch (patternsHappening[j]){
					    		case 1:
					    			sellConfidence=sellConfidence+1;
					    			break;
					    			//"sell"
					    		case 2:
					    			buyConfidence=buyConfidence+1;
					    			break;
					    			//"buy"
					    		case 3:
					    			//"sell"
					    			sellConfidence=sellConfidence+1;
					    			break;
					    		case 4:
					    			//"hold"
					    			holdConfidence=holdConfidence+1;
					    			break;
					    		case 5:
					    			//"buy"
					    			buyConfidence=buyConfidence+1;
					    			break;
					    		case 6:
						    		gLogger.log("IM sixxxxxxxx");
					    			holdConfidence=holdConfidence+1;
					    			break;
					    			//"hold"
					    		case 7:
					    			holdConfidence=holdConfidence+1;
					    			break;
					    			//"hold"
							}
						}
					    
						double totalConfidence=buyConfidence+sellConfidence+holdConfidence;
						double buy=0.0;
						double sell=0.0;
						double hold=0.0;
						if (totalConfidence!=0){
							buy = (buyConfidence/totalConfidence)*100;
							sell = (sellConfidence/totalConfidence)*100;
							hold = (holdConfidence/totalConfidence)*100;
						}
					    
						model.addObject("buy", buy);
						model.addObject("sell",sell);
						model.addObject("hold", hold);
						
					}
					
					
					Ticker ticker = modelManager.getTicker(t.getTickerSymbol());
					model.addObject("current_ticker", ticker);
					model.addObject("available_data",allData);
					gLogger.log("IM HERE2");
					gLogger.log("Got size : "+allData.size());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				model.addObject("current_ticker", null);
				model.addObject("available_data",null);
			}
			
			
		}
		model.addObject(CONSTANTS.AVAILABLE_SYMBOLS.toString(), DataManager.getAvailableSymbols());
		// do search here
		return model;
	}


	@RequestMapping(value = "/getPrice", method=RequestMethod.GET)
	public ModelAndView doGetPrice(
				       @RequestParam("ticker") String ticker,
				       @RequestParam("startDate") long startDate,
				       @RequestParam("endDate") long endDate
				       ) {
		gLogger.log("Getting prices called! "+startDate+"  "+endDate);
		try {
			gLogger.log("Getting prices from DB.........");
			List<InstStock> l = modelManager.getStockQuotes
				(new Ticker(ticker,""),
				 // (new GregorianCalendar(2015,
				 // 			04-1,
				 // 			29,
				 // 			0,
				 // 			0,
				 // 			0)).getTime(),
				 new Date(startDate),
				 new Date(endDate)
				 );


			gLogger.log("Got num of stocks : "+l.size());

			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<response>");

			//int numOutputElems = l.size();
			int numOutputElems = 400;
			int numJumps = l.size()/numOutputElems;
			int x;

			/*
			sb.append("<labels>");
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
			x = 0;
			for(InstStock i : l) {
				if(x++%numJumps == 0)
					sb.append("<l>"+sdf.format(i.entryDate)+"</l>");
			}
			sb.append("</labels>");

			sb.append("<dataset>");
			x = 0;
			for(InstStock i : l) {
				if(x++%numJumps == 0)
					sb.append("<d>"+i.instPrice+"</d>");
			}
			sb.append("</dataset>");
			*/
			//sb.append("<json>{ hello: 'world', places: ['Africa', 'America', 'Asia', 'Australia'] }</json>");

			sb.append("<json>[{ 'key':'Volume','bar':true,'values':[");

			x = 0;
			for(InstStock i : l) {
				if(x++%numJumps == 0) {
					sb.append("["+i.entryDate.getTime()+","+i.volume+"],");
				}
			}

			sb.setLength(sb.length() - 1);
			sb.append("]},{ 'key':'Price','values':[");

			x = 0;
			for(InstStock i : l) {
				if(x++%numJumps == 0)
					sb.append("["+i.entryDate.getTime()+","+i.instPrice+"],");
			}

			sb.setLength(sb.length() - 1);

			sb.append("] }]</json>");


			/*
			gLogger.log("Getting prices from DB.........");
			List<HistStock> lh = modelManager.getHistQuotes
				(new Ticker("GOOG",""),
				 new Date(startDate),
				 new Date(endDate)
				 );


			gLogger.log("Got num of elems: "+lh.size());

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			sb.append("<json2>[{'values':\"[");

			x = 0;
			numJumps = 1;
			for(HistStock i : lh) {
				if(x++%numJumps == 0) {
					sb.append("['");
					sb.append(sdf.format(i.entryDate));
					sb.append("',");
					sb.append(i.min);
					sb.append(",");
					if(i.open > i.close)
						sb.append(i.close);
					else
						sb.append(i.open);
					sb.append(",");
					if(i.open > i.close)
						sb.append(i.open);
					else
						sb.append(i.close);
					sb.append(",");
					sb.append(i.max);
					sb.append("],");
				}
			}

			sb.setLength(sb.length() - 1);

			sb.append("]\"}]</json2>");
*/
			//sb.append("<json2>[['Mon', 20, 28, 38, 45], ['Tueaaaa', 31, 38, 55, 66], ['Wed', 50, 55, 77, 80], ['Thu', 77, 77, 66, 50], ['Fri', 68, 66, 22, 15]]</json2>");

			sb.append("</response>");

			Map<String, Object> myModel = new HashMap<String, Object>();
			//myModel.put("stocks", l);
			myModel.put("msg", sb.toString());

			ModelAndView model = new ModelAndView(PAGES.GETPRICE.toString(), "model", myModel);
			return model;
		} catch(Exception e) {
			gLogger.log("System failure on getprice", LOG_TYPE.FATAL_ERROR);
			return null;
		}
	}

	@RequestMapping(value = "/getHistPrice", method=RequestMethod.GET)
	public ModelAndView doGetHistPrice(
				       @RequestParam("ticker") String ticker,
				       @RequestParam("startDate") long startDate,
				       @RequestParam("endDate") long endDate,
				       @RequestParam("indicator") String indicator,
				       @RequestParam("maWindow") int maWindow
				       ) {
		gLogger.log("Getting prices called! "+startDate+"  "+endDate);
		try {
			gLogger.log("Getting prices from DB.........");
			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<response>");

			gLogger.log("Getting prices from DB.........");
			List<HistStock> lh = modelManager.getHistQuotes
				(new Ticker(ticker,""),
				 new Date(startDate),
				 new Date(endDate)
				 );

			double[] ma = MovingAverage.myAverage(lh, maWindow);
			double[] ema = MovingAverage.myEMAverage(lh, maWindow);


			RSI rsi_ = RSI.GetInstance();
			double[] rsi = rsi_.myRSI(lh, maWindow);

			//int numOutputElems = l.size();
			int numOutputElems = 400;
			int numJumps = lh.size()/numOutputElems;
			int x;

			gLogger.log("Got num of elems: "+lh.size());

			SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
			sb.append("<json2>{'values':[");

			x = 0;
			numJumps = 1;
			for(HistStock i : lh) {
				if(x%numJumps == 0) {
					sb.append("['");
					sb.append(sdf.format(i.entryDate));
					sb.append("',");
					if(!indicator.equals("rsi")) {
						if(i.open > i.close) {
							sb.append(i.min);
							sb.append(",");
							sb.append(i.open);
							sb.append(",");
							sb.append(i.close);
							sb.append(",");
							sb.append(i.max);
						} else {
							sb.append(i.max);
							sb.append(",");
							sb.append(i.close);
							sb.append(",");
							sb.append(i.open);
							sb.append(",");
							sb.append(i.min);
						}
					} else {
						//sb.append("50,50,50,50");
						sb.append("0,0,0,0");
					}
					// win=3
					// 0 1 2 3   x
					// - -       x<win-1
					//     0 1   x-win+1
					if(indicator.equals("ma")) {
						if(x<maWindow-1) {
							sb.append(","+i.close);
							//sb.append(","+0);
							//System.out.println("-- x"+x+" w"+maWindow+" "+sdf.format(i.entryDate)+" sizeMA"+ma.length+" MAindex"+(x-maWindow+1)+" ma NOPE");
						} else {
							//System.out.println("-- x"+x+" w"+maWindow+" "+sdf.format(i.entryDate)+" sizeMA"+ma.length+" MAindex"+(x-maWindow+1)+" close"+i.close+" ma"+ma[x-maWindow+1]+" ema"+ema[x-maWindow+1]);
							sb.append(","+ma[x-maWindow+1]);
						}
					} else if(indicator.equals("ema")) {
						if(x<maWindow-1) {
							sb.append(","+i.close);
						} else {
							sb.append(","+ema[x-maWindow+1]);
						}
					} else if(indicator.equals("rsi")) {
						if(x<maWindow-1) {
							sb.append(",50");
							//System.out.println("-- x"+x+" w"+maWindow+" "+sdf.format(i.entryDate)+" sizeMA"+rsi.length+" MAindex"+(x-maWindow+1)+" rsi NOPE");

						} else {
							//System.out.println("-- lhSize"+lh.size()+" x"+x+" w"+maWindow+" "+sdf.format(i.entryDate)+" sizeMA"+rsi.length+" MAindex"+(x-maWindow+1)+" close"+i.close+" rsi "+rsi[x-maWindow+1]);
							sb.append(","+rsi[x-maWindow+1]);
						}
					}
					sb.append("],");
				}
				x++;
			}

			sb.setLength(sb.length() - 1);

			sb.append("]}</json2>");


			//sb.append("<json2>[['Mon', 20, 28, 38, 45], ['Tueaaaa', 31, 38, 55, 66], ['Wed', 50, 55, 77, 80], ['Thu', 77, 77, 66, 50], ['Fri', 68, 66, 22, 15]]</json2>");

			sb.append("</response>");

			Map<String, Object> myModel = new HashMap<String, Object>();
			myModel.put("msg", sb.toString());

			ModelAndView model = new ModelAndView(PAGES.GETPRICE.toString(), "model", myModel);
			return model;
		} catch(Exception e) {
			gLogger.log(e.getMessage());
			gLogger.log("System failure on getprice", LOG_TYPE.FATAL_ERROR);
			e.printStackTrace();
			return null;
		}
	}






	@RequestMapping(value = "/getListCompanies", method=RequestMethod.GET)
	public String doGetListCompanies() {
		try {
			PredictionResults prGoogle = null;

			ConfigReader config = ConfigReader.getInstance();
			Calendar from = Utilities.getDateFromString(config.getStr(PROPERTIES.HISTORIC_START));
			Calendar to = Calendar.getInstance();
			List<PredictionResults> prs = new ArrayList<PredictionResults>();
			for(Ticker t : DataManager.getAvailableTickers()) {
				gLogger.log("doGetBestStocks trying "+t);

				List<InstStock> allData = modelManager.getDailyClosingPrices(t);
				if(allData.size() < 2) continue;
				else gLogger.log("doGetBestStocks "+t+" got num records: "+allData.size());


				List<HistStock> allHistData = modelManager.getHistQuotes(t,from.getTime(),to.getTime());

				PredictionResults pr = doPrediction(allData);

				if(t.getTickerSymbol().equalsIgnoreCase("GOOG"))
					prGoogle = pr;

				pr.ticker = t;
				pr.allInstData = modelManager.getStockQuotes(t,from.getTime(),to.getTime());
				pr.allHistData = allHistData;
				if(pr.goesUpOrDownForDay.equals("0"))
					pr.nextDayPercentageDifference *= (-1.0);
				if(pr.goesUpOrDownFor5Day.equals("0"))
					pr.fiveDayPercentageDifference *= (-1.0);
				prs.add(pr);
			}

			Map<String, Object> myModel = new HashMap<String, Object>();

			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<response>\n");
			PriorityQueue<PredictionResults> pq;


			sb.append("<table>\n");

			sb.append("<tr>"+

				  "  <td>"+
				  "Ticker"+
				  "</td>  "+

				  "<td>"+
				  "Last price"+
				  "</td>  "+

				  "<td>"+
				  "Max. 10 days"+
				  "</td>  "+

				  "<td>"+
				  "Ave. 1 year"+
				  "</td>  "+

				  "<td>"+
				  "Min. 1 year"+
				  "</td>  "+

				  "<td><span>"+
				  "Predicted Price Difference (%)"+
				  "</span></td>  "+

				  "</tr>\n");


			PredictionResults.compareBy = 
				PredictionResults.Type.nextDayPercentageDifference;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();

				int i=10;
				if(pr.allHistData.size()<i) i = pr.allHistData.size();

				pr.max10days=-100000000000.0;
				i--;
				for( ; i>=0 ; i--) {
					//System.out.println("i"+i+" size"+pr.allHistData.size()+" trying to access element "+(pr.allHistData.size()-i));
					pr.max10days = Math.max(pr.allHistData.get(pr.allHistData.size()-i-1).max,pr.max10days);
				}


				Calendar dateNextYr = Calendar.getInstance(); dateNextYr.add(Calendar.YEAR, -1);

				pr.min1year=100000000.0;
				double total1year=0;
				int total1yearN=0;
				i--;
				for(HistStock hs : pr.allHistData) {
					if(hs.entryDate.getTime() < dateNextYr.getTime().getTime()) 
						continue;
					total1year += hs.close;
					total1yearN ++;
					pr.min1year = Math.min(hs.min,pr.min1year);
				}
				pr.ave1year = total1year/total1yearN;

				// ticker
				// last price (instantaneous)
				// highest stock price during last 10 days
				// average stock price during last year
				// minimum stock price during last year
				sb.append("<tr>"+

					  "  <td>"+
					  (new String(pr.ticker.getTickerSymbol())).toUpperCase()+
					  "</td>  "+

					  "<td>"+
					  pr.allInstData.get(pr.allInstData.size()-1).instPrice+
					  "</td>  "+

					  "<td>"+
					  pr.max10days+
					  "</td>  "+

					  "<td>"+
					  pr.ave1year+
					  "</td>  "+

					  "<td>"+
					  pr.min1year+
					  "</td>  "+

					  "<td><span>"+
					  pr.nextDayPercentageDifference+
					  "%</span></td>  "+

					  "</tr>\n");
			}
			sb.append("</table>\n\n");
			sb.append("<br><br><br>");

			// List the ids of companies along with their name who have
			// the average stock price lesser than the lowest of Google 
			// in the latest one year
			sb.append("<div> <div>Companies with average price lessent than the lowest of Google in the last one year ("+prGoogle.min1year+"):</div><div>  \n");
			for(PredictionResults pr : prs) {
				if(pr == prGoogle) continue;
				if(prGoogle.min1year <= pr.ave1year) continue;
				sb.append(pr.ticker+" (average "+pr.ave1year+")<br>\n");
			}
			//sb.setLength(sb.length() - 1);
			sb.append("</div></div>");


			sb.append("</response>");
			return sb.toString();
			//myModel.put("msg", sb.toString());
			//ModelAndView model = new ModelAndView(PAGES.GETPRICE.toString(), "model", myModel);
			//return model;
		} catch(Exception e) {
			gLogger.log("System failure on getBestStocks", LOG_TYPE.FATAL_ERROR);
			e.printStackTrace();
			return null;
		}
	}




	@RequestMapping(value = "/getBestStocks", method=RequestMethod.GET)
	public ModelAndView doGetBestStocks() {

		try {
			List<PredictionResults> prs = new ArrayList<PredictionResults>();
			for(Ticker t : DataManager.getAvailableTickers()) {
				gLogger.log("doGetBestStocks trying "+t);

				List<InstStock> allData = modelManager.getDailyClosingPrices(t);
				if(allData.size() < 50) continue;
				else gLogger.log("doGetBestStocks "+t+" got num records: "+allData.size());
				PredictionResults pr = doPrediction(allData);
				pr.ticker = t;
				if(pr.goesUpOrDownForDay.equals("0"))
					pr.nextDayPercentageDifference *= (-1.0);
				if(pr.goesUpOrDownFor5Day.equals("0"))
					pr.fiveDayPercentageDifference *= (-1.0);
				prs.add(pr);
			}

			Map<String, Object> myModel = new HashMap<String, Object>();

			StringBuffer sb = new StringBuffer();
			sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			sb.append("<response>");
			PriorityQueue<PredictionResults> pq;


			sb.append("<TABLE_PRED_1>");
			PredictionResults.compareBy = 
				PredictionResults.Type.nextDayPercentageDifference;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();
				sb.append("<tr><td>"+(new String(pr.ticker.getTickerSymbol())).toUpperCase()+"</td><td><span>"+
					  pr.nextDayPercentageDifference+"%</span></td></tr>");
			}
			sb.append("</TABLE_PRED_1>");


			sb.append("<TABLE_PRED_2>");
			PredictionResults.compareBy = 
				PredictionResults.Type.fiveDayPercentageDifference;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();
				sb.append("<tr><td>"+(new String(pr.ticker.getTickerSymbol())).toUpperCase()+"</td><td><span>"+
					  pr.fiveDayPercentageDifference+"%</span></td></tr>");
			}
			sb.append("</TABLE_PRED_2>");


			sb.append("<TABLE_PRED_3>");
			PredictionResults.compareBy = 
				PredictionResults.Type.buy;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();
				sb.append("<tr><td>"+(new String(pr.ticker.getTickerSymbol())).toUpperCase()+"</td><td><span>"+
					  pr.buy+"</span></td></tr>");
			}
			sb.append("</TABLE_PRED_3>");


			sb.append("<TABLE_PRED_4>");
			PredictionResults.compareBy = 
				PredictionResults.Type.sell;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();
				sb.append("<tr><td>"+(new String(pr.ticker.getTickerSymbol())).toUpperCase()+"</td><td><span>"+
					  pr.sell+"</span></td></tr>");
			}
			sb.append("</TABLE_PRED_4>");


			sb.append("<TABLE_PRED_5>");
			PredictionResults.compareBy = 
				PredictionResults.Type.hold;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();
				sb.append("<tr><td>"+(new String(pr.ticker.getTickerSymbol())).toUpperCase()+"</td><td><span>"+
					  pr.hold+"</span></td></tr>");
			}
			sb.append("</TABLE_PRED_5>");


			sb.append("<TABLE_PRED_6>");
			PredictionResults.compareBy = 
				PredictionResults.Type.predict;
			pq = new PriorityQueue<PredictionResults>(prs);
			while(!pq.isEmpty()) {
				PredictionResults pr = pq.poll();
				sb.append("<tr><td>"+(new String(pr.ticker.getTickerSymbol())).toUpperCase()+"</td><td><span>"+
					  pr.predict+"</span></td></tr>");
			}
			sb.append("</TABLE_PRED_6>");



			sb.append("</response>");

			myModel.put("msg", sb.toString());
			ModelAndView model = new ModelAndView(PAGES.GETPRICE.toString(), "model", myModel);
			return model;
		} catch(Exception e) {
			gLogger.log("System failure on getBestStocks", LOG_TYPE.FATAL_ERROR);
			e.printStackTrace();
			return null;
		}

	}


	private static class PredictionResults implements Comparable {
		public double[] predictionPrices;
		public String goesUpOrDownForDay;
		public double nextDayDifference;
		public double nextDayPercentageDifference;
		public String goesUpOrDownFor5Day;
		public double difference;
		public double fiveDayPercentageDifference;
		public double buy;
		public double sell;
		public double hold;
		public double predict;

		public double max10days;
		public double ave1year;
		public double min1year;


		List<InstStock> allInstData;
		List<HistStock> allHistData;
		public Ticker ticker;
		public static Type compareBy;
		public static enum Type {
			nextDayPercentageDifference,
			fiveDayPercentageDifference,
			buy,
			sell,
			hold,
			predict
		};
		PredictionResults() {
			compareBy = Type.nextDayPercentageDifference;
		}
		public int compareTo(Object _o) {
			PredictionResults o = (PredictionResults) _o;
			switch(compareBy) {
			case nextDayPercentageDifference:
				return Double.compare(nextDayPercentageDifference,o.nextDayPercentageDifference)>0?-1:1;
			case fiveDayPercentageDifference:
				return Double.compare(fiveDayPercentageDifference,o.fiveDayPercentageDifference)>0?-1:1;
			case buy:
				return Double.compare(buy,o.buy)>0?-1:1;
			case sell:
				return Double.compare(sell,o.sell)>0?-1:1;
			case hold:
				return Double.compare(hold,o.hold)>0?-1:1;
			case predict:
				return Double.compare(predict,o.predict)>0?-1:1;
			default:
				System.err.println("ERROOOOOOOR in PredictionResults");
				return 0;
			}
		}

	}

	@RequestMapping(value = "/removeUserSymbol", method=RequestMethod.POST)
	public ModelAndView removeUserSymbol(Ticker t, HttpSession pSession) {
		ModelAndView model = null;
		try {
			modelManager.removeUserFavoriteTicker(gLoggedSessions.get(pSession.getId()), t);
			t.setTickerName(t.getTickerSymbol());
			model = doSearch(t, pSession);
			addUserInformation(pSession, model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
	
	@RequestMapping(value = "/addUserSymbol", method=RequestMethod.POST)
	public ModelAndView addUserSymbol(Ticker t, HttpSession pSession) {
		ModelAndView model = null;
		try {
			modelManager.addUserFavoriteTicker(gLoggedSessions.get(pSession.getId()), t);
			t.setTickerName(t.getTickerSymbol());
			model = doSearch(t, pSession);
			addUserInformation(pSession, model);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return model;
	}
	
	private void addUserInformation(HttpSession pSession, ModelAndView pModel) {
		if(gLoggedSessions.containsKey(pSession.getId())) {
			pModel.addObject("userData", gLoggedSessions.get(pSession.getId()));
		} 
	}
	
}
