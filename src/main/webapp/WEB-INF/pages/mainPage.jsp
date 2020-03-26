<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>

    <link href="scripts/nv.d3.css" rel="stylesheet" type="text/css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.2/d3.min.js" charset="utf-8"></script>
    <script src="scripts/nv.d3.js"></script>
    <script type="text/javascript" src="https://www.google.com/jsapi"></script>
  <link rel="stylesheet" href="css/myaccordion.css">
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jqueryui/1.8.21/jquery-ui.min.js"></script>
<!--
  <script src="http://code.jquery.com/jquery-1.10.2.js"></script>
  <script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
-->
  <link rel="stylesheet" href="css/myaccordion.css">


<script>
	var PLOT_range = "year";
	var PLOT_indicators = "ema";

	google.load("visualization", "1", {packages:["corechart"]});
	google.load('search', '1');
</script>

    <style>
        text {
            font: 12px sans-serif;
        }

        svg {
            display: block;
        }
        html, body, #chart1, svg {
            margin: 0px;
            padding: 0px;
            width: 100%;
        }
        button {
            margin: 2px;
            margin-left: 70px;
        }
    </style>
    <script>
	    function showLoading(){
	   		document.getElementById('modal').style.display='block';
	    }
	    
    </script>
		<jsp:include page="/WEB-INF/pages/header.jsp"></jsp:include>
		<script type="text/javascript" src="scripts/portal.js"></script>
		<link rel="stylesheet" href="css/mainPage_style.css">
        <script type="text/javascript" src='scripts/Chart.min.js'></script>
		<link href='http://fonts.googleapis.com/css?family=Ubuntu&subset=cyrillic,latin' rel='stylesheet' type='text/css' />
	    <link href='http://fonts.googleapis.com/css?family=Electrolize' rel='stylesheet' type='text/css'>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>RU Finance</title>
		
	</head>
	<body>
		
		<div class="wrapper" style="background-color:#fff">
			<div class="topDiv">
			
				<div id="modal" class="modal"></div>
				<div onclick="location.href='/stockpred/portal';" style="cursor: pointer;" class="logo">
					<h1 >RU Finance</h1>
				</div>
				    
				<div id="searchDiv">
					<form id="searchTickerForm" method="post" action="search">
						<input id="searchBar" id="tName" name="tickerName" size="50" role="combobox" type="text" placeholder="Search..."  class="textBox" >
						<input id="tickSym" name="tickerSymbol" type="hidden" value="" />
						<input  type="submit" class="searchButton" onclick="grabSymbol(); showLoading()" value="" />
					</form>
					<c:if test="${not empty userData}">
						<form id="logoutForm" action="logout" method="POST">
							<input id="loginPrompt" type="button" value="Logout" onclick="document.getElementById('authForm').submit();" />
						</form>
					</c:if>
				</div>
			</div>
			
			<script>
				function grabSymbol() {
					try {
						var textVal = document.getElementById("tName").value;
						document.getElementById("tickSym").value = textVal.split(" ")[0];
					} catch(e) { }
				}
			</script>
			
			<div id="breadcrumb" class="breadcrumb">			
				
			</div>
			<div class="mainDiv">
			    <c:choose>
				<c:when test="${not empty available_data}">


<!--					<div class="filtering">
					     <div style="margin-top:12px">
					     	  <span style="font-size:13px; font-weight:bold; margin-left:10px">News</span>
					     	  <div id="news_branding"  style="/*float: left;*/"></div>
					     	  <br>
					     	  <div id="news_content" style="font-size:14px">Loading...</div>
					     </div>     
					</div>
