<?php

    // Utils to support sims

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    
    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    if (!defined('INSTALLER_TABLE_NAME')) {
        define('INSTALLER_TABLE_NAME', 'installer_info');
    }

    function installer_add_new_timestamp() {
        $time = time();
        db_insert_row(INSTALLER_TABLE_NAME, array('installer_info_timestamp' => $time));
        return $time;
    }

    function installer_get_latest_timestamp() {
        $extra = "ORDER BY `installer_info_id` DESC LIMIT 1";
        $row = db_get_row_by_condition(INSTALLER_TABLE_NAME, array(), false, false, $extra);
        if (!$row || !isset($row['installer_info_timestamp'])) {
            return false;
        }
        
        return $row['installer_info_timestamp'];
    }

?>