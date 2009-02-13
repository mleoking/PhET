
function fid(target) {
	return document.getElementById(target);
}

function ahah(url, target) {
	document.getElementById(target).innerHTML = 'Loading...';
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
			fid(target).innerHTML = req.responseText;
		} else {
			fid(target).innerHTML=" AHAH Error:\n"+ req.status + "\n" +req.statusText;
		}
	}
}


function loadHTML(url) {
	ahah(url, "results");
}

function getValue(str) {
	if(document.query_info[str]) {
		return document.query_info[str].value;
	} else {
		return null;
	}
}

function setValue(str, val) {
	document.query_info[str] = val;
}




function constraintString(desc, field, url) {
	var str = "";
	str += "<div class='constraint'>";
	str += "<span class='field'>" + desc + ": </span>";
	str += "<span id='" + field + "'>";
	str += "(<a href=\"javascript:ahah('" + url + "', '" + field + "')\">specify</a>)";
	str += "</span>";
	str += "</div>";
	return str;
}

function commonConstraintString(desc, field) {
	return constraintString(desc, field, "query-combo-box.php?select_name=" + field + "&group=" + field);
}

// called when project is changed
function specify_name() {
	fid("name_holder").innerHTML = constraintString("Name", "sim_name", "query-combo-box.php?select_name=sim_name&group=sim_name&hide_all=true&sim_project=" + getValue("sim_project"));
}

var simCountsConstraints = ["sim_project", "sim_name", "sim_dev", "sim_type", "sim_deployment", "sim_distribution_tag", "host_simplified_os"];


function setupSimCounts() {
	
	fid("results").innerHTML = "";
	fid("debug").innerHTML = "";
	
	var opts = fid("query_options");
	var str = "";
	
	for(idx in simCountsConstraints) {
		delete document.query_info[simCountsConstraints[idx]];
	}
	
	str += constraintString("Project", "sim_project", "select-projects.php");
	str += "<div id='name_holder'></div>";
	str += commonConstraintString("Dev", "sim_dev");
	str += commonConstraintString("Type", "sim_type");
	str += commonConstraintString("Deployment", "sim_deployment");
	str += commonConstraintString("Distribution Tag", "sim_distribution_tag");
	str += commonConstraintString("OS", "host_simplified_os");
	
	str += "<div class='constraint'><span class='field'>Group by: </span><select name='group' id='group' onchange='javascript:build_order()'>";
	str += "<option value='none'>none</option>";
	str += "<optgroup label='All'>";
		//str += "<option value='month'>month</option>";
		str += "<option value='week'>Week</option>";
		str += "<option value='month'>Month</option>";
		str += "<option value='version'>Sim Version</option>";
		str += "<option value='sim_locale'>Sim Locale</option>";
		str += "<option value='host_locale'>Host Locale</option>";
		str += "<option value='host_simplified_os'>OS</option>";
		str += "<option value='sim_type'>Java / Flash</option>";
		str += "<option value='sim_project'>Project</option>";
		str += "<option value='sim_name'>Name</option>";
		str += "<option value='project_name'>Project, Name</option>";
		str += "<option value='sim_deployment'>Deployment</option>";
		str += "<option value='sim_distribution_tag'>Distribution Tag</option>";
	str += "</optgroup>";
	str += "<optgroup label='Java'>";
		str += "<option value='host_java_os'>OS (full)</option>";
		str += "<option value='host_java_os_name'>OS (name)</option>";
		str += "<option value='host_java_timezone'>Timezone</option>";
	str += "</optgroup>";
	str += "<optgroup label='Flash'>";
		str += "<option value='host_flash_domain'>Domain</option>";
		str += "<option value='host_flash_os'>OS</option>";
		str += "<option value='host_flash_version_major'>Flash Player Version (major)</option>";
		str += "<option value='host_flash_version'>Flash Player Version (full)</option>";
	str += "</optgroup>";
	str += "</select></div>";
	
	str += "<div class='constraint'><span id='orderholder'></span>";
	str += "<select name='order' id='order'>";
	str += "<option value='ascending'>ascending</option>";
	str += "<option value='descending'>descending</option>";
	str += "</select>";
	str += "</div>";
	
	opts.innerHTML = str;
	
	build_order();
}