-->
	 			<div class="filtering">
					<div style="margin-top:12px">

					<span style="font-size:13px; font-weight:bold; margin-left:10px">Time frame</span>
					<ul style="list-style-type: none; font-size:12px; margin-left:-20px; border-spacing:6px;">
						<li>
							  <a onclick="pastWeekPlot()" href="#">Past week</a>							
						</li>
						<li>
							  <a onclick="pastMonthPlot()" href="#">Past month</a>
						</li>
						<li>
							  <a onclick="past6MonthsPlot()" href="#">Past 6 months</a>
						</li>
						<li>
							  <a onclick="pastYearPlot()" href="#">Past year</a>
						</li>
						<li>
							  <a onclick="todayPlot()" href="#">Today</a>
						</li>
					</ul>
					
					<span style="font-size:13px; font-weight:bold; margin-left:10px">Indicators</span>
					<ul style="list-style-type: none; font-size:12px; margin-left:-20px; border-spacing:6px;">
						<li>
							  <a onclick="noIndicatorsPlot()" href="#">No Indicators</a>
						</li>

						<li>
							  <a onclick="maPlot()" href="#">Moving Average</a>
						</li>
						<li>
							  <a onclick="emaPlot()" href="#">Exponential Moving Average</a>
						</li>
						<li>
							  <a onclick="rsiPlot()" href="#">RSI</a>
						</li>
					</ul>



					<span style="font-size:13px; font-weight:bold; margin-left:10px">News about ${current_ticker.tickerSymbol}</span>
					<div id="news_branding"  style="font-color:#aaa font-size:12px; margin-left:0px; border-spacing:6px;"></div>
<!--					     	  <br>-->
					     	  <div id="news_content"  style="font-size:12px; margin-left:17px; margin-top:3px ; width: 143px; border-spacing:6px;">Loading...</div>



				</div>
				</div>
				
				<div class="resultsDiv">
					<div class="tickerInfoDiv">	
						<form id="addTickersForm" method="POST" action="addUserSymbol">
							<input type="hidden" value="" id="addTickerInput" name="tickerSymbol"/>
							<div style="margin-top: 7px; margin-bottom: 20px;" id="tickerNameDiv">						
								<span id="tickerName" style="font-size:22px; font-weight:bold;">${current_ticker}</span>
							</div>
						</form>
						<div>
						    <span style="font-size:15px">Current price:</span><span style="margin-left:5px">${available_data[0].instPrice}</span>
						</div>
						<span style="font-size:15px">Predicted tomorrow's price:</span><span style="margin-left:5px;">${predictionPrices[0]}</span>
						<c:choose>
							<c:when test="${goesUpOrDownForDay==1}">									
								<span class="upPrice"><img width="10" height="14" style="margin-right:2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="pos_arrow" alt="Up">${nextDayDifference}</span>
								<span class="upPrice">(${nextDayPercentageDifference}%)</span>
							</c:when>
							<c:otherwise>
								<span class="dropPrice"><img width="10" height="14" style="margin-right:2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="neg_arrow" alt="Down">${nextDayDifference}</span>
								<span class="dropPrice">(${nextDayPercentageDifference}%)</span>
							</c:otherwise>
						</c:choose>
						<div>				
							<span style="font-size:15px">Predicted 5-day average price:</span><span style="margin-left:5px">${predictionPrices[4]}</span>
							<c:choose>
								<c:when test="${goesUpOrDownFor5Day==1}">
									<span class="upPrice"><img width="10" height="14" style="margin-right:2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="pos_arrow" alt="Up">${fiveDayDifference}</span>
									<span class="upPrice">(${fiveDayPercentageDifference}%)</span>
								</c:when>
								<c:otherwise>	
									<span class="dropPrice"><img width="10" height="14" style="margin-right:2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="neg_arrow" alt="Down">${fiveDayDifference}</span>
									<span class="dropPrice">(${fiveDayPercentageDifference}%)</span>
								</c:otherwise>
							</c:choose>
						</div>
						<!--<c:choose>
							<c:when test="${predict>70}">	
								<div class="shortpredictionDiv" style="background:red;">
									<span style="position: absolute; margin-top: 13px; margin-left: 22px; ">SELL</span>
								</div>
							</c:when>	
							<c:when test="${predict<30}">
								<div class="shortpredictionDiv" >
									<span style="position: absolute; margin-top: 13px; margin-left: 22px;">BUY</span>
								</div>
							</c:when>
						</c:choose>-->
					<div class="suggestionDiv">
						 <div>
							<span style="font-size: 14px">Short-term prediction</span>
					     </div>	
					     <div>
							<span style="">Suggestion based on RSI indicator (${predict}): </span>
							<c:choose>
								<c:when test="${predict>70}">	
									<span style="margin-left:5px; color:red;">SELL</span>
								</c:when>	
								<c:when test="${predict<30}">
									<span style="margin-left:5px; color:green;">BUY</span>
								</c:when>
								<c:otherwise>
									<span style="margin-left:5px; color:orange;">HOLD</span>
								</c:otherwise>
							</c:choose>
						</div>
					     <div style="margin-top: 8px;">
							<span style="font-size: 14px">Long-term prediction</span>
					     </div>
					     <div>
					     	  <span style="color: #00CC66">BUY</span><span> with confidence: ${buy}%</span>
					     </div>
					     <div>
							<span style="color: red">SELL</span><span> with confidence: ${sell}%</span>
					     </div>
					     <div>
							<span style="color: orange">HOLD</span><span> with confidence: ${hold}%</span>
					     </div>
					</div>	
					</div>
					

					<div class="graphDiv">
