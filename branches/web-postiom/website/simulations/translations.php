<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

    //var_dump("HERE");
require_once("page_templates/SitePage.php");
    //require_once("include/sim-utils.php");

class TranslationsPage extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        print <<<EOT
            <h2>Can't find a translation?</h2>
            <p>
                <a href="{$this->prefix}contribute/translation-utility.php">Create a New Translation!</a>
            </p>

EOT;
        // ' <-- That aprostrophe makes Emacs highlighting back to normal

        /*************************************************************/
        // Translations header, links to sections that contain the localizd sims
        /*************************************************************/

        print <<<EOT
    <hr />

            <h2>All Translations</h2>

EOT;
        
        $sim_translations = sim_get_all_sim_translations();
        $localeUtils = Locale::inst();

        print "<table>\n";
        foreach ($sim_translations as $locale => $sim_list) {
            $locale_info = $localeUtils->getFullInfo($locale);
            if ($locale_info === false) {
                // TODO: log error
                continue;
            }

            $link = "<a href=\"#{$locale}\">{$locale_info['locale_name']}</a>";
            $sim_count = count($sim_list);
            print <<<EOT
                <tr>
                    <td>{$link}</td>
                    <td>
                        <a href="#{$locale}">{$locale_info['language_img']}</a> 
                        <a href="#{$locale}">{$locale_info['country_img']}</a>
                    </td>
                    <td>({$sim_count} translations)</td>
                </tr>

EOT;
        }
        print "</table>\n";

        /*************************************************************/
        // Translations content, sections that contain the localizd sims
        /*************************************************************/
        foreach ($sim_translations as $locale => $sim_list) {
            $locale_info = $localeUtils->getFullInfo($locale);
            if ($locale_info === false) {
                // TODO: log error
                continue;
            }

            print <<<EOT
            <hr />

            <h3 id="{$locale}">{$locale_info['language_img']} {$locale_info['country_img']} - {$locale_info['locale_name']}</h3>
            <table class="localized_sims">

EOT;
            $count = 0;
            $last_sim = count($sim_list);
            foreach ($sim_list as $sim_id) {
                $sim = sim_get_sim_by_id($sim_id);
                $formatted_sim_name = format_for_html($sim['sim_name']);
                $sim_page = sim_get_url_to_sim_page($sim['sim_id']);
                $launch_url = sim_get_launch_url($sim, $locale);
                $launch_link_open = "a href=\"{$launch_url}\" title=\"Click here to launch the {$locale_info['locale_name']} version of {$formatted_sim_name}\"";

                // Flash sims should run in a new window
                $onclick = "";
                if ($sim['sim_type'] == SIM_TYPE_FLASH) {
                    $onclick = 'onclick="javascript:open_limited_window(\''.$launch_url.'\',\'simwindow\'); return false;"';
                }

                $count = $count + 1;
                $last_row = "";
                if ($count == $last_sim) {
                    $last_row .= " last";
                }
                $row_class = ($count % 2) ? "class=\"odd{$last_row}\"" : "class=\"even{$last_row}\"";

                $download_html = '';
                if (!$this->is_installer_builder_rip()) {
                    $download_url = sim_get_download_url($sim, $locale, true);
                    if (empty($download_url)) {
                        $download_html = "<td><em>Download not available</em></td>";
                    }
                    else {
                        $download_html = "<td><a href=\"{$download_url}\" title=\"Click here to download the {$locale_info['locale_name']} version of {$formatted_sim_name}\">Download</a></td>";
                    }
                }

                print <<<EOT
                <tr {$row_class}>
                  <td><{$launch_link_open} {$onclick}>{$formatted_sim_name}</a></td>
                  <td><{$launch_link_open} {$onclick}>Run Now</a></td>
                  {$download_html}
                </tr>

EOT;
            }
            print "</table>\n";
        }

        flush();
    }

}

$page = new TranslationsPage("Translated Sims", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>