<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class ManageDatabasePage extends SitePage {

    function handle_action() {
        if (isset($_REQUEST['action'])) {
            $action = $_REQUEST['action'];

            if ($action == "backup") {
                $this->success = db_backup();
            }
            else if ($action == "restore") {
                $thi->success = db_restore();
            }
        }
    }

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (isset($_REQUEST['action'])) {
            $this->handle_action();
        }
    }

    function print_backup() {
        if ($this->success) {
            print <<<EOT
                <h2>Database Backup Success</h2>

                <p>The database was successfully backed up.</p>

                <p>If you wish, you may <a href="db-backup/database.sql">download the SQL backup file</a>. You will need to specify your email and password and you must be accessing this web page from colorado.edu.</p>

                <p>Downloading the backup file is not necessary in order to use the restore feature.</p>

                <p>Click <a href="manage-db.php">here</a> to return to the database management panel.</p>

EOT;
        }
        else {
            print <<<EOT
                <h2>Database Backup Error</h2>

                <p>The database could not be backed up.</p>

EOT;
        }
    }

    function print_restore() {
        if ($this->success) {
            print <<<EOT
                <h2>Database Restore Success</h2>

                <p>The database was successfully restored from the last backup.</p>

EOT;
        }
        else {
            print <<<EOT
                <h2>Database Restore Error</h2>

                <p>The database could not be restored.</p>

EOT;
        }
    }

    function print_db_management() {
        print <<<EOT
            <p>In this section, you can manage the PhET database.</p>

            <p><strong>You should not use these options unless you know what you are doing.</strong></p>

            <ul>
                <li><a href="manage-db.php?action=backup">Backup the database</a></li>

                <li><a href="manage-db.php?action=restore">Restore the database</a> from the last backup</li>
            </ul>

EOT;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($this->success) && isset($_REQUEST["action"])) {
            if ($_REQUEST["action"] == "backup") {
                $this->print_backup();
                return;
            }
            else if ($_REQUEST["action"] == "restore") {
                $this->print_restore();
                return;
            }
        }

        $this->print_db_management();
    }

}

$page = new ManageDatabasePage("Manage Database", NavBar::NAV_ADMIN, null, SitePage::AUTHLEVEL_TEAM, false);
$page->update();
$page->render();

?>