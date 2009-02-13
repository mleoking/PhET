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
