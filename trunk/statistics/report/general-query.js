function ahah(url, target) {
	document.getElementById(target).innerHTML = 'Query Executing...';
	if (window.XMLHttpRequest) {
		req = new XMLHttpRequest();
	} else if (window.ActiveXObject) {
		req = new ActiveXObject("Microsoft.XMLHTTP");
	}
	if (req != undefined) {
		req.onreadystatechange = function() {ahahDone(url, target);};
		req.open("GET", url, true);
		req.send("");
	}
}  

function ahahDone(url, target) {
	if (req.readyState == 4) { // only if req is "loaded"
		if (req.status == 200) { // only if "OK"
			document.getElementById(target).innerHTML = req.responseText;
		} else {
			document.getElementById(target).innerHTML=" AHAH Error:\n"+ req.status + "\n" +req.statusText;
		}
	}
}

function loadHTML(url) {
	ahah(url, "content");
}

function order_string() {
	var col = document.query_info.ordercol.value;
	var ord = document.query_info.order.value;
	
	if(col == "none") { return ""; }
	
	var str = "&order=";
	
	if(ord == "descending") {
		str += "desc:";
	}
	
	if(col == "type") {
		return str + document.query_info.query.value;
	} else if(col == "group" && document.query_info.group.value != "none") {
		return str + document.query_info.group.value;
	} else {
		return "";
	}
}

function query_string() {
	var str = "";
	str += "query=" + document.query_info.query.value;
	if(document.query_info.sim_project && document.query_info.sim_project.value != "any") {
		str += "&sim_project=" + document.query_info.sim_project.value;
	}
	if(document.query_info.group.value != "none") {
		str += "&group=" + document.query_info.group.value;
	}
	str += order_string();
	document.getElementById("debug").innerHTML = str;
	return str;
}

function show_below() {
	loadHTML("query-table.php?" + query_string());
	alert();
}

function download_csv() {
	window.location.href = ("query-csv.php?" + query_string());
}

onload = function() {
	//ahah("select-projects.php", "projects");
}