<?php

    // Utils to support sims

    if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
    include_once(SITE_ROOT."admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/cache-utils.php");

    define("SIM_THUMBNAIL_WIDTH", 130);
    define("SIM_THUMBNAIL_HEIGHT", 97);

    define("SIM_TYPE_JAVA",  "0");
    define("SIM_TYPE_FLASH", "1");

    $SIM_TYPE_TO_IMAGE =
        array(
            SIM_TYPE_JAVA   => 'java.png',
            SIM_TYPE_FLASH  => 'flash.png'
        );

    // Java localization automatically (and silently) converts some newer ISO language codes to the
    // older obsolete codes.  These lanugages are affected:
    // Hebrew: he -> iw
    // Yiddish: yi -> ji
    // Indonesian id -> in
    // We will us the right hand column (the old ones) until this gets resolved.
    $LANGUAGE_CODE_TO_LANGUAGE_NAME = array(
        "es" => "Spanish",
        "aa" => "Afar",
        "ab" => "Abkhazian",
        "ae" => "Avestan",
        "af" => "Afrikaans",
        "ak" => "Akan",
        "am" => "Amharic",
        "an" => "Aragonese",
        "ar" => "Arabic",
        "as" => "Assamese",
        "av" => "Avaric",
        "ay" => "Aymara",
        "az" => "Azerbaijani",
        "ba" => "Bashkir",
        "be" => "Belarusian",
        "bg" => "Bulgarian",
        "bh" => "Bihari",
        "bi" => "Bislama",
        "bm" => "Bambara",
        "bn" => "Bengali",
        "bo" => "Tibetan",
        "br" => "Breton",
        "bs" => "Bosnian",
        "ca" => "Catalan",
        "ce" => "Chechen",
        "ch" => "Chamorro",
        "co" => "Corsican",
        "cr" => "Cree",
        "cs" => "Czech",
        "cu" => "Church",
        "cv" => "Chuvash",
        "cy" => "Welsh",
        "da" => "Danish",
        "de" => "German",
        "dv" => "Divehi",
        "dz" => "Dzongkha",
        "ee" => "Ewe",
        "el" => "Greek",
        "en" => "English",
        "eo" => "Esperanto",
        "et" => "Estonian",
        "eu" => "Basque",
        "fa" => "Persian",
        "ff" => "Fulah",
        "fi" => "Finnish",
        "fj" => "Fijian",
        "fo" => "Faroese",
        "fr" => "French",
        "fy" => "Western Frisian",
        "ga" => "Irish",
        "gd" => "Scottish Gaelic",
        "gl" => "Galician",
        "gn" => "Guaraní",
        "gu" => "Gujarati",
        "gv" => "Manx",
        "ha" => "Hausa",
        // Hebrew: new is "he", old is "iw"
        //"he" => "Hebrew",
        "iw" => "Hebrew",
        "hi" => "Hindi",
        "ho" => "Hiri Motu",
        "hr" => "Croatian",
        "ht" => "Haitian",
        "hu" => "Hungarian",
        "hy" => "Armenian",
        "hz" => "Herero",
        "ia" => "Interlingua",
        // Indonesian: new is "id", old is "in"
        //"id" => "Indonesian",
        "in" => "Indonesian",
        "ie" => "Interlingue",
        "ig" => "Igbo",
        "ii" => "Sichuan Yi",
        "ik" => "Inupiaq",
        "io" => "Ido",
        "is" => "Icelandic",
        "it" => "Italian",
        "iu" => "Inuktitut",
        "ja" => "Japanese",
        "jv" => "Javanese",
        "ka" => "Georgian",
        "kg" => "Kongo",
        "ki" => "Kikuyu",
        "kj" => "Kwanyama",
        "kk" => "Kazakh",
        "kl" => "Kalaallisut",
        "km" => "Khmer",
        "kn" => "Kannada",
        "ko" => "Korean",
        "kr" => "Kanuri",
        "ks" => "Kashmiri",
        "ku" => "Kurdish",
        "kv" => "Komi",
        "kw" => "Cornish",
        "ky" => "Kirghiz",
        "la" => "Latin",
        "lb" => "Luxembourgish",
        "lg" => "Ganda",
        "li" => "Limburgish",
        "ln" => "Lingala",
        "lo" => "Lao",
        "lt" => "Lithuanian",
        "lu" => "Luba-Katanga",
        "lv" => "Latvian",
        "mg" => "Malagasy",
        "mh" => "Marshallese",
        "mi" => "Māori",
        "mk" => "Macedonian",
        "ml" => "Malayalam",
        "mn" => "Mongolian",
        "mo" => "Moldavian",
        "mr" => "Marathi",
        "ms" => "Malay",
        "mt" => "Maltese",
        "my" => "Burmese",
        "na" => "Nauru",
        "nb" => "Norwegian Bokmål",
        "nd" => "North Ndebele",
        "ne" => "Nepali",
        "ng" => "Ndonga",
        "nl" => "Dutch",
        "nn" => "Norwegian Nynorsk",
        "no" => "Norwegian",
        "nr" => "South Ndebele",
        "nv" => "Navajo",
        "ny" => "Chichewa",
        "oc" => "Occitan",
        "oj" => "Ojibwa",
        "om" => "Oromo",
        "or" => "Oriya",
        "os" => "Ossetian",
        "pa" => "Panjabi",
        "pi" => "Pāli",
        "pl" => "Polish",
        "ps" => "Pashto",
        "pt" => "Portuguese",
        "qu" => "Quechua",
        "rm" => "Raeto-Romance",
        "rn" => "Kirundi",
        "ro" => "Romanian",
        "ru" => "Russian",
        "rw" => "Kinyarwanda",
        "ry" => "Rusyn",
        "sa" => "Sanskrit",
        "sc" => "Sardinian",
        "sd" => "Sindhi",
        "se" => "Northern Sami",
        "sg" => "Sango",
        "sh" => "Serbo-Croatian",
        "si" => "Sinhalese",
        "sk" => "Slovak",
        "sl" => "Slovenian",
        "sm" => "Samoan",
        "sn" => "Shona",
        "so" => "Somali",
        "sq" => "Albanian",
        "sr" => "Serbian",
        "ss" => "Swati",
        "st" => "Sotho",
        "su" => "Sundanese",
        "sv" => "Swedish",
        "sw" => "Swahili",
        "ta" => "Tamil",
        "te" => "Telugu",
        "tg" => "Tajik",
        "th" => "Thai",
        "ti" => "Tigrinya",
        "tk" => "Turkmen",
        "tl" => "Tagalog",
        "tn" => "Tswana",
        "to" => "Tonga",
        "tr" => "Turkish",
        "ts" => "Tsonga",
        "tt" => "Tatar",
        "tw" => "Twi",
        "ty" => "Tahitian",
        "ug" => "Uighur",
        "uk" => "Ukrainian",
        "ur" => "Urdu",
        "uz" => "Uzbek",
        "ve" => "Venda",
        "vi" => "Vietnamese",
        "vo" => "Volapük",
        "wa" => "Walloon",
        "wo" => "Wolof",
        "xh" => "Xhosa",
        // Yiddish: new is "yi", old is "ji"
        //"yi" => "Yiddish",
        "ji" => "Yiddish",
        "yo" => "Yoruba",
        "za" => "Zhuang",
        "zh" => "Chinese",
        "zu" => "Zulu"
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
    define("SIM_OFFLINE_FLASH_CACHE", "offline-flash");
    define("SIM_OFFILNE_FLASH_CACHE_HOURS", 876600);  // 100 years
    define("SIM_CREATE_JAR_EXECUTABLE", "/usr/local/java/bin/jar");

    define("SIMS_PER_PAGE", 9);

    define("SQL_SELECT_ALL_VISIBLE_CATEGORIES",
           "SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_parent`,`cat_order` ASC ");

    function get_sorting_name($name) {
        $matches = array();

        preg_match('/((the|a|an) +)?(.*)/i', $name, $matches);

        return $matches[3];
    }

    function sim_get_language_icon_url_from_language_name($language_name, $is_vertical = false) {
        global $LANGUAGE_CODE_TO_LANGUAGE_NAME;

        $language_code = null;

        foreach ($LANGUAGE_CODE_TO_LANGUAGE_NAME as $code => $name) {
            if (strtolower($name) == strtolower($language_name)) {
                $language_code = $code;

                break;
            }
        }

        if ($language_code == null) {
            return false;
        }

        $icon_name = strtolower($language_name).'-'.strtolower($language_code).'.png';

        $icon_location = SITE_ROOT."images/languages/$icon_name";

        if ($is_vertical) {
            $vertical_icon_location = SITE_ROOT."images/languages/vertical-$icon_name";

            if (!file_exists($vertical_icon_location)) {
                $source = false;
                if (file_exists($icon_location)) {
                    $source = imagecreatefrompng($icon_location);
                }

                if (!$source) { /* See if it failed */
                    $source  = imagecreatetruecolor(100, 30); /* Create a blank image */
                    $bgc     = imagecolorallocate($source, 255, 255, 255);
                    $tc      = imagecolorallocate($source, 0, 0, 0);
                    imagefilledrectangle($source, 0, 0, 100, 30, $bgc);
                    /* Output an errmsg */
                    imagestring($source, 1, 5, 5, $language_name, $tc);
                    //imagestring($source, 1, 5, 5, "Error loading $icon_location", $tc);

                    // If the original didn't exist, write it out
                    if (!file_exists($icon_location)) {
                        imagepng($source, $icon_location, 9);
                    }
                }

                // Rotate
                if (function_exists("imagerotate")) {
                    $rotate = imagerotate($source, 90, 0);
                }
                else {
                    $rotate = $source;
                }

                imagepng($rotate, $vertical_icon_location, 9);
            }

            return $vertical_icon_location;
        }

        return $icon_location;
    }

    function sim_get_all_translated_language_names() {
        $all_translations = cache_get(SIM_TRANSLATIONS_CACHE, 'all-translations.cache', 24);

        if ($all_translations) {
            return unserialize($all_translations);
        }

        $language_to_translations = array();

        // Loop through all sims:
        foreach (sim_get_all_sims() as $sim) {

            $sim_name     = $sim['sim_name'];
            $translations = sim_get_translations($sim);

            // Get all translations of the current sim:
            foreach ($translations as $language_name => $data) {
                if (!isset($language_to_translations[$sim_name])) {
                    $language_to_translations[$sim_name] = array();
                }

                $language_to_translations[$sim_name][$language_name] = $data["url"];
            }
        }


        cache_put(SIM_TRANSLATIONS_CACHE, 'all-translations.cache', serialize($language_to_translations));

        return $language_to_translations;
    }

    // Returns a map from translation name to JNLP file:
    function sim_get_translations($simulation) {
        global $LANGUAGE_CODE_TO_LANGUAGE_NAME;

        $dirname     = $simulation['sim_dirname'];
        $flavorname  = $simulation['sim_flavorname'];

        $translations = array();

        foreach ($LANGUAGE_CODE_TO_LANGUAGE_NAME as $code => $language_name) {
            if ($code == "en") {
                // Skip the English traslations, they are the default
                continue;
            }

            $link = sim_get_launch_url($simulation, $code);
            if ($link) {
                $translations[$language_name] = array("code" => $code, "url" => $link);
            }

            flush();
        }

        return $translations;
    }

    // Returns a string with the sim version, or empty string if it cannot be determined
    function sim_get_version($simulation) {
        $dirname     = $simulation['sim_dirname'];
        $flavorname  = $simulation['sim_flavorname'];

        $properties_filename = PORTAL_ROOT."sims/{$dirname}/{$flavorname}.properties";

        $minor_version = -1;
        $major_version = -1;

        $handle = @fopen($properties_filename, "r");
        if ($handle) {
            while (!feof($handle)) {
                $buffer = fgets($handle, 4096);
                $regs = array();
                if (ereg('version\.(minor|major) *= *([0-9]+)', $buffer, $regs)) {
                    if ($regs[1] == 'minor') {
                        $minor_version = $regs[2];
                    }
                    else if ($regs[1] == 'major') {
                        $major_version = $regs[2];
                    }
                }
            }
            fclose($handle);
        }

        if (($minor_version != -1) && ($major_version != -1)) {
            return $major_version.".".$minor_version;
        }

        return "";
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
                print "Error trying to detect size: ".$simulation['sim_name'].", id = ".$simulation['sim_id'].", url = $sim_launch_url, encoding = $xml_encoding<br/>";
            }
        }
        else if ($ext == 'swf') {
            // Old style just linked to the SWF directly
            $size = url_or_file_size($sim_launch_url);
        }
        else if ($ext == 'html') {
            // New style flash sim with i18n support, look for the SWF
            // TODO: push the filename generation into a function
            $i18n_flash_link = PORTAL_ROOT."sims/{$simulation['sim_dirname']}/{$simulation['sim_flavorname']}.swf";
            $size = url_or_file_size($i18n_flash_link);
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

    function sim_flash_is_internationalized($simulation) {
        $dirname     = $simulation['sim_dirname'];
        $flavorname  = $simulation['sim_flavorname'];

        if ($simulation["sim_type"] != SIM_TYPE_FLASH) {
            return false;
        }

        $sim_dir = PORTAL_ROOT."sims/";
        $html_pattern = "{$sim_dir}{$dirname}/{$flavorname}_*.html";
        $xml_pattern = "{$sim_dir}{$dirname}/{$flavorname}_*.html";

        $files = glob($html_pattern);
        return count($files) > 0;
    }

    /**
     * Return the filename and offline content so a user can download for later.
     * 
     * @return arary(filename, conent), or false if not successful
     **/
    function sim_get_run_offline($simulation, $language_code = null) {

        $verbose = debug_is_on();

        $dirname     = $simulation['sim_dirname'];
        $flavorname  = $simulation['sim_flavorname'];

        // If it is a Java sim, just send the jar
        if ($simulation['sim_type'] == SIM_TYPE_JAVA) {
            $filename = PORTAL_ROOT."sims/{$dirname}/{$flavorname}.jar";
            return array($filename, file_get_contents($filename));
        }

        // Determine if the SIM is the newstyle internationalized or the
        // oldstyle .swf

        if (!sim_flash_is_internationalized($simulation)) {
            // Old style, just send the .swf along to the user
            $filename = PORTAL_ROOT."sims/{$dirname}/{$flavorname}.swf";
            return array($filename, file_get_contents($filename));
        }

        //
        // Internationalized sim, generate a jar file that will launch the localized swf

        // Get the language
        if (is_null($language_code)) {
            $lang = "en";
        }
        else {
            $lang = $language_code;
        }

        // Setup our constants
        $full_dirname = PORTAL_ROOT."sims/{$dirname}/";
        $jar_template_dir = PORTAL_ROOT."phet-dist/flash-launcher/";
        $output_jar_name = $flavorname."_".$lang.".jar";

        // Check if this sim has been cached
        $jar_cache_id = md5($dirname.$output_jar_name);
        $jar_cache_resource = "${jar_cache_id}.jar.cache";
        $cached_jar = cache_get(SIM_OFFLINE_FLASH_CACHE, $jar_cache_resource, SIM_OFFILNE_FLASH_CACHE_HOURS);
        if ($cached_jar) {
            return array($output_jar_name, $cached_jar);
        }

        //
        // Prepares temporary files and directories

        // Create a temporary directory
        $num_tries = 10;
        for ($i = 0; $i < $num_tries; ++$i) {
            $temp_dir_name = sys_get_temp_dir()."/phet_".rand()."/";
            $dir_made = mkdir($temp_dir_name);
            if ($dir_made) {
                break;
            }
        }

        if ($dir_made === false) {
            //print "ERROR: cannot create directory";
            exit;
        }

        // Create the flash-launcher-args.txt file
        $fp = fopen($temp_dir_name."flash-launcher-args.txt", "w");
        if ($fp === false) {
            if ($verbose) {
                print "ERROR: cannot open file 'flash-launcher-args.txt'";
            }
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Write the ags to flash-launcher-args.txt file
        // Fromat: sim_flavorname language_code [flags]
        $result = fwrite($fp, "{$flavorname} {$lang}");
        if ($result === false) {
            if ($verbose) {
                print "ERROR: cannot write to file 'flash-launcher-args.txt'";
            }
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Close flash-launcher-args.txt file...
        $result = fclose($fp);
        if ($fp === false) {
            if ($verbose) {
                print "ERROR: cannot close file 'flash-launcher-args.txt'";
            }
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Create temp jar file...
        $temp_jar_name = tempnam(sys_get_temp_dir(), "phet_jar_");
        if ($temp_jar_name === false) {
            if ($verbose) {
                print "ERROR: cannot create temp jar file";
            }
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Get all the languages, formatted for including on the jar command line
        $jar_include_languages = array();
        $jar_laungage_xmls = '';
        foreach (glob("{$full_dirname}{$flavorname}-strings_*.xml") as $lanugage_xml) {
            $jar_laungage_xmls .= "-C ".dirname($lanugage_xml)." ".basename($lanugage_xml)." ";
        }

        // jar args
        //     -m specifies the manifest file
        //     -C dir file  changes to the dir and puts file in the archive
        $args = array("-C {$temp_dir_name} flash-launcher-args.txt",
                      "-C {$jar_template_dir} flash-launcher-template.html",
                      "-C {$full_dirname} {$flavorname}.properties",
                      "-C {$full_dirname} {$flavorname}.swf",
                      $jar_laungage_xmls,
                      "-C {$jar_template_dir} edu");

        // Construct the command
        $command = SIM_CREATE_JAR_EXECUTABLE." cmf {$jar_template_dir}META-INF/MANIFEST.MF {$temp_jar_name} ".join(" ", $args);

        // Run the command to create the jar file
        $sys_ret = 0;
        $result = system($command, $sys_ret);
        if ($sys_ret != 0) {
            if ($verbose) {
                print "ERROR: fastjar command failed, exit code: {$sys_ret}<br />\n";
                print "command: {$command}<br />\n";
            }
            $result = unlink($tmp_jar_name);
            assert($result === true);
            $result = unlink($temp_dir_name."flash-launcher-args.txt");
            assert($result === true);
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Get the contents of the jar file
        $jar_content = file_get_contents($temp_jar_name);

        //
        // Cleanup

        // Delete temp jar file
        $result = unlink($temp_jar_name);
        if ($result === false) {
            if ($verbose) {
                print "ERROR: cannot delete file 'flash-launcher-args.txt'";
            }
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Delete flash-launcher-args.txt file
        $result = unlink($temp_dir_name."flash-launcher-args.txt");
        if ($result === false) {
            if ($verbose) {
                print "ERROR: cannot delete file 'flash-launcher-args.txt'";
            }
            $result = rmdir($temp_dir_name);
            assert($result === true);
            exit;
        }

        // Delete temp directory
        $result = rmdir($temp_dir_name);
        if ($result === false) {
            if ($verbose) {
                print "ERROR: cannot delete file 'flash-launcher-args.txt'";
            }
            exit;
        }

        // Put the jar in cache
        cache_put(SIM_OFFLINE_FLASH_CACHE, $jar_cache_resource, $jar_content);

        // Return the content
        return array($output_jar_name, $jar_content);
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

    function sim_get_launch_url($simulation, $language_code = "en") {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];
        $sim_type   = $simulation['sim_type'];

        if ($sim_type == SIM_TYPE_FLASH) {
            $oldstyle_link = PORTAL_ROOT."sims/{$dirname}/{$flavorname}.swf";
            $flash_swf = PORTAL_ROOT."sims/{$dirname}/{$flavorname}.swf";
            $flash_strings = PORTAL_ROOT."sims/{$dirname}/{$flavorname}-strings_{$language_code}.xml";
            $flash_html = PORTAL_ROOT."sims/{$dirname}/{$flavorname}_{$language_code}.html";
            if (file_exists($flash_swf) && file_exists($flash_strings) && file_exists($flash_html)) {
                $link = $flash_html;
            }
            else if (($language_code == "en") && file_exists($oldstyle_link)) {
                $link = $oldstyle_link;
            }
            else {
                return false;
                $link = "http://".PHET_DOMAIN_NAME."/sims/$dirname/$flavorname.swf";
            }
        }
        else {
            // Try local first
            $oldstyle_link = PORTAL_ROOT."sims/{$dirname}/{$flavorname}.jnlp";
            $newstyle_link = PORTAL_ROOT."sims/{$dirname}/{$flavorname}_{$language_code}.jnlp";
            if (($language_code == "en") && (file_exists($oldstyle_link))) {
                $link = $oldstyle_link;
            }
            else if (file_exists($newstyle_link)) {
                $link = $newstyle_link;
            }
            else {
                return false;
                $link = "http://".PHET_DOMAIN_NAME."/sims/{$dirname}/{$flavorname}.jnlp";
            }
        }

        return $link;
    }

    function sim_get_screenshot($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];

        // Try local first
        $link = PORTAL_ROOT."sims/{$dirname}/{$flavorname}-screenshot.png";
        if (!file_exists($link)) {
            $link = "http://".PHET_DOMAIN_NAME."/sims/{$dirname}/{$flavorname}-screenshot.png";
        }

        return $link;
    }

    function sim_get_animated_screenshot($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];

        // Try local first
        $link = PORTAL_ROOT."sims/{$dirname}/{$flavorname}-screenshot.gif";
        if (!file_exists($link)) {
            $link = "http://".PHET_DOMAIN_NAME."/sims/{$dirname}/{$flavorname}-animated-screenshot.gif";
        }

        return $link;
    }

    function sim_get_run_offline_link($simulation) {
        return SITE_ROOT.'admin/get-run-offline.php?sim_id='.$simulation['sim_id'];
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