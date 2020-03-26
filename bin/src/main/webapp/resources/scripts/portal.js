$(document).ready(function() {
	console.log("Portal loaded");
	
	$(".Error").fadeIn(1000);
	$(".Highlight").fadeIn(1000);
});

$(function() {
	$("#loginForm").dialog({
		autoOpen: false,
		modal: true,
		position: {
			my: "right top",
			at: "right top",
			of: parent
		},
		close: function(event,ui) {
			$("#page-cover").fadeOut();
		}
	});
	
	$("#registrationDiv").dialog({
		autoOpen: false,
		modal: true,
		width: $(window).width()*0.4,
		position: {
			my: "right top",
			at: "right top",
			of: parent
		},
		close: function(event,ui) {
			$("#page-cover").fadeOut();
		}
	});
});

function openDialog(d) {
	if(d == 'login') {
		$("#loginForm").dialog("open");
	} else if (d == 'register') {
		$("#registrationDiv").dialog("open");
	}
	$("#page-cover").fadeIn();
}

