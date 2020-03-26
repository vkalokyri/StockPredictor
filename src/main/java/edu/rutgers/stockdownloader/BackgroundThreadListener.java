package edu.rutgers.stockdownloader;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import edu.rutgers.util.ConfigReader;
import edu.rutgers.util.enums.PROPERTIES;

public class BackgroundThreadListener implements ServletContextListener {
	private Timer timer;

	public void contextInitialized(ServletContextEvent sce) {
		if(ConfigReader.getInstance().getBool(PROPERTIES.STOCK_UPDATE)) {
			timer = new Timer();
			timer.schedule(StockDownloaderTask.getInstance(), 0, 5000);
		}
	};

	public void contextDestroyed(ServletContextEvent sce) {
		if(ConfigReader.getInstance().getBool(PROPERTIES.STOCK_UPDATE)) {
			if(timer != null)
				timer.cancel();
		}
	};
}
