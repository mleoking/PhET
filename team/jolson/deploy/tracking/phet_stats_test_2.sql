
# proposal database for simulation statistical information
# currently takes up approximately 390 MB / 4 million messages

# user table, stores information about unique users by
# storing the user_preferences_file_creation_time
DROP TABLE user;
CREATE TABLE user (
	user_preferences_file_creation_time BIGINT UNSIGNED NOT NULL PRIMARY KEY,
	
	# number of total sim runs that have been recorded by the user
	user_total_sessions INT UNSIGNED,
	
	# first seen year and month (recorded by the server)
	first_seen_month DATE,
	
	# last seen year and month (recorded by the server)
	last_seen_month DATE
);


# normalized tables for use in the session table

DROP TABLE sim_project;
CREATE TABLE sim_project (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name CHAR(50)
);

DROP TABLE sim_name;
CREATE TABLE sim_name (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name CHAR(50)
);

DROP TABLE deployment;
CREATE TABLE deployment (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(40)
);

DROP TABLE distribution_tag;
CREATE TABLE distribution_tag (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(40)
);

DROP TABLE simplified_os;
CREATE TABLE simplified_os (
	id TINYINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

# session table. stores information relevant to both java and flash simulations
DROP TABLE session;
CREATE TABLE session (
	id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
	
	# timestamp of when the message arrived
	timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	
	# version of the message that was sent
	message_version TINYINT UNSIGNED,
	
	# 0 = Java, 1 = Flash
	sim_type BOOL,
	
	# project and name
	sim_project MEDIUMINT UNSIGNED NOT NULL,
	sim_name MEDIUMINT UNSIGNED NOT NULL,
	
	# version information
	sim_major_version TINYINT UNSIGNED,
	sim_minor_version TINYINT UNSIGNED,
	sim_dev_version SMALLINT UNSIGNED,
	sim_svn_revision MEDIUMINT UNSIGNED,
	
	# locale of the simulation run
	sim_locale_language CHAR(2),
	sim_locale_country CHAR(2),
	
	# number of sim runs for THIS SIM since the last tracking message was successfully received
	# this should usually be 1, but can be much higher if the user ran sims without
	# internet or sending information enabled
	sim_sessions_since SMALLINT UNSIGNED,
	
	# number of sim runs for THIS SIM ever recorded for this user
	sim_sessions_ever MEDIUMINT UNSIGNED,
	
	# how this sim was delivered to the user
	sim_deployment MEDIUMINT UNSIGNED NOT NULL,
	
	# a tag used for particular distribution media (for example, a particular book name if sims are on the CD)
	sim_distribution_tag MEDIUMINT UNSIGNED NOT NULL,
	
	# whether the sim is a development version or not
	sim_dev BOOL,
	
	# default locale of the user's machine
	host_locale_language CHAR(2),
	host_locale_country CHAR(2),
	
	# simplified code that represents the general "flavor" of the user's operating system
	# this is massaged from both Java and Flash's reported OS strings
	host_simplified_os TINYINT UNSIGNED NOT NULL
	
	# indices (added some overhead, but might be useful later on. maybe ALTER TABLE?)
	#INDEX(timestamp),
	#INDEX(sim_project, sim_name),
	
	# foreign keys (added SIGNIFICANT overhead in terms of disk space)
	#FOREIGN KEY (sim_project) REFERENCES sim_project (id) ON DELETE CASCADE,
	#FOREIGN KEY (sim_name) REFERENCES sim_name (id) ON DELETE CASCADE,
	#FOREIGN KEY (sim_deployment) REFERENCES deployment (id) ON DELETE CASCADE,
	#FOREIGN KEY (sim_distribution_tag) REFERENCES distribution_tag (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_simplified_os) REFERENCES simplified_os (id) ON DELETE CASCADE
);


# tables normalized, for flash_info table

DROP TABLE flash_version_type;
CREATE TABLE flash_version_type (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(10)
);

DROP TABLE flash_domain;
CREATE TABLE flash_domain (
	id MEDIUMINT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

DROP TABLE flash_os;
CREATE TABLE flash_os (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);


# stores information specific to flash simulations
DROP TABLE flash_info;
CREATE TABLE flash_info (
	session_id INT NOT NULL PRIMARY KEY,
	
	# player version type (WIN, MAC, LNX, etc)
	host_flash_version_type MEDIUMINT UNSIGNED NOT NULL,
	
	# version information
	host_flash_version_major TINYINT UNSIGNED,
	host_flash_version_minor SMALLINT UNSIGNED,
	host_flash_version_revision SMALLINT UNSIGNED,
	host_flash_version_build SMALLINT UNSIGNED,
	
	# time offset in minutes to (from?) GMT
	host_flash_time_offset SMALLINT,
	
	# whether Flash is being used with an accessible device
	host_flash_accessibility BOOL,
	
	# where the sim was delivered from (either localhost, or an external website)
	host_flash_domain MEDIUMINT UNSIGNED NOT NULL,
	
	# the detected Flash OS string
	host_flash_os INT UNSIGNED NOT NULL
	
	# foreign keys (added SIGNIFICANT overhead in terms of disk space)
	#FOREIGN KEY (session_id) REFERENCES sessions (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_flash_version_type) REFERENCES flash_version_type (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_flash_domain) REFERENCES flash_domain (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_flash_os) REFERENCES flash_os (id) ON DELETE CASCADE
);


# normalized tables for java_info

DROP TABLE java_os_name;
CREATE TABLE java_os_name (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

DROP TABLE java_os_version;
CREATE TABLE java_os_version (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

DROP TABLE java_os_arch;
CREATE TABLE java_os_arch (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

DROP TABLE java_vendor;
CREATE TABLE java_vendor (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

DROP TABLE java_webstart_version;
CREATE TABLE java_webstart_version (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);

DROP TABLE java_timezone;
CREATE TABLE java_timezone (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(50)
);


# stores information specific to java simulations

DROP TABLE java_info;
CREATE TABLE java_info (
	session_id INT NOT NULL PRIMARY KEY,
	
	# report Java OS information
	host_java_os_name INT UNSIGNED NOT NULL,
	host_java_os_version INT UNSIGNED NOT NULL,
	host_java_os_arch INT UNSIGNED NOT NULL,
	
	# the vendor of the Java runtime?
	host_java_vendor INT UNSIGNED NOT NULL,
	
	# java version information
	host_java_version_major SMALLINT UNSIGNED,
	host_java_version_minor SMALLINT UNSIGNED,
	host_java_version_maintenance MEDIUMINT UNSIGNED,
	
	# java webstart version
	host_java_webstart_version INT UNSIGNED NOT NULL,
	
	# user's timezone
	host_java_timezone INT UNSIGNED NOT NULL
	
	# foreign keys (added SIGNIFICANT overhead in terms of disk space)
	#FOREIGN KEY (session_id) REFERENCES session (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_java_os_name) REFERENCES java_os_name (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_java_os_version) REFERENCES java_os_version (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_java_os_arch) REFERENCES java_os_arch (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_java_vendor) REFERENCES java_vendor (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_java_webstart_version) REFERENCES java_webstart_version (id) ON DELETE CASCADE,
	#FOREIGN KEY (host_java_timezone) REFERENCES java_timezone (id) ON DELETE CASCADE
);



# VIEW TABLES

# recombines information about all simulations
DROP VIEW simulation;
CREATE VIEW simulation AS (
	SELECT
		session.id,
		session.timestamp,
		session.message_version,
		ELT(session.sim_type + 1, 'Java', 'Flash') AS sim_type,
		sim_project.name AS sim_project,
		sim_name.name AS sim_name,
		session.sim_major_version,
		session.sim_minor_version,
		session.sim_dev_version,
		session.sim_svn_revision,
		session.sim_locale_language,
		session.sim_locale_country,
		session.sim_sessions_since,
		session.sim_sessions_ever,
		deployment.name AS sim_deployment,
		distribution_tag.name AS sim_distribution_tag,
		session.sim_dev,
		session.host_locale_language,
		session.host_locale_country,
		simplified_os.name AS host_simplified_os
	FROM
		session, sim_project, sim_name, deployment, distribution_tag, simplified_os
	WHERE (
		session.sim_project = sim_project.id
		AND session.sim_name = sim_name.id
		AND session.sim_deployment = deployment.id
		AND session.sim_distribution_tag = distribution_tag.id
		AND session.host_simplified_os = simplified_os.id
	)
);

# recombines information about flash simulations. holds all the data for flash simulations that simulation does
DROP VIEW flash_simulation;
CREATE VIEW flash_simulation AS (
	SELECT
		session.id,
		session.timestamp,
		session.message_version,
		ELT(session.sim_type + 1, 'Java', 'Flash') AS sim_type,
		sim_project.name AS sim_project,
		sim_name.name AS sim_name,
		session.sim_major_version,
		session.sim_minor_version,
		session.sim_dev_version,
		session.sim_svn_revision,
		session.sim_locale_language,
		session.sim_locale_country,
		session.sim_sessions_since,
		session.sim_sessions_ever,
		deployment.name AS sim_deployment,
		distribution_tag.name AS sim_distribution_tag,
		session.sim_dev,
		session.host_locale_language,
		session.host_locale_country,
		simplified_os.name AS host_simplified_os,
		flash_version_type.name AS host_flash_version_type,
		flash_info.host_flash_version_major,
		flash_info.host_flash_version_minor,
		flash_info.host_flash_version_revision,
		flash_info.host_flash_version_build,
		flash_info.host_flash_time_offset,
		flash_info.host_flash_accessibility,
		flash_domain.name AS host_flash_domain,
		flash_os.name AS host_flash_os
	FROM
		session, sim_project, sim_name, deployment, distribution_tag, simplified_os,
		flash_info, flash_version_type, flash_domain, flash_os
	WHERE (
		session.sim_project = sim_project.id
		AND session.sim_name = sim_name.id
		AND session.sim_deployment = deployment.id
		AND session.sim_distribution_tag = distribution_tag.id
		AND session.host_simplified_os = simplified_os.id
		AND session.id = flash_info.session_id
		AND flash_info.host_flash_version_type = flash_version_type.id
		AND flash_info.host_flash_domain = flash_domain.id
		AND flash_info.host_flash_os = flash_os.id
	)
);

# recombines information about java simulations. holds all the data for java simulations that simulation does
DROP VIEW java_simulation;
CREATE VIEW java_simulation AS (
	SELECT
		session.id,
		session.timestamp,
		session.message_version,
		ELT(session.sim_type + 1, 'Java', 'Flash') AS sim_type,
		sim_project.name AS sim_project,
		sim_name.name AS sim_name,
		session.sim_major_version,
		session.sim_minor_version,
		session.sim_dev_version,
		session.sim_svn_revision,
		session.sim_locale_language,
		session.sim_locale_country,
		session.sim_sessions_since,
		session.sim_sessions_ever,
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
		java_info.host_java_version_major,
		java_info.host_java_version_minor,
		java_info.host_java_version_maintenance,
		java_webstart_version.name AS host_java_webstart_version,
		java_timezone.name AS host_java_timezone
	FROM
		session, sim_project, sim_name, deployment, distribution_tag, simplified_os,
		java_info, java_os_name, java_os_version, java_os_arch, java_vendor, java_webstart_version, java_timezone
	WHERE (
		session.sim_project = sim_project.id
		AND session.sim_name = sim_name.id
		AND session.sim_deployment = deployment.id
		AND session.sim_distribution_tag = distribution_tag.id
		AND session.host_simplified_os = simplified_os.id
		AND session.id = java_info.session_id
		AND java_info.host_java_os_name = java_os_name.id
		AND java_info.host_java_os_version = java_os_version.id
		AND java_info.host_java_os_arch = java_os_arch.id
		AND java_info.host_java_vendor = java_vendor.id
		AND java_info.host_java_webstart_version = java_webstart_version.id
		AND java_info.host_java_timezone = java_timezone.id
	)
);





