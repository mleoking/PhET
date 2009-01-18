
DROP TABLE users;
CREATE TABLE users (
	user_preferences_file_creation_time INT UNSIGNED NOT NULL PRIMARY KEY,
	user_total_sessions INT UNSIGNED,
	first_seen_month DATE,
	last_seen_month DATE
);

DROP TABLE sim_projects;
CREATE TABLE sim_projects (
	sim_project_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	sim_project CHAR(50)
);

DROP TABLE sim_names;
CREATE TABLE sim_names (
	sim_name_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	sim_name CHAR(50)
);

DROP TABLE usage_types;
CREATE TABLE usage_types (
	usage_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	usage_type VARCHAR(40)
);

DROP TABLE distribution_tags;
CREATE TABLE distribution_tags (
	distribution_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	distribution_tag VARCHAR(40)
);

DROP TABLE scenarios;
CREATE TABLE scenarios (
	scenario_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	scenario VARCHAR(40)
);

DROP TABLE simplified_os_names;
CREATE TABLE simplified_os_names (
	simplified_os_id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	simplified_os_name VARCHAR(50)
);

DROP TABLE sessions;
CREATE TABLE sessions (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	message_version TINYINT UNSIGNED,
	sim_type BOOL,
	sim_project MEDIUMINT UNSIGNED NOT NULL,
	sim_name MEDIUMINT UNSIGNED NOT NULL,
	sim_major_version TINYINT UNSIGNED,
	sim_minor_version TINYINT UNSIGNED,
	sim_dev_version SMALLINT UNSIGNED,
	sim_svn_revision MEDIUMINT UNSIGNED,
	sim_locale_language CHAR(2),
	sim_locale_country CHAR(2),
	sim_sessions_since SMALLINT UNSIGNED,
	sim_sessions_ever MEDIUMINT UNSIGNED,
	sim_usage_type MEDIUMINT UNSIGNED NOT NULL,
	sim_distribution_tag MEDIUMINT UNSIGNED NOT NULL,
	sim_dev BOOL,
	sim_scenario MEDIUMINT UNSIGNED NOT NULL,
	host_locale_language CHAR(2),
	host_locale_country CHAR(2),
	host_simplified_os TINYINT UNSIGNED NOT NULL,
	
	INDEX(timestamp),
	INDEX(sim_project, sim_name),
	
	FOREIGN KEY (sim_project) REFERENCES sim_projects (sim_project_id) ON DELETE CASCADE,
	FOREIGN KEY (sim_name) REFERENCES sim_names (sim_name_id) ON DELETE CASCADE,
	FOREIGN KEY (sim_usage_type) REFERENCES usage_types (usage_id) ON DELETE CASCADE,
	FOREIGN KEY (sim_distribution_tag) REFERENCES distribution_tags (distribution_id) ON DELETE CASCADE,
	FOREIGN KEY (sim_scenario) REFERENCES scenarios (scenario_id) ON DELETE CASCADE,
	FOREIGN KEY (host_simplified_os) REFERENCES simplified_os_names (simplified_os_id) ON DELETE CASCADE
);

DROP TABLE flash_domains;
CREATE TABLE flash_domains (
	flash_domain_id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	flash_domain_name VARCHAR(50)
);

DROP TABLE flash_os_names;
CREATE TABLE flash_os_names (
	flash_os_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	flash_os_name VARCHAR(50)
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
	host_flash_domain MEDIUMINT UNSIGNED NOT NULL,
	host_flash_os INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
	FOREIGN KEY (host_flash_domain) REFERENCES flash_domains (flash_domain_id) ON DELETE CASCADE,
	FOREIGN KEY (host_flash_os) REFERENCES flash_os_names (flash_os_id) ON DELETE CASCADE
);

DROP TABLE java_os_names;
CREATE TABLE java_os_names (
	java_os_name_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	java_os_name VARCHAR(50)
);

DROP TABLE java_os_versions;
CREATE TABLE java_os_versions (
	java_os_version_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	java_os_version_name VARCHAR(50)
);

DROP TABLE java_os_architectures;
CREATE TABLE java_os_architectures (
	java_os_architecture_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	java_os_architecture_name VARCHAR(50)
);

DROP TABLE java_vendors;
CREATE TABLE java_vendors (
	java_vendor_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	java_vendor_name VARCHAR(50)
);

DROP TABLE java_webstart_versions;
CREATE TABLE java_webstart_versions (
	java_webstart_version_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	java_webstart_version_name VARCHAR(50)
);

DROP TABLE java_timezones;
CREATE TABLE java_timezones (
	java_timezone_id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	java_timezone_name VARCHAR(50)
);

DROP TABLE java_info;
CREATE TABLE java_info (
	session_id INT NOT NULL PRIMARY KEY,
	host_java_os_name INT UNSIGNED NOT NULL,
	host_java_os_version INT UNSIGNED NOT NULL,
	host_java_os_arch INT UNSIGNED NOT NULL,
	host_java_vendor INT UNSIGNED NOT NULL,
	host_java_version_major SMALLINT UNSIGNED,
	host_java_version_minor SMALLINT UNSIGNED,
	host_java_version_maintenance MEDIUMINT UNSIGNED,
	host_java_webstart_version INT UNSIGNED NOT NULL,
	host_java_timezone INT UNSIGNED NOT NULL,
	
	FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
	FOREIGN KEY (host_java_os_name) REFERENCES java_os_names (java_os_name_id) ON DELETE CASCADE,
	FOREIGN KEY (host_java_os_version) REFERENCES java_os_versions (java_os_version_id) ON DELETE CASCADE,
	FOREIGN KEY (host_java_os_arch) REFERENCES java_os_architectures (java_os_architecture_id) ON DELETE CASCADE,
	FOREIGN KEY (host_java_vendor) REFERENCES java_vendors (java_vendor_id) ON DELETE CASCADE,
	FOREIGN KEY (host_java_webstart_version) REFERENCES java_webstart_versions (java_webstart_version_id) ON DELETE CASCADE,
	FOREIGN KEY (host_java_timezone) REFERENCES java_timezones (java_timezone_id) ON DELETE CASCADE
);

