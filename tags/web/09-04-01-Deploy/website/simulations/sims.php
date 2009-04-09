<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

require_once("teacher_ideas/browse-utils.php");

class IndividualSimulationPage extends SitePage {

    const MAX_TITLE_CHARS = 80;

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->sim = null;
        if (!isset($_REQUEST['sim'])) {
            $this->set_title("No Simulation");
            return;
        }

        if (isset($_REQUEST['enable_test_sims']) && 
            $_REQUEST['enable_test_sims']) {
            SimFactory::inst()->enableTestSims();
        }

        // If we're here, a sim was specified

        $sim_encoding = $_REQUEST['sim'];

        $this->sim = SimFactory::inst()->getByWebEncodedName($sim_encoding);

        $this->addKeywordsToTitle();

        // TODO: REFACTOR
        // This fun thing needs to be here because it needs to happen
        // before the rendering, because the print_multiple_selection
        // needs to add a JavaScript thingy to the top of the page.
        ob_start();
        print_multiple_selection(
            'Type',
            contribution_get_all_template_level_names(),
            array(),
            true,
            true,
            "ms",
            $this
            );
        $this->multi_selection_hack = ob_get_clean();

        return true;
    }

    function get_sim_java_upgrade_html() {
        return <<<EOT
            <div class="simupgrade">
                <p>
                    <a href="http://www.java.com/"><img src="{$this->prefix}images/javalogo52x88.gif" alt="Java Jump" /></a>
                </p>
                <div>
                    <p>
                        <strong>PhET is upgrading to Java 1.5!</strong>
                    </p>
                    <p>
                        Effective <strong>May 1st, 2009</strong>, to run the Java-based simulations you will need to upgrade to Java version 1.5 or higher.
                        <a href="http://www.java.com/">Upgrade now!</a>
                    </p>
                    <p>
                        <a href="{$this->prefix}tech_support/support-java.php#q4">How do I check my computer's current version of Java?</a>
                    </p>
                </div>
                <div class="clear"></div>
            </div>

EOT;
        // ' <-- That aprostrophe makes Emacs highlighting back to normal
    }

    function addKeywordsToTitle() {
        // Add keywords to title
        $new_title = "PhET {$this->sim->getName()} - ";

        $delimiter = ', ';
        $title_keywords = array();
        $len = strlen($new_title);
        foreach ($this->sim->getKeywords() as $keyword) {
            if (($len + strlen($keyword)) > self::MAX_TITLE_CHARS) {
                break;
            }
            $title_keywords[] = $keyword;
            $len = $len + strlen($delimiter) + strlen($keyword);
        }
        $new_title .= join($delimiter, $title_keywords);

        // Set the new title, don't use a basename
        $this->set_title($new_title, '');
    }

    // TODO: separate out the update and render functions more.  No time now.
    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        // Will need this later
        $simobj = $this->sim->getWrapped();

        if (is_null($this->sim)) {
            print "<p>There is no simulation by the specified id.</p>";

            return;
        }

        $guidance_html = '';
        if ($this->sim->getGuidanceRecommended()) {
            $guidance_html = SimUtils::inst()->getGuidanceImageAnchorTag();
        }

        // TODO: Remove explicit class checking
        $sim_java_upgrade_html = "";
        if ($this->sim->getType() == 'Java') {
            $sim_java_upgrade_html = $this->get_sim_java_upgrade_html();
        }

        // TODO: Remove explicit class checking
        $sim_type_version = array('win', 'osx', 'lin');
        if ($this->sim->getType() == 'Java') {
            $sim_type_version['win'] = JAVA_MIN_VERSION_WIN_FULL;
            $sim_type_version['osx'] = JAVA_MIN_VERSION_OSX_FULL;
            $sim_type_version['lin'] = JAVA_MIN_VERSION_LIN_FULL;
        }
        else if ($this->sim->getType() == 'Flash') {
            $sim_type_version['win'] = FLASH_MIN_VERSION_FULL;
            $sim_type_version['osx'] = FLASH_MIN_VERSION_FULL;
            $sim_type_version['lin'] = FLASH_MIN_VERSION_FULL;
        }


        $version = $this->sim->getVersion();
        $sim_version_html = '';
        if ((strlen($version['major']) > 0) &&
            (strlen($version['minor']) > 0)) {
            $sim_version_html = <<<EOT
                        <span class="version">
                            Version {$version['major']}.{$version['minor']}
                        </span><br />

EOT;
        }

        $WebUtils = WebUtils::inst();
        $SimUtils = SimUtils::inst();

            print <<<EOT
        <div class="container">

            {$sim_java_upgrade_html}

            <div id="simsummary">
                <p class="sim-abstract">{$this->sim->getDescription()}</p>

                <table id="simratings">
                    <tr>
                        <td>{$guidance_html}</td>   <td>&nbsp;</td>     <td>{$SimUtils->getRatingImageAnchorTag($this->sim->getRating())}</td>
                    </tr>
                </table>

                <div id="simtoolbar">
                    <div class="stats">
                        {$sim_version_html}
                        <span class="size">
                            {$this->sim->getSize()} KB
                        </span>

