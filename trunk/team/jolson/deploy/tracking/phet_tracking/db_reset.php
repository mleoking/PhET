<?php
	include("db_util.php");
	$link = setup_mysql();
	$query = <<<TESTERS
DROP TABLE sessions;
CREATE TABLE sessions (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	message_version TINYINT UNSIGNED,
	sim_type BOOL,
	sim_project CHAR(30),
	sim_name CHAR(30),
	sim_major_version TINYINT UNSIGNED,
	sim_minor_version TINYINT UNSIGNED,
	sim_dev_version SMALLINT UNSIGNED,
	sim_svn_revision MEDIUMINT UNSIGNED,
	sim_locale_language CHAR(2),
	sim_locale_country CHAR(2),
	sim_sessions_since SMALLINT UNSIGNED,
	sim_sessions_ever MEDIUMINT UNSIGNED,
	sim_usage_type CHAR(20),
	sim_distribution_tag CHAR(20),
	sim_dev BOOL,
	sim_scenario CHAR(20),
	host_locale_language CHAR(2),
	host_locale_country CHAR(2),
	host_simplified_os TINYINT UNSIGNED
);

DROP TABLE flash_info;
CREATE TABLE flash_info (
	session_id INT NOT NULL PRIMARY KEY,
	host_flash_version_type CHAR(5),
	host_flash_version_major TINYINT UNSIGNED,
	host_flash_version_minor SMALLINT UNSIGNED,
	host_flash_version_revision SMALLINT UNSIGNED,
	host_flash_version_build SMALLINT UNSIGNED,
	host_flash_time_offset SMALLINT,
	host_flash_accessibility BOOL,
	host_flash_domain VARCHAR(50),
	host_flash_os VARCHAR(50)
);

DROP TABLE java_info;
CREATE TABLE java_info (
	session_id INT NOT NULL PRIMARY KEY,
	host_java_os_name VARCHAR(20),
	host_java_os_version VARCHAR(20),
	host_java_os_arch VARCHAR(20),
	host_java_vendor VARCHAR(40),
	host_java_version_major SMALLINT UNSIGNED,
	host_java_version_minor SMALLINT UNSIGNED,
	host_java_version_maintenance MEDIUMINT UNSIGNED,
	host_java_webstart_version VARCHAR(20),
	host_java_timezone VARCHAR(20)
);

TESTERS;
	mysql_query($query);
	echo mysql_errno($link) . ": " . mysql_error($link). "\n";
?>

Database reset?

