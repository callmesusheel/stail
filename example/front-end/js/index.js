var autoscroll = false;

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
//	$( "#menu" ).draggable();
	requestTails();
}

function showTailLinks(tails) {
	if (tails) {
		for (id in tails) {
			var command = tails[id].command.join(' ');
			command = command.replace(/'/gi, "\\'");
			command = "'" + command + "'";
			$('#tails').append('<li class="tailSelector"><a href="javascript: switchToLog('+id+', '+command+');">"'+tails[id].alias+'"</a></li>');
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
	$('#command').empty();
	$('#command').append("Command: " + command);
	
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
//			$('#logWindow').append(
//					logResponse.data.replace(/\n/gi, "<br/>").replace(/\t/gi, "&nbsp;&nbsp;&nbsp;&nbsp;"));
			
			if (id == currentId) {
				scheduled = setTimeout(function() { showLog(id, index) }, 1000);
			}
			
			if (autoscroll) {
				$("html").animate({ scrollTop: $(document).height() }, "fast");
			}
		}
	});
}


