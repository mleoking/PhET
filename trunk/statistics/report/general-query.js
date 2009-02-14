
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


function constraintGeneralString(desc, field, href) {
	var str = "";
	str += "<div class='constraint'>";
	str += "<span class='field'>" + desc + ": </span>";
	str += "<span id='" + field + "'>";
	str += "(<a href=\"" + href + "\">specify</a>)";
	str += "</span>";
	str += "</div>";
	return str;
}

function constraintString(desc, field, url) {
	return constraintGeneralString(desc, field, "javascript:ahah('" + url + "', '" + field + "')");
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
		str += "<option value='day'>Day</option>";
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
		str += "<option value='host_java_os'>Java OS (full)</option>";
		str += "<option value='host_java_os_name'>Java OS (name)</option>";
		str += "<option value='host_java_timezone'>Java Timezone</option>";
	str += "</optgroup>";
	str += "<optgroup label='Flash'>";
		str += "<option value='host_flash_domain'>Flash Domain</option>";
		str += "<option value='host_flash_os'>Flash OS</option>";
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
	
	setupTimestamp();
}

function setupTimestamp() {
	fid("timestampholder").style.display = "block";
	
	changeTimestampType();
}

function unsetTimestamp() {
	fid("timestampholder").style.display = "none";
}

function changeTimestampType() {
	switch(getValue('timestamptype')) {
		case "all":
			fid("x_timestampA").style.display = "none";
			fid("timestampand").style.display = "none";
			fid("x_timestampB").style.display = "none";
			break;
		case "after":
		case "before":
			fid("x_timestampA").style.display = "inline";
			fid("timestampand").style.display = "none";
			fid("x_timestampB").style.display = "none";
			break;
		case "between":
			fid("x_timestampA").style.display = "inline";
			fid("timestampand").style.display = "inline";
			fid("x_timestampB").style.display = "inline";
			break;
	}
}

/*
		<form>
			<script>DateInput('orderdate', true, 'YYYY-MM-DD')</script>
			<input type="button" onClick="alert(this.form.orderdate.value)" value="Show date value passed">
		</form>
*/

function setupRecentMessages() {
	var str = "";
	
	str += "<div class='constraint'>";
	str += "Sim type: <select name='recent_sim_type' id='recent_sim_type'>";
	str += "<option value='all'>All</option>";
	str += "<option value='java'>Java</option>";
	str += "<option value='flash'>Flash</option>";
	str += "</select>";
	str += "</div>";
	
	str += "<div class='constraint'>";
	str += "Quantity: <input type='text' name='count' size='5' maxlength='10' id='count' value='10'>";
	str += "</input>";
	str += "</div>";
	
	fid("query_options").innerHTML = str;
	
	unsetTimestamp();
}

function setupErrors() {
	fid("query_options").innerHTML = "";
	unsetTimestamp();
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
	
	fid("results").innerHTML = "";
	fid("debug").innerHTML = "";
	
	switch(getValue('query')) {
		case "session_count":
			setupSessionCounts(); break;
		case "message_count":
			setupMessageCounts(); break;
		case "recent_messages":
			setupRecentMessages(); break;
		case "errors":
			setupErrors(); break;
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
		
		if(getValue('timestamptype') != "all") {
			str += "&timestamptype=" + getValue("timestamptype");
			str += "&timestampA=" + getValue("timestampA");
			str += "&timestampB=" + getValue("timestampB");
		}
		
	} else if(getValue("query") == "recent_messages") {
		str += "&recent_sim_type=" + getValue('recent_sim_type');
		str += "&count=" + getValue('count');
	}
	
	return str;
}


function show_table() {
	fid("debug").innerHTML = query_string();
	loadHTML("query-demo.php?" + query_string());
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
	
	//setupSessionCounts();
	query_change();
}

onkeydown = function(code) {
	if(code.which == 13) {
		show_table();
	}
}
