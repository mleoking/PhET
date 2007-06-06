<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	
	function print_success_message() {
	    print <<<EOT
	        <h1>Success</h1>
	        
	        <p>
	            The newsletter has been successfully dispatched.
	        </p>
EOT;

        force_redirect('index.php', 5);
	}
	
	function replace_jokers($text, $contributor) {
	    $name = $contributor['contributor_name'];
	    $date = date("F j, Y, g:i a");
	    
	    $text = str_ireplace('$NAME$', "$name", $text);
	    $text = str_ireplace('$DATE$', "$date", $text);
	    
	    return $text;
	}
	
	$newsletter_subject = $_REQUEST['newsletter_subject'];
	$newsletter_from    = $_REQUEST['newsletter_from'];
	$newsletter_body    = $_REQUEST['newsletter_body'];
	
	foreach(contributor_get_all_contributors() as $contributor) {
        $subs_newsletter_from = replace_jokers($newsletter_from, $contributor);
        $subs_newsletter_body = replace_jokers($newsletter_body, $contributor);

        if ($contributor['contributor_receive_email'] == 1) {
            mail($contributor['contributor_email'], 
                 $subs_newsletter_from, 
                 $subs_newsletter_body);
        }
	}		
	
	print_site_page('print_success_message', 9);

?>