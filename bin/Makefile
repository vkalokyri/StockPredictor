all: stops undeploy compile deploy starts deploy

redeploy: undeploy compile deploy

compile:
	mvn clean install -DskipTests

deploy:
	cp target/stockpred-1.0.war ~/bin/apache-tomcat-7.0.59/webapps/

undeploy:
	rm -f target/stockpred-1.0.war
	rm -f ~/bin/apache-tomcat-7.0.59/webapps/stockpred-1.0.war
	rm -rf ~/bin/apache-tomcat-7.0.59/webapps/stockpred-1.0/

stops:
	~/bin/apache-tomcat-7.0.59/bin/shutdown.sh

starts:
	~/bin/apache-tomcat-7.0.59/bin/startup.sh ; sleep 8

# mysql -u root -e GRANT ALL ON *.* TO ''@'localhost'
