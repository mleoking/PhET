<?php
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("db-utils.php");    
    include_once("xml_parser.php");
    
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
            SIM_RATING_BETA_MINUS   => 'beta-minus25x25.png',
            SIM_RATING_BETA_PLUS    => 'beta-plus25x25.png',
            SIM_RATING_BETA         => 'beta25x25.png',
            SIM_RATING_CHECK        => 'checkmark25x25.png',
            SIM_RATING_ALPHA        => 'alpha25x25.png'
        );
        
    $SIM_RATING_TO_IMAGE_HTML = 
        array(
            SIM_RATING_BETA_MINUS   => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA_MINUS].'" alt="Beta minus rating" title="Beta minus" /></a>',
            SIM_RATING_BETA_PLUS    => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA_PLUS].'" alt="Beta plus rating" title="Beta plus" /></a>',
            SIM_RATING_BETA         => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA].'" alt="Beta rating" title="Beta rating" /></a>',
            SIM_RATING_CHECK        => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_CHECK].'" alt="Checkmark rating" title="Checkmark" /></a>',
            SIM_RATING_ALPHA        => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA_MINUS].'" alt="Alpha rating" title="Alpha" /></a>'
        );        
        
    define("SIM_NO_MAC_IMAGE", '../images/sims/ratings/no-mac25x25.png');
    define("SIM_CRUTCH_IMAGE", '../images/sims/ratings/crutch25x25.png');
    
    define("SIM_NO_MAC_IMAGE_HTML", 
            "<a href=\"../about/legend.php\"><img src=\"".SIM_NO_MAC_IMAGE."\" alt=\"No Mac\" title=\"Not supported on Mac\"/></a>");
            
    define("SIM_CRUTCH_IMAGE_HTML", 
        "<a href=\"../about/legend.php\"><img src=\"".SIM_CRUTCH_IMAGE."\" alt=\"Not standalone\" title=\"Designed to be used with supplementary material\"/></a>");    
        
    define("SIMS_PER_PAGE", 9);
    
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
    
    function sim_get_categories() {
        $cats = array();
        
        $result = db_exec_query("SELECT * FROM `category` ");
        
        while ($cat = mysql_fetch_assoc($result)) {
            $encoded = web_encode_string($cat['cat_name']);
            
            $cats[$encoded] = $cat;
        }
        
        return $cats;
    }
    
    function sim_get_visible_categories() {
        $cats = array();
        
        $result = db_exec_query("SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_order` ASC ");
        
        while ($cat = mysql_fetch_assoc($result)) {
            $encoded = web_encode_string($cat['cat_name']);
            
            $cats[$encoded] = $cat;
        }
        
        return $cats;
    }
    
    function sim_get_category_names() {
        $names = array();
        
        $cats = sim_get_categories();
        
        foreach($cats as $key => $cat) {
            $names[$key] = $cat['cat_name'];
        }
        
        return $names;
    }
    
    function sim_get_visible_category_names() {
        $names = array();
        
        $cats = sim_get_visible_categories();
        
        foreach($cats as $key => $cat) {
            $names[$key] = $cat['cat_name'];
        }
        
        return $names;
    }
    
    function sim_get_category_link_by_cat_encoding($cat_encoding, $desc, $extra_param = '', $class = '') {    
        if ($class == '') {
            $class_html = '';
        }    
        else {
            $class_html = 'class="'.$class.'"';
        }
        return '<a '.$class_html.' href="../simulations/index.php?cat='.$cat_encoding.$extra_param.'">'.$desc.'</a>';
    }
    
    function sim_get_category_url_by_cat_id($cat_id) {
        $category = db_get_row_by_id('category', 'cat_id', $cat_id);
        
        $cat_name     = $category['cat_name'];
        $cat_encoding = web_encode_string($cat_name);
        
        return "simulations/index.php?cat=$cat_encoding";
    }
    
    function sim_get_category_link_by_cat_id($cat_id, $desc, $extra_param = '', $class = '') {
        $category = db_get_row_by_id('category', 'cat_id', $cat_id);
        
        $cat_name     = $category['cat_name'];
        $cat_encoding = web_encode_string($cat_name);
        
        return sim_get_category_link_by_cat_encoding($cat_encoding, $desc, $extra_param, $class);
    }
    
    function sim_get_sim_by_name($sim_name) {
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_name`= '$sim_name' ";
        $simulation    = mysql_fetch_assoc(mysql_query($select_sim_st));
        
        return $simulation;
    }
    
    function sim_get_sim_by_id($sim_id) {
        return db_get_row_by_id('simulation', 'sim_id', $sim_id);
    }
    
    function sim_update_sim($simulation) {
        return db_update_table('simulation', $simulation, 'sim_id', $simulation['sim_id']);
    }
    
    function sim_search_for_sims($search_for) {
        $simulations = array();
        
        $st = "SELECT * FROM `simulation` WHERE ";
        
        $is_first = true;
        
        foreach(preg_split('/( +)|( *, *)/i', $search_for) as $word) {            
            if ($is_first) {
                $is_first = false;
            }
            else {
                $st .= " AND ";
            }
            
            $st .= "(`sim_name` LIKE '%$word%' OR `sim_desc` LIKE '%$word%' OR `sim_keywords` LIKE '%$word%' OR  `sim_main_topics` LIKE '%$word%' OR `sim_sample_goals` LIKE '%$word%' )";
        }
        
        $result = db_exec_query($st);
        
        while ($simulation = mysql_fetch_assoc($result)) {
            $simulations[] = $simulation;
        }
        
        return $simulations;
    }
    
    function gather_sim_fields_into_globals($sim_id) {
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
        $simulation    = mysql_fetch_assoc(mysql_query($select_sim_st));
        
        gather_array_into_globals($simulation);
        
        $GLOBALS["sim_id"] = "$sim_id"; 
    }

    function sim_get_all_sims() {
        $simulations = array();
        
        $simulation_rows = db_exec_query("SELECT * FROM `simulation` ");
        
        while($simulation = mysql_fetch_assoc($simulation_rows)) {
            $sim_id = $simulation['sim_id'];
            
            if (is_numeric($sim_id)) {                
                $simulations["sim_id_$sim_id"] = $simulation;
            }
        }
        
        return $simulations;        
    }
    
    function sim_get_sims_by_cat_id($cat_id) {
        $select_sims_st = sim_get_select_sims_by_category_statement($cat_id);

        $result = db_exec_query($select_sims_st);
        
        $cleans = array();
        
        while ($unclean = mysql_fetch_assoc($result)) {
            $cleans[] = sim_get_sim_by_id($unclean['sim_id']);
        }
        
        return $cleans;
    }
    
    function sim_get_cat_by_cat_encoding($cat_encoding) {
        $categories = sim_get_categories();
        
        return $categories[$cat_encoding];
    }
    
    function sim_get_cat_id_by_cat_encoding($cat_encoding) {
        $category = sim_get_cat_by_cat_encoding($cat_encoding);
        
        return $category['cat_id'];
    }
    
    function sim_get_all_sim_names() {
        $simulations = array();
        
        $simulation_rows = db_exec_query("SELECT * FROM `simulation` ");
        
        while($simulation = mysql_fetch_assoc($simulation_rows)) {
            $sim_id   = $simulation['sim_id'];
            $sim_name = $simulation['sim_name'];
            
            if (is_numeric($sim_id)) {                
                $simulations["sim_id_$sim_id"] = format_for_html("$sim_name");
            }
        }
        
        return $simulations;        
    }
    
    function sim_auto_calc_sim_size($sim_id) {        
        $simulation = sim_get_sim_by_id($sim_id);
        
        $sim_launch_url = $simulation['sim_launch_url'];
        
        $ext = get_file_extension($sim_launch_url);
        
        if ($ext == 'jnlp') {            
            $size = 0;
            
            // Java jnlp file; have to compute file size by examining jnlp
            $xml = file_get_contents($sim_launch_url);
            
            $xml_encoding = mb_detect_encoding($xml);
            
            if (!$xml_encoding || $xml_encoding == '') {
                // Can't detect it; assume UTF-16 big endian
                $xml = mb_convert_encoding($xml, "UTF-8", "UTF-16BE");
            }
            else {            
                $xml = mb_convert_encoding($xml, "UTF-8", $xml_encoding);
            }
            
            // Try to clean it up:
            $xml = str_replace('<br>', '<br/>', $xml);
            
            $parser = new XMLParser($xml);

            if ($parser->Parse()) {
                $codebase = $parser->document->tagAttrs['codebase'];
            
                foreach($parser->document->resources as $resource) {                
                    foreach($resource->jar as $jar) {
                        $href = $codebase.'/'.$jar->tagAttrs['href'];
                
                        $size += url_or_file_size($href);
                    }
                }
            }
            else {
                print "Error trying to detect size: ".$simulation['sim_name'].", id = ".$simulation['sim_id'].", url = $sim_launch_url, encoding = $xml_encoding<br/>";
            }
        }
        else {
            $size = url_or_file_size($sim_launch_url);
        }
        
        $simulation = array(
            'sim_id'   => $sim_id,
            'sim_size' => $size / 1024
        );
        
        return sim_update_sim($simulation);        
    }
    
    function sim_get_select_sims_by_category_statement($cat) {
        return "SELECT DISTINCT `simulation`.`sim_id` FROM `simulation`, `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat' AND `simulation`.`sim_id`=`simulation_listing`.`sim_id` ORDER BY `simulation`.`sim_sorting_name` ASC ";
    }
    
    function sim_get_image_previews($type, $field) {
        global $connection;

        $select_categories_st = "SELECT * FROM `category` WHERE `cat_is_visible`='0' ";
        $category_rows        = mysql_query($select_categories_st, $connection);
        
        while ($category_row = mysql_fetch_assoc($category_rows)) {
            $cat_id   = $category_row['cat_id'];
            $cat_name = $category_row['cat_name'];
            
            if (preg_match("/.*$type.*preview.*/i", $cat_name) == 1) {
                $animated_images = array();
                
                foreach (sim_get_sims_by_cat_id($cat_id) as $simulation) {
                    $sim_animated_image_url = $simulation["$field"];
                    
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