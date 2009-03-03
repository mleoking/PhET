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
			function boo() {
				alert("boobo");
			}
		</script>
		<script language="javascript" src="general-query.js">
		</script>
		<script type="text/javascript" src="calendarDateInput.js">
		/***********************************************
		* Jason's Date Input Calendar- By Jason Moon http://calendar.moonscript.com/dateinput.cfm
		* Script featured on and available at http://www.dynamicdrive.com
		* Keep this notice intact for use.
		***********************************************/
		</script>
		<script type="text/javascript" src="CalendarPopup.js">
		</script>
		<link rel="stylesheet" type="text/css" href="general-query.css" />
	</head>
	<body>
		<div id="content">
			<form name="query_info" id="query_info">
				<div id="query_type">
					<span class="field">Query type:&nbsp;</span>
					<select name="query" id="query">
						<optgroup label="Counts">
							<option value="session_count">Session Counts</option>
							<option value="message_count">Message Counts</option>
						</optgroup>
						<optgroup label="Raw Data">
							<option value="recent_messages">Recent Messages</option>
							<option value="errors">Message Errors</option>
							<option value="full_table">Raw Tables</option>
						</optgroup>
						<optgroup label="Other">
							<option value="unique_users">Unique user Information</option>
							<!-- <option value="raw_tables">Raw Tables (DO NOT CLICK IF TABLES ARE LARGE)</option> -->
						</optgroup>
					</select>
				</div>
				<div id="query_options">
				</div>
				<div id="timestampholder" class='constraint'>
					<span class="field">Timestamp:&nbsp;</span>
					<select name="timestamptype" id="timestamptype" onchange='javascript:changeTimestampType()'>
						<option value='all'>All</option>
						<option value='after'>After</option>
						<option value='before'>Before</option>
						<option value='between'>Between</option>
					</select>
					<script>DateInput('timestampA', true, 'YYYY-MM-DD', '2009-01-01')</script>
					<span id='timestampand'>and</span>
					<script>DateInput('timestampB', true, 'YYYY-MM-DD', '2010-01-01')</script>
				</div>
				<div id="formcontrols">
					<button name="show_table" id="show_table" type="button">show below</button>
					<button name="show_csv" id="show_csv" type="button">download csv</button>
				</div>
				
			</form>
			<div id="debug">
			</div>
			<div id="results">
			</div>
		</div>
	</body>
</html>
