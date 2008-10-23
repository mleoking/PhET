<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");
include_once(SITE_ROOT."teacher_ideas/browse-utils.php");

class IndividualSimulationPage extends SitePage {

    const MAX_TITLE_CHARS = 80;

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->simulation = null;
        if (!isset($_REQUEST['sim'])) {
            $this->set_title("No Simulation");
            return;
        }

        // If we're here, a sim was specified
        global $SIM_RATING_TO_IMAGE_HTML, $SIM_TYPE_TO_IMAGE_HTML;

        $sim_encoding = $_REQUEST['sim'];

        $this->sim_id = sim_get_sim_id_by_sim_encoding($sim_encoding);

        $this->simulation = sim_get_sim_by_id($this->sim_id);

        $sim_rating = $this->simulation["sim_rating"];
        $this->sim_type = $this->simulation["sim_type"];
        $sim_name = format_string_for_html($this->simulation["sim_name"]);
        $sim_crutch = $this->simulation["sim_crutch"];
        $this->sim_crutch_html = '';

        if ($sim_crutch) {
            $this->sim_crutch_html = SIM_CRUTCH_IMAGE_HTML;
        }

        $this->sim_rating_html = $SIM_RATING_TO_IMAGE_HTML["$sim_rating"];
        $this->sim_type_html   = $SIM_TYPE_TO_IMAGE_HTML[$this->sim_type];
        $this->sim_launch_url  = sim_get_launch_url($this->simulation);
        $this->sim_image_url   = sim_get_screenshot($this->simulation);
        $this->sim_version     = sim_get_version($this->simulation);
        if ($this->sim_version === false) {
            $this->sim_version = "N/A";
        }

        // Temp change while PhET team decides how to handle ratings; for now just
        // include under construction & classroom tested:
        if ($sim_rating != SIM_RATING_CHECK && $sim_rating != SIM_RATING_ALPHA) {
            $this->sim_rating_html = "";
        }

        if ($this->sim_type == SIM_TYPE_FLASH) {
            // "Run Now!" Flash sims will launch in an new window

            if (strstr($this->sim_launch_url, "html")) {
                // New style of launching, needs no gen-flash-page
                $gen_flash_page = $this->sim_launch_url;
            }
            else {
                // Old style of launching
                $gen_flash_page = "{$this->prefix}admin/gen-flash-page.php?flash={$this->sim_launch_url}&amp;title={$sim_name}&amp;lang=en";
            }

            // Make sim run in new window
            $this->on_click_html = 'onclick="javascript:open_limited_window(\''.$gen_flash_page.'\',\'simwindow\'); return false;"';
        }
        else {
            $this->on_click_html = '';
        }

        $this->sim_run_offline_link = sim_get_run_offline_link($this->simulation);

        ob_start();
        $this->print_content();
        $this->add_content(ob_get_clean());
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
                        Effective <strong>September 1st, 2008</strong>, to run the Java-based simulations you will need to upgrade to Java version 1.5 or higher.
                        <a href="http://www.java.com/">Upgrade now!</a>
                    </p>
                    <p>
                        <a href="{$this->prefix}tech_support/support-java.php#q4">How do I check my computer's current version of Java?</a>
                    </p>
                </div>
                <div class="clear"></div>
            </div>

EOT;
    }
    // TODO: separate out the update and render functions more.  No time now.
    function print_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (is_null($this->simulation)) {
            print "<p>There is no simulation by the specified id.</p>";

            return;
        }

        global $SIM_RATING_TO_IMAGE_HTML, $SIM_TYPE_TO_IMAGE_HTML;

        /*
        // Don't require authentication, but do it if the cookies are available:
        do_authentication(false);

        if (!isset($_REQUEST['sim'])) {
            print "<h2>No Simulation</h2><p>There is no simulation by the specified id.</p>";

            return;
        }
        */
