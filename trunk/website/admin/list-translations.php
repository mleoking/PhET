<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("page_templates/SitePage.php");

class ListTranslationsPage extends SitePage {

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $sim_to_translations = sim_get_all_translated_language_names();

         $languages = array();

         foreach ($sim_to_translations as $sim_name => $map) {
             foreach ($map as $language_name => $launch_url) {
                 $languages["$language_name"] = "$language_name";
             }
         }

        $columns = count($languages) + 1;

//        print <<<EOT
//            <div id="translated-versions">
//            <table>
//                <thead>
//                    <tr>
//                        <td class="even"></td>
//EOT;


//        flush();

        $col_count = 1;

        $row_count = 0;

        foreach (sim_get_all_sim_names() as $sim_name) {
            if (!isset($sim_to_translations[$sim_name])) {
                $map = array();
            }
            else {
                $map = $sim_to_translations[$sim_name];
            }

            $row_is_even = ($row_count % 2) == 0;

            $row_class = $row_is_even ? "even" : "odd";

//            print "<tr class=\"$row_class\">";

//            flush();

            $sim_page_link = sim_get_url_to_sim_page_by_sim_name($sim_name);

//            print "<td class=\"even\"><a href=\"$sim_page_link#versions\">$sim_name</a></td>";

            flush();

            $col_count = 1;

            foreach ($languages as $language_name) {
                $col_is_even = ($col_count % 2) == 0;

                $col_class = $col_is_even ? "even" : "odd";

//                print "<td title=\"$language_name\" class=\"$col_class\">";

                if (isset($map[$language_name])) {
                    $launch_url = $map[$language_name];
                    print "$launch_url";
                    print "";
                    print "<br />";
//                    print <<<EOT
//                        <a href="$launch_url"/>
//EOT;
//                    print "<br/>";

//                    flush();
                }
                else {
                    print "";
                }

//                print "</td>";

                $col_count++;

                flush();
            }

//            print "</tr>";

            flush();

            $row_count++;
        }

//        print "</tbody>";
//        print "</table>";
//        print "</div>";
        foreach ($languages as $language) {
            $url = sim_get_language_icon_url_from_language_name($language);

//            print "<h1 id=\"$language\"><img src=\"$url\" alt=\"\" title=\"$language\"/></h1>";

//            print "<ul>";

            foreach ($sim_to_translations as $sim_name => $translation) {
                foreach ($translation as $cur_language => $launch_url) {
                    if ($cur_language == $language) {
//                        print "<li><a href=\"$launch_url\">$sim_name</a></li>";
                    }
                }
            }

            //print "</ul>";
        }

        flush();
    }

}

$page = new ListTranslationsPage("List Translations", NAV_SIMULATIONS, null, AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>