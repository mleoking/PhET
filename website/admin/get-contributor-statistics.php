<?php

    include_once("db.php");
    include_once("db-utils.php");
    include_once("contrib-utils.php");

    function cleanup($v) {
        $v = str_replace(',',  ' ', $v);
        $v = str_replace('\n', '',  $v);
        $v = str_replace('\r', '',  $v);

        return $v;
    }

    function is_allowed($key) {
        $forbidden_keys = array(
            '/.+_id$/i',
            '/.*password.*/i'
        );

        foreach ($forbidden_keys as $cur) {
            if (preg_match($cur, $key)) {
                return false;
            }
        }

        return true;
    }

    // FIXME: This query is very intensive for the server
    $result = db_exec_query('SELECT * FROM `contributor` LEFT JOIN `download_statistics` ON `contributor`.`contributor_id`=`download_statistics`.`contributor_id` ORDER BY `contributor_name` ASC ');

    $contents = '';

    $is_first_row = true;

    while ($row = mysql_fetch_assoc($result)) {
        if ($is_first_row) {
            $is_first_row = false;

            $is_first_col = true;

            foreach ($row as $key => $value) {
                if (!is_allowed($key)) continue;

                $key = cleanup($key);

                if ($is_first_col) {
                    $is_first_col = false;
                }
                else {
                    $contents .= ',';
                }

                $contents .= $key;
            }

            $contents .= "\n";
        }

        $is_first_col = true;

        foreach ($row as $key => $value) {
            if (!is_allowed($key)) continue;

            $value = cleanup($value);

            if ($value == '') $value = 'NA';

            if ($is_first_col) {
                $is_first_col = false;
            }
            else {
                $contents .= ",";
            }

            $contents .= "$value";
        }

        $contents .= "\n";
    }

    send_file_to_browser('contributor-statistics.csv', $contents, 'text/csv', 'attachment');

?>