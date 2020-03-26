
Spring 2015 - 332:567 Project - Stock Predictor Web App
Spring 2015 - 332:568 Project - Stock Predictor Web App


PARTICIPANTS:
	
      	Parishad Karimi
      	Ali Rostami
	Valia Kalokyri
	Marc Gamell
	Fernando Geraci
		
IMPLEMENTATION OVERVIEW:

	The skeleton has been built as a Maven project, with a master pom in the root. There are setting files for
	importing into Eclipse. The packaging is WAR so it will be fully deployable in any tomcat 7+ instance.
	
	Ideally, just for the skeleton, it will be useful to keep the scope as humble as possible hence I haven't
	created many packages yet but the essentials (model, view and controllers).
	
	As per web.xml configuration, we are using Spring as the framework. There will be a main RequestDispatcher 
	at the root 	(could be changed at any time) and for testing purposes a MainController which ideally it will 
	just instantiate basic apps resources and configurations.
	
	Jan 26, 2015 --
	
	I finished implementing the skeleton and so far so good, we have:
		Logging services via a Logger (singleton) acquired through the LoggerFactory
		Configuration services via the ConfigReader (singleton) and the app.properties file
		Data access via the DataManager (singleton) which implements the ModelManager api
		Spring managing the framework
	
	The basic operation is the following:
	
	REQUEST FROM CLIENT >
		RequestFilter >
			SpringDispatchServlet >
				MainController > Implementation.
