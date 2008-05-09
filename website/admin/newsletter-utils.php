<?php

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

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