<!--
						<div id="chart1" class='with-3d-shadow with-transitions'>
    						<svg> </svg>
						</div>
-->
						<div id="chart1" class='with-3d-shadow with-transitions'>
						     <div id="chart_historic"></div>
						     <div id="chart_historic_2"></div>
						     <svg></svg>
						</div>

						<!--
						style="width: 900px; height: 500px;"   
						style="height:1000px"
						-->

						<!--canvas id="myChart" width="560px" height="300px"></canvas-->
					</div>
					<div class="graphHeader" >	
					    <c:set var="symbol" value="${fn:split(current_ticker, ' - ')}" />
						<span style="color:#bbb; font-size:14px"><span id='PLOT_title' style="color:#000;">Plot title</span>   ${symbol[0]} | Last updated: ${available_data[0].entryDate}</span>
					</div>
				</div>
				</c:when>
			    <c:otherwise>
				    <c:choose>
				    	<c:when test="${not empty dashboard_data }">
						<div class="filtering"></div>
				    		<div style="text-align: center; padding: 0px; margin-top:20px" class="resultsDiv">
				    			${dashboard_data}
				    		</div>
				    	</c:when>
				    	<c:otherwise>
						<div class="filtering"></div>
				    		<div id="dashboardDataDiv" class="resultsDiv" >
								<span style="position:absolute; margin-top:10px;margin-left:-135px;">No results found.</span>
							</div>
				    	</c:otherwise>
				    </c:choose>
				</c:otherwise>
				</c:choose>				
				<div class="personalSpace">
					<div class="myTickers" style="height:354px">
						<div class="tickersHeader">
							<span style="margin-left:10px">Predicted Hot Stocks</span>
						</div>

<div id="accordion">
  <h3><a href="#">Next Day Difference</a></h3><div><table class="t_pred"><tbody id="TABLE_PRED_1"></tbody></table></div>
  <h3><a href="#">Five Day Difference</a></h3><div><table class="t_pred"><tbody id="TABLE_PRED_2"></tbody></table></div>
  <h3><a href="#">RSI</a></h3><div><table class="t_pred"><tbody id="TABLE_PRED_6"></tbody></table></div>
  <h3><a href="#">SVM Sell Patterns</a></h3><div><table class="t_pred"><tbody id="TABLE_PRED_3"></tbody></table></div>
  <h3><a href="#">SVM Buy Patterns</a></h3><div><table class="t_pred"><tbody id="TABLE_PRED_4"></tbody></table></div>
  <h3><a href="#">SVM Hold Patterns</a></h3><div><table class="t_pred"><tbody id="TABLE_PRED_5"></tbody></table></div>
