<?php
	include("queries.php");
	include("../db-auth.php");
	include("../db-stats.php");
	$link = setup_mysql();
	
	print <<<INTRO
<html>
<head>
<title>PhET Statistics - General</title>
</head>
<body>
INTRO;
	
	$total_sessions = report_single_value(array("query" => "session_count"));
	$total_sessions_nd = report_single_value(array("query" => "session_count", "sim_dev" => "false"));
	$total_messages = report_single_value(array("query" => "message_count"));
	$total_messages_nd = report_single_value(array("query" => "message_count", "sim_dev" => "false"));
	print "<p>";
	print "Total sessions: {$total_sessions_nd} ({$total_sessions})<br />";
	print "Total messages: {$total_messages_nd} ({$total_messages})<br />";
	
	print report_table("Total sessions, by flash / java",
	    "query=session_count&sim_dev=false&group=sim_type");

	print report_table("Total sessions, by simulation",
	    "query=session_count&sim_dev=false&group=sim_name&order=sim_name");

	print report_table("Total sessions, by week",
	    "query=session_count&sim_dev=false&group=week&order=week");

	print report_table("Total sessions, by os",
	    "query=session_count&sim_dev=false&group=os&order=desc:session_count");

	print report_table("Total sessions, by deployment",
	    "query=session_count&sim_dev=false&group=sim_deployment&order=desc:session_count");

	print report_table("Total sessions, by distribution tag",
	    "query=session_count&sim_dev=false&group=sim_distribution_tag&order=desc:session_count");

	print report_table("Total sessions, by language",
	    "query=session_count&group=sim_locale_language&order=desc:session_count");

	print report_table("Total sessions, by sim locale",
	    "query=session_count&group=sim_locale&order=desc:session_count");

	print report_table("Total sessions, by host locale",
	    "query=session_count&group=host_locale&order=desc:session_count");
	    
	print "</p>";
	
	
	
	print <<<OUTRO
</body>
</html>
OUTRO;
?>