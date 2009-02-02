<?php
    include("db-auth.php");
	include("db-stats.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	function display_desc($desc) {
		print "\n<h4>{$desc}</h4>\n";
	}
	
	function display_query($query) {
		$show_results = true;
		
		$start_time = microtime(true);
		$result = mysql_query($query) or die("SELECT ERROR: " . mysql_error());
		$end_time = microtime(true);
		$num_rows = mysql_num_rows($result);
		if($show_results) {
			print "<table border=1>\n";
		} else {
			print "<table border=1 style='display: none;'>\n";
		}
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			print "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		print "</tr>\n";
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				print "\t<td><font face=arial size=2/>{$field}</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	
	$total_start_time = microtime(true);
	
	print "<h1>Report for all simulations</h1>";
	
	display_desc("total number of messages");
	display_query("SELECT COUNT(*) FROM session;");
	
	display_desc("total number of messages (non-dev)");
	display_query("SELECT COUNT(*) FROM session WHERE sim_dev = false;");
	
	display_desc("total number of sim runs (non-dev)");
	display_query("SELECT SUM(sim_sessions_since) FROM session WHERE sim_dev = false;");
	
	display_desc("number of times each sim has been run (non-dev)");
	display_query("SELECT DISTINCT sim_name.name AS sim_name, SUM(session.sim_sessions_since) AS sim_runs FROM session, sim_name WHERE (sim_name.id = session.sim_name AND session.sim_dev = false) GROUP BY sim_name.name ORDER BY sim_name.name;");
	
	display_desc("number of times sims have been run by week");
	display_query("SELECT YEARWEEK(timestamp), SUM(session.sim_sessions_since) FROM session GROUP BY YEARWEEK(timestamp);");
	
	display_desc("number of times each simplified OS has been seen (non-dev)");
	display_query("SELECT simplified_os.name, SUM(session.sim_sessions_since) FROM session, simplified_os WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each deployment type has been seen (non-dev)");
	display_query("SELECT deployment.name, SUM(session.sim_sessions_since) FROM session, deployment WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each (non-null) distribution tag has been seen (non-dev)");
	display_query("SELECT distribution_tag.name, SUM(session.sim_sessions_since) FROM session, distribution_tag WHERE (distribution_tag.id = session.sim_distribution_tag AND distribution_tag.name IS NOT NULL AND session.sim_dev = false) GROUP BY distribution_tag.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times the distribution tag Wiley has been seen, by year and month (non-dev)");
	// TODO: show months where no sims were run?
	display_query("SELECT YEAR(session.timestamp), MONTH(session.timestamp), SUM(session.sim_sessions_since) FROM session, (SELECT id FROM distribution_tag WHERE name = 'Wiley') AS x WHERE (session.sim_distribution_tag = x.id AND session.sim_dev = false) GROUP BY YEAR(session.timestamp), MONTH(session.timestamp);");
	
	display_desc("number of times each flash domain has been seen (non-dev)");
	display_query("SELECT flash_domain.name, SUM(session.sim_sessions_since) FROM session, session_flash_info, flash_domain WHERE (session.id = session_flash_info.session_id AND session_flash_info.host_flash_domain = flash_domain.id AND sim_dev = false) GROUP BY flash_domain.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each flash OS has been seen (non-dev)");
	display_query("SELECT flash_os.name, SUM(session.sim_sessions_since) FROM session, session_flash_info, flash_os WHERE (session.id = session_flash_info.session_id AND session_flash_info.host_flash_os = flash_os.id AND sim_dev = false) GROUP BY flash_os.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each java OS has been seen (non-dev)");
	display_query("SELECT java_os_name.name, java_os_version.name, java_os_arch.name, SUM(session.sim_sessions_since) FROM session, session_java_info, java_os_name, java_os_version, java_os_arch WHERE (session.id = session_java_info.session_id AND session_java_info.host_java_os_name = java_os_name.id AND session_java_info.host_java_os_version = java_os_version.id AND session_java_info.host_java_os_arch = java_os_arch.id AND session.sim_dev = false) GROUP BY java_os_name.name, java_os_version.name, java_os_arch.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each java timezone has been seen (non-dev)");
	display_query("SELECT java_timezone.name, SUM(session.sim_sessions_since) from session, session_java_info, java_timezone WHERE (session.id = session_java_info.session_id AND session_java_info.host_java_timezone = java_timezone.id AND session.sim_dev = false) GROUP BY java_timezone.name ORDER BY COUNT(session.id) DESC;");
	
	
	/*
	display_desc("total unique users");
	display_query("SELECT COUNT(*) AS total_unique_users FROM user;");
	
	display_desc("new unique users, by month");
	display_query("SELECT YEAR(first_seen_month) AS year, MONTH(first_seen_month) AS month, MONTHNAME(first_seen_month) AS month_name, COUNT(*) AS new_unique_users FROM user GROUP BY first_seen_month ORDER BY YEAR(first_seen_month), MONTH(first_seen_month);");
	
	display_desc("percentage of unique users who only visited once");
	display_query("SELECT COUNT(a.user_preferences_file_creation_time) * 100 / b.counts AS percent_one_visit FROM user AS a, (SELECT COUNT(*) AS counts FROM user) AS b WHERE (a.user_total_sessions = 1);");
	
	display_desc("histogram of user total visits");
	display_query("SELECT user_total_sessions, COUNT(*) FROM user GROUP BY user_total_sessions;");
	
	display_desc("approximate number of unique users who have visited within the last 6 months");
	display_query("SELECT COUNT(*) AS users_last_6_months FROM user WHERE last_seen_month >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH);");
	display_query("SELECT COUNT(*) AS users_last_6_months FROM user WHERE DATEDIFF(CURRENT_DATE, last_seen_month) <= 6 * 31;");
	*/
	
	$total_end_time = microtime(true);
	
	print "<br/><h1>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h1>\n";
	
?>