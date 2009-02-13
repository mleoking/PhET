<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
?>
<html>
	<head>
		<title>PhET Statistics - General Query</title>
		<script type="text/javascript">
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
				} else if(col == "group") {
					return str + document.query_info.group.value;
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
		</script>
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
					<a href="javascript:ahah('select-projects.php', 'projects')">
						(load)
					</a>
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
						<option value="os">OS</option>
						<option value="sim_type">Java / Flash</option>
						<option value="sim_project">Project</option>
						<option value="sim_name">Name</option>
						<option value="project_name">Project, Name</option>
					</optgroup>
					<optgroup label="Java sims">
					</optgroup>
					<optgroup label="Flash sims">
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
