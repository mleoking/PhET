<?php

    // Called from view-contribution to get a zip archive of uploaded files.

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/sys-utils.php");
    require_once("include/contrib-utils.php");

    require_once("include/zip.lib.php");

    if (isset($_REQUEST['contribution_id'])) {

        $zipfile = new zipfile();

        $contribution_id = $_REQUEST['contribution_id'];

        $contribution = contribution_get_contribution_by_id($contribution_id);
        $contribution_title = $contribution['contribution_title'];

        $files = contribution_get_contribution_files($contribution_id);

        foreach($files as $file) {
            // TODO: Test if the file name needs to be unescaped

            $decoded_file_contents = base64_decode($file['contribution_file_contents']);

            $zipfile->add_file($decoded_file_contents, $file['contribution_file_name']);
        }

        send_file_to_browser("${contribution_title}.zip", $zipfile->build_zipped_file(), 'application/zip');

    }

?>