<?php

	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/nominate-utils.php");	
	include_once(SITE_ROOT."admin/referring-statistics.php");

	function print_file_to_stats($file_to_stats, $print_missing = true) {
		if (count($file_to_stats) > 0) {
			print "<ul>";

			foreach($file_to_stats as $stats) {
				$file  = basename($stats['file']);
				$count = $stats['count'];

				print "<li>$file - $count</li>";
			}

			print "</ul>";
		}
		else if ($print_missing) {
			print "No downloads for this month.";
		}
	}

	function print_stats() {
		// download_statistics_date
		// download_statistics_file
		// contributor_id
		
		print <<<EOT
			<h1>View Statistics</h1>
			
			<h2>Referring Page Statistics</h2>
			
			<p>These statistics show you what source page brought a user to a given target page.</p>
EOT;

			referring_statistics_print_into_table('referring-statistics');
			
			print <<<EOT
			<h2>Contributor Statistics</h2>
			
			<p>Click <a href="get-contributor-statistics.php">here</a> to download the contributor data as a CSV file.</p>
			
			<p>Note that if a contributor has downloaded more than one installer, they will be listed in multiple rows (one row for each installer they have downloaded).</p>
			
			<h2>File Download Count</h2>
EOT;

		$result = db_exec_query(
			'SELECT * FROM `download_statistics` ORDER BY `download_statistics`.`download_statistics_date` DESC '
		);
		
		$file_to_stats = array();
		
		$last_month = null;
		$last_year  = null;
		
		$num_rows = mysql_num_rows($result);
		$cur_row  = 0;
				
		while ($download = mysql_fetch_assoc($result)) {
			$cur_row++;
			
			$file = trim($download['download_statistics_file']);
			
			$mysql_timestamp = $download['download_statistics_date'];
			
			$unix_timestamp = strtotime($mysql_timestamp);
			
			$date  = getdate($unix_timestamp);
			
			$month = $date['month']; // mday
			$year  = $date['year'];  // yday
			
			if ($month != $last_month || $year != $last_year) {
				if ($last_month != null) {
					print "<h3>$last_month Statistics</h3>";
				
					print_file_to_stats($file_to_stats);
				
					$file_to_stats = array();
				}

				$last_month = $month;
				$last_year  = $year;
			}
			
			$file_to_stats[$file]['file']  = $file;
			$file_to_stats[$file]['count'] = isset($file_to_stats[$file]['count']) ? $file_to_stats[$file]['count'] + 1 : 1;
		}
		
		if (count($file_to_stats) > 0) {
			print "<h3>$month Statistics</h3>";
		
			print_file_to_stats($file_to_stats);
		}
		
		print <<<EOT
			<h2>Gold Star Nomination Statistics</h2>
EOT;

		$stats = get_nomination_statistics();
		$descs = get_nomination_descriptions();

		if (count($stats) > 0) {
			print "<ul>";
		
			foreach ($stats as $contribution_id => $count) {
				$contribution = contribution_get_contribution_by_id($contribution_id);
			
				$title      = format_string_for_html($contribution['contribution_title']);
				$title_html = "<a href=\"../teacher_ideas/edit-contribution.php?contribution_id=$contribution_id\">$title</a>";
				
				$desc  = $descs[$contribution_id];
				
				print "<li>$title_html - $count Gold Star Nominations $desc</li>";
			}
			
			print "</ul>";			
		}
		else {
			print "<p>No contributions have been nominated as Gold Star contributions.</p>";
		}
	}
	
	print_site_page('print_stats', 9);
?>