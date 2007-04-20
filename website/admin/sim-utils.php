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
    
    define("SQL_SELECT_ALL_VISIBLE_CATEGORIES", 
           "SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_order` ASC ");

    function print_sim_categories($prefix = "") {
        global $connection;
        
        // List all the categories:

        // start selecting SIMULATION CATEGORIES from database table
        $category_table = mysql_query(SQL_SELECT_ALL_VISIBLE_CATEGORIES, $connection);

        while ($category = mysql_fetch_row($category_table)) {
            $cat_id   = $category[0];
            $cat_name = format_for_html($category[1]);
        
            print "<li class=\"sub\"><span class=\"sub-nav\"><a href=\"${prefix}index.php?cat=$cat_id\">&rarr; $cat_name</a></span></li>";          
        } 
    }
    
    function get_sorting_name($name) {
        $matches = array();

        preg_match('/((the|a|an) +)?(.*)/i', $name, $matches);

        return $matches[3];
    }
    
    function gather_array_into_globals($array) {
        foreach($array as $key => $value) {
            $GLOBALS["$key"] = format_for_html("$value");
        }
    }
    
    function gather_sim_fields_into_globals($sim_id) {
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
        $simulation    = mysql_fetch_assoc(mysql_query($select_sim_st));
        
        gather_array_into_globals($simulation);
        
        $GLOBALS["sim_id"] = "$sim_id"; 
    }

?>