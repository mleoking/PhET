<?php
	// script to fill the current statistics database with randomly sampled data
	// useful for testing processor / disk space usage and feasibility, and for
	// creating report tools
	
	include("db-auth.php");
	include("db-stats.php");
	$link = setup_mysql();
	
	// number of simulated messages to be sent
	$num_entries = 100000;
	
	
	// percentage of sims that are flash (100-x % are java)
	$percent_flash = 30;
	
	
	
	
	// so execution isn't stopped 30 seconds in
	set_time_limit(4000000);
	
	$flashSimNames = array("arithmetic", "blackbody-spectrum", "charges-and-fields", "curve-fitting", "equation-grapher", "estimation", "faradays-law", "friction", "geometric-optics", "lunar-lander", "mass-spring-lab", "my-solar-system", "ohms-law", "pendulum-lab", "plinko-probability", "projectile-motion", "resistance-in-a-wire", "stern-gerlach", "vector-addition", "wave-on-a-string");
	
	$javaSimNames = array("acid-base-solutions", "balloons", "battery-voltage", "bound-states", "circuit-construction-kit", "color-vision", "common-strings", "conductivity", "discharge-lamps", "eating-and-exercise", "efield", "electric-hockey", "energy-skate-park", "faraday", "forces-1d", "fourier", "glaciers", "greenhouse", "hydrogen-atom", "ideal-gas", "lasers", "maze-game", "microwaves", "motion-2d", "moving-man", "mri", "mvc-example", "nuclear-physics", "ohm-1d", "optical-quantum-control", "optical-tweezers", "phetgraphics-demo", "photoelectric", "ph-scale", "quantum-tunneling", "quantum-wave-interference", "radio-waves", "reactions-and-rates", "rotation", "rutherford-scattering", "self-driven-particle-model", "semiconductor", "signal-circuit", "sim-template", "soluble-salts", "sound", "states-of-matter", "test-project", "the-ramp", "travoltage", "wave-interference");
	
	// version information is plucked out of these arrays with a common index
	$revisions = array(22386, 26143, 26764, 27200, 27853);
	$major_versions = array(1, 1, 1, 2, 3);
	$minor_versions = array(0, 5, 9, 0, 4);
	$num_versions = 5;
	
	
	// generates a randomly sampled locale (ish)
	function genlocale() {
		$lang = "en";
		if(rand(0, 99) < 50) {
			$country = "US";
		} else if(rand(0, 99) < 5) {
			$country = "GB";
		} else if(rand(0, 99) == 0) {
			$country = "AU";
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
		if($country == "none") {
			$country = "NULL";
		}
		return array($lang, $country);
	}
	
	for($i=0; $i<$num_entries; $i++) {
		// report progress
		/*
		if($i % 10000 == 0) {
			print "<p>" . $i . "</p>";
		}
		*/
		
		
		if(rand(0, 99) < $percent_flash) {
			$simType = "flash";
		} else {
			$simType = "java";
		}
		
		// COMMON FIELDS
		
		$messageVersion = 1;
		
		// simName and simProject
		if($simType == "flash") {
			$simName = $flashSimNames[rand(0, sizeof($flashSimNames) - 1)];
			if(rand(0, 99) < 20) {
				$simName = "projectile-motion";
			}
			if(rand(0, 99) < 10) {
				$simName = "equation-grapher";
			}
			if(rand(0, 99) < 10) {
				$simName = "pendulum-lab";
			}
			$simProject = $simName;
		} else {
			$simName = $javaSimNames[rand(0, sizeof($javaSimNames) - 1)];
			if(rand(0, 99) < 20) {
				$simName = "circuit-construction-kit";
			}
			if(rand(0, 99) < 10) {
				$simName = "moving-man";
			}
			if(rand(0, 99) < 10) {
				$simName = "faraday";
			}
			$simProject = $simName;
		}
		
		// simDev
		$simDev = 0;
		if(rand(0, 99) < 1) {
			$simDev = 1;
		}
		
		// version information
		$versionIndex = rand(0, $num_versions - 1);
		$simMajorVersion = $major_versions[$versionIndex];
		$simMinorVersion = $minor_versions[$versionIndex];
		$simSvnRevision = $revisions[$versionIndex];
		if($simDev) {
			$simDevVersion = rand(1, 30);
			$simMinorVersion += rand(0, 3);
			$simSvnRevision += rand(0, 100);
		} else {
			$simDevVersion = 0;
		}
		
		// locale information
		$simLocale = genlocale();
		if(rand(0, 99) < 5) {
			$hostLocale = genlocale();
		} else {
			$hostLocale = $simLocale;
		}
		$simLocaleLanguage = $simLocale[0];
		$simLocaleCountry = $simLocale[1];
		$hostLocaleLanguage = $hostLocale[0];
		$hostLocaleCountry = $hostLocale[1];
		if($simType == "flash") {
			$hostLocaleCountry = "NULL";
		}
		
		// sim sessions
		$simSessionsSince = 1;
		if(rand(0, 99) < 3) {
			$simSessionsSince += rand(1, 5);
		}
		$simTotalSessions = $simSessionsSince + rand(1, 10) * rand(1, 30) + rand(0, 10);
		
		
		// deployment
		$simDeployment = 'phet-installation';
		if(rand(0, 99) < 10) {
			$simDeployment = 'other-website';
		}
		if(rand(0, 99) < 30) {
			$simDeployment = 'standalone-jar';
		}
		
		// distribution tag
		$simDistributionTag = "NULL";
		if($simDeployment == "phet-installation") {
			if(rand(0, 99) < 40) {
				$simDistributionTag = "O'Reilly";
			} else {
				$simDistributionTag = "Pierce";
			}
		}
		
		// combined operating system information
		$coreOS = "Windows";
		if(rand(0, 99) < 15) {
			$coreOS = "Mac";
		} else if(rand(0, 99) < 1) {
			$coreOS = "Linux";
		}
		
		if($coreOS == "Windows") {
			$subOS = "Windows XP";
			if(rand(0, 99) < 30) {
				$subOS = "Windows Vista";
			} else if(rand(0, 99) < 5) {
				$subOS = "Windows NT";
			}
		} else if($coreOS == "Mac") {
			$subOS = "Mac OS X";
		} else if($coreOS == "Linux") {
			$subOS = "Linux";
		}
		
		$timestampOffset = rand(0, 11231)*rand(0, 11231);
		$timestampRatio = 1 - $timestampOffset / (11231*11231);
		
		$simVersionTimestamp = time() - 2 * $timestampOffset;
		
		if($simType == "flash") {
			
			switch($coreOS) {
				case "Windows":
					$hostFlashVersionType = "WIN"; break;
				case "Mac":
					$hostFlashVersionType = "MAC"; break;
				case "Linux":
					$hostFlashVersionType = "LNX"; break;
				default:
					$hostFlashVersionType = "UNK"; break;
			}
			
			$hostFlashVersionMajor = 8;
			if(rand(0, 99) < 80) {
				$hostFlashVersionMajor = 9;
			}
			if(rand(0, 99) < 10) {
				$hostFlashVersionMajor = 10;
			}
			
			$hostFlashVersionMinor = 0;
			$hostFlashVersionRevision = rand(0, 1) * 124;
			$hostFlashVersionBuild = 0;
			
			$hostFlashTimeOffset = rand(-12, 12)*60;
			
			$hostFlashAccessibility = (rand(0, 99) < 1 ? 1 : 0);
			
			if($simDeployment == 'external-website') {
				if(rand(0, 99) < 30) {
					$hostFlashDomain = "www.clonephet.com";
				} else if(rand(0, 99) < 60) {
					$hostFlashDomain = "www.yetanotherphetclone.com";
				} else {
					$hostFlashDomain = "www.physicsstuff.com";
				}
			} else {
				$hostFlashDomain = "localhost";
			}
			
			$hostFlashOS = $subOS;
			
			$sessionID = insert_flash_message(
				array (
					"message_version" => $messageVersion,
					"sim_project" => $simProject,
					"sim_name" => $simName,
					"sim_major_version" => $simMajorVersion,
					"sim_minor_version" => $simMinorVersion,
					"sim_dev_version" => $simDevVersion,
					"sim_svn_revision" => $simSvnRevision,
					"sim_version_timestamp" => $simVersionTimestamp,
					"sim_locale_language" => $simLocaleLanguage,
					"sim_locale_country" => $simLocaleCountry,
					"sim_sessions_since" => $simSessionsSince,
					"sim_total_sessions" => $simTotalSessions,
					"sim_deployment" => $simDeployment,
					"sim_distribution_tag" => $simDistributionTag,
					"sim_dev" => $simDev,
					"host_locale_language" => $hostLocaleLanguage,
					"host_locale_country" => $hostLocaleCountry,
					"host_flash_version_type" => $hostFlashVersionType,
					"host_flash_version_major" => $hostFlashVersionMajor,
					"host_flash_version_minor" => $hostFlashVersionMinor,
					"host_flash_version_revision" => $hostFlashVersionRevision,
					"host_flash_version_build" => $hostFlashVersionBuild,
					"host_flash_time_offset" => $hostFlashTimeOffset,
					"host_flash_accessibility" => $hostFlashAccessibility,
					"host_flash_domain" => $hostFlashDomain,
					"host_flash_os" => $hostFlashOS
				)
			);
			
			// fix timestamp
			phet_mysql_query("UPDATE session SET timestamp = " . date("YmdHis", time() - $timestampOffset) . " WHERE id = " . $sessionID);
			
		}
		
		
		if($simType == "java") {
			
			$hostJavaOSName = $subOS;
			if($coreOS == "Windows") {
				$hostJavaOSVersion = 4.0;
				if($subOS == "Windows XP") {
					$hostJavaOSVersion = 5.1;
				}
				if($subOS == "Windows Vista") {
					$hostJavaOSVersion = 6.0;
				}
			} else if($coreOS == "Mac") {
				$hostJavaOSVersion = '10.' . (string)(rand(0, 9));
			} else {
				$hostJavaOSVersion = '2.' . (string)(rand(0, 2) + 4) . '.' . (string)(rand(11, 26));
			}
			
			$hostJavaOSArch = "x86";
			if(rand(0, 99) < 5) {
				$hostJavaOSArch = "x86_64";
			}
			
			$hostJavaVendor = "Sun Microsystems Inc.";
			
			$hostJavaVersionMajor = 1;
			
			$hostJavaVersionMinor = 5;
			if(rand(0, 99) < 5 + 90 * (1 - $timestampRatio)) { $hostJavaVersionMinor = 4; }
			if(rand(0, 99) < 90 * $timestampRatio) { $hostJavaVersionMinor = 6; }
			
			$hostJavaVersionMaintenance = rand(0, 1) * rand(0, 2) * rand(1, 3);
			
			$hostJavaWebstartVersion = "NULL";
			
			if(rand(0, 99) < 30) {
				$hostJavaTimezone = "America/Denver";
			} else if(rand(0, 99) < 50) {
				$hostJavaTimezone = "America/New York";
			} else {
				$hostJavaTimezone = "America/Los Angeles";
			}
			
			$sessionID = insert_java_message(
				array (
					"message_version" => $messageVersion,
					"sim_project" => $simProject,
					"sim_name" => $simName,
					"sim_major_version" => $simMajorVersion,
					"sim_minor_version" => $simMinorVersion,
					"sim_dev_version" => $simDevVersion,
					"sim_svn_revision" => $simSvnRevision,
					"sim_version_timestamp" => $simVersionTimestamp,
					"sim_locale_language" => $simLocaleLanguage,
					"sim_locale_country" => $simLocaleCountry,
					"sim_sessions_since" => $simSessionsSince,
					"sim_total_sessions" => $simTotalSessions,
					"sim_deployment" => $simDeployment,
					"sim_distribution_tag" => $simDistributionTag,
					"sim_dev" => $simDev,
					"host_locale_language" => $hostLocaleLanguage,
					"host_locale_country" => $hostLocaleCountry,
					"host_java_os_name" => $hostJavaOSName,
					"host_java_os_version" => $hostJavaOSVersion,
					"host_java_os_arch" => $hostJavaOSArch,
					"host_java_vendor" => $hostJavaVendor,
					"host_java_version_major" => $hostJavaVersionMajor,
					"host_java_version_minor" => $hostJavaVersionMinor,
					"host_java_version_maintenance" => $hostJavaVersionMaintenance,
					"host_java_webstart_version" => $hostJavaWebstartVersion,
					"host_java_timezone" => $hostJavaTimezone
				)
			);
			
			// fix timestamp
			phet_mysql_query("UPDATE session SET timestamp = " . date("YmdHis", time() - $timestampOffset) . " WHERE id = " . $sessionID);
			
		}
		
	}
	
	// insert user visits
	for($i = 0; $i < $num_entries; $i++) {
		$firstOffset = rand(0, 11231)*rand(0, 11231);
		$userPreferencesFileCreationTime = time() - $firstOffset;
		$userInstallTimestamp = $userPreferencesFileCreationTime - rand(0, 20000);
		if(rand(0, 99) < 60) {
			$userInstallTimestamp = "NULL";
		}
		$userTotalSessions = rand(0, 10) * rand(1, 10) * rand(0, 1) * rand(0, 1) * rand(0, 1) + rand(1, 30);
		if(rand(0, 99) < 7) {
			$userTotalSessions = 1;
		}
		$first_time = $userPreferencesFileCreationTime;
		if($userTotalSessions == 1) {
			$last_time = $first_time;
		} else {
			$last_time = time() - $firstOffset / (rand(1, 3) + rand(0, 1) * rand(5, 30));
		}
		$values = array(
			'user_preferences_file_creation_time' => $userPreferencesFileCreationTime,
			'user_installation_timestamp' => $userInstallTimestamp,
			'user_total_sessions' => $userTotalSessions,
			'first_seen_month' => quo(date("Y-m-01", $first_time)),
			'last_seen_month' => quo(date("Y-m-01", $last_time))
		);
		$insert_query = query_from_values("user", $values);
		phet_mysql_query($insert_query);
		
		$i += $userTotalSessions - 1;
	}
	
?>