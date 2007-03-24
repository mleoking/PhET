<?php

require_once("file-util.php");

function make_autorun_file($dest_dir, $icon_file, $open_file) {
    /*
     [autorun]
     open=InstData\Windows\VM\install.exe
     icon=Phet-logo-16x16.ico
    */
    
    // Copy the icon file to the distribution directory:
    copy($icon_file, $dest_dir.basename(PHET_AUTORUN_ICON));
    
    $open_file = str_ireplace($dest_dir, "", $open_file);
    
    $icon_file = basename($icon_file);
    
    if (($wh = fopen("${dest_dir}autorun.inf", 'wt')) !== false) {
        fwrite($wh, "[autorun]\n");
        fwrite($wh, "open=".with_local_separator("$open_file")."\n");
        fwrite($wh, "icon=".with_local_separator("$icon_file")."\n");
        fflush($wh);
        fclose($wh);
    }
}

?>
