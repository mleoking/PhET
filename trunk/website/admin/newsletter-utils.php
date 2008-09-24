<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    /*
     * September 24th, 2008
     * Newsletters are handleded differently than the original design.
     * They are done on an entirely differet program, exported in PDF,
     * and NOT emailed out to anybody.
     * 
     * Currently the website will have an archive of newsletter, and 
     * all updating will be done by hand.  Newsletters will exist in
     * phet-dist/newsletters/phet_newsletter_MMDD_YYYY.pdf
     * where MM is the month, DD is the day, YYYY is the year.  Ex:
     * phet_newsletter_july16_2008.pdf
     * 
     */

    function newsletter_create($subject, $body) {
        return db_insert_row(
            'newsletter',
            array(
                'newsletter_subject' => $subject,
                'newsletter_body'    => $body
            )
        );
    }

    function newsletter_get_all() {
        $newsletters = array();

        $result = db_exec_query('SELECT * FROM `newsletter` ORDER BY `newsletter_date_created` DESC ');

        while ($row = mysql_fetch_assoc($result)) {
            $time = strtotime($row['newsletter_date_created']);

            $newsletter = format_for_html($row);

            $newsletters["$time"] = $newsletter;
        }

        return $newsletters;
    }

?>