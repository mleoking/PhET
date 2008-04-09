<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class TranslationsPage extends SitePage {

    function update() {
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
                <a href="../contribute/translate.php">Create a New Translation!</a>
            </p>

EOT;

        $sim_to_translations = sim_get_all_translated_language_names();

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
                            <img src="../images/electron-small.png" alt="Image of Electron" />
                        </a>

EOT;

                    flush();
                }
                else {
                    print "<a class=\"translate-prompt\" title=\"Click to translate this sim into $language_name!\" href=\"../contribute/translate.php\"></a>\n";
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

        flush();
    }
}

$page = new TranslationsPage("Translated Sims", NAV_SIMUALTIONS, null);
$page->update();
$page->render();

?>