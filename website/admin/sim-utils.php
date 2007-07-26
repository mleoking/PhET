<?php
    include_once("db.inc");
    include_once("web-utils.php");
    include_once("db-utils.php");    
    include_once("xml_parser.php");

	define("SIM_THUMBNAIL_WIDTH", 130);
	define("SIM_THUMBNAIL_HEIGHT", 97);
    
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
            SIM_RATING_ALPHA        => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_ALPHA].'" alt="Alpha rating" title="Alpha" /></a>'
        );

    $SIM_TYPE_TO_IMAGE_HTML = 
		array(
			SIM_TYPE_JAVA  => '<a href="../tech_support/support-java.php"> <img src="../images/sims/ratings/'.$SIM_TYPE_TO_IMAGE[SIM_TYPE_JAVA].'" alt="Java Icon" title="This simulation is a Java simulation" /></a>',
			SIM_TYPE_FLASH => '<a href="../tech_support/support-flash.php"><img src="../images/sims/ratings/'.$SIM_TYPE_TO_IMAGE[SIM_TYPE_FLASH].'" alt="Java Icon" title="This simulation is a Flash simulation" /></a>'
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
    
    function sim_get_sim_encoding_by_sim_id($sim_id) {
        $sim = sim_get_sim_by_id($sim_id);
        
        return web_encode_string($sim['sim_name']);
    }
    
    function sim_get_sim_by_sim_encoding($sim_encoding) {
        $map = sim_get_name_to_sim_map();
        
        foreach($map as $name => $sim) {
            $encoding = web_encode_string($name);
            
            if ($encoding == $sim_encoding) {
                return $sim;
            }
        }
        
        return false;
    }
    
    function sim_get_sim_id_by_sim_encoding($sim_encoding) {
        $sim = sim_get_sim_by_sim_encoding($sim_encoding);
        
        if (!$sim) return false;
        
        return $sim['sim_id'];
    }

    function sim_get_url_to_sim_page_by_sim_name($sim_name) {
        $sim_encoding = web_encode_string($sim_name);
        
        return "../simulations/sims.php?sim=$sim_encoding";
    }
    
    function sim_get_url_to_sim_page($sim_id) {
        $sim = sim_get_sim_by_id($sim_id);
        
        $sim_name = $sim['sim_name'];

		return sim_get_url_to_sim_page_by_sim_name($sim_name);
    }
    
    function sim_get_link_to_sim_page($sim_id, $desc = null) {
        $url = sim_get_url_to_sim_page($sim_id);
        
        if ($desc == null) {
            $sim = sim_get_sim_by_id($sim_id);
            
            $desc = $sim['sim_name'];
        }
        
        return "<a href=\"$url\">$desc</a>";
    }

	function sim_get_link_to_sim_page_by_name($sim_name) {
		$url = sim_get_url_to_sim_page_by_sim_name($sim_name);
		
		return "<a href=\"$url\">$sim_name</a>";
	}
    
    function sim_get_visible_categories() {
        $cats = array();
        
        $result = db_exec_query("SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_order` ASC ");
        
        while ($cat = mysql_fetch_assoc($result)) {
            $encoded = web_encode_string($cat['cat_name']);
            
            $cats[$encoded] = format_for_html($cat);
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
		$sim = db_get_row_by_id('simulation', 'sim_name', "$sim_name", false);
		
		if ($sim === false) {
			// Could not find sim by name; try html decoding name:
			$sim = db_get_row_by_id('simulation', 'sim_name', html_entity_decode($sim_name), false);
		}
		
		return $sim;
    }
    
    function sim_get_sim_by_id($sim_id) {
        return db_get_row_by_id('simulation', 'sim_id', $sim_id);
    }
    
    function sim_update_sim($simulation) {
        return db_update_table('simulation', $simulation, 'sim_id', $simulation['sim_id']);
    }
    
    function sim_search_for_sims($search_for) {
		return db_search_for(
			'simulation', 
			$search_for, 
			array('sim_name', 'sim_desc', 'sim_keywords', 'sim_main_topics', 'sim_sample_goals')
		);
    }
    
    function gather_sim_fields_into_globals($sim_id) {
        $select_sim_st = "SELECT * FROM `simulation` WHERE `sim_id`= '$sim_id' ";
        $simulation    = mysql_fetch_assoc(mysql_query($select_sim_st));
        
        gather_array_into_globals($simulation);
        
        $GLOBALS["sim_id"] = "$sim_id"; 
    }

	function sim_compare_by_sorting_name($sim1, $sim2) {
		return strcasecmp($sim1['sim_sorting_name'], $sim2['sim_sorting_name']);
	}

    function sim_get_all_sims() {
        $simulations = array();
        
        $simulation_rows = db_get_all_rows('simulation');
        
        foreach($simulation_rows as $simulation) {
            $sim_id = $simulation['sim_id'];
            
            if (is_numeric($sim_id)) {                
                $simulations["sim_id_$sim_id"] = $simulation;
            }
        }

		// Sort by sorting name:
		usort($simulations, 'sim_compare_by_sorting_name');
        
        return $simulations;        
    }
    
    function sim_get_name_to_sim_map() {
        $simulations = db_get_all_rows('simulation');
        
        $map = array();
        
        foreach($simulations as $simulation) {
            $name = $simulation['sim_name'];
            
            $map[$name] = $simulation;
        }
        
        return $map;
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

	function sim_get_sims_by_cat_id_alphabetically($cat_id) {
        $select_sims_st = sim_get_select_sims_by_category_statement_order_alphabetically($cat_id);

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
        
        $simulation_rows = db_exec_query("SELECT * FROM `simulation` ORDER BY `sim_sorting_name` ");
        
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

            $matches = array();

			if (preg_match('/codebase *= *"([^"]+)"/i', $xml, $matches)) {
                $codebase = $matches[1];

				if (preg_match_all('/(?<!homepage) +href *= *"([^"]+)"/i', $xml, $matches)) {
					foreach ($matches[1] as $match) {
						if (string_starts_with($match, 'http://')) {
							$href = $match;
						}
						else {
	                    	$href = $codebase.'/'.$match;
	
							$href = web_get_real_path($href);
						}
						
						$size += url_or_file_size($href);
	                }
				}
				else {
					print "No hrefs found to auto-calc sim size <br/>";
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
    
    function sim_get_sim_listing($sim_id, $cat_id) {
        $result = db_exec_query(
            "SELECT * FROM `simulation_listing` WHERE `sim_id`='$sim_id' AND `cat_id`='$cat_id' "
        );
        
        if (!$result) {
            return false;
        }
        
        return mysql_fetch_assoc($result);
    }
    
    function sim_get_sim_listings_by_cat_id($cat_id) {
		$listings = array();
		
		$results = db_exec_query("SELECT * FROM `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat_id' ORDER BY `simulation_listing`.`simulation_listing_order` ASC ");
		
		while ($row = mysql_fetch_assoc($results)) {
			$listings[] = format_for_html($row);
		}
		
		return $listings;
    }

	function sim_get_run_offline_content_location($simulation) {
		$dirname     = $simulation['sim_dirname'];
		$flavorname  = $simulation['sim_flavorname'];
		
		if ($simulation['sim_type'] == SIM_TYPE_FLASH) {
			$link = "http://phet.colorado.edu/sims/$dirname/$flavorname.swf";
		}
		else {
			$link = "http://phet.colorado.edu/sims/$dirname/$flavorname.jar";
		}
		
		return $link;
	}
	
	function sim_is_in_category($sim_id, $cat_id) {
		$sim_listings = sim_get_sim_listings_by_cat_id($cat_id);
		
		foreach ($sim_listings as $sim_listing) {
			if ($sim_listing['sim_id'] == $sim_id) {
				return true;
			}
		}
		
		return false;
	}
	
	function sim_get_launch_url($simulation) {
		$dirname    = $simulation['sim_dirname'];
		$flavorname = $simulation['sim_flavorname'];
		
		if ($simulation['sim_type'] == SIM_TYPE_FLASH) {
			$link = "http://phet.colorado.edu/sims/$dirname/$flavorname.swf";
		}
		else {
			$link = "http://phet.colorado.edu/sims/$dirname/$flavorname.jnlp";
		}
		
		return $link;		
	}
	
	function sim_get_screenshot($simulation) {
		$dirname    = $simulation['sim_dirname'];
		$flavorname = $simulation['sim_flavorname'];
		
		$link = "http://phet.colorado.edu/sims/$dirname/$flavorname-screenshot.png";
		
		return $link;
	}

	function sim_get_run_offline_link($simulation) {
		return '../admin/get-run-offline.php?sim_id='.$simulation['sim_id'];
	}
    
    function sim_get_select_sims_by_category_statement($cat_id) {
        return "SELECT DISTINCT `simulation`.`sim_id` FROM `simulation`, `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat_id' AND `simulation`.`sim_id`=`simulation_listing`.`sim_id` ORDER BY `simulation_listing`.`simulation_listing_order` ASC ";
    }

    function sim_get_select_sims_by_category_statement_order_alphabetically($cat_id) {
        return "SELECT DISTINCT `simulation`.`sim_id` FROM `simulation`, `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat_id' AND `simulation`.`sim_id`=`simulation_listing`.`sim_id` ORDER BY `simulation`.`sim_sorting_name` ASC ";
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
                   
					if ($sim_animated_image_url != '') {
                   		$animated_images[] = $sim_animated_image_url;
					}
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