function build_order() {
	var str = "";
	str += "<span class='field'>Order by: </span><select name='ordercolumn' id='ordercolumn'>";
	//str += "<option value='none'>none</option>";
	str += "<option value='" + getValue("query") + "'>" + getValue("query") + "</option>";
	if(getValue("group") !== null && getValue("group") != "none") {
		str += "<option value='" + getValue("group") + "'>" + getValue("group") + "</option>";
	}
	str += "</select>";
	
	
	
	var old_order = getValue("order");
	
	fid("orderholder").innerHTML = str;
	
	if(old_order !== null) {
		setValue("order", old_order);
	}
}

function setupSessionCounts() {
	setupSimCounts();
}

function setupMessageCounts() {
	setupSimCounts();
}



function query_change() {
	switch(getValue('query')) {
		case "session_count":
			setupSessionCounts(); break;
		case "message_count":
			setupMessageCounts(); break;
	}
}




function query_string() {
	var str = "query=" + getValue("query");
	
	if(getValue("query") == "session_count" || getValue("query") == "message_count") {
		for(idx in simCountsConstraints) {
			var name = simCountsConstraints[idx];
			if(getValue(name) !== null && getValue(name) != "all") {
				str += "&" + name + "=" + getValue(name);
			}
		}
		if(getValue('group') != "none") {
			str += "&group=" + getValue('group');
		}
		
		str += "&order=";
		if(getValue("order") == "descending") {
			str += "desc:";
		}
		str += getValue("ordercolumn");
	}
	
	return str;
}


function show_table() {
	fid("debug").innerHTML = query_string();
	loadHTML("query-table.php?" + query_string());
}

function show_csv() {
	window.location.href = ("query-csv.php?" + query_string());
}


onload = function() {
	fid("query").onchange = function() {
		query_change();
	}
	fid("show_table").onclick = function() {
		show_table();
	}
	fid("show_csv").onclick = function() {
		show_csv();
	}
	
	setupSessionCounts();
}




/*

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

*/


/*
<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
?>
<html>
	<head>
		<title>PhET Statistics - General Query</title>
		<script type="text/javascript" src="general-query.js" />
	</head>
	<body>
		<form name="query_info">
			<p>
				Query type:
				<select name="query">
					<optgroup label="Counts">
						<option value="session_count">Session Counts</option>
						<option value="message_count">Message Counts</option>
					</optgroup>
					<optgroup label="Other">
					</optgroup>
				</select>
			</p>
			<p>
				Project
				<span id='projects'>
					(<a href="javascript:ahah('select-projects.php', 'projects')">specify</a>)
				</span>
			</p>
			<p>
				Group by:
				<select name="group">
					<option value="none">none</option>
					<optgroup label="All sims">
						<option value="week">Week</option>
						<option value="month">Month</option>
						<option value="version">Sim Version</option>
						<option value="sim_locale">Sim Locale</option>
						<option value="host_locale">Host Locale</option>
						<option value="host_simplified_os">OS</option>
						<option value="sim_type">Java / Flash</option>
						<option value="sim_project">Project</option>
						<option value="sim_name">Name</option>
						<option value="project_name">Project, Name</option>
						<option value="sim_deployment">Deployment</option>
						<option value="sim_distribution_tag">Distribution Tag</option>
					</optgroup>
					<optgroup label="Java sims">
					</optgroup>
					<optgroup label="Flash sims">
						<option value="host_flash_domain">Domain</option>
						<option value="host_flash_os">OS</option>
						<option value="host_flash_version">Flash Player</option>
					</optgroup>
				</select>
			</p>
			<p>
				Order by:
				<select name="ordercol">
					<option value="none">none</option>
					<option value="type">type</option>
					<option value="group">group</option>
				</select>
				<select name="order">
					<option value="ascending">ascending</option>
					<option value="descending">descending</option>
				</select>
			</p>
			
			<p>
				<button name="show_table" type="button" onclick="javascript:show_below()">show below</button>
				<button name="show_csv" type="button" onclick="javascript:download_csv()">download csv</button>
			</p>
		</form>
		<div id="debug">
		</div>
		<div id="content">
		</div>
	</body>
</html>
*/

