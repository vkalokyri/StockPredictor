<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>

    <link href="scripts/nv.d3.css" rel="stylesheet" type="text/css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/d3/3.5.2/d3.min.js" charset="utf-8"></script>
    <script src="scripts/nv.d3.js"></script>

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
            height: 100%;
            width: 100%;
        }
        button {
            margin: 2px;
            margin-left: 70px;
        }
    </style>
    
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
	
		<script>
			var userTickers = [];
			<c:if test="${not empty userData}">
				<c:forEach items="${userData}" var="sym">
					userTickers.push("${sym}");
				</c:forEach>
			</c:if>
		</script>
		
		<div class="wrapper" style="background-color:#fff">
			<div class="topDiv">
				
				<div onclick="location.href='/stockpred/portal';" style="cursor: pointer;" class="logo">
					<h1 >RU Finance</h1>
				</div>
				    
				<div id="searchDiv">
					<form method="post" action="search">
						<input id="tags" name="searchTerms" size="50" role="combobox" type="text" placeholder="Search..."  class="textBox" >
						<input  type="submit" class="searchButton" value=" " />
					</form>
				</div>
			</div>
			<div id="breadcrumb" class="breadcrumb">			
				
			</div>
			<div class="mainDiv">
	 			<div class="filtering">
					<div style="margin-top:12px">
					<span style="font-size:13px; font-weight:bold; margin-left:10px">Any time</span>
					<ul style="list-style-type: none; font-size:12px; margin-left:-20px; border-spacing:6px;">
						<li>
							  <a onclick="todayPlot()" href="#">Today</a>
						</li>
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
					</ul>
					
				</div>
				</div>
				<div class="resultsDiv">
					<div class="tickerInfoDiv">	
						<div style="margin-top:7px;">						
							<span style="font-size:18px; font-weight:bold;">Google Inc. (GOOG)</span>
						</div>
						<div>
							<span style="font-size:15px">Current price:</span><span style="margin-left:5px">555.22</span>
						</div>
						<span style="font-size:15px">Predicted tomorrow's price:</span><span style="margin-left:5px;">556.43</span>
						<span class="upPrice"><img width="10" height="14" style="margin-right:2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="pos_arrow" alt="Up">76.77</span>
						<span class="upPrice">(1.71%)</span>
						<div>
							<span style="font-size:15px">Predicted 5-day average price:</span><span style="margin-left:5px">555.22</span>
							<span class="dropPrice"><img width="10" height="14" style="margin-right:2px;" border="0" src="http://l.yimg.com/os/mit/media/m/base/images/transparent-1093278.png" class="neg_arrow" alt="Down">76.77</span>
							<span class="dropPrice">(1.71%)</span>
						</div>
					</div>
					<div class="suggestionDiv">
						<span style="position: absolute; margin-top: 13px; margin-left: 22px;">BUY</span>
					</div>
					<div class="graphDiv">
						<div class="graphHeader" >	
							<span style="color:#bbb; font-size:14px">GOOG | Last updated: 04/20/2015</span>
						</div>


<div id="chart1" class='with-3d-shadow with-transitions'>
    <svg> </svg>
</div>


						<!--canvas id="myChart" width="560px" height="300px"></canvas-->	
					</div>
					
				</div>
				<div class="personalSpace">
					<div class="myTickers">
						<div class="tickersHeader">
							<span style="margin-left:10px">My Tickers</span>
						</div>
						<table id="userTickersTable">
							<thead>
								<tr>
									<th scope="col">Symbol</th>
									<th scope="col">% Chg</th>
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
					</div>
					<div class="myNews">
						<div class="tickersHeader">
							<span style="margin-left:10px">My News</span>
						</div>
						<table>
							<tbody>
								<tr><td>GOOG</td><td><span>jbhghghgf jhsgfsjg hsf hsgdf</span></td></tr>						
								<tr><td>YHOO</td><td><span>dhdhfnffnfdjghhg jhghshsg shgjksh</span></td></tr>						
								<tr><td>GOOG</td><td><span>bhb hgfhs gfhsgf sgfhsgf</span></td></tr>
							</tbody>
							
						</table>
					</div>
				</div>
			</div>
			
		</div>

		<div class="footer">
			<span>RU Finance © 2015</span>
		</div>
		<script>



function drawNewPlot(datapoints) {
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
	    data: {startDate:startDate_ , endDate:endDate_},
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

window.onload = function() {
console.debug("hello");
//var startDate_ = $.now()-24*3600*1000;
//var endDate_ = $.now();
//doAjax(startDate_, endDate_);
todayPlot();
//setTimeout(doAjax, interval);			
}

function todayPlot() { doAjax($.now()-24*3600*1000, $.now()); }
function pastWeekPlot() { doAjax($.now()-7*24*3600*1000, $.now()); }
function pastMonthPlot() { doAjax($.now()-30*3600*1000, $.now()); }
function past6MonthsPlot() { doAjax($.now()-6*30*24*3600*1000, $.now()); }
function pastYearPlot() { doAjax($.now()-365*24*3600*1000, $.now()); }

</script>

		<c:if test="${not empty available_symbols}">
			<script>
			var availSymbols = [];
			<c:forEach items="${available_symbols}" var="sym">
				availSymbols.push("${sym}");
			</c:forEach>
			console.log(availSymbols.length);
			$("#tags").autocomplete({
				source: availSymbols
			});
			</script>
		</c:if>
		
	<script>
		// complete users' ticker here
		
		$(function() {
			var tickersTab = document.getElementById("userTickersTable");
			for(var i = 0; i < userTickers.length; i++) {
				var row = tickersTab.insertRow(i);
				var cell1 = row.insertCell(0);
				var cell2 = row.insertCell(1);
				
				cell1.innerHTML = "Hello "+i;
				cell2.innerHTML = "Hello2 "+i;
			}
		});
		
	</script>

	</body>
</html>
