<?php

    include_once("../admin/global.php");
    include_once(SITE_ROOT."admin/db.php");
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."admin/db-utils.php");
    include_once(SITE_ROOT."admin/xml_parser.php");
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
        "he" => "Hebrew",
        "hi" => "Hindi",
        "ho" => "Hiri Motu",
        "hr" => "Croatian",
        "ht" => "Haitian",
        "hu" => "Hungarian",
        "hy" => "Armenian",
        "hz" => "Herero",
        "ia" => "Interlingua",
        "id" => "Indonesian",
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
        "yi" => "Yiddish",
        "yo" => "Yoruba",
        "za" => "Zhuang",
        "zh" => "Chinese",
        "zu" => "Zulu"
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
            SIM_RATING_BETA_MINUS   => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA_MINUS].'" alt="Beta Minus Rating Image"     width="37" title="This simulation has undergone minimal testing, but may not have been fully refined or subjected to extensive user testing." /></a>',
            SIM_RATING_BETA_PLUS    => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA_PLUS].'"     alt="Beta Plus Rating Image"     width="37" title="This simulation has undergone minimal testing, but may not have been fully refined or subjected to extensive user testing." /></a>',
            SIM_RATING_BETA         => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_BETA].'"         alt="Beta Rating Image"         width="37" title="This simulation has undergone minimal testing, but may not have been fully refined or subjected to extensive user testing." /></a>',
            SIM_RATING_CHECK        => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_CHECK].'"         alt="Checkmark Rating Image"     width="37" title="Classroom Tested: This simulation has been used and tested in the classroom, and on multiple computer platforms. The simulation has been refined based on that experience and on student interviews." /></a>',
            SIM_RATING_ALPHA        => '<a href="../about/legend.php"><img src="../images/sims/ratings/'.$SIM_RATING_TO_IMAGE[SIM_RATING_ALPHA].'"         alt="Alpha Rating Image"         width="37" title="Under Construction: This simulation is an early preview version, and may have functional or usability bugs." /></a>'
        );

    $SIM_TYPE_TO_IMAGE_HTML =
        array(
            SIM_TYPE_JAVA  => '<a href="../tech_support/support-java.php"> <img src="../images/sims/ratings/'.$SIM_TYPE_TO_IMAGE[SIM_TYPE_JAVA].'" alt="Java Icon" title="This simulation is a Java simulation" /></a>',
            SIM_TYPE_FLASH => '<a href="../tech_support/support-flash.php"><img src="../images/sims/ratings/'.$SIM_TYPE_TO_IMAGE[SIM_TYPE_FLASH].'" alt="Java Icon" title="This simulation is a Flash simulation" /></a>'
        );

    define("SIM_NO_MAC_IMAGE", '../images/sims/ratings/no-mac25x25.png');
    define("SIM_CRUTCH_IMAGE", '../images/sims/ratings/crutch25x25.png');

    define("FROM_PHET_IMAGE_HTML", '<a href="../about/legend.php"><img src="../images/phet-logo-icon.jpg" alt="Designed by PhET Icon" title="PhET Designed: This contribution was designed by PhET." /></a>');

    define("SIM_NO_MAC_IMAGE_HTML",
            "<a href=\"../about/legend.php\"><img src=\"".SIM_NO_MAC_IMAGE."\" alt=\"No Mac\" title=\"Not supported on Mac\"/></a>");

    define("SIM_CRUTCH_IMAGE_HTML",
        "<a href=\"../about/legend.php\"><img src=\"".SIM_CRUTCH_IMAGE."\" alt=\"Not standalone\" width=\"37\" title=\"Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity.\"/></a>");

    define("SIM_THUMBNAILS_CACHE",      "thumbnails");
    define("SIM_TRANSLATIONS_CACHE", "translations");

    define("SIMS_PER_PAGE", 9);

    define("SQL_SELECT_ALL_VISIBLE_CATEGORIES",
           "SELECT * FROM `category` WHERE `cat_is_visible`='1' ORDER BY `cat_order` ASC ");

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

        $icon_location = "../images/languages/$icon_name";

        if ($is_vertical) {
            $vertical_icon_location = "../images/languages/vertical-$icon_name";

            if (!file_exists($vertical_icon_location)) {
                $source = imagecreatefrompng($icon_location);

                if (!$source) { /* See if it failed */
                $source  = imagecreatetruecolor(150, 30); /* Create a blank image */
                $bgc     = imagecolorallocate($source, 255, 255, 255);
                $tc      = imagecolorallocate($source, 0, 0, 0);
                imagefilledrectangle($source, 0, 0, 150, 30, $bgc);
                /* Output an errmsg */
                imagestring($source, 1, 5, 5, "Error loading $icon_location", $tc);
            }

                // Rotate
                $rotate = imagerotate($source, 90, 0);

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
            foreach ($translations as $language_name => $launch_url) {
                if (!isset($language_to_translations[$sim_name])) {
                    $language_to_translations[$sim_name] = array();
                }

                $language_to_translations[$sim_name][$language_name] = $launch_url;
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

        $launch_file_base_dir = realpath(dirname(__FILE__)."/../../sims");

        foreach ($LANGUAGE_CODE_TO_LANGUAGE_NAME as $code => $language_name) {
            $translated_flavorname = "${flavorname}_${code}";

            // This is to speed things up on the actual server, where the JNLP files should
            // be accessible from the file system, and not just from HTTP.
            $launch_file = $launch_file_base_dir."/${dirname}/${translated_flavorname}.jnlp";

            $launch_url = sim_form_launch_url($dirname, $translated_flavorname, SIM_TYPE_JAVA);

            if (file_exists($launch_file_base_dir)) {
                if (file_exists($launch_file)) {
                    $translations[$language_name] = $launch_url;
                }
            }
            else if (url_exists($launch_url)) {
                $translations[$language_name] = $launch_url;
            }

            flush();
        }

        return $translations;
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
            $listings[] = $row;
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

    function sim_form_launch_url($dirname, $flavorname, $sim_type = SIM_TYPE_JAVA) {
        if ($sim_type == SIM_TYPE_FLASH) {
            $link = "http://phet.colorado.edu/sims/$dirname/$flavorname.swf";
        }
        else {
            $link = "http://phet.colorado.edu/sims/$dirname/$flavorname.jnlp";
        }

        return $link;
    }

    function sim_get_launch_url($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];
        $type       = $simulation['sim_type'];

        return sim_form_launch_url($dirname, $flavorname, $type);
    }

    function sim_get_screenshot($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];

        $link = "http://phet.colorado.edu/sims/$dirname/$flavorname-screenshot.png";

        return $link;
    }

    function sim_get_animated_screenshot($simulation) {
        $dirname    = $simulation['sim_dirname'];
        $flavorname = $simulation['sim_flavorname'];

        $link = "http://phet.colorado.edu/sims/$dirname/$flavorname-animated-screenshot.gif";

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

    function sim_get_file_contents($resource) {
        $new_resource = str_replace('http://phet.colorado.edu/sims/', '../../sims/', $resource);

        if (file_exists($new_resource)) return file_get_contents($new_resource);

        return file_get_contents($resource);
    }

    function sim_get_image_previews($type, $is_static_screenshot = true) {
        global $connection;

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

?>