/*
        $sim_encoding = $_REQUEST['sim'];

        $sim_id = sim_get_sim_id_by_sim_encoding($sim_encoding);

        $simulation = sim_get_sim_by_id($sim_id);
*/
        // Removing unsafe function 'get_code_to_create_variables_from_array',
        // just doing the equivalent by hand
        //eval(get_code_to_create_variables_from_array($this->simulation));
        $sim_id = $this->simulation["sim_id"];
        $sim_name = $this->simulation["sim_name"];
        $sim_dirname = $this->simulation["sim_dirname"];
        $sim_flavorname = $this->simulation["sim_flavorname"];
        $sim_rating = $this->simulation["sim_rating"];
        $sim_no_mac = $this->simulation["sim_no_mac"];
        $sim_crutch = $this->simulation["sim_crutch"];
        $sim_type = $this->simulation["sim_type"];
        $sim_size = $this->simulation["sim_size"];
        $sim_launch_url = $this->simulation["sim_launch_url"];
        $sim_image_url = $this->simulation["sim_image_url"];
        $sim_desc = $this->simulation["sim_desc"];
        $sim_keywords = $this->simulation["sim_keywords"];
        $sim_system_req = $this->simulation["sim_system_req"];
        $sim_teachers_guide_id = $this->simulation["sim_teachers_guide_id"];
        $sim_main_topics = $this->simulation["sim_main_topics"];
        $sim_design_team = $this->simulation["sim_design_team"];
        $sim_libraries = $this->simulation["sim_libraries"];
        $sim_thanks_to = $this->simulation["sim_thanks_to"];
        $formatted_sim_sample_goals = format_string_for_html($this->simulation["sim_sample_goals"]);
        $sim_sorting_name = $this->simulation["sim_sorting_name"];
        $sim_animated_image_url = $this->simulation["sim_animated_image_url"];
        $sim_is_real = $this->simulation["sim_is_real"];

        $formatted_sim_name = format_string_for_html($sim_name);
        $formatted_sim_desc = format_string_for_html($sim_desc);

        // Add keywords to title
        $new_title = "PhET {$formatted_sim_name} - ";
        $keywords_array = explode(",", $sim_keywords);

        $comma_first_time = true;
        foreach ($keywords_array as $keyword) {
            if ((strlen($new_title) + strlen($keyword)) > self::MAX_TITLE_CHARS) {
                // Adding this word would make title too long, we're done
                break;
            }

            if ($comma_first_time) {
                // Don't need a comma the first time
                $comma_first_time = false;
            }
            else {
                $new_title .= ",";
            }

            $new_title .= $keyword;
        }

        // Set the new title, don't use a basename
        $this->set_title($new_title, '');

        /*
        // Gather sim_rating_html & sim_type_html information:
        $sim_rating_html = $SIM_RATING_TO_IMAGE_HTML["$sim_rating"];
        $sim_type_html   = $SIM_TYPE_TO_IMAGE_HTML[$sim_type];
        $sim_launch_url  = sim_get_launch_url($simulation);
        $sim_image_url   = sim_get_screenshot($simulation);
        */
        // Temp change while PhET team decides how to handle ratings; for now just
        // include under construction & classroom tested:
/*
        if ($sim_rating != SIM_RATING_CHECK && $sim_rating != SIM_RATING_ALPHA) {
            $sim_rating_html = "";
        }

        if ($sim_type == SIM_TYPE_FLASH) {
            $gen_flash_page = "{$this->prefix}admin/gen-flash-page.php?flash=$sim_launch_url&amp;title=$sim_name";

            $on_click_html = 'onclick="javascript:open_limited_window(\''.$gen_flash_page.'\',\'simwindow\'); return false;"';
        }
        else {
            $on_click_html = '';
        }
*/

        $sim_java_upgrade_html = "";
        if ($this->sim_type == 0) {
            $sim_java_upgrade_html = $this->get_sim_java_upgrade_html();
        }

            print <<<EOT
        <div class="container">

            {$sim_java_upgrade_html}

            <div id="simsummary">
                <p class="sim-abstract">{$formatted_sim_desc}</p>

                <table id="simratings">
                    <tr>
                        <td>{$this->sim_crutch_html}</td>   <td>&nbsp;</td>     <td>{$this->sim_rating_html}</td>
                    </tr>
                </table>

                <div id="simtoolbar">
                    <div class="stats">
                    <!--
                        <span class="version">
                            Version {$this->sim_version}
                        </span><br />
                    -->
                        <span class="size">
                            {$sim_size} KB
                        </span>

