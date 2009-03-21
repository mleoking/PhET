<?php
	// display the 10 most recent entries in general messages, flash messages, and java messages

	include("db-auth.php");
	include("db-stats.php");
	$link = setup_mysql();
	
	// used to extract fieldnames and display each table
	function display_query($query) {
		$result = mysql_query($query) or die("SELECT ERROR");
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
	
	print <<<EOT
<html>
<head><title>Recent Messages</title></head>
<body>
<h1>Recent messages:</h1>
EOT;
	
	print "<h4>Last 10 messages</h4>";
	$query = <<<SES
SELECT
	session.id,
	session.timestamp,
	session.message_version,
	session.server_revision,
	sim_type.name AS sim_name,
	sim_project.name AS sim_project,
	sim_name.name AS sim_name,
	session.sim_major_version,
	session.sim_minor_version,
	session.sim_dev_version,
	session.sim_revision,
	session.sim_version_timestamp,
	session.sim_locale_language,
	session.sim_locale_country,
	session.sim_sessions_since,
	session.sim_total_sessions,
	deployment.name AS sim_deployment,
	distribution_tag.name AS sim_distribution_tag,
	session.sim_dev,
	session.host_locale_language,
	session.host_locale_country,
	simplified_os.name AS host_simplified_os
FROM session, sim_type, sim_project, sim_name, deployment, distribution_tag, simplified_os
WHERE (
	session.sim_project = sim_project.id
	AND session.sim_name = sim_name.id
	AND session.sim_type = sim_type.id
	AND session.sim_deployment = deployment.id
	AND session.sim_distribution_tag = distribution_tag.id
	AND session.host_simplified_os = simplified_os.id
)
ORDER BY session.id DESC LIMIT 10;
SES;
	display_query($query);
	
	print "<h4>Last 10 flash messages</h4>";
	$query = <<<FLA
SELECT
	session.id,
	session.timestamp,
	session.message_version,
	session.server_revision,
	sim_project.name AS sim_project,
	sim_name.name AS sim_name,
	session.sim_major_version,
	session.sim_minor_version,
	session.sim_dev_version,
	session.sim_revision,
	session.sim_version_timestamp,
	session.sim_locale_language,
	session.sim_locale_country,
	session.sim_sessions_since,
	session.sim_total_sessions,
	deployment.name AS sim_deployment,
	distribution_tag.name AS sim_distribution_tag,
	session.sim_dev,
	session.host_locale_language,
	session.host_locale_country,
	simplified_os.name AS host_simplified_os,
	flash_version_type.name AS host_flash_version_type,
	session_flash_info.host_flash_version_major,
	session_flash_info.host_flash_version_minor,
	session_flash_info.host_flash_version_revision,
	session_flash_info.host_flash_version_build,
	session_flash_info.host_flash_time_offset,
	session_flash_info.host_flash_accessibility,
	flash_domain.name AS host_flash_domain,
	flash_os.name AS host_flash_os
FROM session, sim_project, sim_name, deployment, distribution_tag, simplified_os, session_flash_info, flash_version_type, flash_domain, flash_os
WHERE (
	session.sim_project = sim_project.id
	AND session.sim_name = sim_name.id
	AND session.sim_deployment = deployment.id
	AND session.sim_distribution_tag = distribution_tag.id
	AND session.host_simplified_os = simplified_os.id
	AND session.id = session_flash_info.session_id
	AND session_flash_info.host_flash_version_type = flash_version_type.id
	AND session_flash_info.host_flash_domain = flash_domain.id
	AND session_flash_info.host_flash_os = flash_os.id
)
ORDER BY session.id DESC LIMIT 10;
FLA;
	display_query($query);
	
	print "<h4>Last 10 java messages</h4>";
	$query = <<<JAV
SELECT
	session.id,
	session.timestamp,
	session.message_version,
	session.server_revision,
	sim_project.name AS sim_project,
	sim_name.name AS sim_name,
	session.sim_major_version,
	session.sim_minor_version,
	session.sim_dev_version,
	session.sim_revision,
	session.sim_version_timestamp,
	session.sim_locale_language,
	session.sim_locale_country,
	session.sim_sessions_since,
	session.sim_total_sessions,
	deployment.name AS sim_deployment,
	distribution_tag.name AS sim_distribution_tag,
	session.sim_dev,
	session.host_locale_language,
	session.host_locale_country,
	simplified_os.name AS host_simplified_os,
	java_os_name.name AS host_java_os_name,
	java_os_version.name AS host_java_os_version,
	java_os_arch.name AS host_java_os_arch,
	java_vendor.name AS host_java_vendor,
	session_java_info.host_java_version_major,
	session_java_info.host_java_version_minor,
	session_java_info.host_java_version_maintenance,
	java_webstart_version.name AS host_java_webstart_version,
	java_timezone.name AS host_java_timezone
FROM session, sim_project, sim_name, deployment, distribution_tag, simplified_os, session_java_info, java_os_name, java_os_version, java_os_arch, java_vendor, java_webstart_version, java_timezone
WHERE (
	session.sim_project = sim_project.id
	AND session.sim_name = sim_name.id
	AND session.sim_deployment = deployment.id
	AND session.sim_distribution_tag = distribution_tag.id
	AND session.host_simplified_os = simplified_os.id
	AND session.id = session_java_info.session_id
	AND session_java_info.host_java_os_name = java_os_name.id
	AND session_java_info.host_java_os_version = java_os_version.id
	AND session_java_info.host_java_os_arch = java_os_arch.id
	AND session_java_info.host_java_vendor = java_vendor.id
	AND session_java_info.host_java_webstart_version = java_webstart_version.id
	AND session_java_info.host_java_timezone = java_timezone.id
)
ORDER BY session.id DESC LIMIT 10;
JAV;
	display_query($query);
	
	
	mysql_close($link);
	
	print "</body></html>";
?>