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
		<script language="javascript" src="general-query.js" />
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
						<optgroup label="Other">
							<option value="recent_messages">Recent Messages</option>
						</optgroup>
					</select>
				</div>
				<div id="query_options">
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
