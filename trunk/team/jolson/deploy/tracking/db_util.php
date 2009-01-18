
<?php
	function setup_mysql() {
		$link = mysql_connect("localhost", "www-data", "d3#r3m0nt$") or die(mysql_error());
		mysql_select_db("phet_stats_test_1") or die(mysql_error());
		return $link;
	}
	function insert_session($simType, $simProject, $simName, $simMajorVersion, $simMinorVersion, $simDevVersion, $simSvnRevision, $simLocaleLanguage, $simLocaleCountry, $simSessionsSince, $simSessionsEver, $simUsageType, $simDistributionTag, $simDev, $simScenario, $hostLocaleLanguage, $hostLocaleCountry, $hostSimplifiedOS) {
		
	}
?>

