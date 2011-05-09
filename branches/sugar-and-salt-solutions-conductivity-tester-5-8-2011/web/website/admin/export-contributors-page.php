<?php

// This must be defined before including global.php
$GLOBALS['IE6_DOWNLOAD_WORKAROUND'] = true;

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("include/web-utils.php");

class ExportContributorsPage extends SitePage {
    function __construct($nav_selected_page, $page_title = "Download File") {
        $this->render = true;
        parent::__construct($page_title, $nav_selected_page, null, SitePage::AUTHLEVEL_TEAM, false);
    }

    // from:http://www.ineedtutorials.com/code/php/export-mysql-data-to-csv-php-tutorial
    // dano: this dosen't match my memory of the page out there that talks about the de-facto CSV standard (there is no official standard), this function may need to be altered.
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
        /*
        $result = mysql_query($sql_query);
        $fields_cnt = mysql_num_fields($result);
        */
 
 
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

        header("Cache-Control: must-revalidate, post-check=0, pre-check=0");
        header("Content-Length: " . strlen($out));
        // Output to browser with appropriate mime type, you choose ;)
        header("Content-type: text/x-csv");
        //header("Content-type: text/csv");
        //header("Content-type: application/csv");
        header("Content-Disposition: attachment; filename=$filename");
        echo $out;
        exit;
    }
    
    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Assume failure
        $this->success = false;

        // Get the count of the rows in the table
        $num_rows = db_count_rows('contributor');
        if ($num_rows === false) {
            // Failed to get the count
            return;
        }

        // Got the number or rows, set up the download
        $this->success = true;
        $this->num_rows = $num_rows;
        $this->meta_refresh('export-contributors.php', 2);
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            BasePage::render_content();
            return $result;
        }

        if ($this->success) {
        print <<<EOT
            There are <em>{$this->num_rows}</em> contributors being exported.  The download will start in a moment.

EOT;
        }
        else {
        print <<<EOT
            There was an error preparing the download.  Please contact the system administrator.

EOT;
            
        }
    }
}

$download_page = new ExportContributorsPage(NavBar::NAV_NOT_SPECIFIED);
$download_page->update();
$download_page->render();

?>