</div>

					</div>


					<div class="myNews">
						<div class="tickersHeader">
							<span style="margin-left:10px">My Tickers</span>
						</div>
						<form id="userTickersForm" action="removeUserSymbol" method="POST">
							
							<input id="removeTickerSymbol" name="tickerSymbol" type="hidden" value="" />
							
							<table id="userTickersTable">
								<thead>
									<tr>
										<th scope="col">Symbol</th>
										<th scope="col"></th>
									</tr>
								</thead>
								<tbody>
									<!--
									<tr><td>Google Inc.</td><td><span class="upPrice">1.71%</span></td></tr>						
									<tr><td>Yahoo Inc.</td><td><span class="dropPrice">1.71%</span></td></tr>						
									<tr><td>Tesla Inc.</td><td><span class="upPrice">1.71%</span></td></tr>
									-->
								</tbody>
								
							</table>
						</form>
					</div>
<!--
					<div class="myNews">
						<div class="tickersHeader">
							<span style="margin-left:10px">My News</span>
						</div>

<div id="news_branding"  style="float: left;"></div>
<br>
<div id="news_content" style="font-size:14px">Loading...</div>
					</div>
-->
				</div>
			</div>
			
		</div>

		<div class="footer">
			<span>RU Finance © 2015</span>
		</div>
		<script>



function drawNewPlot(datapoints) {
	 //PLOT_title_update();
	 document.getElementById('PLOT_title').innerHTML = "";


	// $('graphcontainer1').html("<div id='chart1' class='with-3d-shadow with-transitions'><svg></svg></div>");
	 //$('graphcontainer2').html("");

	 // $('chart_historic').text("");
	 $('svg').text("");

    var testdata = datapoints.map(function(series) {
            series.values = series.values.map(function(d) { return {x: d[0], y: d[1] } });
            return series;
        });

    var chart;
    nv.addGraph(function() {
        chart = nv.models.linePlusBarChart()
            .margin({top: 50, right: 60, bottom: 30, left: 70})
            .x(function(d,i) { return i })
            .legendRightAxisHint(' (right axis)')
            .color(d3.scale.category10().range());

        chart.xAxis.tickFormat(function(d) {
            var dx = testdata[0].values[d] && testdata[0].values[d].x || 0;
            return dx ? d3.time.format('%x')(new Date(dx)) : '';
            })
            .showMaxMin(false);

        chart.y1Axis.tickFormat(function(d) { return '$' + d3.format(',f')(d) });
        chart.bars.forceY([0]).padData(false);

        chart.xAxis.showMaxMin(false);
        chart.x2Axis.showMaxMin(false);

        d3.select('#chart1 svg')
            .datum(testdata)
            .transition().duration(500).call(chart);

        nv.utils.windowResize(chart.update);

        chart.dispatch.on('stateChange', function(e) { nv.log('New State:', JSON.stringify(e)); });

        return chart;
    });
};



		var interval = 1000;

function doAjax(startDate_, endDate_) {
    $.ajax({
            type: 'GET',
            url: 'getPrice',
	    data: {ticker:"${symbol[0]}", startDate:startDate_ , endDate:endDate_},
            dataType: 'xml',
            success: function (data) {
		    // console.debug("ajax response: "+data.getElementsByTagName("l")[0].innerHTML);
		    // var ctx = document.getElementById("myChart").getContext("2d");

		    // var list1= data.getElementsByTagName("l");
    		    // var labels1 = []; for (var i = 0; i < list1.length; i++) {labels1.push(list1[i].innerHTML);}

		    // var list2= data.getElementsByTagName("d");
    		    // var datapoints1 = []; for (var i = 0; i < list2.length; i++) {datapoints1.push(list2[i].innerHTML);}
		    // 	var plotData = {
		    // 		    labels: labels1,
		    // 		    datasets: [
		    // 		        {
		    // 		            label: "My Second dataset",
		    // 		            fillColor: "rgba(151,187,205,0.2)",
		    // 		            strokeColor: "rgba(151,187,205,1)",
		    // 		            pointColor: "rgba(151,187,205,1)",
		    // 		            pointStrokeColor: "#fff",
		    // 		            pointHighlightFill: "#fff",
		    // 		            pointHighlightStroke: "rgba(151,187,205,1)",
		    // 		            data: datapoints1
		    // 		        }
		    // 		    ]
		    // 		};
		    // 	var myLineChart = new Chart(ctx).Line(plotData);

			var string_json = data.getElementsByTagName("json")[0].innerHTML;
			var json_data = JSON.stringify(eval("(" + string_json + ")"));
			var json_obj = jQuery.parseJSON( json_data );
			drawNewPlot(json_obj);
            },
            complete: function (data) {
                    // Schedule the next
                    //setTimeout(doAjax, interval);
            }
    });
}


