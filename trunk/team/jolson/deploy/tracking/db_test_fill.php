<?php
	include("db_util.php");
	$link = setup_mysql();
	
	$num_entries = 4000000;
	
	
	function genlocale() {
		$lang = "en";
		if(rand(0, 99) < 50) {
			$country = "US";
		} else if(rand(0, 99) < 5) {
			$country = "GB";
		} else if(rand(0, 99) == 0) {
			$coutnry = "AU";
		} else if(rand(0, 99) == 5) {
			$country = "CA";
		} else {
			$country = "none";
		}
		if(rand(0, 99) < 4) {
			$lang = "es";
			if(rand(0, 99) < 50) {
				$country = "MX";
			} else if(rand(0, 99) < 15) {
				$country = "ES";
			} else {
				$country = "none";
			}
		}
		if(rand(0, 99) < 2) {
			$lang = "pt";
			if(rand(0, 99) < 40) {
				$country = "BR";
			} else if(rand(0, 99) < 30) {
				$country = "PT";
			} else {
				$country = "none";
			}
		}
		if(rand(0, 99) < 1) {
			$lang = "el";
			if(rand(0, 99) < 40) {
				$country = "GR";
			} else {
				$country = "none";
			}
		}
		if(rand(0, 99) < 1) {
			$lang = "nl";
			if(rand(0, 99) < 40) {
				$country = "NL";
			} else if(rand(0, 99) < 10) {
				$country = "BE";
			} else {
				$country = "none";
			}
		}
		if(rand(0, 99) < 1) {
			$lang = "sk";
			$country = "none";
			if(rand(0, 99) < 40) {
				$country = "SK";
			} else {
				$country = "none";
			}
		}
		if(rand(0, 99) < 1) {
			$lang = "ru";
			$country = "RU";
		}
		if(rand(0, 99) < 1) {
			$lang = "de";
			$country = "DE";
		}
		return array($lang, $country);
	}
	
	for($i=0; $i<$num_entries; $i++) {
		if($i % 10000 == 0) {
			echo "<p>" . $i . "</p>";
		}
		
		if(rand(0, 99) < 30) {
			// Flash
			
			
			$simNames = array("arithmetic", "blackbody-spectrum", "charges-and-fields", "curve-fitting", "equation-grapher", "estimation", "faradays-law", "friction", "geometric-optics", "lunar-lander", "mass-spring-lab", "my-solar-system", "ohms-law", "pendulum-lab", "plinko-probability", "projectile-motion", "resistance-in-a-wire", "stern-gerlach", "vector-addition", "wave-on-a-string");
			$revisions = array(22386, 26143, 26764, 27200, 27853);
			$major_versions = array(1, 1, 1, 2, 3);
			$minor_versions = array(0, 5, 9, 0, 4);
			$num_versions = 5;
			
			$simLocale = genlocale();
			if(rand(0, 99) < 5) {
				$hostLocale = genlocale();
			} else {
				$hostLocale = $simLocale;
			}
			
			$versionIndex = rand(0, $num_versions - 1);
			
			$simName = $simNames[rand(0, sizeof($simNames) - 1)];
			if(rand(0, 99) < 20) {
				$simName = "projectile-motion";
			}
			if(rand(0, 99) < 10) {
				$simName = "equation-grapher";
			}
			if(rand(0, 99) < 10) {
				$simName = "pendulum-lab";
			}
			
			$dev = false;
			if(rand(0, 99) < 1) {
				$dev = true;
			}
			
			$usageType = 'book-cd';
			if(rand(0, 99) < 30) {
				$usageType = 'external-website';
			}
			if(rand(0, 99) < 30) {
				$usageType = 'standalone-install';
			}
			if(rand(0, 99) < 60) {
				$usageType = 'full-install';
			}
			$distributionTag = "none";
			if($usageType == "book-cd") {
				if(rand(0, 99) < 40) {
					$distributionTag = "Wiley";
				} else {
					$distributionTag = "Pierce";
				}
			}
			
			$os = 1;
			$r = rand(0, 99);
			if($r >= 30 && $r < 60) {
				$os = 2;
			}
			if($r >= 60 && $r < 70) {
				$os = 3;
			}
			if($r >= 70 && $r < 75) {
				$os = 4;
			}
			if($r >= 75 && $r < 90) {
				$os = 5;
			}
			if($r >= 90 && $r < 94) {
				$os = 6;
			}
			if($r >= 94 && $r < 100) {
				$os = 0;
			}
			
			$query = "INSERT INTO sessions (timestamp, message_version, sim_type, sim_project, sim_name, sim_major_version, sim_minor_version, sim_dev_version, sim_svn_revision, sim_locale_language, sim_locale_country, sim_sessions_since, sim_sessions_ever, sim_usage_type, sim_distribution_tag, sim_dev, sim_scenario, host_locale_language, host_locale_country, host_simplified_os) VALUES (";
			
			
			$query .= date("YmdHis", time() - rand(0, 11231)*rand(0, 11231)) . ", "; // timestamp
			$query .= "1" . ", "; // message_version
			$query .= "1" . ", "; // sim_type
			$query .= "'" . $simName . "'" . ", "; // sim_project
			$query .= "'" . $simName . "'" . ", "; // sim_name
			if($dev) {
				$query .= (string)$major_versions[$versionIndex] . ", "; // sim_major_version
				$query .= (string)($minor_versions[$versionIndex] + rand(0, 3)) . ", "; // sim_minor_version
				$query .= (string)(rand(1, 30)) . ", "; // sim_dev_version
				$query .= (string)($revisions[$versionIndex] + rand(0, 100)) . ", "; // sim_svn_revision
			} else {
				
				$query .= $major_versions[$versionIndex] . ", "; // sim_major_version
				$query .= $minor_versions[$versionIndex] . ", "; // sim_minor_version
				$query .= "0, "; // sim_dev_version
				$query .= $revisions[$versionIndex] . ", "; // sim_svn_revision
			}
			$query .= "'" . $simLocale[0] . "'" . ", "; // sim_locale_language
			if($simLocale[1] == "none") {
				$query .= "NULL, "; // sim_locale_country
			} else {
				$query .= "'".  $simLocale[1] . "'" . ", "; // sim_locale_country
			}
			$query .= (string)(1 + rand(0, 1)*rand(0, 1)*rand(0, 1)*rand(0, 1)*rand(0, 1)*rand(1, 15)) . ", "; // sim_sessions_since
			$query .= (string)(1 + rand(0, 1) * rand(1, 15) + rand(0, 1) * rand(0, 1) * rand(0, 1) * rand(1, 300)) . ", "; // sim_sessions_ever
			$query .= "'" . $usageType . "'" . ", "; // sim_usage_type
			if($distributionTag == "none") {
				$query .= "NULL, "; // sim_distribution_tag
			} else {
				$query .= "'" . $distributionTag . "'" . ", "; // sim_distribution_tag
			}
			if($dev) {
				$query .= "true, "; // sim_dev
			} else {
				$query .= "false, "; // sim_dev
			}
			$query .= "NULL, "; // sim_scenario
			$query .= "'" . $hostLocale[0] . "'" . ", "; // host_locale_language
			$query .= "NULL, "; // host_locale_country
			$query .= $os; // host_simplified_os
			
			$query .= ");";
			
			//echo("<p>" . $query . "</p>");
			
			mysql_query($query);
			echo mysql_error($link);
			
			$session_id = mysql_insert_id();
			$query = "INSERT INTO flash_info (session_id, host_flash_version_type, host_flash_version_major, host_flash_version_minor, host_flash_version_revision, host_flash_version_build, host_flash_time_offset, host_flash_accessibility, host_flash_domain, host_flash_os) VALUES ("
				. $session_id . ", ";
			
			switch($os) {
				//(1 WIN_XP, 2 WIN_VISTA, 3 WIN_7, 4 WIN_OTHER, 5 MAC, 6 *NIX, 0 UNKNOWN)
				case 1:
					$query .= "'WIN', "; break;
				case 2:
					$query .= "'WIN', "; break;
				case 3:
					$query .= "'WIN', "; break;
				case 4:
					$query .= "'WIN', "; break;
				case 5:
					$query .= "'MAC', "; break;
				case 6:
					$query .= "'LNX', "; break;
				case 0:
					$query .= "'UNK', "; break;
			}
			$query .= rand(8, 10) . ", "; // host_flash_version_major
			$query .= "0" . ", "; // host_flash_version_minor
			$query .= (string)(rand(0, 3)*64) . ", "; // host_flash_version_revision
			$query .= "0" . ", "; // host_flash_version_build
			$query .= (string)(rand(-12, 12)*60) . ", "; // host_flash_time_offset
			if(rand(0, 99) < 2) {
				$query .= "true, "; // host_flash_accessibility
			} else {
				$query .= "false, "; // host_flash_accessibility
			}
			if($usageType == 'external-website') {
				$query .= "'";
				if(rand(0, 99) < 30) {
					$query .= "www.clonephet.com";
				} else if(rand(0, 99) < 60) {
					$query .= "www.yetanotherphetclone.com";
				} else {
					$query .= "www.physicsstuff.com";
				}
				$query .= "', ";
			} else {
				$query .= "'localhost', "; // host_flash_domain
			}
			switch($os) {
				//(1 WIN_XP, 2 WIN_VISTA, 3 WIN_7, 4 WIN_OTHER, 5 MAC, 6 *NIX, 0 UNKNOWN)
				case 1:
					$query .= "'Windows XP'"; break;
				case 2:
					$query .= "'Windows Vista'"; break;
				case 3:
					$query .= "'Windows 7'"; break;
				case 4:
					$query .= "'Windows ME'"; break;
				case 5:
					$query .= "'Mac OS X'"; break;
				case 6:
					$query .= "'Linux 2.6.20-17-generic'"; break;
				case 0:
					$query .= "'Something Unknown'"; break;
			}
			$query .= ");";
			
			//echo("<p>" . $query . "</p>");
			
			mysql_query($query);
			echo mysql_error($link);
			
		} else {
			// Java
			
			
			
			$simNames = array("acid-base-solutions", "all-sims", "balloons", "battery-voltage", "bound-states", "circuit-construction-kit", "color-vision", "common-strings", "conductivity", "discharge-lamps", "eating-and-exercise", "efield", "electric-hockey", "energy-skate-park", "faraday", "forces-1d", "fourier", "glaciers", "greenhouse", "hydrogen-atom", "ideal-gas", "lasers", "maze-game", "microwaves", "motion-2d", "moving-man", "mri", "mvc-example", "nuclear-physics", "ohm-1d", "optical-quantum-control", "optical-tweezers", "phetgraphics-demo", "photoelectric", "ph-scale", "quantum-tunneling", "quantum-wave-interference", "radio-waves", "reactions-and-rates", "rotation", "rutherford-scattering", "self-driven-particle-model", "semiconductor", "signal-circuit", "sim-template", "soluble-salts", "sound", "states-of-matter", "test-project", "the-ramp", "travoltage", "wave-interference");
			$revisions = array(22386, 26143, 26764, 27200, 27853);
			$major_versions = array(1, 1, 1, 2, 3);
			$minor_versions = array(0, 5, 9, 0, 4);
			$num_versions = 5;
			
			$simLocale = genlocale();
			if(rand(0, 99) < 5) {
				$hostLocale = genlocale();
			} else {
				$hostLocale = $simLocale;
			}
			
			$versionIndex = rand(0, $num_versions - 1);
			
			$simName = $simNames[rand(0, sizeof($simNames) - 1)];
			if(rand(0, 99) < 20) {
				$simName = "circuit-construction-kit";
			}
			if(rand(0, 99) < 10) {
				$simName = "moving-man";
			}
			if(rand(0, 99) < 10) {
				$simName = "faraday";
			}
			
			$dev = false;
			if(rand(0, 99) < 1) {
				$dev = true;
			}
			
			$usageType = 'book-cd';
			if(rand(0, 99) < 30) {
				$usageType = 'external-website';
			}
			if(rand(0, 99) < 30) {
				$usageType = 'standalone-install';
			}
			if(rand(0, 99) < 60) {
				$usageType = 'full-install';
			}
			$distributionTag = "none";
			if($usageType == "book-cd") {
				if(rand(0, 99) < 40) {
					$distributionTag = "Wiley";
				} else {
					$distributionTag = "Pierce";
				}
			}
			
			$os = 1;
			$r = rand(0, 99);
			if($r >= 30 && $r < 60) {
				$os = 2;
			}
			if($r >= 60 && $r < 70) {
				$os = 3;
			}
			if($r >= 70 && $r < 75) {
				$os = 4;
			}
			if($r >= 75 && $r < 90) {
				$os = 5;
			}
			if($r >= 90 && $r < 94) {
				$os = 6;
			}
			if($r >= 94 && $r < 100) {
				$os = 0;
			}
			
			$scenario = "standalone-jar";
			if(rand(0, 99) < 30) {
				$scenario = "installed-jar";
			}
			if(rand(0, 99) < 30) {
				$scenario = "jnlp";
			}
			
			$query = "INSERT INTO sessions (timestamp, message_version, sim_type, sim_project, sim_name, sim_major_version, sim_minor_version, sim_dev_version, sim_svn_revision, sim_locale_language, sim_locale_country, sim_sessions_since, sim_sessions_ever, sim_usage_type, sim_distribution_tag, sim_dev, sim_scenario, host_locale_language, host_locale_country, host_simplified_os) VALUES (";
			
			
			$query .= date("YmdHis", time() - rand(0, 11231)*rand(0, 11231)) . ", "; // timestamp
			$query .= "1" . ", "; // message_version
			$query .= "0" . ", "; // sim_type
			$query .= "'" . $simName . "'" . ", "; // sim_project
			$query .= "'" . $simName . "'" . ", "; // sim_name
			if($dev) {
				$query .= (string)$major_versions[$versionIndex] . ", "; // sim_major_version
				$query .= (string)($minor_versions[$versionIndex] + rand(0, 3)) . ", "; // sim_minor_version
				$query .= (string)(rand(1, 30)) . ", "; // sim_dev_version
				$query .= (string)($revisions[$versionIndex] + rand(0, 100)) . ", "; // sim_svn_revision
			} else {
				
				$query .= $major_versions[$versionIndex] . ", "; // sim_major_version
				$query .= $minor_versions[$versionIndex] . ", "; // sim_minor_version
				$query .= "0, "; // sim_dev_version
				$query .= $revisions[$versionIndex] . ", "; // sim_svn_revision
			}
			$query .= "'" . $simLocale[0] . "'" . ", "; // sim_locale_language
			if($simLocale[1] == "none") {
				$query .= "NULL, "; // sim_locale_country
			} else {
				$query .= "'".  $simLocale[1] . "'" . ", "; // sim_locale_country
			}
			$query .= (string)(1 + rand(0, 1)*rand(0, 1)*rand(0, 1)*rand(0, 1)*rand(0, 1)*rand(1, 15)) . ", "; // sim_sessions_since
			$query .= (string)(1 + rand(0, 1) * rand(1, 15) + rand(0, 1) * rand(0, 1) * rand(0, 1) * rand(1, 300)) . ", "; // sim_sessions_ever
			$query .= "'" . $usageType . "'" . ", "; // sim_usage_type
			if($distributionTag == "none") {
				$query .= "NULL, "; // sim_distribution_tag
			} else {
				$query .= "'" . $distributionTag . "'" . ", "; // sim_distribution_tag
			}
			if($dev) {
				$query .= "true, "; // sim_dev
			} else {
				$query .= "false, "; // sim_dev
			}
			$query .= "'" . $scenario . "', "; // sim_scenario
			$query .= "'" . $hostLocale[0] . "'" . ", "; // host_locale_language
			if($hostLocale[1] == "none") {
				$query .= "NULL, "; // host_locale_country
			} else {
				$query .= "'".  $hostLocale[1] . "'" . ", "; // host_locale_country
			}
			$query .= $os; // host_simplified_os
			
			$query .= ");";
			
			
			//echo("<p>" . $query . "</p>");
			mysql_query($query);
			echo mysql_error($link);
			
			
			
			$arch = "x86";
			if(rand(0, 99) < 10) {
				$arch = "x86_64";
			}
			if(rand(0, 99) < 1) {
				$arch = "SPARC";
			}
			
			$session_id = mysql_insert_id();
			$query = "INSERT INTO java_info (session_id, host_java_os_name, host_java_os_version, host_java_os_arch, host_java_vendor, host_java_version_major, host_java_version_minor, host_java_version_maintenance, host_java_webstart_version, host_java_timezone) VALUES ("
				. $session_id . ", ";
			switch($os) {
				//(1 WIN_XP, 2 WIN_VISTA, 3 WIN_7, 4 WIN_OTHER, 5 MAC, 6 *NIX, 0 UNKNOWN)
				case 1:
					$query .= "'Windows XP'"; break;
				case 2:
					$query .= "'Windows Vista'"; break;
				case 3:
					$query .= "'Windows 7'"; break;
				case 4:
					$query .= "'Windows ME'"; break;
				case 5:
					$query .= "'Mac OS X'"; break;
				case 6:
					$query .= "'Linux 2.6'"; break;
				case 0:
					$query .= "'Unknown'"; break;
			}
			$query .= ", ";
			
			switch($os) {
				//(1 WIN_XP, 2 WIN_VISTA, 3 WIN_7, 4 WIN_OTHER, 5 MAC, 6 *NIX, 0 UNKNOWN)
				case 1:
					$query .= "'5.0'"; break;
				case 2:
					$query .= "'6.0'"; break;
				case 3:
					$query .= "'7.0'"; break;
				case 4:
					$query .= "'4.5'"; break;
				case 5:
					if(rand(0, 99) < 30) {
						$query .= "'10.9'"; break;
					} else if(rand(0, 99) < 40) {
						$query .= "'10.7'"; break;
					} else {
						$query .= "'10.4'"; break;
					}
				case 6:
					if(rand(0, 99) < 30) {
						$query .= "'2.6'"; break;
					} else if(rand(0, 99) < 40) {
						$query .= "'2.4'"; break;
					}
				case 0:
					$query .= "'0'"; break;
			}
			$query .= ", ";
			
			$query .= "'" . $arch . "'" . ", "; // arch
			
			$query .= "'Sun Microsystems Inc.', "; // vendor
			$query .= "1, "; // major
			$query .= rand(4, 6) . ", "; // minor
			$query .= rand(0,30) . ", "; // maint
			
			$query .= "NULL, "; // webstart version
			
			if(rand(0, 99) < 30) {
				$query .= "'America/Denver'";
			} else if(rand(0, 99) < 50) {
				$query .= "'America/New York'";
			} else {
				$query .= "'America/Los Angeles'";
			}
			
			$query .= ");";
			
			//echo("<p>" . $query . "</p>");
			mysql_query($query);
			echo mysql_error($link);
			
		}
	}
	
?>