EOT;

                        $url        = urlencode("http://".PHET_DOMAIN_NAME."/".$_SERVER['REQUEST_URI']);
                        $title      = urlencode($simobj->getName()." - Interactive Physics Simulation");

                        $digg_body  = urlencode($simobj->getDescription());
                        $digg_topic = urlencode("general_sciences");
                        $digg_link  = "http://digg.com/submit?phase=2&amp;url={$url}&amp;title={$title}&amp;bodytext={$digg_body}&amp;topic={$digg_topic}";

                        $stumble_link = "http://www.stumbleupon.com/submit?url={$url}&amp;title={$title}";

                        if ($this->is_installer_builder_rip()) {
                            $download_button_slot = '&nbsp;';
                        }
                        else {
                            $download_url = $this->sim->getDownloadUrl();

                            $download_button_slot = '';
                            if (!empty($download_url)) {
                                $download_button_slot = <<<EOT
                                <div class="rage_button_928365">
                                    <a href="{$download_url}" title="Click here to download the simulation to your computer, to run when you do not have an Internet connection">Download</a>
                                </div>

EOT;
                            }
                        }

                        $launch_onclick = $this->sim->getLaunchOnClick();
                        if (!empty($launch_onclick)) {
                            $launch_onclick = ' onclick="'.$launch_onclick.'"';
                        }

                        print <<<EOT
                    </div>
                    <span class="promote" title="If you like this simulation, please consider sharing it with others by submitting it to Digg or StumbleUpon">
                            share sim:

                            <a href="{$digg_link}"><img class="digg" src="{$this->prefix}images/digg-thumb-10x10.gif" alt="Icon for Digg" title="Click here to submit this page to Digg" /></a>

                            <a href="{$stumble_link}"><img class="stumble" src="{$this->prefix}images/stumble.gif" alt="StumbleUpon Toolbar" title="Click here to submit this page to StumbleUpon" /></a>
                    </span>
                </div>

            </div>

            <div id="simpreview">
                <a href="{$this->sim->getLaunchUrl()}"{$launch_onclick}>
                    {$this->sim->getScreenshotImageTag()}
                </a>

                <div id="simrunoptions">
                    <table>
                        <tr>
                            <td>
                            {$download_button_slot}
                            </td>

                            <td>
                                <div class="rage_button_358398">
                                  <a href="{$this->sim->getLaunchUrl()}"{$launch_onclick} title="Click here to run the simulation from your browser">Run Now!</a>
                                </div>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </div>

        <div class="shortcuts">
            <span class="burg"><a href="#topics">Topics</a></span>

            <span class="burg"><a href="#ideas">Teaching Ideas</a></span>

            <span class="burg"><a href="#software">Software Requirements</a></span>

            <span class="burg"><a href="#versions">Translated Versions</a></span>

            <span class="burg"><a href="#credits">Credits</a></span>
        </div>

        <h1 class="indi-sim" id="topics">Topics</h1>

        <h2>Main Topics</h2>
{$WebUtils->buildBulletedList($this->sim->getMainTopics())}
        <h2>Related Topics</h2>

        <ul>
            <li>
{$WebUtils->buildSpanCommaList($this->sim->getKeywordAnchorTags(), array('class' => 'keywordlist'))}
            </li>
        </ul>

        <h2>Sample Learning Goals</h2>
{$WebUtils->buildBulletedList($this->sim->getLearningGoals())}

