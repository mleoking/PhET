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
    
    function gather_sim_fields_into_globals($sim_id) {
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
        $simulation    = mysql_fetch_assoc(mysql_query($select_sim_st));
        
        gather_array_into_globals($simulation);
        
        $GLOBALS["sim_id"] = "$sim_id"; 
    }
    
    function sim_get_select_sims_by_category_statement($cat) {
        return "SELECT * FROM `simulation`, `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat' AND `simulation`.`sim_id`=`simulation_listing`.`sim_id` ORDER BY `simulation`.`sim_sorting_name` ASC ";
    }
    
    function sim_get_image_previews($type, $field) {
        global $connection;

        $select_categories_st = "SELECT * FROM `category` WHERE `cat_is_visible`='0' ";
        $category_rows        = mysql_query($select_categories_st, $connection);
        
        while ($category_row = mysql_fetch_assoc($category_rows)) {
            $cat_id   = $category_row['cat_id'];
            $cat_name = $category_row['cat_name'];
            
            if (preg_match("/.*$type.*preview.*/i", $cat_name) == 1) {                
                $select_sims_st = sim_get_select_sims_by_category_statement($cat_id);
                
                $simulation_rows = mysql_query($select_sims_st, $connection);

                $animated_images = array();
                
                while ($simulation_row = mysql_fetch_assoc($simulation_rows)) {
                    $sim_animated_image_url = $simulation_row["$field"];
                    
                    $animated_images[] = $sim_animated_image_url;
                }
                
                return $animated_images;
            }
        }
        
        return FALSE;
    }
    
    function sim_get_animated_previews() {
        return sim_get_image_previews("animated", "sim_animated_image_url");
    }
    
    function sim_get_static_previews() {
        return sim_get_image_previews("static", "sim_image_url");
    }

?>