function doAjaxHist(startDate_, endDate_, maWindow_) {
    $.ajax({
            type: 'GET',
            url: 'getHistPrice',
	    data: {ticker:"${symbol[0]}", startDate:startDate_ , endDate:endDate_, indicator:PLOT_indicators, maWindow:maWindow_},
            dataType: 'xml',
            success: function (data) {
			var string_json2 = data.getElementsByTagName("json2")[0].innerHTML;
			console.debug(" json2_data st "+string_json2);
			var json2_data = JSON.stringify(eval("(" + string_json2 + ")"));
			console.debug(" json2_data "+json2_data);
			var json2_obj = jQuery.parseJSON( json2_data );
			console.debug("========== json2_obj.values "+json2_obj.values);
			//if(PLOT_indicators=="none")
			//	drawHistoricChart(json2_obj.values);
			//else
				drawVisualization(json2_obj.values);
            },
            complete: function (data) {
                    // Schedule the next
                    //setTimeout(doAjax, interval);
            }
    });
}

window.onload = function() {
PLOT_update();

TABLE_PRED_update();


//var startDate_ = $.now()-24*3600*1000;
//var endDate_ = $.now();
//doAjax(startDate_, endDate_);
        // Create a News Search instance.
        newsSearch = new google.search.NewsSearch();
  
        // Set searchComplete as the callback function when a search is 
        // complete.  The newsSearch object will have results in it.
        newsSearch.setSearchCompleteCallback(this, searchComplete, null);

        // Specify search quer(ies)
        newsSearch.execute('${symbol[0]}');

        // Include the required Google branding
        google.search.Search.getBranding('news_branding');

//todayPlot();
//setTimeout(pastYearPlot(), 2000);
//pastMonthPlot();

//setTimeout(doAjax, interval);			
}

function TABLE_PRED_update() {
    $.ajax({
            type: 'GET',
            url: 'getBestStocks',
	    data: {},
            dataType: 'xml',
            success: function (data) {
			document.getElementById('TABLE_PRED_1').innerHTML =
			   data.getElementsByTagName("TABLE_PRED_1")[0].innerHTML;
			document.getElementById('TABLE_PRED_2').innerHTML =
			   data.getElementsByTagName("TABLE_PRED_2")[0].innerHTML;
			document.getElementById('TABLE_PRED_3').innerHTML =
			   data.getElementsByTagName("TABLE_PRED_3")[0].innerHTML;
			document.getElementById('TABLE_PRED_4').innerHTML =
			   data.getElementsByTagName("TABLE_PRED_4")[0].innerHTML;
			document.getElementById('TABLE_PRED_5').innerHTML =
			   data.getElementsByTagName("TABLE_PRED_5")[0].innerHTML;
			document.getElementById('TABLE_PRED_6').innerHTML =
			   data.getElementsByTagName("TABLE_PRED_6")[0].innerHTML;
            },
            complete: function (data) {
            }
    });
}

function PLOT_update() {
	 if(PLOT_range == "day") todayPlot();
	 else if(PLOT_range == "week") pastWeekPlot();
	 else if(PLOT_range == "month") pastMonthPlot();
	 else if(PLOT_range == "6month") past6MonthsPlot();
	 else if(PLOT_range == "year") pastYearPlot();
}

function todayPlot() { PLOT_range="day"; 
	 console.debug("todayPlot"); doAjax($.now()-24*3600*1000, $.now()); }
