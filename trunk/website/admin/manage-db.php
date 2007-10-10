<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/db.inc");
    include_once(SITE_ROOT."admin/password-protection.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    
    function handle_action() {
		if (isset($_REQUEST['action'])) {
			$action = $_REQUEST['action'];
			
            if ($action == "backup") {
                $GLOBALS['success'] = db_backup();

				print_site_page('print_backup', 9);
            }
            else if ($action == "restore") {
                $GLOBALS['success'] = db_restore();

				print_site_page('print_restore', 9, "manage-db.php", 2);
            }
        }
    }

	function print_backup() {
		global $success;
		
		if ($success) {
			print <<<EOT
				<h1>Database Backup Success</h1>
				
				<p>The database was successfully backed up.</p>
				
				<p>If you wish, you may <a href="db-backup/database.sql">download the SQL backup file</a>. You will need to specify your email and password and you must be accessing this web page from colorado.edu.</p>
				
				<p>Downloading the backup file is not necessary in order to use the restore feature.</p>
				
				<p>Click <a href="manage-db.php">here</a> to return to the database management panel.</p>
EOT;
		}
		else {
			print <<<EOT
				<h1>Database Backup Error</h1>
				
				<p>The database could not be backed up.</p>
EOT;
		}
	}
	
	function print_restore() {
		global $success;

		if ($success) {
			print <<<EOT
				<h1>Database Restore Success</h1>
				
				<p>The database was successfully restored from the last backup.</p>
EOT;
		}
		else {
			print <<<EOT
				<h1>Database Restore Error</h1>
				
				<p>The database could not be restored.</p>
EOT;
		}
	}	

    function print_db_management() {
		print <<<EOT
			<h1>Manage Database</h1>
			
			<p>In this section, you can manage the PhET database.</p>
			
			<p><strong>You should not use these options unless you know what you are doing.</strong></p>
			
			<ul>
				<li><a href="manage-db.php?action=backup">Backup the database</a></li>
				
				<li><a href="manage-db.php?action=restore">Restore the database</a> from the last backup</li>
			</ul>
EOT;
    }
    
	if (isset($_REQUEST['action'])) {
    	handle_action();		
	}
	else {
    	print_site_page('print_db_management', 9);
	}

?>