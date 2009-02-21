#!/web/chroot/phet/usr/local/php/bin/php
<?php

    // Return all the sim info as a \0 delimited string
    //
    // Format:
    //     field_name:field_data\0field_name:field_data\0...\0\0\0
    // One "\0" delimits columns
    // Three "\0\0\0"s delimits rows
    //
    // Example:
    //    sim_id:78\0sim_name:data fields\0...sim_is_real:1\0\0\0sim_id:...


    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");
    require_once("include/sim-utils.php");
    
    $sims = sim_get_all_sims();
    $rows = array();
    foreach ($sims as $sim_id => $sim) {
        $fields = array();
        foreach ($sim as $key => $value) {
            $fields[] = "{$key}:{$value}";
        }
        $rows[] = join("\0", $fields);
    }

    print join("\0\0\0", $rows);

?>
