<?php

	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/sim-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	
	function print_translations() {
		print <<<EOT
			<h1>Translated Sims</h1>
EOT;

		$sim_to_translations = sim_get_all_translated_language_names();
		
		$languages = array();
		
		foreach ($sim_to_translations as $sim_name => $map) {
			foreach ($map as $language_name => $launch_url) {
				$languages["$language_name"] = "$language_name";
			}
		}
		
		$columns = count($languages) + 1;
		
		print <<<EOT
			<div id="translated-versions">
			<table>
				<thead>
					<tr>
						<td class="even"></td>
EOT;

		$col_count = 1;

		foreach ($languages as $language) {
			$col_is_even = ($col_count % 2) == 0;
			
			$col_class = $col_is_even ? "even" : "odd";
			
			print "<td class=\"$col_class\">$language</td>";
			
			$col_count++;
		}
		
		print "</tr>";
		print "</thead>";
		
		print "<tbody>";
		
		$row_count = 0;
				
		foreach ($sim_to_translations as $sim_name => $map) {
			$row_is_even = ($row_count % 2) == 0;
			
			$row_class = $row_is_even ? "even" : "odd";
			
			print "<tr class=\"$row_class\">";

			$sim_page_link = sim_get_url_to_sim_page_by_sim_name($sim_name);
			
			print "<td class=\"even\"><a href=\"$sim_page_link\">$sim_name</a></td>";
			
			$col_count = 1;
			
			foreach ($languages as $language_name) {
				$col_is_even = ($col_count % 2) == 0;
				
				$col_class = $col_is_even ? "even" : "odd";
				
				print "<td title=\"$language_name\" class=\"$col_class\">";
				
				if (isset($map[$language_name])) {
					$launch_url = $map[$language_name];
					
					print <<<EOT
						<form action="$launch_url">
							<span>
								<input type="submit" name="submit" value="&nbsp;" />
							</span>
						</form>
EOT;
				}
				else {
					print "";
				}
				
				print "</td>";
				
				$col_count++;
			}
			
			print "</tr>";
			
			$row_count++;
		}
		
		print "</tbody>";		
		print "</table>";
		print "</div>";
	}
	
	print_site_page('print_translations', 2);

?>