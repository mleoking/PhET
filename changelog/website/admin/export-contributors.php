<?php

// This must be defined before including global.php
$GLOBALS['IE6_DOWNLOAD_WORKAROUND'] = true;

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/web-utils.php");

class ExportMemberFile extends SitePage {
    function __construct($nav_selected_page, $page_title = "Download File") {
        $this->render = true;
        parent::__construct($page_title, $nav_selected_page, null, SitePage::AUTHLEVEL_TEAM, false);
    }

    // from:http://www.ineedtutorials.com/code/php/export-mysql-data-to-csv-php-tutorial
    // TODO: convert to use db-utils.php functions
    //
    // Note: The first time I used this function I tried exporting all
    // the contributor information, sans password info and id.  Open
    // office would NOT load all the rows of the resulting file.
    // Instead it lost/skipped/missed a couple hundred, and the weird
    // part is that it was in the middle of the file.  The request was
    // just for the email addreses, so I've gone and done just that
    // and it exports fine.  If we want more info in the future, this
    // function may not be adequate, it may need some fixin'
    //
    function exportMysqlToCsv($table, $fields = NULL, $filename = 'export.csv') {
        $csv_terminated = "\n";
        $csv_separator = ",";
        $csv_enclosed = '"';
        $csv_escaped = "\\";
        if (is_null($fields)) {
            $sql_query = "select * from $table";
        }
        else {
            $sql_query = "select ".join(',', $fields)." from $table";
        }
 
        // Gets the data from the database
        $result = mysql_query($sql_query);
        $fields_cnt = mysql_num_fields($result);
 
        $schema_insert = '';
 
        for ($i = 0; $i < $fields_cnt; $i++) {
            $l = $csv_enclosed . str_replace($csv_enclosed, $csv_escaped . $csv_enclosed,
                                             stripslashes(mysql_field_name($result, $i))) . $csv_enclosed;
            $schema_insert .= $l;
            $schema_insert .= $csv_separator;
        } // end for
 
        $out = trim(substr($schema_insert, 0, -1));
        $out .= $csv_terminated;
 
        // Format the data
        while ($row = mysql_fetch_array($result)) {
            $schema_insert = '';
            for ($j = 0; $j < $fields_cnt; $j++) {
                if ($row[$j] == '0' || $row[$j] != '') {
 
                    if ($csv_enclosed == '') {
                        $schema_insert .= $row[$j];
                    }
                    else {
                        $schema_insert .= $csv_enclosed . 
                            str_replace($csv_enclosed, $csv_escaped . $csv_enclosed, $row[$j]) . $csv_enclosed;
                    }
                }
                else {
                    $schema_insert .= '';
                }
 
                if ($j < $fields_cnt - 1) {
                    $schema_insert .= $csv_separator;
                }
            } // end for
 
            $out .= $schema_insert;
            $out .= $csv_terminated;
        } // end while

        return $out;

        /*
         Original headers in the function
        header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
        header("Content-Length: " . strlen($out));
        // Output to browser with appropriate mime type, you choose ;)
        header("Content-type: text/x-csv");
        //header("Content-type: text/csv");
        //header("Content-type: application/csv");
        header("Content-Disposition: attachment; filename=$filename");
        echo $out;
        exit;
        */
    }
    
    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        //$contributors = db_get_all_rows('contributor');
        $i = 0;
        //Header
        $all_fields = array('contributor_email');

        //        $all_fields = array('contributor_is_team_member', 'contributor_email', 'contributor_name', 'contributor_organization', 'contributor_address', 'contributor_office', 'contributor_city', 'contributor_state', 'contributor_country', 'contributor_postal_code', 'contributor_primary_phone', 'contributor_secondary_phone', 'contributor_fax', 'contributor_title', 'contributor_receive_email', 'contributor_desc');
        
        $out = $this->exportMysqlToCsv('contributor', $all_fields);
        send_file_to_browser("phet_contributors_export.csv", $out, "application/vnd.ms-excel", "attachment");
        exit;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            BasePage::render_content();
            return $result;
        }
    }
}

$download_page = new ExportMemberFile(NavBar::NAV_NOT_SPECIFIED);
$download_page->update();
$download_page->render();

?>