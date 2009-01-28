<?php
	include("db-tracking.php");
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
	
	$simName = mysql_real_escape_string($_SERVER['QUERY_STRING']);
	
	print "<h1>Report for " . $simName . "</h1>";
	
	mysql_query("SELECT (@sid := sim_name.id) FROM sim_name WHERE sim_name.name = " . quo($simName) . ";");
	
	display_desc("total number of messages");
	display_query("SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid)");
	
	display_desc("total number of messages (non-dev)");
	display_query("SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_desc("total number of sim runs (non-dev)");
	display_query("SELECT SUM(session.sim_sessions_since) AS total_sessions FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_desc("number of times sim has been run by week");
	display_query("SELECT YEARWEEK(timestamp), SUM(session.sim_sessions_since) FROM session WHERE (session.sim_name = @sid) GROUP BY YEARWEEK(timestamp);");
	
	
	print "<h1>old style</h1>";
	
	display_desc("java or flash?");
	display_query("SELECT SQL_NO_CACHE DISTINCT session.sim_type FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE session.sim_name = x.id;");
	
	display_desc("number of times each simplified OS has been seen (non-dev)");
	display_query("SELECT SQL_NO_CACHE simplified_os.name, SUM(session.sim_sessions_since) FROM session, simplified_os, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
	//display_query("SELECT SQL_NO_CACHE simplified_os.name, COUNT(session.id) FROM session, simplified_os, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each deployment type has been seen (non-dev)");
	display_query("SELECT SQL_NO_CACHE deployment.name, SUM(session.sim_sessions_since) FROM session, deployment, (SELECT id FROM sim_name WHERE name = '" . $simName . "')  AS x WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
	//display_query("SELECT SQL_NO_CACHE deployment.name, COUNT(session.id) FROM session, deployment, (SELECT id FROM sim_name WHERE name = '" . $simName . "')  AS x WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each (non-null) distribution tag has been seen (non-dev)");
	display_query("SELECT SQL_NO_CACHE distribution_tag.name, SUM(session.sim_sessions_since) FROM session, distribution_tag, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (distribution_tag.id = session.sim_distribution_tag AND distribution_tag.name IS NOT NULL AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY distribution_tag.name ORDER BY COUNT(session.id) DESC;");
	//display_query("SELECT SQL_NO_CACHE distribution_tag.name, COUNT(session.id) FROM session, distribution_tag, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (distribution_tag.id = session.sim_distribution_tag AND distribution_tag.name IS NOT NULL AND session.sim_dev = false AND session.sim_name = x.id) GROUP BY distribution_tag.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times sim have been run by week");
	display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), SUM(session.sim_sessions_since) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY YEARWEEK(timestamp);");
	//display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), COUNT(*) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY YEARWEEK(timestamp);");
	
	display_desc("number of times sim has been run, by language, since the start of 2009");
	display_query("SELECT SQL_NO_CACHE session.sim_locale_language, SUM(session.sim_sessions_since) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp > '2009-01-01') GROUP BY session.sim_locale_language ORDER BY session.sim_locale_language;");
	//display_query("SELECT SQL_NO_CACHE session.sim_locale_language, COUNT(session.id) FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id AND session.timestamp > '2009-01-01') GROUP BY session.sim_locale_language ORDER BY session.sim_locale_language;");
	
	display_desc("number of times the sim has been run, by year month and day (within the last year) (non-dev)");
	display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, SUM(session.sim_sessions_since) AS sim_runs FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (timestamp > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR) AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
	//display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(session.id) AS sim_runs FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (timestamp > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR) AND sim_dev = false) GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
	
	display_desc("sim runs by locale");
	display_query("SELECT SQL_NO_CACHE IF(y.country IS NULL, y.language, CONCAT(y.language, '_', y.country)) AS locale, y.counts AS sim_runs FROM (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, SUM(session.sim_sessions_since) AS counts FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as y;");
	//display_query("SELECT SQL_NO_CACHE IF(y.country IS NULL, y.language, CONCAT(y.language, '_', y.country)) AS locale, y.counts AS sim_runs FROM (SELECT DISTINCT session.sim_locale_language AS language, session.sim_locale_country AS country, COUNT(session.id) AS counts FROM session, (SELECT id FROM sim_name WHERE name = '" . $simName . "') AS x WHERE (session.sim_name = x.id) GROUP BY session.sim_locale_language, sim_locale_country ORDER BY session.sim_locale_language, sim_locale_country) as y;");
	
	$total_end_time = microtime(true);
	
	print "<br/><h6>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h6>\n";
	
?>