DROP VIEW simulations;
CREATE VIEW simulations AS (SELECT sessions.id, sessions.timestamp, sessions.message_version, sessions.sim_type, sim_projects.sim_project, sim_names.sim_name, sessions.sim_major_version, sessions.sim_minor_version, sessions.sim_dev_version, sessions.sim_svn_revision, sessions.sim_locale_language, sessions.sim_locale_country, sessions.sim_sessions_since, sessions.sim_sessions_ever, usage_types.usage_type, distribution_tags.distribution_tag, sessions.sim_dev, scenarios.scenario, sessions.host_locale_language, sessions.host_locale_country, simplified_os_names.simplified_os_name
FROM sessions, sim_projects, sim_names, usage_types, distribution_tags, simplified_os_names, scenarios
WHERE (
	sessions.sim_project = sim_projects.sim_project_id
	AND sessions.sim_name = sim_names.sim_name_id
	AND sessions.sim_usage_type = usage_types.usage_id
	AND sessions.sim_distribution_tag = distribution_tags.distribution_id
	AND sessions.sim_scenario = scenarios.scenario_id
	AND sessions.host_simplified_os = simplified_os_names.simplified_os_id
));

DROP VIEW flash_simulations;
CREATE VIEW flash_simulations AS (SELECT sessions.id, sessions.timestamp, sessions.message_version, sessions.sim_type, sim_projects.sim_project, sim_names.sim_name, sessions.sim_major_version, sessions.sim_minor_version, sessions.sim_dev_version, sessions.sim_svn_revision, sessions.sim_locale_language, sessions.sim_locale_country, sessions.sim_sessions_since, sessions.sim_sessions_ever, usage_types.usage_type, distribution_tags.distribution_tag, sessions.sim_dev, scenarios.scenario, sessions.host_locale_language, sessions.host_locale_country, simplified_os_names.simplified_os_name, flash_info.host_flash_version_type, flash_info.host_flash_version_major, flash_info.host_flash_version_minor, flash_info.host_flash_version_revision, flash_info.host_flash_version_build, flash_info.host_flash_time_offset, flash_info.host_flash_accessibility, flash_domains.flash_domain_name AS host_flash_domain, flash_os_names.flash_os_name AS host_flash_os
FROM sessions, sim_projects, sim_names, usage_types, distribution_tags, simplified_os_names, scenarios, flash_info, flash_domains, flash_os_names
WHERE (
	sessions.sim_project = sim_projects.sim_project_id
	AND sessions.sim_name = sim_names.sim_name_id
	AND sessions.sim_usage_type = usage_types.usage_id
	AND sessions.sim_distribution_tag = distribution_tags.distribution_id
	AND sessions.sim_scenario = scenarios.scenario_id
	AND sessions.host_simplified_os = simplified_os_names.simplified_os_id
	AND sessions.id = flash_info.session_id
	AND flash_info.host_flash_domain = flash_domains.flash_domain_id
	AND flash_info.host_flash_os = flash_os_names.flash_os_id
));

DROP VIEW java_simulations;
CREATE VIEW java_simulations AS (SELECT sessions.id, sessions.timestamp, sessions.message_version, sessions.sim_type, sim_projects.sim_project, sim_names.sim_name, sessions.sim_major_version, sessions.sim_minor_version, sessions.sim_dev_version, sessions.sim_svn_revision, sessions.sim_locale_language, sessions.sim_locale_country, sessions.sim_sessions_since, sessions.sim_sessions_ever, usage_types.usage_type, distribution_tags.distribution_tag, sessions.sim_dev, scenarios.scenario, sessions.host_locale_language, sessions.host_locale_country, simplified_os_names.simplified_os_name, java_os_names.java_os_name AS host_java_os_name, java_os_versions.java_os_version_name AS host_java_os_version, java_os_architectures.java_os_architecture_name AS host_java_os_arch, java_vendors.java_vendor_name AS host_java_vendor, java_info.host_java_version_major, java_info.host_java_version_minor, java_info.host_java_version_maintenance, java_webstart_versions.java_webstart_version_name AS host_java_webstart_version, java_timezones.java_timezone_name AS host_java_timezone
FROM sessions, sim_projects, sim_names, usage_types, distribution_tags, simplified_os_names, scenarios, java_os_names, java_os_versions, java_os_architectures, java_vendors, java_webstart_versions, java_timezones, java_info
WHERE (
	sessions.sim_project = sim_projects.sim_project_id
	AND sessions.sim_name = sim_names.sim_name_id
	AND sessions.sim_usage_type = usage_types.usage_id
	AND sessions.sim_distribution_tag = distribution_tags.distribution_id
	AND sessions.sim_scenario = scenarios.scenario_id
	AND sessions.host_simplified_os = simplified_os_names.simplified_os_id
	AND sessions.id = java_info.session_id
	AND java_info.host_java_os_name = java_os_names.java_os_name_id
	AND java_info.host_java_os_version = java_os_versions.java_os_version_id
	AND java_info.host_java_os_arch = java_os_architectures.java_os_architecture_id
	AND java_info.host_java_vendor = java_vendors.java_vendor_id
	AND java_info.host_java_webstart_version = java_webstart_versions.java_webstart_version_id
	AND java_info.host_java_timezone = java_timezones.java_timezone_id
));





