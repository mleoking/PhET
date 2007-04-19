<?php
    include_once("db.inc");
    include_once("web-utils.php");
    
    define("SIM_TYPE_JAVA",  "0");
    define("SIM_TYPE_FLASH", "1");
    
    $SIM_TYPE_TO_IMAGE =
        array(
            SIM_TYPE_JAVA   => 'java.png',
            SIM_TYPE_FLASH  => 'flash.png'
        );
        
    define("SIM_RATING_BETA_MINUS",     "0");
    define("SIM_RATING_BETA",           "1");
    define("SIM_RATING_BETA_PLUS",      "2");
    define("SIM_RATING_CHECK",          "3");    
    define("SIM_RATING_ALPHA",          "4");
    
    $SIM_RATING_TO_IMAGE = 
        array(
            SIM_RATING_BETA_MINUS   => 'beta-minus-rating.gif',
            SIM_RATING_BETA_PLUS    => 'beta-plus-rating.gif',
            SIM_RATING_BETA         => 'beta-rating.gif',
            SIM_RATING_CHECK        => 'check_Icon.gif',
            SIM_RATING_ALPHA        => 'alpha-rating.gif'
        );
        
    define("SIMS_PER_PAGE", 9);
    
    define("SIM_SYSTEM_REQ_ANY",    "0");
    define("SIM_SYSTEM_REQ_NO_MAC", "1");

    // run error checks
    // check for missing fields
    function verify_field($field_name) {
        if ($field_name == null) {
            print "<center><font color=red><b>!!ERROR!!</b> You must include a <u>$field_name</u> for your Simulation! Your simulation was not edited.";

            print "<br><br> Please fill in all required fields<br><br></b></font>";

            //include 'updateexistingsim.php';

            exit();
        }
    }
    
    function print_sim_categories($prefix = "") {
        global $connection;
        
        // List all the categories:

        // start selecting SIMULATION CATEGORIES from database table
        $select_category_st = "SELECT * FROM `category` ORDER BY `cat_order` ASC ";
        $category_table     = mysql_query($select_category_st, $connection);

        while ($category = mysql_fetch_row($category_table)) {
            $cat_id     = $category[0];
            $cat_name   = format_for_html($category[1]);
        
            print "<li class=\"sub\"><span class=\"sub-nav\"><a href=\"${prefix}index.php?cat=$cat_id\">&rarr; $cat_name</a></span></li>";          
        } 
    }
    
    function gather_sim_fields_into_globals($sim_id) {
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
        $simulation    = mysql_fetch_assoc(mysql_query($select_sim_st));
        
        foreach($simulation as $key => $value) {
            $GLOBALS["$key"] = format_for_html("$value");
        }
        
        $GLOBALS["sim_id"] = "$sim_id"; 
    }

?>