function pastWeekPlot() { PLOT_range="week"; 
	 console.debug("pastWeekPlot"); doAjaxHist($.now()-7*24*3600*1000, $.now(),2); }
function pastMonthPlot() { PLOT_range="month"; 
	 console.debug("pastMonthPlot"); doAjaxHist($.now()-30*24*3600*1000, $.now(),5); }
function past6MonthsPlot() { PLOT_range="6month"; 
	 console.debug("past6MonthsPlot"); doAjaxHist($.now()-6*30*24*3600*1000, $.now(),10); }
function pastYearPlot() { PLOT_range="year"; 
	 console.debug("pastYearPlot"); doAjaxHist($.now()-365*24*3600*1000, $.now(),30); }

function noIndicatorsPlot() { PLOT_indicators="none"; PLOT_update(); }
function maPlot() { PLOT_indicators="ma"; PLOT_update(); }
function emaPlot() { PLOT_indicators="ema"; PLOT_update(); }
function rsiPlot() { PLOT_indicators="rsi"; PLOT_update(); }







function PLOT_title_update() {
	 document.getElementById('PLOT_title').innerHTML = ""+PLOT_range+" range plot  (indicator: "+PLOT_indicators+")";
}

function drawVisualization(datapoints) {
	 PLOT_title_update();
    // Populate the data table.
    // var datapoints = [
    //     ['Mon', 20, 28, 38, 45, 30],
    //     ['Tue', 31, 38, 55, 66, 50],
    //     ['Wed', 50, 55, 77, 80, 45],
    //     ['Thu', 77, 77, 66, 50, 65],
    //     ['Fri', 68, 66, 22, 15, 80]
    // // Treat first row as data as well.
    // ];
    var dataTable = google.visualization.arrayToDataTable(datapoints, true);

    // Draw the chart.
    var chart = new google.visualization.ComboChart(document.getElementById('chart_historic_2'));
    chart.draw(dataTable, {
        legend:'none',
        width:775,
        height:350,
        seriesType: 'candlesticks',
        series: {
		0:{color: "Black"},
            1: {
                type: 'line'
            }
        }
    });
}





  function drawHistoricChart(datapoints) {
	 PLOT_title_update();
	 //$('graphcontainer1').html("");
	 //$('graphcontainer2').html("<div id='chart_historic' style='width: 900px; height: 500px;'></div>");

    var datah = google.visualization.arrayToDataTable(datapoints, true);
    var options = {
      legend:'none',height:'350',weight:'775'
    };

    var charth = new google.visualization.CandlestickChart(document.getElementById('chart_historic'));

    console.debug("datah    "+datah);
    charth.draw(datah, options);
  }

	var newsSearch;
      function searchComplete() {
      console.debug("GOOGLE NEWS FINISHED");
        // Check that we got results
        var news_content_el = document.getElementById('news_content');
	news_content_el.innerHTML = '';
        if (newsSearch.results && newsSearch.results.length > 0) {
          for (var i = 0; i < newsSearch.results.length; i++) {
            // Create HTML elements for search results
            //var p = document.createElement('p');
            //var a = document.createElement('a');
            //a.href="/news-search/v1/newsSearch.results[i].url;"
            //a.innerHTML = newsSearch.results[i].title;
            //p.appendChild(a);
            //document.body.appendChild(p);

	    //news_content_el
            var p = document.createElement('p');
            var a = document.createElement('a');
            //a.href="/news-search/v1/newsSearch.results[i].url;"
            a.href=decodeURIComponent(newsSearch.results[i].url);
            a.innerHTML = newsSearch.results[i].title;
            p.appendChild(a);
            news_content_el.appendChild(p);
          }
        }
      }












  $(function() {
    $( "#accordion" ).accordion({
      event: "click hoverintent"
    });
  });
 
  $.event.special.hoverintent = {
    setup: function() {
      $( this ).bind( "mouseover", jQuery.event.special.hoverintent.handler );
    },
    teardown: function() {
      $( this ).unbind( "mouseover", jQuery.event.special.hoverintent.handler );
    },
    handler: function( event ) {
      var currentX, currentY, timeout,
        args = arguments,
        target = $( event.target ),
        previousX = event.pageX,
        previousY = event.pageY;
 
      function track( event ) {
        currentX = event.pageX;
        currentY = event.pageY;
      };
 
      function clear() {
        target
          .unbind( "mousemove", track )
          .unbind( "mouseout", clear );
        clearTimeout( timeout );
      }
 
      function handler() {
        var prop,
          orig = event;
 
        if ( ( Math.abs( previousX - currentX ) +
            Math.abs( previousY - currentY ) ) < 7 ) {
          clear();
 
          event = $.Event( "hoverintent" );
          for ( prop in orig ) {
            if ( !( prop in event ) ) {
              event[ prop ] = orig[ prop ];
            }
          }
          // Prevent accessing the original event since the new event
          // is fired asynchronously and the old event is no longer
          // usable (#6028)
          delete event.originalEvent;
 
          target.trigger( event );
        } else {
          previousX = currentX;
          previousY = currentY;
          timeout = setTimeout( handler, 100 );
        }
      }
 
      timeout = setTimeout( handler, 100 );
      target.bind({
        mousemove: track,
        mouseout: clear
      });
    }
  };

