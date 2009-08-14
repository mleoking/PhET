<?php

chdir(dirname(__FILE__));

    // SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    
    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once('include/db-utils.php');
    require_once('include/contrib-utils.php');
    
    $email_recipients =
        array('"Loeblein Patricia J." <ploeblei@jeffco.k12.co.us>',
              '"Marjorie Mildred Frankel" <marjorie.frankel@colorado.edu>',
              '"Daniel McKagan" <daniel.mckagan@gmail.com>');

    define('DEFAULT_INTERVAL', 100);

    function get_new_contributions($interval = DEFAULT_INTERVAL) {
        $sql = 'SELECT * '.
            'FROM contribution '.
            'WHERE '.
            "contribution_date_created > (NOW() - INTERVAL {$interval} DAY)";
        return db_get_rows_custom_query($sql);
    }

    function get_updated_contributions($interval = DEFAULT_INTERVAL) {
        $sql = 'SELECT * '.
            'FROM contribution '.
            'WHERE '.
            "contribution_date_updated > (NOW() - INTERVAL {$interval} DAY)".
            'AND contribution_date_created != contribution_date_updated';
        return db_get_rows_custom_query($sql);
    }

    function display_contributions($contributions) {
        $text = array();
        foreach ($contributions as $contribution) {
            $text[] = <<<EOT
Title: {$contribution['contribution_title']} 
Authors: {$contribution['contribution_authors']}
URL: http://phet.colorado.edu/teacher_ideas/view-contribution.php?contribution_id={$contribution['contribution_id']}

EOT;
        }
        return join("\n---\n\n", $text);
    }

    function main() {
        $new_contributions = get_new_contributions();
        $updated_contributions = get_updated_contributions();
        
        $num_new_contributions = count($new_contributions);
        $num_updated_contributions = count($updated_contributions);
        if (($num_new_contributions == 0) && ($num_updated_contributions == 0)) {
            $subject = 'PhET Website: No new or updated contributions this week';
            $message = <<<EOT
There are no new or updated contributions to the Teacher Ideas & Activites
section of the website this week.

EOT;
        }
        else {
            $subject = "PhET Website: {$num_new_contributions} new, {$num_updated_contributions} updated contribution(s) this week";
            
            $message = wordwrap("There is/are {$num_new_contributions} new and {$num_updated_contributions} updated contribution(s) this week to the Teacher Ideas & Activities section of the website this week.");
            if ($num_new_contributions > 0) {
                $message .= "\n\nNew Contributions:\n\n";
                $message .= display_contributions($new_contributions);
            }

            if ($num_updated_contributions > 0) {
                if ($num_new_contributions > 0) {
                    $message .= "\n================================================\n";
                    $message .= "================================================\n";
                    $message .= "================================================\n\n";
                }
                $message .= "Updated Contributions:\n\n";
                $message .= display_contributions($updated_contributions);
            }

            $message .= "\n\nThis is an automated email, please do not reply.\n\n";
            $message .= "The PhET Website Contribution Checking Robot\n";
        }

        $headers = 'From: dmckagan@tigercat.colorado.edu' . "\r\n" .
            'Reply-To: daniel.mckagan@gmail.com' . "\r\n" .
            'X-Mailer: PHP/' . phpversion();
        email(
            join(',', $email_recipients),
            $subject,
            $message,
            $headers);
    }

    main();

?>
