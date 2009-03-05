<?php

    // Utils to support sims

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    
    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");
    require_once("include/db-utils.php");

    if (!defined('INSTALLER_TABLE_NAME')) {
        define('INSTALLER_TABLE_NAME', 'installer_info');
    }

    if (!defined('INSTALLER_MAX_ROWS')) {
        define('INSTALLER_MAX_ROWS', 10);
    }

    function installer_check_timestamp($new_timestamp) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $table = INSTALLER_TABLE_NAME;
        $safe_timestamp = mysql_real_escape_string($new_timestamp, $connection);
        $query = "SELECT * FROM `{$table}` WHERE ".
            "`installer_info_timestamp` >= '{$safe_timestamp}'";
        $result = db_exec_query($query);
        $count = mysql_num_rows($result);
        if ($count > 0) {
            return false;
        }

        return true;
    }

    function installer_add_new_timestamp($new_timestamp) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        // Escape the timestamp
        $safe_timestamp = mysql_real_escape_string("$new_timestamp", $connection);

        $result = db_insert_row(INSTALLER_TABLE_NAME,
                                array('installer_info_timestamp' => $safe_timestamp));
        if (!($result > 0)) {
            return false;
        }

        $id = $result;

        // Remove the old rows
        $rows = db_get_all_rows(INSTALLER_TABLE_NAME);
        if (count($rows) > INSTALLER_MAX_ROWS) {
            // Get the last id to save
            $last_row_to_save = $rows[count($rows) - INSTALLER_MAX_ROWS];
            $last_id = $last_row_to_save['installer_info_id'];

            // Delete anything older than that id
            $sql = 'DELETE FROM `'.INSTALLER_TABLE_NAME.'` WHERE `installer_info_id`<'.$last_id;
            $result = db_exec_query($sql);

            // We want the table sorted, this statement will do that (among other things)
            $sql = 'OPTIMIZE TABLE `'.INSTALLER_TABLE_NAME.'`';
            db_exec_query($sql);

            return $result;
        }

        // Table is smaller than the max number of rows
        return true;
    }


    function installer_get_latest_timestamp() {
        $extra = "ORDER BY `installer_info_id` DESC LIMIT 1";
        $row = db_get_row_by_condition(INSTALLER_TABLE_NAME, array(), false, $extra);
        if (!$row || !isset($row['installer_info_timestamp'])) {
            return false;
        }

        return $row['installer_info_timestamp'];
    }

    function installer_get_win_filename() {
        $file = PHET_DIST_ROOT.'installers/PhET-Installer_windows.exe';
        if (file_exists($file)) {
        	return $file;
        }

        // See ticket #1263
        // LEGACY SUPPORT: remove this code when John B has finish updating the installer names.
        return PHET_DIST_ROOT.'installers/PhET-windows-installer.exe';
    }

    function installer_get_mac_filename() {
        $file = PHET_DIST_ROOT.'installers/PhET-Installer_osx.zip';
        if (file_exists($file)) {
        	return $file;
        }

        // See ticket #1263
        // LEGACY SUPPORT: remove this code when John B has finish updating the installer names.
        return PHET_DIST_ROOT.'installers/PhET-osx-installer.zip';
    }
	
    function installer_get_linux_filename() {
        $file = PHET_DIST_ROOT.'installers/PhET-Installer_linux.bin';
        if (file_exists($file)) {
        	return $file;
        }

        // See ticket #1263
        // LEGACY SUPPORT: remove this code when John B has finish updating the installer names.
        return PHET_DIST_ROOT.'installers/PhET-linux-installer.bin';
    }

    function installer_get_cdrom_filename() {
        $file = PHET_DIST_ROOT.'installers/PhET-Installer_cdrom.zip';
        if (file_exists($file)) {
        	return $file;
        }

        // See ticket #1263
        // LEGACY SUPPORT: remove this code when John B has finish updating the installer names.
        return PHET_DIST_ROOT.'installers/PhET-CD-ROM.zip';
    }

    function installer_get_win_filesize() {
        $file = installer_get_win_filename();
        return (file_exists($file)) ? (int) (filesize($file) / (1024 * 1024)) : 0;
    }

    function installer_get_mac_filesize() {
        $file = installer_get_mac_filename();
        return (file_exists($file)) ? (int) (filesize($file) / (1024 * 1024)) : 0;
    }
	
    function installer_get_linux_filesize() {
        $file = installer_get_linux_filename();
        return (file_exists($file)) ? (int) (filesize($file) / (1024 * 1024)) : 0;
    }

    function installer_get_cdrom_filesize() {
        $file = installer_get_cdrom_filename();
        return (file_exists($file)) ? (int) (filesize($file) / (1024 * 1024)) : 0;
    }

?>