</script>

		<c:if test="${not empty available_symbols}">
			<script>
			var availSymbols = [];
			<c:forEach items="${available_symbols}" var="sym">
				availSymbols.push("${sym}");
			</c:forEach>
			console.log(availSymbols.length);
			$("#searchBar").autocomplete({
				source: availSymbols
			});
			</script>
		</c:if>
		
		<c:if test="${not empty available_data}">	
			<script>
			var availData = [];
			<c:forEach items="${available_data}" var="sym">
				availData.push("${sym}");
			</c:forEach>
			
			</script>
		</c:if>
		
		<script>
				var userTickers = [];
				var userName = "";
				
				<c:if test="${not empty userData}">
					userName = "${userData.firstName}";
					console.log(userName+" is in the house...");
					<c:forEach items="${userData.favoriteSymbols}" var="sym">
						userTickers.push("${sym.tickerSymbol}");
					</c:forEach>
				</c:if>
				console.log("tickers: "+userTickers.length);
				var tickForm = document.getElementById("userTickersTable");
				var addTicker = true;
				var toks = "${current_ticker}".split(" ");
				var curTick = toks[0];
				
				for(var i = 0; i < userTickers.length; i++) {
					if(userTickers[i] == curTick) addTicker = false;
					var r = tickForm.insertRow(i+1);
					var c = r.insertCell(0);
					c.id = userTickers[i];
					$(c).on('click', function(ev) {
						var ticker = ev.target.id;
						document.getElementById("searchBar").value = ticker;
						document.getElementById("searchTickerForm").submit();
					});
					$(c).css("cursor","pointer");
					$(c).mouseenter(function() {
						  $(this).css("color","red")
					});
					$(c).mouseout(function() {
						  $(this).css("color","black")
					});
					c.innerHTML = userTickers[i];
					var c2 = r.insertCell(1);
					c2.innerHTML = "<img style=\"cursor: pointer;\" src=\"images/delete-icon.png\" onclick=\"removeTicker('"+userTickers[i]+"');\"></img>"
				}
				
				if(addTicker && userName.length > 0) {
					$("#tickerNameDiv").append("<img style=\"cursor: pointer;\" src=\"images/add_icon.png\" onclick=\"addUserTicker('"+curTick+"');\"></img>");
				}
				
				function removeTicker(tick) {
					console.log("removing: "+tick);
					document.getElementById("removeTickerSymbol").value = tick;
					document.getElementById("userTickersForm").submit();
				}
				
				function addUserTicker(tick) {
					console.log("adding: "+tick);
					document.getElementById("addTickerInput").value = tick;
					document.getElementById("addTickersForm").submit();
				}
		</script>
		
	</body>
</html>
