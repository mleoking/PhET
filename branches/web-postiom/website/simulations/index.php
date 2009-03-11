<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once('page_templates/SitePage.php');

class SimulationsPage extends SitePage {

    const SIMS_PER_PAGE = 9;

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        $CatUtils = CategoryUtils::inst();
        $WebUtils = WebUtils::inst();

        if (isset($_REQUEST['cat'])) {
            $cat_encoding = $_REQUEST['cat'];
            $ccategory = $CatUtils->getCategory($cat_encoding);
        }
        else {	
            $ccategory = $CatUtils->getDefaultCategory();
            $cat_encoding = $WebUtils->encodeString($ccategory['cat_name']);
        }

        $cat_id = $ccategory['cat_id'];
        if (!$ccategory) {
            print "<h2>Invalid Category</h2>";
            // FIXME: some type of error here, do not proceed
        }
        else {
            print "<h2>{$ccategory['cat_name']}</h2>";            
        }

        $sim_limit = self::SIMS_PER_PAGE;

        if (isset($_REQUEST['st'])) {
            $sim_start_number = $_REQUEST['st'];

            if ($sim_start_number == -1) {
                $sim_start_number = 0;
                $sim_limit        = 999;
            }
        }
        else {
            $sim_start_number = 0;
            $sim_limit        = 999;
        }

        if (isset($_REQUEST['view_type'])) {
            $view_type = $_REQUEST['view_type'];
        }
        else {
            if (($cat_encoding == "All_Sims") && (!isset($_REQUEST['st']))) {
                $view_type = "index";
            }
            else {
                $view_type = "thumbs";
            }
        }

        // This statement selects for all sims in the category, and orders by the sim sorting name:
        if (($view_type == "index") || ($cat_encoding == "All_Sims")) {
            $simulations = SimFactory::inst()->getSimsByCatId($cat_id, true);
        }
        else {
            $simulations = SimFactory::inst()->getSimsByCatId($cat_id, false);
        }

        $num_sims_in_category = count($simulations);

        if ($view_type == "thumbs") {
            $url = $CatUtils->getCategoryBaseUrl($cat_encoding);
            $url .= '&amp;view_type=index';
            $anchor = $WebUtils->buildAnchorTag($url, 'Index View');

            // THUMBNAIL INDEX
            print "<div id=\"listing_type\">{$anchor}</div>";

            $pages_html = '';

            if ($num_sims_in_category > self::SIMS_PER_PAGE) {
                $current_page = $sim_start_number / self::SIMS_PER_PAGE + 1;

                // Don't bother printing this section unless there are more sims than will fit on one page:
                $pages_html .= "<div id=\"pg\">\n";

                if ($sim_limit == 999) {
                    $anchor = "View All";
                }
                else {
                    $url = $CatUtils->getCategoryBaseUrl($cat_encoding);
                    $url .= '&amp;st=-1';
                    $anchor = $WebUtils->buildAnchorTag($url, 'View All', array('class' => 'pg'));
                }

                $pages_html .= "{$anchor} | ";

                $num_pages = (int)ceil((float)$num_sims_in_category / (float)self::SIMS_PER_PAGE);

                for ($n = 0; $n < $num_pages; $n = $n + 1) {
                    $page_number = $n + 1;

                    $page_sim_start_number = self::SIMS_PER_PAGE * $n;

                    if ($page_number == $current_page && $sim_limit != 999) {
                        $anchor = "$page_number";
                        //$link = "$page_number";
                    }
                    else {
                        $url = $CatUtils->getCategoryBaseUrl($cat_encoding);
                        $url .= '&amp;st='.$page_sim_start_number;
                        $anchor = $WebUtils->buildAnchorTag(
                            $url,
                            "{$page_number}",
                            array('class' => 'pg'));
                    }

                    $pages_html .=  "{$anchor}\n";
                }

                $pages_html .=  "</div>\n";
            }

            print $pages_html;

            //--------------------------------------------------

            print '<div class="full-width"></div>';

            // Setting the style to display: inline fixes an IE6 double-margin bug
            // Source: http://www.positioniseverything.net/explorer/floatIndent.html
            print '<div class="productList" style="display: inline;">';

            $sim_number   = -1;
            $sims_printed = 0;

            foreach ($simulations as $sim) {

                ++$sim_number;
                
                if ($sim_number <  $sim_start_number) continue;
                if ($sim_number >= $sim_start_number + $sim_limit) break;
                
                print "<div class=\"productEntry\">\n";
                
                $link_to_sim = "<a href=\"{$sim->getPageUrl()}\">";
                
                $sim_thumbnail_link = $sim->getThumbnailUrl();
                
                print <<<EOT
                        <a href="{$sim->getPageUrl()}">
                            <img src="$sim_thumbnail_link"
                                 width="130"
                                 alt="Screenshot of {$sim->getName()} Simulation"
                                 title="Clear here to view the {$sim->getName()} simulation"
                             />
                        </a>

EOT;

                print "<p>$link_to_sim{$sim->getName()}</a></p>\n";
                
                // Close product:
                print "</div>\n";

                ++$sims_printed;
            }

            print "</div>"; // Close product list

            if ($sims_printed == 9) {
                print $pages_html;
            }
        }
        else {
            $url = $CatUtils->getCategoryBaseUrl($cat_encoding);
            $url .= '&amp;view_type=thumbs';
            $anchor = WebUtils::inst()->buildAnchorTag($url, 'Thumbnail View');

            print "<div id=\"listing_type\">{$anchor}</div>";

            // ALPHABETICAL INDEX

            print "<div id=\"pg\">\n";

            $last_printed_char = '';

            foreach ($simulations as $sim) {
                $cur_char = $sim->getSortingFirstChar();

                if ($cur_char !== $last_printed_char) {
                    print "<a class=\"pg\" href=\"#$cur_char\">$cur_char</a> ";
                    $last_printed_char = $cur_char;
                }
            }

            print "</div>\n";

            print '<div class="full-width"></div>';

            print "<div class=\"productList\">";

            $last_printed_char = '';

            foreach ($simulations as $sim) {
                $cur_char = $sim->getSortingFirstChar();

                if ($cur_char !== $last_printed_char) {
                    print "<h3 id=\"$cur_char\">$cur_char</h3>\n";

                    $last_printed_char = $cur_char;
                }

                print "<a href=\"{$sim->getPageUrl()}\">{$sim->getName()}</a><br />\n";
            }

            print "</div>";  // Close product list
        }

        print <<<EOT
            <div class="full-width">
                <div class="rage_button_218928">
                    <a href="{$this->prefix}teacher_ideas/browse.php?cat=$cat_encoding">Related Activities &amp; Ideas</a>
                </div>
            </div>

EOT;
    }

}

$page = new SimulationsPage("Simulations", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>