<?php
	include("db-tracking.php");
	$link = setup_mysql();
	
	set_time_limit(60 * 20);
	
	function display_desc($desc) {
		print "\n<h4>{$desc}</h4>\n";
	}
	
	function display_query($query) {
		$result = mysql_query($query) or die("SELECT ERROR: " . mysql_error());
		$num_rows = mysql_num_rows($result);
		print "<table border=1>\n";
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
	
	display_desc("java or flash?");
	display_query("SELECT DISTINCT IF(sim_type = 0, 'java', 'flash') AS sim_type FROM session WHERE sim_name = @sid;");
	
	display_desc("total number of messages");
	display_query("SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid)");
	
	display_desc("total number of messages (non-dev)");
	display_query("SELECT COUNT(*) AS total_messages FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_desc("total number of sim runs (non-dev)");
	display_query("SELECT SUM(session.sim_sessions_since) AS total_sessions FROM session WHERE (session.sim_name = @sid AND session.sim_dev = false)");
	
	display_desc("number of times sim has been run by week");
	display_query("SELECT YEARWEEK(timestamp) AS year_week, SUM(session.sim_sessions_since) AS sessions FROM session WHERE (session.sim_name = @sid) GROUP BY YEARWEEK(timestamp);");
	
	display_desc("number of times each simplified OS has been seen (non-dev)");
	display_query("SELECT simplified_os.name, SUM(session.sim_sessions_since) AS sessions FROM session, simplified_os WHERE (session.host_simplified_os = simplified_os.id AND session.sim_dev = false AND session.sim_name = @sid) GROUP BY simplified_os.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each deployment type has been seen (non-dev)");
	display_query("SELECT deployment.name, SUM(session.sim_sessions_since) AS sessions FROM session, deployment WHERE (session.sim_deployment = deployment.id AND session.sim_dev = false AND session.sim_name = @sid) GROUP BY deployment.name ORDER BY COUNT(session.id) DESC;");
	
	display_desc("number of times each (non-null) distribution tag has been seen (non-dev)");
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
	display_query($query);
	
	display_desc("number of times sim has been run, by language");
	display_query("SELECT session.sim_locale_language, SUM(session.sim_sessions_since) FROM session WHERE (session.sim_name = @sid) GROUP BY session.sim_locale_language ORDER BY SUM(session.sim_sessions_since) DESC;");
	
	display_desc("sim runs by locale");
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
	display_query($query);
	
	display_desc("number of times the sim has been run, by year month and day (within the last year) (non-dev)");
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
	display_query($query);
	
	if($simType == "flash") {
		display_desc("number of times each (non-null) Flash OS has been seen (non-dev)");
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
		display_query($query);
		
		display_desc("number of times each (non-null) Flash domain has been seen (non-dev)");
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
		display_query($query);
		
		display_desc("number of times each Flash major version has been seen (non-dev)");
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
		display_query($query);
		
		display_desc("number of times each Flash version has been seen (non-dev)");
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
		display_query($query);
	}
	
	$total_end_time = microtime(true);
	
	print "<br/><h6>TOTAL Running time: " . ($total_end_time - $total_start_time) . " seconds</h6>\n";
	
?>