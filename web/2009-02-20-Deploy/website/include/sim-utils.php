<?php

    // Utils to support sims

    // In each web accessable script SITE_ROOT must be defined FIRST
    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    
    // See global.php for an explaination of the next line
    require_once(dirname(dirname(__FILE__))."/include/global.php");

    require_once("include/db.php");
    require_once("include/web-utils.php");
    require_once("include/db-utils.php");
    require_once("include/cache-utils.php");
    require_once("include/locale-utils.php");
    require_once("include/installer-utils.php");

    // Do not access this variable directly
    $g_SIM_ROOT = SIMS_ROOT;
    function sim_get_root() {
        global $g_SIM_ROOT;
        return $g_SIM_ROOT;
    }

    function sim_set_root($new_root) {
        global $g_SIM_ROOT;
        $g_SIM_ROOT = ($new_root && !empty($new_root)) ? $new_root : SIMS_ROOT;
    }


    define("SIM_THUMBNAIL_WIDTH", 130);
    define("SIM_THUMBNAIL_HEIGHT", 97);

    define("SIM_TYPE_JAVA",  "0");
    define("SIM_TYPE_FLASH", "1");

    $SIM_TYPE_TO_IMAGE =
        array(
            SIM_TYPE_JAVA   => 'java.png',
            SIM_TYPE_FLASH  => 'flash.png'
        );

    // Beta has been removed but I'm keeping these here for reference
    define("SIM_RATING_NONE",           "0");
    define("SIM_RATING_ALPHA",          "1");
    define("SIM_RATING_CHECK",          "2");

    $SIM_RATING_TO_IMAGE =
        array(
            // Beta has been removed but these must stay to match the database
            SIM_RATING_NONE   => '',
            SIM_RATING_ALPHA        => 'alpha25x25.png',
            SIM_RATING_CHECK        => 'checkmark25x25.png'
            );

    $SIM_RATING_TO_IMAGE_HTML =
        array(
            // Beta has been removed but these must stay to match the database
            SIM_RATING_NONE         => '',
            SIM_RATING_CHECK        => '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.SITE_ROOT.'images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_CHECK].'"         alt="Checkmark Rating Image"     width="37" title="Classroom Tested: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews." /></a>',
            SIM_RATING_ALPHA        => '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.SITE_ROOT.'images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_ALPHA].'"         alt="Alpha Rating Image"         width="37" title="Under Construction: This simulation is an early preview version, and may have functional or usability bugs." /></a>'
            );

    $SIM_TYPE_TO_IMAGE_HTML =
        array(
            SIM_TYPE_JAVA  => '<a href="'.SITE_ROOT.'tech_support/support-java.php"> <img src="'.SITE_ROOT.'images/sims/ratings/'.$SIM_TYPE_TO_IMAGE[SIM_TYPE_JAVA].'" alt="Java Icon" title="This simulation is a Java simulation" /></a>',
            SIM_TYPE_FLASH => '<a href="'.SITE_ROOT.'tech_support/support-flash.php"><img src="'.SITE_ROOT.'images/sims/ratings/'.$SIM_TYPE_TO_IMAGE[SIM_TYPE_FLASH].'" alt="Java Icon" title="This simulation is a Flash simulation" /></a>'
        );

    define("SIM_NO_MAC_IMAGE", SITE_ROOT.'images/sims/ratings/no-mac25x25.png');
    define("SIM_CRUTCH_IMAGE", SITE_ROOT.'images/sims/ratings/crutch25x25.png');

    define("FROM_PHET_IMAGE_HTML", '<a href="'.SITE_ROOT.'about/legend.php"><img src="'.SITE_ROOT.'images/phet-logo-icon.jpg" alt="Designed by PhET Icon" title="PhET Designed: This contribution was designed by PhET." /></a>');

    define("SIM_NO_MAC_IMAGE_HTML",
            "<a href=\"".SITE_ROOT."about/legend.php\"><img src=\"".SIM_NO_MAC_IMAGE."\" alt=\"No Mac\" title=\"Not supported on Mac\"/></a>");

    define("SIM_CRUTCH_IMAGE_HTML",
        "<a href=\"".SITE_ROOT."about/legend.php\"><img src=\"".SIM_CRUTCH_IMAGE."\" alt=\"Not standalone\" width=\"37\" title=\"Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity.\"/></a>");

    define("SIM_THUMBNAILS_CACHE",      "thumbnails");
    define("SIM_TRANSLATIONS_CACHE", "translations");

    define("SIMS_PER_PAGE", 9);

    define("SQL_SELECT_ALL_VISIBLE_CATEGORIES",
           "SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_parent`,`cat_order` ASC ");

    function get_sorting_name($name) {
        $matches = array();

        preg_match('/ *((the|a|an) +)?(.*)/i', $name, $matches);

        return $matches[3];
    }

    /**
     * Return all translations of all sims
     * 
     * @return arary with format 'locale' => array(sim_id1, sim_id2, ...)
     **/
    function sim_get_all_sim_translations() {
        $sims = sim_get_all_sims();
        if (!$sims) {
            return array();
        }

        $translations = array();
        foreach ($sims as $sim_id => $sim) {
            $sim_trans = sim_get_translations($sim);
            foreach ($sim_trans as $locale) {
                if (!array_key_exists($locale, $translations)) {
                    $translations[$locale] = array();
                }

                $translations[$locale][] = $sim_id;
            }
        }

        // Sort the codes so that the English locale names will come out
        // in the right order
        uksort($translations, 'locale_sort_code_by_name');

        return $translations;
    }

    /*
     * Get all the translation for the given sim
     * 
     * @param simulation array Simulation to get translations from
     * @return mixed array of locales (if any), false if sim type not valid
     */
    function sim_get_translations($simulation) {
        $sim = $simulation;

        $translations = array();
        if ($sim['sim_type'] == SIM_TYPE_JAVA) {
            $base = sim_get_root()."{$sim['sim_dirname']}/{$sim['sim_flavorname']}";
            $base_glob = $base."*.jnlp";
            $base_regex = $base."(_)([A-Za-z]{2})?(_([A-Za-z]{2}))?.jnlp";
        }
        else if ($sim['sim_type'] == SIM_TYPE_FLASH) {
            $base = sim_get_root()."{$sim['sim_dirname']}/{$sim['sim_flavorname']}";
            $base_glob = $base."*.*ml";
            $base_regex = $base."(-strings)?_([A-Za-z]{2})?(_([A-Za-z]{2}))?.(html|xml)";
        }
        else {
            return false;
        }

        $files = glob($base_glob);
        foreach ($files as $file) {
            // print "file: {$file}\n";
            $regs = array();
            $result = ereg($base_regex, $file, $regs);
            if ($result !== false) {
                $locale = "{$regs[2]}{$regs[3]}";
            }
            else {
                // Skip the default locale, it is not a translation
                continue;
            }
            
            if (!locale_valid($locale)) {
                // Locale is not in the table, log error and skip
                // TODO: log an error
                continue;
            }
            else if (locale_is_default($locale)) {
                // Skip the default locale, it is not a translation
                continue;
            }
            
            if (!isset($translations[$locale])) {
                $translations[$locale] = 1;
            }
            else {
                $translations[$locale] += 1;
            }
        }

        $end = array();
        foreach ($translations as $key => $value) {
            if ($sim['sim_type'] == SIM_TYPE_JAVA) {
                if ($value > 0) {
                    $end[] = $key;
                }
            }
            else if ($sim['sim_type'] == SIM_TYPE_FLASH) {
                if ($value > 1) {
                    $end[] = $key;
                }
            }
        }

        $translations = $end;
        usort($translations, 'locale_sort_code_by_name');
        return $translations;        
    }

    // Returns an array with the sim version, keys 
    // will be empty if the information is not available
    //
    // TEMP: Flash versioning is inaccurate, return blank version
    //
    // Example array form: 
    //     'major' => '1'
    //     'minor' => '07'
    //     'dev' => '00'
    //     'revision' => '2615700'
    //     'timestamp' => '98340'
    //     'installer_timestamp' => '98343'
    function sim_get_version($simulation, $ignore_flash = true) {
        $dirname     = $simulation['sim_dirname'];

        $properties_filename = sim_get_root()."{$dirname}/{$dirname}.properties";

        $revision_tags = array(
            'major', 'minor', 'dev', 'revision',
            'timestamp', 'installer_timestamp');
        $regex = 'version\.('.join('|', $revision_tags).') *= *([^ \n\r\t]+)';

        $version = array();
        foreach ($revision_tags as $tag) {
            $version[$tag] = '';
        }

        $installer_timestamp = installer_get_latest_timestamp();
        if ($installer_timestamp && !empty($installer_timestamp)) {
            $version['installer_timestamp'] = installer_get_latest_timestamp();
        }

        // TEMP: Flash versioning is inaccurate, return blank version
        if ($ignore_flash && ($simulation['sim_type'] == SIM_TYPE_FLASH)) {
            return $version;
        }

        $handle = @fopen($properties_filename, "r");

        if ($handle) {
            while (!feof($handle)) {
                $buffer = fgets($handle, 4096);
                $regs = array();
                if (ereg($regex, $buffer, $regs)) {
                    if ($regs[1] && in_array($regs[1], $revision_tags)) {
                        $version[$regs[1]] = trim($regs[2]);
                    }
                }
            }
            fclose($handle);
        }

        return $version;
    }

    function sim_get_encoded_default_category() {
        $result = db_exec_query("SELECT * FROM `category` ");

        while ($cat = mysql_fetch_assoc($result)) {
            if (!$cat['cat_is_visible']) {
                continue;
            }

            return web_encode_string($cat['cat_name']);
        }
        return '';
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

    function sim_get_sim_by_sim_encoding($sim_encoding) {
        $map = sim_get_name_to_sim_map();

        foreach($map as $name => $sim) {
            $encoding = web_encode_string($name);

            if ($encoding == $sim_encoding) {
                return $sim;
            }
        }

        // Look for best match using substrings:
        foreach($map as $name => $sim) {
            $encoding = web_encode_string($name);

            $s1 = strtolower($sim_encoding);
            $s2 = strtolower($encoding);

            if (strpos($s1, $s2) !== false) {
                return $sim;
            }
            else if (strpos($s2, $s1) !== false) {
                return $sim;
            }
        }

        // TODO: extract this into its own algorithm
        $best_dist = 9999999;
        $best_sim  = false;

        // Look for best match using Levenshtein distance function:
        foreach($map as $name => $sim) {
            $encoding = web_encode_string($name);

            $distance = levenshtein(strtolower($sim_encoding), strtolower($encoding), 0, 2, 1);

            if ($distance < $best_dist && $distance !== -1) {
                $best_dist = $distance;
                $best_sim  = $sim;
            }
        }

        return $best_sim;
    }

    function sim_get_sim_id_by_sim_encoding($sim_encoding) {
        $sim = sim_get_sim_by_sim_encoding($sim_encoding);

        if (!$sim) return false;

        return $sim['sim_id'];
    }

    function sim_get_url_to_sim_page_by_sim_name($sim_name) {
        $sim_encoding = web_encode_string($sim_name);

        return SITE_ROOT."simulations/sims.php?sim=$sim_encoding";
    }

    function sim_get_url_to_sim_page($sim_id) {
        $sim = sim_get_sim_by_id($sim_id);

        $sim_name = $sim['sim_name'];

        return sim_get_url_to_sim_page_by_sim_name($sim_name);
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
        return '<a '.$class_html.' href="'.SITE_ROOT.'simulations/index.php?cat='.$cat_encoding.$extra_param.'">'.$desc.'</a>';
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

    function sim_get_sim_by_id($sim_id) {
        return db_get_row_by_id('simulation', 'sim_id', $sim_id);
    }

    function sim_get_sim_by_dirname_flavorname($dirname, $flavorname) {
        $condition = array();
        $condition['sim_dirname'] = $dirname;
        $condition['sim_flavorname'] = $flavorname;
        return db_get_row_by_condition('simulation', $condition);
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

    function sim_compare_by_sorting_name($sim1, $sim2) {
        return strcasecmp($sim1['sim_sorting_name'], $sim2['sim_sorting_name']);
    }

    function sim_get_all_sims() {
        $simulations = array();

        $simulation_rows = db_get_all_rows('simulation');

        usort($simulation_rows, 'sim_compare_by_sorting_name');

        foreach($simulation_rows as $simulation) {
            $sim_id = $simulation['sim_id'];

            $simulations[$sim_id] = $simulation;
        }

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

        if (!isset($categories[$cat_encoding])) {
            return null;
        }

        return $categories[$cat_encoding];
    }

    function sim_get_cat_id_by_cat_encoding($cat_encoding) {
        $category = sim_get_cat_by_cat_encoding($cat_encoding);

        if (!isset($category['cat_id'])) {
            return null;
        }

        return $category['cat_id'];
    }

    function sim_get_all_sim_names($real_only = false) {
        $simulations = array();

        $condition = '';

        if ($real_only) {
            $condition = "WHERE `sim_is_real`='1'";
        }

        $simulation_rows = db_exec_query("SELECT sim_id, sim_name FROM `simulation` $condition ORDER BY `sim_sorting_name` ");

        while($simulation = mysql_fetch_assoc($simulation_rows)) {
            $sim_id   = $simulation['sim_id'];
            $sim_name = $simulation['sim_name'];

            if (is_numeric($sim_id)) {
                $simulations["sim_id_$sim_id"] = $sim_name;
            }
        }

        return $simulations;
    }

    function sim_auto_calc_sim_size($sim_id) {
        $simulation = sim_get_sim_by_id($sim_id);

        $sim_launch_url = sim_get_launch_url($simulation);

        $ext = strtolower(get_file_extension($sim_launch_url));

        if ($ext == 'jnlp') {
            $size = 0;

            // Java jnlp file; have to compute file size by examining jnlp
            @$xml = file_get_contents($sim_launch_url);
            if ($xml == false) {
                return false;
            }

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
                // FIXME: these strings should be formatted
                print "Error trying to detect size: ".$simulation['sim_name'].", id = ".$simulation['sim_id'].", url = $sim_launch_url, encoding = $xml_encoding<br/>";
            }
        }
        else if ($ext == 'html') {
            // Flash sim, look for the SWF
            // TODO: push the filename generation into a function
            $flash_link = sim_get_root()."{$simulation['sim_dirname']}/{$simulation['sim_flavorname']}.swf";
            $size = url_or_file_size($flash_link);
        }
        else {
            // ERROR: Sim not specified
            //$size = 0;
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
            $listings[] = $row;
        }

        return $listings;
    }

    /**
     * Return the filename and downloadable content.
     * If $strict is true, the requested locale must exist.  If it doesn't return false.
     * If $strict is false, if the requested locale does not exists the default is substituted.
     * 
     * @param array $simulation Array of simulation info as given by the database
     * @param string $requested_locae Locale desired
     * @param bool $strict True if the file 
     * @return arary(filename, conent), or false if not successful
     **/
    function sim_get_download($simulation, $locale = DEFAULT_LOCALE, $strict = true) {

        $verbose = debug_is_on();

        $dirname     = $simulation['sim_dirname'];
        $flavorname  = $simulation['sim_flavorname'];
        
        // Create 2 variables:
        //    $default_file is the JAR filename for the default locale
        //    $locale_file is the JAR filename for the requested locale
        // The default will be used if strict is off and the locale file cannot be found
        if ($simulation['sim_type'] == SIM_TYPE_JAVA) {
            $default_file = sim_get_root()."{$dirname}/{$flavorname}.jar";
            if (locale_is_default($locale)) {
                $locale_file = $default_file;
            }
            else {
                $locale_file = sim_get_root()."{$dirname}/{$flavorname}_{$locale}.jar";
            }
        }
        else if ($simulation['sim_type'] == SIM_TYPE_FLASH) {
            $default_file = sim_get_root()."{$dirname}/{$flavorname}".DEFAULT_LOCALE.".jar";
            $locale_file = sim_get_root()."{$dirname}/{$flavorname}_{$locale}.jar";
        }
        else {
            return 0;
        }

        // Check if the file exists
        if (!file_exists($locale_file)) {
            if ($strict) {
                // Strict matching requested, return false
                return false;
            }

            // Not strict matching, try to get the default
            $locale_file = $default_file;
            if (!file_exists($locale_file)) {
                // Can't find the default JAR either
                return false;
            }
        }

        // Return the filename and the contents of that file
        return array($locale_file, file_get_contents($locale_file));
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

    /** TODO: docstring **/
    function sim_get_launch_url($simulation, $locale = DEFAULT_LOCALE, $test_existance = false) {
        if (!locale_valid($locale)) {
            return '';
        }

        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];
        $sim_type   = $simulation['sim_type'];
        $url = '';

        if ($sim_type == SIM_TYPE_JAVA) {
            if (locale_is_default($locale)) {
                $url = sim_get_root()."{$dirname}/{$flavorname}.jnlp";
            }
            else {
                $url = sim_get_root()."{$dirname}/{$flavorname}_{$locale}.jnlp";
            }
        }
        else if ($sim_type == SIM_TYPE_FLASH) {
            $url = sim_get_root()."{$dirname}/{$flavorname}_{$locale}.html";
        }

        if ($test_existance) {
            if (!file_exists($url)) {
                return '';
            }
        }

        return $url;
    }

    /**
     * Get the simulataion download link of the specified languagefile exists
     * 
     * @param array $simulation Simulation information
     * @param string $locale OPTIONAL locale to use
     * @param bool $test_existance OPTIONAL if true will check the existance of the downloadable file
     * @return mixed Returns an empty string if $test_existance is true and the file does not exist, otherwise returns a string with the link that will allow downloading of the sim
     */
    function sim_get_download_url($simulation, $requested_locale = DEFAULT_LOCALE, $test_existance = false) {
        $locale = (locale_valid($requested_locale)) ? $requested_locale : DEFAULT_LOCALE;

        if ($test_existance) {
            $dirname    = $simulation['sim_dirname'];
            $flavorname = $simulation['sim_flavorname'];
            $sim_type   = $simulation['sim_type'];
            $file = '';
            
            if ($sim_type == SIM_TYPE_JAVA) {
                if (locale_is_default($locale)) {
                    $file = sim_get_root()."{$dirname}/{$flavorname}_all.jar";
                }
                else {
                    $file = sim_get_root()."{$dirname}/{$flavorname}_{$locale}.jar";
                }
            }
            else if ($sim_type == SIM_TYPE_FLASH) {
                $file = sim_get_root()."{$dirname}/{$flavorname}_{$locale}.jar";
            }
            
            if (!file_exists($file)) {
                return '';
            }
        }

        return SITE_ROOT."admin/get-run-offline.php?sim_id={$simulation['sim_id']}&amp;locale={$locale}";
    }

    function sim_get_screenshot($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];

        // Try local first
        $link = sim_get_root()."{$dirname}/{$flavorname}-screenshot.png";
        if (!file_exists($link)) {
            $link = "http://".PHET_DOMAIN_NAME."/sims/{$dirname}/{$flavorname}-screenshot.png";
        }

        return $link;
    }

    function sim_get_animated_screenshot($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];

        // Try local first
        $link = sim_get_root()."{$dirname}/{$flavorname}-animated-screenshot.gif";
        if (!file_exists($link)) {
            $link = "http://".PHET_DOMAIN_NAME."/sims/{$dirname}/{$flavorname}-animated-screenshot.gif";
        }

        return $link;
    }

    function sim_get_select_sims_by_category_statement($cat_id) {
        return "SELECT DISTINCT `simulation`.`sim_id` FROM `simulation`, `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat_id' AND `simulation`.`sim_id`=`simulation_listing`.`sim_id` ORDER BY `simulation_listing`.`simulation_listing_order` ASC ";
    }

    function sim_get_select_sims_by_category_statement_order_alphabetically($cat_id) {
        return "SELECT DISTINCT `simulation`.`sim_id` FROM `simulation`, `simulation_listing` WHERE `simulation_listing`.`cat_id`='$cat_id' AND `simulation`.`sim_id`=`simulation_listing`.`sim_id` ORDER BY `simulation`.`sim_sorting_name` ASC ";
    }

    function sim_get_file_contents($resource) {
        // TODO: called by random-thumbnail.php, but due to weird include finangling this dir is specific ot
        $new_resource = str_replace('http://'.PHET_DOMAIN_NAME.'/sims/', 'sims/', $resource);

        if (file_exists($new_resource)) return file_get_contents($new_resource);

        return file_get_contents($resource);
    }

    function sim_get_image_previews($type, $is_static_screenshot = true) {
        // Get the database connection, start it if if this is the first call
        global $connection;
        if (!isset($connection)) {
            connect_to_db();
        }

        $select_categories_st = "SELECT * FROM `category` WHERE `cat_is_visible`='0' ";
        $category_rows        = mysql_query($select_categories_st, $connection);

        while ($category_row = mysql_fetch_assoc($category_rows)) {
            $cat_id   = $category_row['cat_id'];
            $cat_name = $category_row['cat_name'];

            if (preg_match("/.*$type.*preview.*/i", $cat_name) == 1 || !$is_static_screenshot) {
                $images = array();

                foreach (sim_get_sims_by_cat_id($cat_id) as $simulation) {
                    if ($is_static_screenshot) {
                        $static_screenshot = sim_get_screenshot($simulation);

                        $images[] = $static_screenshot;
                    }
                    else {
                           $animated_screenshot = sim_get_animated_screenshot($simulation);

                        // if (file_or_url_exists($animated_screenshot)) {
                               $images[] = $animated_screenshot;
                        // }
                        // else {
                        //
                        // }
                    }
                }

                return $images;
            }
        }

        return FALSE;
    }

    function sim_get_animated_previews() {
        return sim_get_image_previews("animated", false);
    }

    function sim_get_static_previews() {
        return sim_get_image_previews("static", true);
    }

    function sim_get_resource_name_for_image($sim_image_url) {
        $file_name_hash = md5($sim_image_url);

        return $file_name_hash.".jpg";
    }

    function sim_get_thumbnail($sim) {
        $sim_image_url = sim_get_screenshot($sim);

        $sim_image_resource = sim_get_resource_name_for_image($sim_image_url);

        $file = cache_get(SIM_THUMBNAILS_CACHE, $sim_image_resource, 24);

        if (!$file) {
            // Load image -- assume image is in png format (for now):
            @$img = imagecreatefrompng($sim_image_url);
            if ($img == false) {
                return;
            }

            $existing_width  = imagesx($img);
            $existing_height = imagesy($img);

            // Scale to thumbnail width, preserving aspect ratio:
            $new_width  = SIM_THUMBNAIL_WIDTH;
            $new_height = SIM_THUMBNAIL_HEIGHT;//floor($existing_height * ( SIM_THUMBNAIL_WIDTH / $existing_width ));

            $tmp_img = imagecreatetruecolor($new_width, $new_height);

/*
bool imagecopyresized   ( resource $dst_image, resource $src_image, int $dst_x, int $dst_y, int $src_x, int $src_y, int $dst_w, int $dst_h, int $src_w, int $src_h )
bool imagecopyresampled ( resource $dst_image, resource $src_image, int $dst_x, int $dst_y, int $src_x, int $src_y, int $dst_w, int $dst_h, int $src_w, int $src_h )
*/

            imagecopyresampled($tmp_img, $img, 0, 0, 0, 0, $new_width, $new_height, $existing_width, $existing_height);

            $temp_file = tempnam('/tmp', 'sim-thumbnail');

            // Output image to cached image location:
            imagejpeg($tmp_img, $temp_file);

            cache_put(SIM_THUMBNAILS_CACHE, $sim_image_resource, file_get_contents($temp_file));

            unlink($temp_file);
        }

        return cache_get_file_location(SIM_THUMBNAILS_CACHE, $sim_image_resource);
    }

    function sim_remove_teachers_guide($sim_id) {
        $sim = sim_get_sim_by_id($sim_id);
        if (!$sim || !isset($sim["sim_teachers_guide_id"])) {
            return false;
        }

        db_delete_row("teachers_guide", array("teachers_guide_id" => $sim["sim_teachers_guide_id"]));
        db_update_table("simulation", array("sim_teachers_guide_id" => 0), "sim_id", $sim_id);
    }

    function sim_set_teachers_guide($sim_id, $filename, $size, $contents) {
        sim_remove_teachers_guide($sim_id);

        $encoded_data = base64_encode($contents);

        $new_id = db_insert_row(
                "teachers_guide",
                array(
                    "teachers_guide_filename" => $filename,
                    "teachers_guide_size" => $size,
                    "teachers_guide_contents" => $encoded_data
                )
            );

        assert($new_id);
        db_update_table("simulation", array("sim_teachers_guide_id" => $new_id), "sim_id", $sim_id);
    }

    function sim_get_teachers_guide_by_sim_id($sim_id, $decode_contents = false) {
        $sim = sim_get_sim_by_id($sim_id);
        if (!$sim) {
            return false;
        }

        return sim_get_teachers_guide($sim["sim_teachers_guide_id"], $decode_contents);
    }

    function sim_get_teachers_guide($teachers_guide_id, $decode_contents = false) {
        // Get the row
        $teachers_guide = db_get_row_by_condition("teachers_guide", array("teachers_guide_id" => $teachers_guide_id));

        // Decode the contents
        if ($decode_contents) {
            $encoded_contents = $teachers_guide["teachers_guide_contents"];
            $teachers_guide["teachers_guide_contents"] = base64_decode($encoded_contents);
        }

        return $teachers_guide;
    }

?>