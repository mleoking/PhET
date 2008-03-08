<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/password-protection.php");
	include_once(SITE_ROOT."admin/db.inc");
	include_once(SITE_ROOT."admin/web-utils.php");
	include_once(SITE_ROOT."admin/contrib-utils.php");
	include_once(SITE_ROOT."admin/site-utils.php");
	include_once(SITE_ROOT."admin/newsletter-utils.php");
	
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
	    
	    $text = str_replace('$NAME$', "$name", $text);
	    $text = str_replace('$DATE$', "$date", $text);
	    
	    return $text;
	}
	
	$newsletter_subject = $_REQUEST['newsletter_subject'];
	$newsletter_from    = $_REQUEST['newsletter_from'];
	$newsletter_body    = $_REQUEST['newsletter_body'];
	
	$no_contributor = array();
	
	$no_contributor['contributor_name'] = 'PhET User';
	
	newsletter_create(
		replace_jokers($newsletter_subject, $no_contributor), 
		replace_jokers($newsletter_body,    $no_contributor)
	);
	
	foreach(contributor_get_all_contributors() as $contributor) {
        $subs_newsletter_subject = replace_jokers($newsletter_subject, $contributor);
        $subs_newsletter_body    = replace_jokers($newsletter_body,    $contributor);

        // FIXME: hardcoded email address of someone who has departed, search for other occurances, probably for testing
        if ($contributor['contributor_receive_email'] == 1 && $contributor['contributor_email'] == 'degoes@colorado.edu') {
            mail($contributor['contributor_email'], 
                 $subs_newsletter_subject,
                 $subs_newsletter_body,
				 "From: $newsletter_from");
        }
	}		
	
	print_site_page('print_success_message', 9);

?>