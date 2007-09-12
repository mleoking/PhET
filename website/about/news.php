<?php

	include_once("../admin/global.php");

	include_once(SITE_ROOT."admin/site-utils.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/newsletter-utils.php");	

	function print_content() {
		$changes_file = file_get_contents('http://phet.svn.sourceforge.net/viewvc/*checkout*/phet/trunk/simulations-java/docs/changes.txt');
		
		$changes_html = preg_replace('/ *\n+ */', '<br/>', $changes_file);
		
		$newsletters = newsletter_get_all();
		
		print <<<EOT
			<h1>News</h1>
			
			<h2>Website &amp; Simulation Updates</h2>
			
			<p>$changes_html</p>
			
			<h2>Newsletter Archive</h2>
EOT;

			if (count($newsletters) == 0) {
				print "<p>No newsletters have been issued yet.</p>";
			}
			else {
				$counter = 0;

				print "<ul>";

				foreach ($newsletters as $time => $newsletter) {			
					$date    = date('F j Y', $time);
					$subject = $newsletter['newsletter_subject'];
				
					print "<li><a href=\"#newsletter$counter\">$date - $subject</a>";
				
					$counter++;
				}
			
				print "</ul>";

				$counter = 0;

				foreach ($newsletters as $time => $newsletter) {
					$date    = date('F j Y', $time);
					$subject = $newsletter['newsletter_subject'];
					$body    = $newsletter['newsletter_body'];
				
					$body = str_replace('  ', '&nbsp;&nbsp;', $body);
					$body = preg_replace('/\s*(\r)?\n+(\s*((\r)?\n)+)?\s*/', '<br/>', $body);
				
					print <<<EOT
						<h3 id="newsletter$counter">$date - $subject</h3>
						<p><em>$body</em></p>
EOT;
					$counter++;
				}
			}
	}
	
    print_site_page('print_content', 8);

?>