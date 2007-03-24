<?php

require_once("file-util.php");
require_once("global.php");
require_once("config.php");

function get_full_dist_name($dist) {
    return "PhET-dist-$dist";
}

function build_installers($dist, $macro_map = array()) {
    global $g_bitrock_dists;
    
    $build_prefix  = get_full_dist_name($dist);
    $buildfile_ext = get_file_extension(BITROCK_BUILDFILE);
    
    $new_buildfile = BITROCK_BUILDFILE_DIR."${build_prefix}.${buildfile_ext}";
    
    create_parents_dirs_of_file($new_buildfile);
    
    if (!copy(BITROCK_BUILDFILE, $new_buildfile)) {
        return false;
    }
    
    $macro_map['VERSION'] = PHET_VERSION;
    
    if (!replace_macros_in_file($new_buildfile, $macro_map)) {
        return false;
    }
    
    $return_var = 0;
    
    $cwd = getcwd();
    
    // Change working directory to location of EXE:
    chdir(with_local_separator(BITROCK_EXE_DIR));
    
    foreach ($g_bitrock_dists as $platform => $distfile) {
        $cmd_line = BITROCK_EXE.BITROCK_PRE_ARGS.'"'."$new_buildfile".'" '.$platform;
        
        $cmd_line = with_local_separator($cmd_line);
        
        system($cmd_line, $return_var);
        
        if ($return_var != 0) {
            flushing_echo("BitRock failed to build installer $distfile for configuration $dist on platform $platform.");
        }
    }
    
    chdir($cwd);
    
    return true;
}

?>