EOT;

                        $slashed_sim_name = addslashes($sim_name);
                        $slashed_sim_desc = addslashes($sim_desc);

                        $url        = urlencode("http://".PHET_DOMAIN_NAME."/".$_SERVER['REQUEST_URI']);
                        $title      = urlencode(html_entity_decode($sim_name)." - Interactive Physics Simulation");

                        $digg_body  = urlencode(html_entity_decode($sim_desc));
                        $digg_topic = urlencode("general_sciences");
                        $digg_link  = "http://digg.com/submit?phase=2&amp;url={$url}&amp;title={$title}&amp;bodytext={$digg_body}&amp;topic={$digg_topic}";

                        $stumble_link = "http://www.stumbleupon.com/submit?url={$url}&amp;title={$title}";

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
                <a href="{$this->sim_launch_url}" {$this->on_click_html}>
                    <img src="{$this->sim_image_url}" alt="Sim preview image" title="Click here to launch the simulation from your browser" width="300" height="225"/>
                </a>

                <div id="simrunoptions">
                    <table>
                        <tr>
                            <td>
                                <div class="rage_button_928365">
                                    <a href="{$this->sim_run_offline_link}" title="Click here to download the simulation to your computer, to run when you do not have an Internet connection">Run Offline</a>
                                </div>
                            </td>

                            <td>
                                <div class="rage_button_358398">
                                    <a href="{$this->sim_launch_url}" {$this->on_click_html} title="Click here to run the simulation from your browser">Run Now!</a>
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

EOT;

        print_comma_list_as_bulleted_list($sim_main_topics);

        print <<<EOT

        <h2>Related Topics</h2>

        <ul>
            <li>

EOT;
                print convert_comma_list_into_linked_keyword_list($sim_keywords, true);

                print <<<EOT
            </li>
        </ul>

        <h2>Sample Learning Goals</h2>

