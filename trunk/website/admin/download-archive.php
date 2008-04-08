<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");

    include_once(SITE_ROOT."admin/zip.lib.php");

    if (isset($_REQUEST['contribution_id'])) {

        $zipfile = new zipfile();

        $contribution_id = $_REQUEST['contribution_id'];

        $contribution = contribution_get_contribution_by_id($contribution_id);
        $contribution_title = $contribution['contribution_title'];

        $files = contribution_get_contribution_files($contribution_id);

        foreach($files as $file) {
            //eval(get_code_to_create_variables_from_array($file));

            $decoded_file_contents = base64_decode($file['contribution_file_contents']);

            $zipfile->add_file($decoded_file_contents, $file['contribution_file_name']);
        }

        send_file_to_browser("${contribution_title}.zip", $zipfile->build_zipped_file(), 'application/zip');

    }

?>