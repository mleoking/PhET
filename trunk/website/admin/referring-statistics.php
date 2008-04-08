<?php

    include_once("../admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");

    function referring_statistics_add($source, $target) {
        $condition = array(
            'referring_statistics_source_page' => $source,
            'referring_statistics_target_page' => $target
        );

        $row = db_get_row_by_condition('referring_statistics', $condition);

        if (!$row) {
            $condition['referring_statistics_count'] = "1";

            return db_insert_row('referring_statistics', $condition);
        }
        else {
            return db_update_table(
                'referring_statistics',
                array(
                    'referring_statistics_count' => $row['referring_statistics_count'] + 1
                ),
                'referring_statistics_id',
                $row['referring_statistics_id']
            );
        }
    }

    function referring_statistics_comparator($a, $b) {
        return strcasecmp($a['referring_statistics_target_page'], $b['referring_statistics_target_page']);
    }

    function referring_statistics_get_all() {
        $rows = db_get_rows_by_condition('referring_statistics', array(), false, false);

        usort($rows, "referring_statistics_comparator");

        return $rows;
    }

    function referring_statistics_print_into_table($table_id) {
        $source_page_to_count = array();

        print "<table id=\"$table_id\">";

        print <<<EOT
            <thead>
                <tr>
                    <td>Target Page</td>    <td>Source Page</td>    <td>Count</td>
                </tr>
            </thead>

            <tbody>
EOT;

        foreach (referring_statistics_get_all() as $stat) {
            $source = format_for_html($stat['referring_statistics_source_page']);
            $target = format_for_html($stat['referring_statistics_target_page']);
            $count  = format_for_html($stat['referring_statistics_count']);

            print <<<EOT
                <tr>
                    <td>$target</td>    <td>$source</td>    <td>$count</td>
                </tr>
EOT;
        }

        print "</tbody></table>";
    }

?>