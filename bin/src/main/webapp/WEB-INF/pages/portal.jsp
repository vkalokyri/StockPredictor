<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<jsp:include page="/WEB-INF/pages/header.jsp"></jsp:include>
	<script type="text/javascript" src="scripts/portal.js"></script>
	<link rel="stylesheet" href="css/portal_style.css">
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>332:568 Project</title>
</head>
	<body>
	
		<div id="page-cover"></div>
		
		<div id="loginForm">
			<form id="authForm" method="post" action="authenticate" >
				<span>
					<input type="text" placeholder="User Name" name="userName"/>
					<br>
					<input type="password" placeholder="Password" name="userPassword"/>
					<br>
					<input type="button" value="Login"/>
				</span>
			</form>
		</div>
		
		<div class="Header">
			<div id="loginDiv">
				<c:choose>
					<c:when test="${empty userData}">
						<input id="loginPrompt" type="button" value="Login" onclick="openDialog('login');" />
						<p class="SmallNote" style="display: inline; font-size: 80%" >Not a user?<a class="ButtonLink" href="#" onclick="openDialog('register');" > Register</a></p>
					</c:when>
					<c:otherwise>
						<input id="loginPrompt" type="button" value="Dashboard" onclick="document.getElementById('authForm').submit();" />
					</c:otherwise>
				</c:choose>
			</div>
		</div>
		
		<div class="BodyContainer">
			<div id="introDiv" class="FloatingDiv">
				<p>ECE568 Project</p>
				<p class=BreakLine />
				<p><b>Stock Prediction System</b></p>
			</div>
			<div id="searchDiv">
				<form method="post" action="search">
					<input type="text" id="searchBar" name="tickerName" placeholder="Search symbol" />
					<input  type="submit" class="searchButton" value=" " />
				</form>
			</div>
			<div id="registrationDiv">
				<form method="post" action="register">
					<c:if test="${not empty registration_successful}">
						<div class="Highlight">${registration_successful}</div>
					</c:if>
					<input class="RegInput" type="text" placeholder="First Name" name="firstName"/>
					<input class="RegInput" type="text" placeholder="Last Name" name="lastName"/>
					<c:choose>
						<c:when test="${not empty user_registration_failed}">
							<div class="Error">${user_registration_failed}</div>
							<input class="RegInput InvalidRegField" type="text" placeholder="User Name" name="userName"/><br>
							<input class="RegInput InvalidRegField" type="text" placeholder="Email Address" name="userEmail"/><br>
						</c:when>
						<c:otherwise>
							<input class="RegInput" type="text" placeholder="User Name" name="userName"/><br>
							<input class="RegInput" type="text" placeholder="Email Address" name="userEmail"/><br>
						</c:otherwise>
					</c:choose>
					<input class="RegInput" type="password" placeholder="Password" name="userPassword"/><br>
					<input class="RegInput" type="password" placeholder="Repeat Password" /><br>
					<input type="button" value="Register"/>
				</form>
			</div>
		</div>
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
			
	</body>
</html>
