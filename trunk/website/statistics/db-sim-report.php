<?php
    // displays a report for a particular sim, with flash and java-specific queries.
    // usage: db-sim-report.php?pendulum-lab

    include("db-auth.php");
	include("db-stats.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	$query_counter = 0;
	
	function display_query($desc, $query) {
		global $query_counter;
		
		$query_counter++;
		
		$id = 'id' . $query_counter;
		
		print "\n<h4 style='padding-bottom: 0px; margin-top: 5px; margin-bottom: 5px;'>{$desc}</h4><a href='#' style='color: #FF0000; font-size: 10px; cursor: hand; padding: 0px; margin: 0px' onclick=\"var ob = document.getElementById('{$id}'); ob.style.display = 'none';\">hide data</a>\n";
		
		$result = mysql_query($query) or die("SELECT ERROR: " . mysql_error());
		$num_rows = mysql_num_rows($result);
		print "<table border=1 id='{$id}'>\n";
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
	
	// TODO: get faster query for flash/java question?
	$result = mysql_query("SELECT DISTINCT IF(sim_type = 0, 'java', 'flash') AS sim_type FROM session WHERE sim_name = @sid;");
	$row = mysql_fetch_row($result);
	$simType = $row[0];
	
	display_query("java or flash?", "SELECT DISTINCT IF(sim_type = 0, 'java', 'flash') AS sim_type FROM session WHERE sim_name = @sid;");
	
	display_query("total number of messages", "SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid)");
	
	display_query("total number of messages (non-dev)", "SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_query("total number of sim runs (non-dev)", "SELECT SUM(session.sim_sessions_since) AS total_sessions FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_query("number of times sim has been run by week", "SELECT YEARWEEK(timestamp) AS year_week, SUM(session.sim_sessions_since) AS sessions FROM session WHERE (session.sim_name = @sid) GROUP BY YEARWEEK(timestamp);");
	
	display_query("number of times each simplified OS has been seen (non-dev)", "SELECT simplified_os.name, SUM(session.sim_sessions_since) AS sessions FROM session, simplified_os WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false AND session.sim_name = @sid) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
	
	display_query("number of times each deployment type has been seen (non-dev)", "SELECT deployment.name, SUM(session.sim_sessions_since) AS sessions FROM session, deployment WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false AND session.sim_name = @sid) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
	
	$query = <<<DIST
SELECT
	distribution_tag.name,
	SUM(session.sim_sessions_since) AS sessions
FROM session, distribution_tag
WHERE (
	distribution_tag.id = session.sim_distribution_tag
	AND distribution_tag.name IS NOT NULL
	AND session.sim_dev = false
	AND session.sim_name = @sid
)
GROUP BY distribution_tag.name
ORDER BY COUNT(session.id) DESC;
DIST;
	display_query("number of times each (non-null) distribution tag has been seen (non-dev)", $query);
	
	display_query("number of times sim has been run, by language", "SELECT session.sim_locale_language, SUM(session.sim_sessions_since) FROM session WHERE (session.sim_name = @sid) GROUP BY session.sim_locale_language ORDER BY SUM(session.sim_sessions_since) DESC;");
	
	$query = <<<LOC
SELECT
	IF(y.country IS NULL, y.language, CONCAT(y.language, '_', y.country)) AS locale,
	y.counts AS sim_runs
FROM (
	SELECT DISTINCT
		session.sim_locale_language AS language,
		session.sim_locale_country AS country,
		SUM(session.sim_sessions_since) AS counts
	FROM session
	WHERE (session.sim_name = @sid)
	GROUP BY session.sim_locale_language, sim_locale_country
	ORDER BY session.sim_locale_language, sim_locale_country
) as y
ORDER BY sim_runs DESC;
LOC;
	display_query("sim runs by locale", $query);
	
	$query = <<<RUN
SELECT DISTINCT
	YEAR(timestamp) AS year,
	MONTH(timestamp) AS month,
	DAY(timestamp) AS day,
	SUM(session.sim_sessions_since) AS sim_runs
FROM session
WHERE (
	timestamp > DATE_SUB(CURRENT_TIMESTAMP, INTERVAL 1 YEAR)
	AND sim_dev = false
	AND sim_name = @sid
)
GROUP BY YEAR(timestamp), MONTH(timestamp), DAY(timestamp);
RUN;
	display_query("number of times the sim has been run, by year month and day (within the last year) (non-dev)", $query);
	
	if($simType == "flash") {
		$query = <<<DIST
SELECT
	flash_os.name,
	SUM(session.sim_sessions_since) AS sessions
FROM session, session_flash_info, flash_os
WHERE (
	session.id = session_flash_info.session_id
	AND flash_os.id = session_flash_info.host_flash_os
	AND flash_os.name IS NOT NULL
	AND session.sim_dev = false
	AND session.sim_name = @sid
)
GROUP BY flash_os.name
ORDER BY COUNT(session.id) DESC;
DIST;
		display_query("number of times each (non-null) Flash OS has been seen (non-dev)", $query);
		
		$query = <<<DIST
SELECT
	flash_domain.name,
	SUM(session.sim_sessions_since) AS sessions
FROM session, session_flash_info, flash_domain
WHERE (
	session.id = session_flash_info.session_id
	AND flash_domain.id = session_flash_info.host_flash_domain
	AND flash_domain.name IS NOT NULL
	AND session.sim_dev = false
	AND session.sim_name = @sid
)
GROUP BY flash_domain.name
ORDER BY COUNT(session.id) DESC;
DIST;
		display_query("number of times each (non-null) Flash domain has been seen (non-dev)", $query);
		
		$query = <<<DIST
SELECT
	session_flash_info.host_flash_version_major,
	SUM(session.sim_sessions_since) AS sessions
FROM session, session_flash_info
WHERE (
	session.id = session_flash_info.session_id
	AND session.sim_dev = false
	AND session.sim_name = @sid
)
GROUP BY session_flash_info.host_flash_version_major
ORDER BY session_flash_info.host_flash_version_major;
DIST;
		display_query("number of times each Flash major version has been seen (non-dev)", $query);
		
		$query = <<<EOT
SELECT
	CONCAT(
		flash_version_type.name,
		' ',
		session_flash_info.host_flash_version_major,
		',',
		session_flash_info.host_flash_version_minor,
		',',
		session_flash_info.host_flash_version_revision,
		',',
		session_flash_info.host_flash_version_build
	) AS version,
	SUM(session.sim_sessions_since) AS sessions
FROM session, session_flash_info, flash_version_type
WHERE (
	session.id = session_flash_info.session_id
	AND session.sim_name = @sid
	AND session_flash_info.host_flash_version_type = flash_version_type.id
)
GROUP BY
	session_flash_info.host_flash_version_major,
	session_flash_info.host_flash_version_minor,
	session_flash_info.host_flash_version_revision,
	session_flash_info.host_flash_version_build
ORDER BY sessions DESC;
EOT;
		display_query("number of times each Flash version has been seen (non-dev)", $query);
	}
	
	$total_end_time = microtime(true);
	
	print "<br/><h6>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h6>\n";
	
?>