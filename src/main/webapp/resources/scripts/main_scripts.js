$(document).ready(function() {
	$("button, input[type='button']").hover(function (ev) {
		$(this).stop().animate({"opacity": 1});
	}, function() {
		$(this).stop().animate({"opacity": 0.7});
	});
	$("button, input[type='button']").click(function() {
		try {
			this.form.submit();
		} catch (e) { }
	});
	
	$("#BodyWrapper").hide();
})