EOT;

        if ($this->sim->hasTeachersGuide()) {
            $guide_html = <<<EOT
                The {$this->sim->getTeachersGuideAnchorTag()} contains tips for teachers created by the PhET team (PDF).

EOT;
        }
        else {
            $guide_html = "There is no teacher's guide for this simulation.";
        }

        print <<<EOT
        <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="ideas">Teaching Ideas</h1>

        <h2>Tips for Teachers</h2>

        <p class="indi-sim">
            {$guide_html}
        </p>

        <h2>Ideas and Activities for this Sim</h2>

        <div id="simcontribbrowser">

EOT;

            $contributor_id = $this->user["contributor_id"];

            $content_only = true;

        if (isset($_REQUEST['sort_by'])) {
            $sort_by = $_REQUEST['sort_by'];
        }
        else {
            $sort_by = 'contribution_title';
        }

        if (isset($_REQUEST['order'])) {
            $order = strtolower($_REQUEST['order']);
        }
        else {
            $order = 'asc';
        }

        $next_order = 'asc';

        if ($order == 'asc') {
            $next_order = 'desc';
        }

        $Simulations = array($simobj->getName());
        $Types = array( 'all' );
        $Levels = array( 'all' );

        $result = browse_print_content_only($Simulations, $Types, $Levels,
            $sort_by, $order, $next_order, false);
        if (!$result) {
            print "<p>There are no contributions for this simulation.</p>";
        }

        $file_max_size = ini_get("upload_max_filesize");
        $post_max_size = ini_get("post_max_size");

        $os_min_version_win = OS_MIN_VERSION_WIN;
        $os_min_version_osx = OS_MIN_VERSION_OSX;

        print <<<EOT
        </div>

        <h2>Submit Your Ideas &amp; Activities</h2>

        <div class="p-indentation">
        <form id="quicksubmit" enctype="multipart/form-data" action="submit-contribution.php" method="post">
            <div>
                <input type="hidden" name="sim_id"   value="{$this->sim->getId()}" />
            </div>

            <p>Note: The maximum file size is <strong>{$file_max_size}</strong>, with a maximum upload of <strong>{$post_max_size}</strong> at a time.</p>

            <p>Required fields are marked with an asterisk (*).</p>

            <table class="form">
                <tr>
                    <td>
                        title*
                    </td>

                    <td>
                        <input type="text" name="contribution_title" size="40" />
                    </td>
                </tr>

                <tr>
                    <td>
                        file(s)
                    </td>

                    <td>
                        <input type="file" class="multi" name="contribution_file_url" />
                    </td>
                </tr>

                <tr>
                    <td>
                        grade level(s)*
                    </td>

                    <td>
                        <noscript><p style="text-align: right;">JavaScript is OFF, you cannot submit data</p></noscript>
{$this->multi_selection_hack}                    </td>
                </tr>

                <tr>
                    <td colspan="2">
                    <span style="float: left">
                        Check the <a href="{$this->prefix}teacher_ideas/contribution-guidelines.php">PhET design guidelines</a>
                        (<a href="{$this->prefix}teacher_ideas/contribution-guidelines.pdf">PDF</a>)</span>
                    <input type="submit" name="submit" value="Submit" />
                    </td>
                </tr>
            </table>
        </form>
        </div>

        <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="software">Software Requirements</h1>

        <p>{$this->sim->getTypeImageAnchorTag()}</p>

        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>
                            Windows Systems
                        </td>

                        <td>
                            Macintosh Systems
                        </td>

                        <td>
                            Linux Systems
                        </td>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>
                            {$os_min_version_win}<br/>
{$sim_type_version['win']} or later<br/>                        </td>


                        <td>
                            OS {$os_min_version_osx} or later<br/>
{$sim_type_version['osx']} or later<br/>                        </td>


                        <td>
{$sim_type_version['lin']} or later<br/>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="versions">Translated Versions</h1>

