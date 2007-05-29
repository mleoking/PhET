<?php 
    include_once("../admin/global.php");
    
    include_once(SITE_ROOT."admin/sys-utils.php");
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    
    include_once(SITE_ROOT."admin/zip.lib.php");    
    
    $zipfile = new zipfile();
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    $files = contribution_get_contribution_files($contribution_id);
    
    $first_file_name = null;
    
    foreach($files as $file) {
        eval(get_code_to_create_variables_from_array($file));
        
        $file_name = contribution_extract_original_file_name($contribution_file_url);
        
        if ($first_file_name == null) {
            $first_file_name = remove_file_extension($file_name);
        }
        
        $file_contents = file_get_contents(SITE_ROOT.$contribution_file_url);
        
        $zipfile->add_file($file_contents, $file_name);
    }
    
    if ($first_file_name == null) {
        $first_file_name = 'empty.zip';
    }

    $temp_file_name = $zipfile->write_to_temp_file($first_file_name);

    send_file_to_browser($temp_file_name, 'application/zip');
    
    unlink($temp_file_name);
?>