<?php
	include("db_util.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	function display_query($query) {
		$show_results = false;
		
		$start_time = microtime(true);
		$result = mysql_query($query) or die("SELECT ERROR");
		$end_time = microtime(true);
		$num_rows = mysql_num_rows($result);
		print "<h4>$query</h4>\n";
		echo "Running time: " . ($end_time - $start_time) . " seconds\n";
		if($show_results) {
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
	
	$total_start_time = microtime(true);
	
	// total sim run count
	display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session;");
	display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation;");
	
	// sim run count for pendulum-lab
	display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, sim_name WHERE (sim_name.name = 'pendulum-lab' AND sim_name.id = session.sim_name);");
	display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation WHERE sim_name = 'pendulum-lab';");
	
	// sim run count for pendulum-lab after the start of Jan 2009
	display_query("SELECT SQL_NO_CACHE COUNT(*) FROM session, sim_name WHERE (sim_name.name = 'pendulum-lab' AND sim_name.id = session.sim_name AND timestamp > '2009-01-01');");
	display_query("SELECT SQL_NO_CACHE COUNT(*) FROM simulation WHERE (sim_name = 'pendulum-lab' AND timestamp > '2009-01-01');");
	
	// sim count by sim name, for Jan 2007
	display_query("SELECT SQL_NO_CACHE DISTINCT sim_name.name AS sim_name, COUNT(session.id) AS sim_runs FROM session, sim_name WHERE (timestamp > '2007-01-01' AND timestamp < '2007-02-01' AND sim_name.id = session.sim_name) GROUP BY sim_name ORDER BY sim_name;");
	display_query("SELECT SQL_NO_CACHE DISTINCT sim_name, COUNT(id) AS sim_runs FROM simulation WHERE (timestamp > '2007-01-01' AND timestamp < '2007-02-01') GROUP BY sim_name ORDER BY sim_name;");
	
	// sim count by sim name
	display_query("SELECT SQL_NO_CACHE DISTINCT sim_name.name AS sim_name, COUNT(session.id) AS sim_runs FROM session, sim_name WHERE sim_name.id = session.sim_name GROUP BY sim_name.name ORDER BY sim_name.name;");
	display_query("SELECT SQL_NO_CACHE DISTINCT sim_name, COUNT(id) AS sim_runs FROM simulation GROUP BY sim_name ORDER BY sim_name;");
	
	// sim runs by week
	display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), COUNT(*) FROM session GROUP BY YEARWEEK(timestamp);");
	display_query("SELECT SQL_NO_CACHE YEARWEEK(timestamp), COUNT(*) FROM simulation GROUP BY YEARWEEK(timestamp);");
	
	// sim runs for each day, indexed by year, month and day
	display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(id) AS sim_runs FROM session WHERE DATEDIFF(CURRENT_DATE, timestamp) < 365 GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
	display_query("SELECT SQL_NO_CACHE DISTINCT YEAR(timestamp) AS year, MONTH(timestamp) AS month, DAY(timestamp) AS day, COUNT(id) AS sim_runs FROM simulation WHERE DATEDIFF(CURRENT_DATE, timestamp) < 365 GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);");
	
	// show sim run counts for pendulum-lab since the start of 2009
	display_query("SELECT session.sim_locale_language, COUNT(session.id) FROM session, sim_name WHERE (session.sim_name = sim_name.id AND sim_name.name = 'pendulum-lab' AND session.timestamp > '2009-01-01') GROUP BY session.sim_locale_language ORDER BY session.sim_locale_language;");
	display_query("SELECT sim_locale_language, COUNT(id) FROM simulation WHERE (sim_name = 'pendulum-lab' AND timestamp > '2009-01-01') GROUP BY sim_locale_language ORDER BY session.sim_locale_language;");
	
	// show versions of pendulum-lab, ranked by sim-runs within the last month
	display_query("SELECT session.sim_major_version, session.sim_minor_version, COUNT(session.id) FROM session, sim_name WHERE (session.sim_name = sim_name.id AND sim_name.name = 'pendulum-lab' AND session.sim_dev = 0 AND DATEDIFF(CURRENT_DATE, session.timestamp) < 31) GROUP BY session.sim_major_version, session.sim_minor_version ORDER BY COUNT(session.id) DESC;");
	display_query("SELECT sim_major_version, sim_minor_version, COUNT(id) FROM simulation WHERE (sim_name = 'pendulum-lab' AND sim_dev = 0 AND DATEDIFF(CURRENT_DATE, timestamp) < 31) GROUP BY sim_major_version, sim_minor_version ORDER BY COUNT(id) DESC;");
	
	// simplified os sim run counts
	display_query("SELECT simplified_os.name, COUNT(session.id) FROM session, simplified_os WHERE session.host_simplified_os = simplified_os.id GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
	display_query("SELECT host_simplified_os, COUNT(id) FROM simulation GROUP BY host_simplified_os ORDER BY COUNT(*) DESC;");
	
	display_query("SELECT SQL_NO_CACHE alls.month, alls.all_versions, v4.ver4, v5.ver5, v6.ver6 FROM ( SELECT DISTINCT MONTH(java_simulation.timestamp) AS monthnumber, MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS all_versions FROM java_simulation WHERE YEAR(java_simulation.timestamp) = 2007 GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS alls, ( SELECT DISTINCT MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS ver4 FROM java_simulation WHERE ( YEAR(java_simulation.timestamp) = 2007 AND java_simulation.host_java_version_minor = 4 ) GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS v4, ( SELECT DISTINCT MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS ver5 FROM java_simulation WHERE ( YEAR(java_simulation.timestamp) = 2007 AND java_simulation.host_java_version_minor = 5 ) GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS v5, ( SELECT DISTINCT MONTHNAME(java_simulation.timestamp) AS month, COUNT(java_simulation.id) AS ver6 FROM java_simulation WHERE ( YEAR(java_simulation.timestamp) = 2007 AND java_simulation.host_java_version_minor = 6 ) GROUP BY MONTHNAME(java_simulation.timestamp) ORDER BY MONTH(java_simulation.timestamp) ) AS v6 WHERE ( alls.month = v4.month AND v4.month = v5.month AND v5.month = v6.month ) ORDER BY alls.monthnumber;");
	
	
	
	
	
	$total_end_time = microtime(true);
	
	echo "TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds\n";
	
?>