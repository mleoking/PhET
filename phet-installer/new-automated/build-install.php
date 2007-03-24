<?php

require_once("autorun.php");
require_once("config.php");
require_once("global.php");
require_once("file-util.php");
require_once("web-util.php");
require_once("jnlp.php");
require_once("ia.php");
require_once("xml-util.php");

function rip_website() {
    flushing_echo("Ripping website...");
    
    exec(RIPPER_EXE." ".RIPPER_ARGS);
}

function jnlp_open_tag_callback($depth, $tag, $attrs) {
    global $g_codebase;
    
    foreach ($attrs as $key => $value) {        
        if ($key == "href") {
            $href = $value;
            
            if (!is_absolute_http_url($href)) {
                $absolute_url = get_real_path($g_codebase.$href);
            }
            else {
                $absolute_url = $href;
            }
            
            if (($contents = file_get_contents($absolute_url)) !== false) {
                $local_file_name = cleanup_local_filename(preg_replace(PHET_WEBSITE_ROOT_PATTERN, RIPPED_WEBSITE_TOP, $absolute_url));
                
                if (!is_dir($local_file_name)) {
                    flushing_echo("Downloaded $absolute_url to $local_file_name\n");
                    
                    any_file_put_contents($local_file_name, $contents);
                }
            }
            else {
                flushing_echo("Could not find resource: $absolute_url. Removing element $tag from JNLP file...\n");
                
                // We remove the element by returning (before it's output)
                return;
            }
        }
    }
    
    xml_output_tag_open($depth, $tag, $attrs);
}

function download_simulations() {
    global $g_codebase;
    
    // Here we have to find and parse all jnlp files, and download all the
    // resources referenced in the codebase.
    flushing_echo("Downloading simulations...");
    
    foreach (get_all_jnlp(RIPPED_WEBSITE_TOP) as $jnlp_filename => $jnlp) {
        $g_codebase = $jnlp["codebase"]."/";
        
        $new_jnlp = xml_transform_file($jnlp_filename, "jnlp_open_tag_callback");
        
        file_put_contents($jnlp_filename, $new_jnlp);
    }
}

function download_installer_webpages() {
    // Here we download all the '-installer' versions of webpages, which are 
    // not linked into the website and therefore not ripped.
    flushing_echo("Downloading installer versions of webpages...");
    
    // Loop through all ripped webpages:
    foreach (list_files(RIPPED_WEBSITE_TOP, "*.htm*") as $local_name) {
        // Form the URL: [PhET Website]/[Webpage Name]-installer.htm
        $extension = get_file_extension($local_name);
        
        $local_installer_name = remove_file_extension($local_name)."-installer.$extension";
        
        $url = str_replace(RIPPED_WEBSITE_TOP, PHET_ROOT_URL, $local_installer_name);
        
        // If the file exists, get it and put it locally (replacing the online 
        // version of the same name):
        if (($contents = file_get_contents($url)) !== false) {
            any_file_put_contents($local_name, $contents);
        }
    }
}

function perform_macro_substitutions() {
    // This function performs a macro substitution in all HTML files. For 
    // example, $DATE$ is replaced by the current date.
    flushing_echo("Performing macro substitutions...");
    
    $macro_map = array();
    
    $macro_map["DATE"] = date("n/j/Y");
    
    foreach ($macro_map as $key => $value) {
        flushing_echo("\$$key\$ = $value");
    }
    
    foreach (list_files(RIPPED_WEBSITE_TOP, "*.htm*") as $filename) {
        replace_macros_in_file($filename, $macro_map);
    }
}

function build_all_installers() {
    flushing_echo("Building all installers for all configurations...");
    
    build_installers("all");
    
    // Make the autorun file for Windows CD-ROM:
    make_autorun_file(BITROCK_DIST_DIR, PHET_AUTORUN_ICON, BITROCK_DIST_WINNT);
}

function deploy_all_installers() {
    flushing_echo("Deploying all installers for all configurations...");
}

function print_help() {
    flushing_echo("Usage: build-install [--full]\n".
                  "                     [--rip_website]\n".
                  "                     [--download_simulations]\n".
                  "                     [--download_installer_webpages]\n".
                  "                     [--perform_macro_substitutions]\n".
                  "                     [--build_all_installers]\n".
                  "                     [--deploy_all_installers]\n".
                  "                     [--help]\n");
                  
}

function is_checked($attr) {
    if (is_cmd_line_option_enabled($attr)) {
        return true;
    }
    else if (is_cmd_line_option_enabled("full")) {
        return true;
    }
    
    return isset($_GET[$attr]) || isset($_POST[$attr]);
}

function main() {
    ob_start();
    
    if (is_cmd_line_option_enabled("help")) {
        print_help();
    }
    else {    
        if (file_lock("install-builder")) {
            if (is_checked('rip_website'))
                rip_website();
            
            if (is_checked('download_simulations'))
                download_simulations();
            
            if (is_checked('download_installer_webpages'))
                download_installer_webpages();
            
            if (is_checked('perform_macro_substitutions'))
                perform_macro_substitutions();
            
            if (is_checked('build_all_installers'))
                build_all_installers();
            
            if (is_checked('deploy_all_installers'))
                deploy_all_installers();
            
            flushing_echo("All done!");
        }
        else {
            flushing_echo("The PhET AutoInstallBuilder appears to be completing a previous build. Refresh this page to ignore this warning and build anyway.");
        }
        
        file_unlock("install-builder");
    }
    
    flush();
    ob_end_flush();
}

main();

?>