EOT;

        $translations = $this->sim->getTranslations();

        if (count($translations) > 0) {
            // Only have a "Download" column if this is not for the installer
            $download_column_html = '';
            if (!$this->is_installer_builder_rip()) {
                $download_column_html = '<td><strong>Download</strong></td>';
            }

            // The table header
            print <<<EOT
            <table>
                <thead>
                    <tr>
                        <td colspan="2" style="text-align: center;"><strong>Run localized</strong></td>
                        {$download_column_html}
                    </tr>
                </thead>
                <tbody>

EOT;

        foreach ($translations as $locale) {
            $locale_info = Locale::inst()->getFullInfo($locale);
            if ($locale_info === false) {
                // TODO: log error
                continue;
            }

            $launch_link_open = "a href=\"{$this->sim->getLaunchUrl($locale)}\" title=\"Click here to launch the {$locale_info['locale_name']} version of {$this->sim->getName()}\"";

            // Download link is provided if it exists and this is not the installer-builder
            $download_html = '';
            if (!$this->is_installer_builder_rip()) {
                $download_url = $this->sim->getDownloadUrl($locale);
                if (empty($download_url)) {
                    $download_html = "<td><em>Not available</em></td>";
                }
                else {
                    $download_html = "<td><a href=\"{$download_url}\" title=\"Click here to download the {$locale_info['locale_name']} version of {$this->sim->getName()}\">Download</a></td>";
                }
            }

            $launch_onclick = $this->sim->getLaunchOnClick($locale);
            if (!empty($launch_onclick)) {
                $launch_onclick = ' onclick="'.$launch_onclick.'"';
            }


            $language_launch = '';
            if (!is_null($locale_info['language_code'])) {
                $language_launch = "<{$launch_link_open}{$launch_onclick}>{$locale_info['language_img']}</a>";
            }

            $country_launch = '';
            if (!is_null($locale_info['country_code'])) {
                $country_launch = "<{$launch_link_open}{$launch_onclick}>{$locale_info['country_img']}</a>";
            }

            print <<<EOT
<tr style="vertical-align: bottom;">
  <td>
    {$language_launch}
    {$country_launch}
  </td>
  <td>
    <{$launch_link_open}{$launch_onclick}>{$locale_info['locale_name']}</a>
  </td>
  {$download_html}
</tr>

EOT;
        }
            // Close the table
            print <<<EOT
                </tbody>
            </table>

EOT;
        }
        else {
            print <<<EOT
                <p class="indi-sim">
                    There are no translations available for this simulation.
                </p>

EOT;
        }



        print <<<EOT

        <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="credits">Credits</h1>

        <div class="compact">
            <table>
                <thead>
                    <tr>
                        <td>
                            Design Team
                        </td>

                        <td>
                            Third-party Libraries
                        </td>

                        <td>
                            Thanks To
                        </td>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>
{$WebUtils->buildBulletedList($this->sim->getDesignTeam())}
                        </td>

                        <td>
{$WebUtils->buildBulletedList($this->sim->getLibraries())}
                        </td>

                        <td>
{$WebUtils->buildBulletedList($this->sim->getThanksTo())}
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

EOT;
    }

    function render_title() {
        if (is_null($this->sim)) {
            parent::render_title();
            return;
        }

        if (isset($this->user) && ($this->user["contributor_is_team_member"] == '1')) {
            print "<h1 class=\"first-child\"><a href=\"{$this->prefix}admin/edit-sim.php?sim_id={$this->sim->getId()}\" title=\"Click here to edit the simulation\">{$this->sim->getName()}</a></h1>";
        }
        else {
            $launch_onclick = $this->sim->getLaunchOnClick();
            if (!empty($launch_onclick)) {
                $launch_onclick = ' onclick="'.$launch_onclick.'"';
            }

            print "<h1 class=\"first-child\"><a href=\"{$this->sim->getLaunchUrl()}\" {$launch_onclick}>{$this->sim->getName()}</a></h1>\n";
        }
    }

}

$page = new IndividualSimulationPage("Simulation", NavBar::NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>