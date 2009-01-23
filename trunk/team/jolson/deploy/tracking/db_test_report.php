<?php
	include("db_util.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	function display_desc($desc) {
		echo "\n<h2>" . $desc . "</h2>\n";
	}
	
	function display_query($query) {
		$show_results = true;
		$explain = true;
		
		$start_time = microtime(true);
		$result = mysql_query($query) or die("SELECT ERROR: " . mysql_error());
		$end_time = microtime(true);
		$num_rows = mysql_num_rows($result);
		print "<h6>$query</h6>\n";
		echo "Running time: " . ($end_time - $start_time) . " seconds\n";
		if($explain) {
			display_explain($query);
		}
		if($show_results) {
			echo "<h6>data:</h6>\n";
			print "<table border=1>\n";
		} else {
			print "<table border=1 style='display: none;'>\n";
		}
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			echo "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		echo "</tr>\n";
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				print "\t<td><font face=arial size=2/>$field</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	function display_explain($query) {
		$result = mysql_query("EXPLAIN " . $query) or die("EXPLAIN ERROR");
		$num_rows = mysql_num_rows($result);
		print "<h6>explanation:</h6>\n";
		print "<table border=1>\n";
		$fields_num = mysql_num_fields($result);
		for($i=0; $i<$fields_num; $i++) {
			$field = mysql_fetch_field($result);
			echo "<td><font face=arial size=1>{$field->name}</font></td>";
		}
		echo "</tr>\n";
		while($get_info = mysql_fetch_row($result)) {
			print "<tr>\n";
			foreach($get_info as $field) {
				print "\t<td><font face=arial size=2/>$field</font></td>\n";
			}
			print "</tr>\n";
		}
		print "</table>\n";
		
		print "<br/>\n";
	}
	
	$total_start_time = microtime(true);
	
	
	if($_SERVER['QUERY_STRING']) {
		$simName = mysql_real_escape_string($_SERVER['QUERY_STRING']);
		
		display_desc("total number of runs for " . $simName);
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id)");
		//display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, sim_name WHERE (session.sim_name = sim_name.id AND sim_name.name = '" . $simName . "')");
		
		display_desc("total number of runs for " . $simName . " (non-dev)");
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.sim_dev = false)");
		//display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, sim_name WHERE (session.sim_dev = false AND session.sim_name = sim_name.id AND sim_name.name = '" . $simName . "')");
		
		display_desc("total number of runs for " . $simName . " since start of Jan 2009 (non-dev)");
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.timestamp >= '2009-01-01' AND session.sim_name = x.id AND session.sim_dev = false)");
		
		display_desc("java or flash?");
		display_query("SELECT SQL_NO_CACHE DISTINCT session.sim_type FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE session.sim_name = x.id;");
		
		display_desc("number of times each simplified OS has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE simplified_os.name, COUNT(session.id) FROM session, simplified_os, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
		
		display_desc("number of times each deployment type has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE deployment.name, COUNT(session.id) FROM session, deployment, (SELECT id FROM sim_name WHERE name = '" . $simName . "')  AS x WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
		
		display_desc("number of times each (non-null) distribution tag has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE distribution_tag.name, COUNT(session.id) FROM session, distribution_tag, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (distribution_tag.id = session.sim_distribution_tag AND distribution_tag.name IS NOT NULL AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY distribution_tag.name ORDER BY COUNT(session.id) DESC;");
		
		display_desc("number of times sim have been run by week");
		display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), COUNT(*) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY YEARWEEK(timestamp);");
		
		display_desc("number of times sim has been run, by language, since the start of 2009");
		display_query("SELECT SQL_NO_CACHE session.sim_locale_language, COUNT(session.id) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp > '2009-01-01') GROUP BY session.sim_locale_language ORDER BY session.sim_locale_language;");
		
		display_desc("number of times the sim has been run, by year month and day (within the last year) (non-dev)");
		display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(session.id) AS sim_runs FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (timestamp > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR) AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
		
		display_desc("sim runs by locale");
		display_query("SELECT SQL_NO_CACHE IF(y.country IS NULL, y.language, CONCAT(y.language, '_', y.country)) AS locale, y.counts AS sim_runs FROM (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS counts FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as y;");
		
		display_desc("sim runs by locale and time-frame");
		display_query("SELECT SQL_NO_CACHE IF(totals.country IS NULL, totals.language, CONCAT(totals.language, '_', totals.country)) AS locale, totals.val AS total, last_year.val AS last_year, last_month.val AS last_month, last_week.val AS last_week, last_day.val AS last_day FROM (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS val FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as totals, (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS val FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR)) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as last_year, (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS val FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH)) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as last_month, (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS val FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 WEEK)) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as last_week, (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS val FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY)) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as last_day WHERE (totals.language = last_year.language AND last_year.language = last_month.language AND last_month.language = last_week.language AND last_week.language = last_day.language AND ((totals.country = last_year.country AND last_year.country = last_month.country AND last_month.country = last_week.country AND last_week.country = last_day.country) OR (totals.country IS NULL AND last_year.country IS NULL AND last_month.country IS NULL AND last_week.country IS NULL AND last_day.country IS NULL)));");
		
		display_desc("sim runs by version and time-frame (non-dev)");
		display_query("SELECT SQL_NO_CACHE CONCAT(totals.sim_major_version, '.', totals.sim_minor_version) AS version, totals.val AS total, last_year.val AS last_year, last_month.val AS last_month, last_week.val AS last_week, last_day.val AS last_day FROM ( SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val FROM session, ( SELECT id FROM sim_name WHERE name = '" . $simName . "' ) AS x WHERE (session.sim_name = x.id AND session.sim_dev = false) GROUP BY session.sim_major_version, sim_minor_version ) as totals, ( SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val FROM session, ( SELECT id FROM sim_name WHERE name = '" . $simName . "' ) AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR) AND session.sim_dev = false) GROUP BY session.sim_major_version, sim_minor_version ) as last_year, ( SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val FROM session, ( SELECT id FROM sim_name WHERE name = '" . $simName . "' ) AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 MONTH) AND session.sim_dev = false) GROUP BY session.sim_major_version, sim_minor_version ) as last_month, ( SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val FROM session, ( SELECT id FROM sim_name WHERE name = '" . $simName . "' ) AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 WEEK) AND session.sim_dev = false) GROUP BY session.sim_major_version, sim_minor_version ) as last_week, ( SELECT DISTINCT session.sim_major_version AS sim_major_version, session.sim_minor_version AS sim_minor_version, COUNT(session.id) AS val FROM session, ( SELECT id FROM sim_name WHERE name = '" . $simName . "' ) AS x WHERE (session.sim_name = x.id AND session.timestamp >= DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 DAY) AND session.sim_dev = false) GROUP BY session.sim_major_version, sim_minor_version ) as last_day WHERE ( totals.sim_major_version = last_year.sim_major_version AND last_year.sim_major_version = last_month.sim_major_version AND last_month.sim_major_version = last_week.sim_major_version AND last_week.sim_major_version = last_day.sim_major_version AND totals.sim_minor_version = last_year.sim_minor_version AND last_year.sim_minor_version = last_month.sim_minor_version AND last_month.sim_minor_version = last_week.sim_minor_version AND last_week.sim_minor_version = last_day.sim_minor_version ) ORDER BY totals.sim_major_version, totals.sim_minor_version;");
		
		
		
	} else {
		
		
		display_desc("total number of simulations run");
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session;");
		//display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation;");
		
		display_desc("total number of simulations run (non-dev)");
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session WHERE sim_dev = false;");
		//display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation WHERE sim_dev = false;");
		
		display_desc("number of times pendulum-lab has been run");
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, sim_name WHERE (sim_name.name = 'pendulum-lab' AND sim_name.id = session.sim_name);");
		//display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation WHERE sim_name = 'pendulum-lab';");
		
		display_desc("number of times pendulum-lab has been run since start of January 2009 (non-dev)");
		display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, sim_name WHERE (sim_name.name = 'pendulum-lab' AND sim_name.id = session.sim_name AND timestamp > '2009-01-01' AND session.sim_dev = false);");
		//display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation WHERE (sim_name = 'pendulum-lab' AND timestamp > '2009-01-01' AND sim_dev = false);");
		
		display_desc("number of times each sim was run in January 2007 (non-dev)");
		display_query("SELECT SQL_NO_CACHE DISTINCT sim_name.name AS sim_name, COUNT(session.id) AS sim_runs FROM session, sim_name WHERE (timestamp > '2007-01-01' AND timestamp < '2007-02-01' AND sim_name.id = session.sim_name AND session.sim_dev = false) GROUP BY sim_name ORDER BY sim_name;");
		//display_query("SELECT SQL_NO_CACHE DISTINCT sim_name, COUNT(id) AS sim_runs FROM simulation WHERE (timestamp > '2007-01-01' AND timestamp < '2007-02-01' AND sim_dev = false) GROUP BY sim_name ORDER BY sim_name;");
		
		display_desc("number of times each sim has been run (non-dev)");
		display_query("SELECT SQL_NO_CACHE DISTINCT sim_name.name AS sim_name, COUNT(session.id) AS sim_runs FROM session, sim_name WHERE (sim_name.id = session.sim_name AND session.sim_dev = false) GROUP BY sim_name.name ORDER BY sim_name.name;");
		//display_query("SELECT SQL_NO_CACHE DISTINCT sim_name, COUNT(id) AS sim_runs FROM simulation WHERE sim_dev = false GROUP BY sim_name ORDER BY sim_name;");
		
		display_desc("number of times sims have been run by week");
		display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), COUNT(*) FROM session GROUP BY YEARWEEK(timestamp);");
		//display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), COUNT(*) FROM simulation GROUP BY YEARWEEK(timestamp);");
		
		display_desc("number of times sims have been run, by year, month and day for the last year (non-dev)");
		// TODO: show days where no sims were run?
		display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(id) AS sim_runs FROM session WHERE (timestamp > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR) AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
		display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(id) AS sim_runs FROM session WHERE (DATEDIFF(CURRENT_DATE, timestamp) < 365 AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
		//display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(id) AS sim_runs FROM simulation WHERE (DATEDIFF(CURRENT_DATE, timestamp) < 365 AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
		
		display_desc("number of times pendulum-lab has been run, by language, since the start of 2009");
		display_query("SELECT SQL_NO_CACHE session.sim_locale_language, COUNT(session.id) FROM session, sim_name WHERE (session.sim_name = sim_name.id AND sim_name.name = 'pendulum-lab' AND session.timestamp > '2009-01-01') GROUP BY session.sim_locale_language ORDER BY session.sim_locale_language;");
		//display_query("SELECT SQL_NO_CACHE sim_locale_language, COUNT(id) FROM simulation WHERE (sim_name = 'pendulum-lab' AND timestamp > '2009-01-01') GROUP BY sim_locale_language ORDER BY sim_locale_language;");
		
		display_desc("number of times each version of pendulum-lab was run within the last month");
		display_query("SELECT SQL_NO_CACHE session.sim_major_version, session.sim_minor_version, COUNT(session.id) FROM session, sim_name WHERE (session.sim_name = sim_name.id AND sim_name.name = 'pendulum-lab' AND session.sim_dev = 0 AND session.timestamp > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 31 DAY)) GROUP BY session.sim_major_version, session.sim_minor_version ORDER BY COUNT(session.id) DESC;");
		display_query("SELECT SQL_NO_CACHE session.sim_major_version, session.sim_minor_version, COUNT(session.id) FROM session, sim_name WHERE (session.sim_name = sim_name.id AND sim_name.name = 'pendulum-lab' AND session.sim_dev = 0 AND DATEDIFF(CURRENT_DATE, session.timestamp) < 31) GROUP BY session.sim_major_version, session.sim_minor_version ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE sim_major_version, sim_minor_version, COUNT(id) FROM simulation WHERE (sim_name = 'pendulum-lab' AND sim_dev = 0 AND DATEDIFF(CURRENT_DATE, timestamp) < 31) GROUP BY sim_major_version, sim_minor_version ORDER BY COUNT(id) DESC;");
		
		display_desc("number of times each simplified OS has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE simplified_os.name, COUNT(session.id) FROM session, simplified_os WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE host_simplified_os, COUNT(id) FROM simulation WHERE sim_dev = false GROUP BY host_simplified_os ORDER BY COUNT(*) DESC;");
		
		display_desc("number of times each deployment type has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE deployment.name, COUNT(session.id) FROM session, deployment WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE sim_deployment, COUNT(id) FROM simulation WHERE sim_dev = false GROUP BY sim_deployment ORDER BY COUNT(id) DESC;");
		
		display_desc("number of times each (non-null) distribution tag has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE distribution_tag.name, COUNT(session.id) FROM session, distribution_tag WHERE (distribution_tag.id = session.sim_distribution_tag AND distribution_tag.name IS NOT NULL AND session.sim_dev = false) GROUP BY distribution_tag.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE sim_distribution_tag, COUNT(id) FROM simulation WHERE (sim_distribution_tag IS NOT NULL AND sim_dev = false) GROUP BY sim_distribution_tag ORDER BY COUNT(id) DESC;");
		
		display_desc("number of times the distribution tag Wiley has been seen, by year and month (non-dev)");
		// TODO: show months where no sims were run?
		display_query("SELECT SQL_NO_CACHE YEAR(session.timestamp), MONTH(session.timestamp), COUNT(session.id) FROM session, (SELECT id FROM distribution_tag WHERE name = 'Wiley') AS x WHERE (session.sim_distribution_tag = x.id AND session.sim_dev = false) GROUP BY YEAR(session.timestamp), MONTH(session.timestamp);");
		//display_query("SELECT SQL_NO_CACHE YEAR(timestamp), MONTH(timestamp), COUNT(id) FROM simulation WHERE (sim_distribution_tag = 'Wiley' AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp);");
		
		display_desc("number of times each flash domain has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE flash_domain.name, COUNT(session.id) FROM session, session_flash_info, flash_domain WHERE (session.id = session_flash_info.session_id AND session_flash_info.host_flash_domain = flash_domain.id AND sim_dev = false) GROUP BY flash_domain.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE host_flash_domain, COUNT(id) FROM flash_simulation WHERE (sim_dev = false) GROUP BY host_flash_domain ORDER BY COUNT(id) DESC;");
		
		display_desc("number of times each flash OS has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE flash_os.name, COUNT(session.id) FROM session, session_flash_info, flash_os WHERE (session.id = session_flash_info.session_id AND session_flash_info.host_flash_os = flash_os.id AND sim_dev = false) GROUP BY flash_os.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE host_flash_os, COUNT(id) FROM flash_simulation WHERE (sim_dev = false) GROUP BY host_flash_os ORDER BY COUNT(id) DESC;");
		
		display_desc("number of times each java OS has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE java_os_name.name, java_os_version.name, java_os_arch.name, COUNT(session.id) FROM session, session_java_info, java_os_name, java_os_version, java_os_arch WHERE (session.id = session_java_info.session_id AND session_java_info.host_java_os_name = java_os_name.id AND session_java_info.host_java_os_version = java_os_version.id AND session_java_info.host_java_os_arch = java_os_arch.id AND session.sim_dev = false) GROUP BY java_os_name.name, java_os_version.name, java_os_arch.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE host_java_os_name, host_java_os_version, host_java_os_arch, COUNT(id) FROM java_simulation WHERE (sim_dev = false) GROUP BY host_java_os_name, host_java_os_version, host_java_os_arch ORDER BY COUNT(id) DESC;");
		
		display_desc("number of times each java timezone has been seen (non-dev)");
		display_query("SELECT SQL_NO_CACHE java_timezone.name, COUNT(session.id) from session, session_java_info, java_timezone WHERE (session.id = session_java_info.session_id AND session_java_info.host_java_timezone = java_timezone.id AND session.sim_dev = false) GROUP BY java_timezone.name ORDER BY COUNT(session.id) DESC;");
		//display_query("SELECT SQL_NO_CACHE host_java_timezone, COUNT(id) from java_simulation WHERE (sim_dev = false) GROUP BY host_java_timezone ORDER BY COUNT(id) DESC;");
		
		
		
		display_desc("total unique users");
		display_query("SELECT SQL_NO_CACHE COUNT(*) AS total_unique_users FROM user;");
		
		display_desc("new unique users, by month");
		display_query("SELECT YEAR(first_seen_month) AS year, MONTH(first_seen_month) AS month, MONTHNAME(first_seen_month) AS month_name, COUNT(*) AS new_unique_users FROM user GROUP BY first_seen_month ORDER BY YEAR(first_seen_month), MONTH(first_seen_month);");
		
		display_desc("percentage of unique users who only visited once");
		display_query("SELECT COUNT(a.user_preferences_file_creation_time) * 100 / b.counts AS percent_one_visit FROM user AS a, (SELECT COUNT(*) AS counts FROM user) AS b WHERE (a.user_total_sessions = 1);");
		
		display_desc("histogram of user total visits");
		display_query("SELECT user_total_sessions, COUNT(*) FROM user GROUP BY user_total_sessions;");
		
		display_desc("approximate number of unique users who have visited within the last 6 months");
		display_query("SELECT COUNT(*) AS users_last_6_months FROM user WHERE last_seen_month >= DATE_SUB(CURDATE(), INTERVAL 6 MONTH);");
		display_query("SELECT COUNT(*) AS users_last_6_months FROM user WHERE DATEDIFF(CURRENT_DATE, last_seen_month) <= 6 * 31;");
		
		
		/* REMOVED for now due to lack of permissions to create view tables
		display_desc("number of sims run for each Java version by month in 2007");
		display_query("SELECT SQL_NO_CACHE alls.month, alls.all_versions, v4.ver4, v5.ver5, v6.ver6 FROM ( SELECT DISTINCT MONTH(java_simulation.timestamp) AS monthnumber, MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS all_versions FROM java_simulation WHERE YEAR(java_simulation.timestamp) = 2007 GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS alls, ( SELECT DISTINCT MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS ver4 FROM java_simulation WHERE ( YEAR(java_simulation.timestamp) = 2007 AND java_simulation.host_java_version_minor = 4 ) GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS v4, ( SELECT DISTINCT MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS ver5 FROM java_simulation WHERE ( YEAR(java_simulation.timestamp) = 2007 AND java_simulation.host_java_version_minor = 5 ) GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS v5, ( SELECT DISTINCT MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS ver6 FROM java_simulation WHERE ( YEAR(java_simulation.timestamp) = 2007 AND java_simulation.host_java_version_minor = 6 ) GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS v6 WHERE ( alls.month = v4.month AND v4.month = v5.month AND v5.month = v6.month ) ORDER BY alls.monthnumber;");
		*/
		
	}
	
	
	$total_end_time = microtime(true);
	
	echo "<br/><h1>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h1>\n";
	
?>