EOT;

        print_comma_list_as_bulleted_list($formatted_sim_sample_goals);

        $teachers_guide = sim_get_teachers_guide($sim_teachers_guide_id);

        if ($teachers_guide) {
            $guide_html = <<<EOT
                The <a href="{$this->prefix}admin/get-teachers-guide.php?teachers_guide_id={$teachers_guide["teachers_guide_id"]}">teacher's guide</a> contains tips for teachers created by the PhET team (PDF).

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

        $sim_id = $this->simulation["sim_id"];
        $sim = sim_get_sim_by_id($sim_id);
        $Simulations = array( html_entity_decode($sim['sim_name']) );
        $Types = array( 'all' );
        $Levels = array( 'all' );

        $result = browse_print_content_only($Simulations, $Types, $Levels, 
            $sort_by, $order, $next_order, false, get_referrer());
        if (!$result) {
            print "<p>There are no contributions for this simulation.</p>";
        }

        $file_max_size = ini_get("upload_max_filesize");
        $post_max_size = ini_get("post_max_size");

        print <<<EOT
        </div>

        <h2>Submit Your Ideas &amp; Activities</h2>

        <div class="p-indentation">
        <form id="quicksubmit" enctype="multipart/form-data" action="submit-contribution.php" method="post">
            <div>
                <input type="hidden" name="sim_id"   value="{$sim_id}" />
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

EOT;

                            print_multiple_selection(
                                'Type',
                                contribution_get_all_template_level_names(),
                                array(),
                                true,
                                true,
                                "ms",
                                $this
                            );

                print <<<EOT
                    </td>
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

        <p>{$this->sim_type_html}</p>

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
                            Microsoft Windows 98SE/2000/XP/Vista<br/>

EOT;

                                if ($sim_type == '0') {
                                    print JAVA_MIN_VERSION_WIN_FULL." or later<br/>";
                                }
                                else if ($sim_type == '1') {
                                    print FLASH_MIN_VERSION_FULL." or later<br/>";
                                }

                            $os_min_version_osx = OS_MIN_VERSION_OSX;
                            print <<<EOT
                        </td>


                        <td>
                            OS {$os_min_version_osx} or later<br/>

EOT;

                            if ($sim_type == '0') {
                                print JAVA_MIN_VERSION_OSX_FULL." or later<br/>";
                            }
                            else if ($sim_type == '1') {
                                print FLASH_MIN_VERSION_FULL." or later<br/>";
                            }

                            print <<<EOT
                        </td>


                        <td>

EOT;

                            if ($sim_type == '0') {
                                print JAVA_MIN_VERSION_LIN_FULL." or later<br/>";
                            }
                            else if ($sim_type == '1') {
                                print FLASH_MIN_VERSION_FULL." or later<br/>";
                            }

                            print <<<EOT

                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

        <p><a href="#top"><img src="{$this->prefix}images/top.gif" alt="Go to Top Image"/></a></p>

        <h1 class="indi-sim" id="versions">Translated Versions</h1>

EOT;

        $translations = sim_get_translations($this->simulation);

        if (count($translations) > 0) {
            if ($this->sim_type == SIM_TYPE_FLASH) {
                // Do a fancy header for Flash sims, because there are 2 choices:
                //     Run it now immediatley (by clicking on the language icon)
                //     Download and run it later
                // Java may someday have this ability (it only has the first)

                print <<<EOT
            <table>
                <thead>
                    <tr>
                        <td colspan="2" style="text-align: center;"><strong>Language</strong></td>
                        <td><strong>Download</strong></td>
                    </tr>
                </thead>
                <tbody>

EOT;
            }
            else {
                // Java, no fancy header
                print <<<EOT
            <table>
                <tbody>

EOT;
            }

            foreach ($translations as $language => $data) {
                $language_code = $data["code"];
                $launch_url = $data["url"];

                $language_icon_url = sim_get_language_icon_url_from_language_name($language);

                // Flash sims should run in a new window
                $onclick = "";
                $flash_download_html = "";
                if ($this->sim_type == SIM_TYPE_FLASH) {
                    $onclick = 'onclick="javascript:open_limited_window(\''.$launch_url.'\',\'simwindow\'); return false;"';

                    // Here is the special download flash version option
                    $flash_download_html = <<<EOT
                <td>
                    <a href="{$this->sim_run_offline_link}&amp;lang={$language_code}" title="Click here to download the {$language} version of {$formatted_sim_name}">Download {$language} version to run offline</a>
                </td>

EOT;
                }

                print <<<EOT
            <tr>
                <td>
                    <a href="{$launch_url}" {$onclick} title="Click here to launch the {$language} version of {$formatted_sim_name}"><img class="image-text" src="{$language_icon_url}" alt="{$language}"/></a>
                </td>
                <td>
                    <a href="{$launch_url}" {$onclick} title="Click here to launch the {$language} version of {$formatted_sim_name}">{$language}</a>
                </td>
                {$flash_download_html}
            </tr>

EOT;
            }

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
                            Libraries
                        </td>

                        <td>
                            Thanks To
                        </td>
                    </tr>
                </thead>

                <tbody>
                    <tr>
                        <td>

EOT;
                            print_comma_list_as_bulleted_list($sim_design_team);

                            print <<<EOT
                        </td>

                        <td>

EOT;
                            print_comma_list_as_bulleted_list($sim_libraries);

                            print <<<EOT
                        </td>

                        <td>

EOT;
                            print_comma_list_as_bulleted_list($sim_thanks_to);

                            print <<<EOT
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>

EOT;
    }

    function render_title() {
        if (is_null($this->simulation)) {
            parent::render_title();
            return;
        }

        $sim_id = $this->simulation["sim_id"];
        $sim_name = format_string_for_html($this->simulation["sim_name"]);
        if (isset($this->user) && ($this->user["contributor_is_team_member"] == '1')) {
            print "<h1 class=\"first-child\"><a href=\"{$this->prefix}admin/edit-sim.php?sim_id={$sim_id}\" title=\"Click here to edit the simulation\">{$sim_name}</a></h1>";
        }
        else {
            print "<h1 class=\"first-child\"><a href=\"{$this->sim_launch_url}\" {$this->on_click_html}>{$sim_name}</a></h1>";
        }
    }

}

$page = new IndividualSimulationPage("Simulation", NAV_SIMULATIONS, null);
$page->update();
$page->render();

?>