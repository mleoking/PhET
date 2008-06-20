<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."admin/sim-utils.php");

class TranslationsPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        // Workaround for an IE6 bug rendering this page.
        // See the css file and BasePage.php for more
        // explaination.
        $this->set_css_container_name("container-wide");
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <p>
                <a href="{$this->prefix}contribute/translation-utility.php">Create a New Translation!</a>
            </p>

EOT;

        $sim_to_translations = sim_get_all_translated_language_names();

        // TODO: make own function to reshuffle data
        $sim_lang_map = array();
        foreach ($sim_to_translations as $sim_name => $map) {
            foreach ($map as $language_name => $language_launch_url) {
                if (!array_key_exists($language_name, $sim_lang_map)) {
                    $sim_lang_map[$language_name] = array();
                }

                $sim_lang_map[$language_name][$sim_name] = $language_launch_url;
            }
        }

        function mycountcmp($a, $b) {
            return count($a) < count($b);
        }

        // ON HOLD: Google uses a cached version of the webpage to translate, and it doesn't have all the translations we do
        global $LANGUAGE_CODE_TO_LANGUAGE_NAME;
        $reverse_lanugage_lookup = array_flip($LANGUAGE_CODE_TO_LANGUAGE_NAME);
        function mk_google_translate_link($page, $to, $from = 'en') {
            //mk_google_translate_link("/new/index.php", 'de');
            // http://translate.google.com/translate?u=http%3A%2F%2Fphet.colorado.edu%2Fnew%2Findex.php&hl=en&ie=UTF8&sl=en&tl=de
            $link = "http://translate.google.com/translate?u=";
            $link .= urlencode("http://".PHET_DOMAIN_NAME.$page);
            $link .= "&amp;hl=en";
            $link .= "&amp;ie=UTF8";
            $link .= "&amp;sl={$from}";
            $link .= "&amp;tl={$to}";
            return $link;
            //var_dump($link);
            //var_dump("http://translate.google.com/translate?u=http%3A%2F%2Fphet2.colorado.edu%2Fnew%2Findex.php&amp;hl=en&amp;ie=UTF8&amp;sl=en&amp;tl=de");
        }
        function mk_google_translate_link2($page, $to, $from = 'en') {
            // http://209.85.171.104/translate_c?hl=en&sl=en&tl=pt&u=http://phet2.colorado.edu/simulations/translations.php#Portuguese
            //$link = "http://translate.google.com/translate?";
            $link = "http://209.85.171.104/translate_c?";
            $link .= "hl=en";
            $link .= "&amp;sl={$from}";
            $link .= "&amp;tl={$to}";
            $link .= "&amp;u=http://".PHET_DOMAIN_NAME.$page;// urlencode("http://".PHET_DOMAIN_NAME.$page);
            return $link;
        }

        uasort($sim_lang_map, "mycountcmp");
        print "<ul>\n";
        foreach ($sim_lang_map as $language_name => $sim_info) {
            //$google_translation_link = mk_google_translate_link2("/simulations/translations.php#{$language_name}", $reverse_lanugage_lookup[$language_name]);
            //print "<li><a href=\"#{$language_name}\">{$language_name}</a> - (".count($sim_info)." translations) - <a href=\"{$google_translation_link}\">Translation in Google</a></li>\n";
            print "<li><a href=\"#{$language_name}\">{$language_name}</a> - (".count($sim_info)." translations)</li>\n";
        }
        print "</ul>\n";

        foreach ($sim_lang_map as $language_name => $sim_info) {
            $language_icon_src = sim_get_language_icon_url_from_language_name($language_name);

            print "<h1 id=\"{$language_name}\"><img src=\"{$language_icon_src}\" alt=\"\" title=\"{$language_name}\"/></h1>\n";
            print "<ul>\n";
            foreach ($sim_info as $sim_name => $sim_url) {
                $formatted_sim_name = format_string_for_html($sim_name);
                print "<li><a href=\"{$sim_url}\">$formatted_sim_name</a></li>\n";
            }
            print "</ul>\n";
        }

    if(false) {
        $languages = array();

        foreach ($sim_to_translations as $sim_name => $map) {
            foreach ($map as $language_name => $launch_url) {
                $languages["$language_name"] = "$language_name";
            }
        }

        $columns = count($languages) + 1;

        print <<<EOT
            <div id="translated-versions">
            <table>
                <thead>
                    <tr>
                        <td class="even"></td>

EOT;


        flush();

        $col_count = 1;

        foreach ($languages as $language) {
            $col_is_even = ($col_count % 2) == 0;

            $col_class = $col_is_even ? "even" : "odd";

            $url = sim_get_language_icon_url_from_language_name($language, true);

            print "<td class=\"$col_class\"><a href=\"#$language\"><img src=\"$url\" alt=\"\" title=\"$language\"/></a></td>\n";

            $col_count++;
        }

        print "</tr>\n";
        print "</thead>\n";

        print "<tbody>\n";

        flush();

        $row_count = 0;

        foreach (sim_get_all_sim_names(true) as $sim_name) {
            if (!isset($sim_to_translations[$sim_name])) {
                $map = array();
            }
            else {
                $map = $sim_to_translations[$sim_name];
            }

            $row_is_even = ($row_count % 2) == 0;

            $row_class = $row_is_even ? "even" : "odd";

            print "<tr class=\"$row_class\">\n";

            flush();

            $sim_page_link = sim_get_url_to_sim_page_by_sim_name($sim_name);

            $formatted_sim_name = format_string_for_html($sim_name);
            print "<td class=\"even\"><a href=\"$sim_page_link#versions\">$formatted_sim_name</a></td>\n";

            flush();

            $col_count = 1;

            foreach ($languages as $language_name) {
                $col_is_even = ($col_count % 2) == 0;

                $col_class = $col_is_even ? "even" : "odd";

                print "<td title=\"$language_name\" class=\"$col_class\">\n";

                if (isset($map[$language_name])) {
                    $launch_url = $map[$language_name];

                    print <<<EOT
                        <a href="$launch_url">
                            <img src="{$this->prefix}images/electron-small.png" alt="Image of Electron" />
                        </a>

EOT;

                    flush();
                }
                else {
                    print "<a class=\"translate-prompt\" title=\"Click to translate this sim into $language_name!\" href=\"{$this->prefix}contribute/translation-utility.php\"></a>\n";
                }

                print "</td>\n";

                $col_count++;
                flush();
            }

            print "</tr>\n";

            flush();

            $row_count++;
        }

        print "</tbody>\n";
        print "</table>\n";
        print "</div>\n";

        foreach ($languages as $language) {
            $url = sim_get_language_icon_url_from_language_name($language);

            print "<h1 id=\"$language\"><img src=\"$url\" alt=\"\" title=\"$language\"/></h1>\n";

            print "<ul>\n";

            foreach ($sim_to_translations as $sim_name => $translation) {
                foreach ($translation as $cur_language => $launch_url) {
                    if ($cur_language == $language) {
                       $formatted_sim_name = format_string_for_html($sim_name);
                       print "<li><a href=\"$launch_url\">$formatted_sim_name</a></li>\n";
                    }
                }
            }

            print "</ul>\n";
        }
    }
        flush();
    }

}

$page = new TranslationsPage("Translated Sims", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>