<?php
	include("db_util.php");
	$link = setup_mysql();
	
	// number of simulated messages to be sent
	$num_entries = 4000000;
	
	
	// percentage of sims that are flash (100-x % are java)
	$percent_flash = 30;
	
	
	
	
	// so execution isn't stopped 30 seconds in
	set_time_limit(4000000);
	
	$flashSimNames = array("arithmetic", "blackbody-spectrum", "charges-and-fields", "curve-fitting", "equation-grapher", "estimation", "faradays-law", "friction", "geometric-optics", "lunar-lander", "mass-spring-lab", "my-solar-system", "ohms-law", "pendulum-lab", "plinko-probability", "projectile-motion", "resistance-in-a-wire", "stern-gerlach", "vector-addition", "wave-on-a-string");
	
	$javaSimNames = array("acid-base-solutions", "all-sims", "balloons", "battery-voltage", "bound-states", "circuit-construction-kit", "color-vision", "common-strings", "conductivity", "discharge-lamps", "eating-and-exercise", "efield", "electric-hockey", "energy-skate-park", "faraday", "forces-1d", "fourier", "glaciers", "greenhouse", "hydrogen-atom", "ideal-gas", "lasers", "maze-game", "microwaves", "motion-2d", "moving-man", "mri", "mvc-example", "nuclear-physics", "ohm-1d", "optical-quantum-control", "optical-tweezers", "phetgraphics-demo", "photoelectric", "ph-scale", "quantum-tunneling", "quantum-wave-interference", "radio-waves", "reactions-and-rates", "rotation", "rutherford-scattering", "self-driven-particle-model", "semiconductor", "signal-circuit", "sim-template", "soluble-salts", "sound", "states-of-matter", "test-project", "the-ramp", "travoltage", "wave-interference");
	
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
		// report progress
		/*
		if($i % 10000 == 0) {
			echo "<p>" . $i . "</p>";
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
			$hostLocaleCountry = "none";
		}
		
		// sim sessions
		$simSessionsSince = 1;
		if(rand(0, 99) < 95) {
			$simSessionsSince += rand(1, 50);
		}
		$simSessionsEver = $simSessionsSince + rand(1, 10) * rand(1, 30) + rand(0, 10);
		
		
		// usage type
		$simUsageType = 'book-cd';
		if(rand(0, 99) < 30) {
			$simUsageType = 'external-website';
		}
		if(rand(0, 99) < 30) {
			$simUsageType = 'standalone-install';
		}
		if(rand(0, 99) < 60) {
			$simUsageType = 'full-install';
		}
		
		// distribution tag
		$simDistributionTag = "none";
		if($simUsageType == "book-cd") {
			if(rand(0, 99) < 40) {
				$simDistributionTag = "Wiley";
			} else {
				$simDistributionTag = "Pierce";
			}
		}
		
		// scenario
		if($simType == "flash") {
			$simScenario = "none";
		} else {
			$simScenario = "standalone-jar";
			if(rand(0, 99) < 30) {
				$simScenario = "installed-jar";
			}
			if(rand(0, 99) < 30) {
				$simScenario = "jnlp";
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
			
			if($simUsageType == 'external-website') {
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
				$messageVersion,
				$simProject,
				$simName,
				$simMajorVersion,
				$simMinorVersion,
				$simDevVersion,
				$simSvnRevision,
				$simLocaleLanguage,
				$simLocaleCountry,
				$simSessionsSince,
				$simSessionsEver,
				$simUsageType,
				$simDistributionTag,
				$simDev,
				$simScenario,
				$hostLocaleLanguage,
				$hostLocaleCountry,
				$hostFlashVersionType,
				$hostFlashVersionMajor,
				$hostFlashVersionMinor,
				$hostFlashVersionRevision,
				$hostFlashVersionBuild,
				$hostFlashTimeOffset,
				$hostFlashAccessibility,
				$hostFlashDomain,
				$hostFlashOS
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
			} else {
				$hostJavaOSVersion = rand(1, 5) . rand(0, 9);
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
			
			$hostJavaWebstartVersion = "none";
			
			if(rand(0, 99) < 30) {
				$hostJavaTimezone = "America/Denver";
			} else if(rand(0, 99) < 50) {
				$hostJavaTimezone = "America/New York";
			} else {
				$hostJavaTimezone = "America/Los Angeles";
			}
			
			$sessionID = insert_java_message(
				$messageVersion,
				$simProject,
				$simName,
				$simMajorVersion,
				$simMinorVersion,
				$simDevVersion,
				$simSvnRevision,
				$simLocaleLanguage,
				$simLocaleCountry,
				$simSessionsSince,
				$simSessionsEver,
				$simUsageType,
				$simDistributionTag,
				$simDev,
				$simScenario,
				$hostLocaleLanguage,
				$hostLocaleCountry,
				$hostJavaOSName,
				$hostJavaOSVersion,
				$hostJavaOSArch,
				$hostJavaVendor,
				$hostJavaVersionMajor,
				$hostJavaVersionMinor,
				$hostJavaVersionMaintenance,
				$hostJavaWebstartVersion,
				$hostJavaTimezone
			);
			
			// fix timestamp
			phet_mysql_query("UPDATE session SET timestamp = " . date("YmdHis", time() - $timestampOffset) . " WHERE id = " . $sessionID);
			
		}
		
	}
	
?>

