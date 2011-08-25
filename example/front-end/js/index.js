var xhr = new easyXDM.Rpc({
	remote : server + "/cors/"
}, {
	remote : {
		request : {} // request is exposed by /cors/
	}
});

function requestTails() {
	xhr.request({
		url : "dispacher",
		method : "POST",
		data : {
			action : "requestTails"
		}
	}, function(response) {
		if (response.status == 200) {
			showTailLinks(JSON.parse(response.data));	
		}
	});
}

function initialize() {
	requestTails();
}

function dispose() {
	if (scrollTimer) {
		clearInterval(scrollTimer);
	}
}

function showTailLinks(tails) {
	if (tails) {
		for (id in tails) {
			var command = tails[id].command.join(' ');
			command = command.replace(/'/gi, "\\'");
			command = "'" + command + "'";
			$('#tails').append('<li><a href="javascript: switchToLog('+id+', '+command+');"><span>'+tails[id].alias+'</span></a></li>');
		}
	}
}

var scheduled = null;
var currentId = null;

var switchToLog = function (id, command, idx) {
	currentId = id;
	
	if (scheduled) {
		clearTimeout(scheduled);
		scheduled = null;
	}
	
	$('#loader').css("visibility", "visible");
	$('#command').css("visibility", "visible");
	$('#autoscroll').css("visibility", "visible");
	$('#command').empty();
	$('#command').append(command);
	
	$('#logWindow').empty();

	showLog(id, idx);
}

function showLog(id, idx) {
	var index = idx | 0;
	
	xhr.request({
		url : "dispacher",
		method : "POST",
		data : {
			action: "proceedLog",
			id: id,
			index: index
		}
	}, function(response) {
		if (response.status == 200) {
			var logResponse = JSON.parse(response.data);
			var index = logResponse.marker.index;
			$('#logWindow').append("<pre>" + logResponse.data + "</pre>");
			
			if (id == currentId) {
				scheduled = setTimeout(function() { showLog(id, index) }, 1000);
			}
		}
	});
}


var scrollTimer = null;

function switchAutoScroll() {
	if (scrollTimer) {
		clearInterval(scrollTimer);
		scrollTimer = null;
		$('#autoscroll').empty();
		$('#autoscroll').append("Enable auto-scroll");
	} else {
		scrollTimer = setInterval("scroll()", 1000);
		$('#autoscroll').empty();
		$('#autoscroll').append("Disable auto-scroll");
	}
	
}

function scroll() {
	$("#logWindow").scrollTop($("#logWindow")[0].scrollHeight);
}


