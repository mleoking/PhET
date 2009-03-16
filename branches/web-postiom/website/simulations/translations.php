<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class TranslationsPage extends SitePage {

    private function build_locale_to_sim_table($simulations) {
        $translations = array();
        foreach ($simulations as $sim) {
            $sim_trans = $sim->getTranslations();
            foreach ($sim_trans as $locale) {
                if (!array_key_exists($locale, $translations)) {
                    $translations[$locale] = array();
                }

                $translations[$locale][] = $sim;
            }
        }

        return $translations;
    }

    private function sort_translations_inplace(&$translations) {
        uksort($translations, array(Locale::inst(), 'sortCodeByNameCmp'));
    }

    private function get_all_translations() {
        $simulations = SimFactory::inst()->getAllSims(true);
        $translations = $this->build_locale_to_sim_table($simulations);
        $this->sort_translations_inplace($translations);
        return $translations;
    }

    private function render_translations_toc($sim_translations) {
        print <<<EOT

            <h2>All Translations</h2>

EOT;
        
        $localeUtils = Locale::inst();

        print "<table>\n";
        foreach ($sim_translations as $locale => $sim_list) {
            $locale_info = $localeUtils->getFullInfo($locale);
            if ($locale_info === false) {
                // TODO: log error
                continue;
            }

            $link = "<a href=\"#{$locale}\">{$locale_info['locale_name']}</a>";
            //$link = WebUtils::inst()->buildAnchorTag("#{$locale}", $locale_info['locale_name']);
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
    }

    private function render_locale_translated_sims($locale, $sim_list) {
        $localeUtils = Locale::inst();
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
            foreach ($sim_list as $sim) {
                $launch_anchor_attributes = array(
                    'title' => "Click here to launch the {$locale_info['locale_name']} version of {$sim->getName()}"
                    );

                $onclick = $sim->getLaunchOnClick();
                if (!empty($onclick)) {
                    $launch_anchor_attributes['onclick'] = $sim->getLaunchOnClick($locale);
                }

                $lang_launch_anchor_tag = WebUtils::inst()->buildAnchorTag(
                    $sim->getLaunchUrl($locale),
                    $sim->getName(),
                    $launch_anchor_attributes
                    );
                $launch_anchor_tag = WebUtils::inst()->buildAnchorTag(
                    $sim->getLaunchUrl($locale),
                    'Run Now',
                    $launch_anchor_attributes
                    );

                $count = $count + 1;
                $last_row = "";
                if ($count == $last_sim) {
                    $last_row .= " last";
                }
                $row_class = ($count % 2) ? "class=\"odd{$last_row}\"" : "class=\"even{$last_row}\"";

                $download_html = '';
                if (!$this->is_installer_builder_rip()) {
                    $download_url = $sim->getDownloadUrl($locale);
                    if (empty($download_url)) {
                        $download_html = '<td><em>Download not available</em></td>';
                    }
                    else {
                        $download_html = "<td><a href=\"{$download_url}\" title=\"Click here to download the {$locale_info['locale_name']} version of {$sim->getName()}\">Download</a></td>";
                    }
                }

                print <<<EOT
                <tr {$row_class}>
                  <td>{$lang_launch_anchor_tag}</td>
                  <td>{$launch_anchor_tag}</td>
                  {$download_html}
                </tr>

EOT;
            }
            print "</table>\n";
    }

    public function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }
    }

    public function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $translations = $this->get_all_translations();

        print <<<EOT
            <h2>Can't find a translation?</h2>
            <p>
                <a href="{$this->prefix}contribute/translation-utility.php">Create a New Translation!</a>
            </p>

            <hr />

EOT;
        // ' <-- That aprostrophe makes Emacs highlighting back to normal

        // Translations header, links to sections that contain the localizd sims
        $this->render_translations_toc($translations);

        // Translations content, sections that contain the localizd sims
        foreach ($translations as $locale => $sim_list) {
            $this->render_locale_translated_sims($locale, $sim_list);
        }

        flush();
    }

}

$page = new TranslationsPage("Translated Sims", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>