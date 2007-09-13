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
			<table>
				<tr>
					<td></td>
EOT;

		foreach ($languages as $language) {
			print "<td>$language</td>";
		}
		
		print "</tr>";
				
		foreach ($sim_to_translations as $sim_name => $map) {
			print "<tr>";

			$sim_page_link = sim_get_url_to_sim_page_by_sim_name($sim_name);
			
			print "<td><a href=\"$sim_page_link\">$sim_name</a></td>";
			
			foreach ($languages as $language_name) {
				print "<td>";
				
				if (isset($map[$language_name])) {
					$launch_url = $map[$language_name];
					
					print <<<EOT
						<form action="$launch_url">
							<div>
								<input type="submit" name="submit" value="&nbsp;" />
							</div>
						</form>
EOT;
				}
				else {
					print "";
				}
				
				print "</td>";
			}
			
			print "</tr>";
		}
		
		print "</table>";
	}
	
	print_site_page('print_translations', 2);

?>