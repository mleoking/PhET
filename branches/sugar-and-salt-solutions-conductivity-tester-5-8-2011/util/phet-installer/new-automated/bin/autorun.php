<?php
	require_once("config.php");
	require_once("file-util.php");
	require_once("string-util.php");

	function autorun_create_autorun_file($file_to_open) {
	    /*
	     [autorun]
	     open=InstData\Windows\VM\install.exe
	     icon=Phet-logo-16x16.ico
	    */
    
	    // Copy the icon file to the distribution directory:
	    copy(AUTORUN_ICON_SRC, AUTORUN_ICON_DEST);
    
	    if (($wh = fopen(AUTORUN_FILE_DEST, 'wt')) !== false) {
	        fwrite($wh, "[autorun]\r\n");
	        fwrite($wh, "open=".file_with_separator("$file_to_open",   FILE_SEPARATOR_WINNT)."\r\n");
	        fwrite($wh, "icon=".file_with_separator(AUTORUN_ICON_NAME, FILE_SEPARATOR_WINNT)."\r\n");
	        fflush($wh);
	        fclose($wh);
	    }
	}
	
	function autorun_cleanup_files() {
		unlink(AUTORUN_ICON_DEST);
		unlink(AUTORUN_FILE_DEST);
	}

?>
