DROP TABLE IF EXISTS sim_type;
CREATE TABLE sim_type (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL
) DEFAULT CHARACTER SET utf8;

DROP TABLE IF EXISTS project;
CREATE TABLE project (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	
	# foreign key for sim_type
	sim_type INT UNSIGNED NOT NULL
) DEFAULT CHARACTER SET utf8;

DROP TABLE IF EXISTS simulation;
CREATE TABLE simulation (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(100) NOT NULL,
	
	# foreign key for project
	project INT UNSIGNED NOT NULL
) DEFAULT CHARACTER SET utf8;

DROP TABLE IF EXISTS localized_simulation;
CREATE TABLE localized_simulation (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	
	# foreign key for simulation
	simulation INT UNSIGNED NOT NULL,
	
	# locale
	locale CHAR(5),
	
	# localized title
	title VARCHAR(200),
	
	# localized description
	description VARCHAR(5000)
) DEFAULT CHARACTER SET utf8;
