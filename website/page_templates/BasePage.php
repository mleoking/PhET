<?php

if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");
include_once(SITE_ROOT."admin/global.php");
include_once(SITE_ROOT."teacher_ideas/referrer.php");

define("WEBSITE_BASE_TITLE", "PhET :: Physics Education Technology at CU Boulder");

class BasePage {

    // Page content
    protected $content;

    // Variable for any debugging content
    private $debug_content;

    // Title and title sections of the page
    protected $base_title;
    protected $page_title;
    protected $render_title;
    protected $full_title;

    // Stylesheets
    private $stylesteets;

    // JavaScript files
    private $javascript_files;

    // Array containing JavaScript code to run once page is loaded
    private $javascript_ready;

    // Array containing header key=> value pairs, ex: Content-Type: text/html
    private $headers;

    // Favicon file
    private $favicon;

    // Special cases, if redirection is needed
    protected $header_redirect_location;

    // Used for a meta refresh, if set
    protected $meta_refresh_location;
    protected $meta_refresh_timeout;

    // Name of the main container for the content
    // This is a workaround for an IE6 bug and translations.php
    // rendering incorrectly.  Would be better to fix with different
    // css files for each browser, but his solution is the easiest
    // at this point.  See the css file for a more detailed
    // description.
    private $css_container_name;

    protected $prefix;

    /**
     * BasePage constructor
     *
     * @param $page_title string[optional] specific title of this page
     * @param $base_title string[optional] base tile of the website
     */
    function __construct($page_title = "",
                         $referrer = null,
                         $base_title = WEBSITE_BASE_TITLE) {
        $this->set_title($page_title, $base_title);

        // Setup the content
        $this->content = array();

        // Setup stylesheets
        $this->stylesheets = array();

        // Setup java scripts
        // NOTE: order is important
        $this->javascript_files = array();

        $this->javascript_ready = array();

        // Setup favicon
        $this->favicon = '';

        // HTML headers, $key = $value pairs
        $this->headers = array();

        // Setup the debug content
        $this->debug_content = array();

        // Setup with NO redirection
        $this->redirect_location = NULL;

        // Setup with NO meta refreshing
        $this->meta_refresh_location = NULL;
        $this->meta_refresh_timeout = NULL;

        // Keep track of the referring page
        $this->referrer = null;
        if (!is_null($referrer) && (0 != strcmp("", $referrer))) {
            $this->referrer = $referrer;
        }

        // Prefix to the base directory
        $this->set_prefix();

        $this->css_container_name = "container";
    }

    /**
     * Set the title of this page
     *
     * @param $page_title string[optional] specific title of this page
     * @param $base_title string[optional] base tile of the website
     */
    function set_title($page_title, $base_title = WEBSITE_BASE_TITLE) {
        // Setup the page's full title
        $this->base_title = $base_title;
        $this->page_title = $page_title;

        $this->render_title = $this->page_title;
        $full_title = $this->page_title;

        if ((strcmp("", $this->page_title)) &&
            (strcmp("", $this->base_title))) {
            $full_title = $full_title." - ";
        }

        $full_title = $full_title.$this->base_title;
        $this->full_title = $full_title;
    }

    /**
     * Set the relative path prefix to the root directory, should not begin or end with slashes
     *
     * @param $prefix string[optional] relative path prefix to the root directory
     */
    function set_prefix($prefix = SITE_ROOT) {
        $this->prefix = $prefix;
    }

    /**
     * Set the css id name of the div container containing all the page 
     *
     * @param $new_container_name string name of the new container
     */
    function set_css_container_name($new_container_name) {
        $this->css_container_name = $new_container_name;
    }

    /**
     * Uses a header redirect, ** MUST be called before any data is sent **
     *
     * @param $location string page to redirect to
     */
    function header_redirect($location) {
        $this->title = 'Redirect - '.$this->base_title;
        $this->header_redirect_location = $location;
        header("Location: ".$location);
    }

    /**
     * Does a meta redirect
     *
     * @param $location string page to redirect to
     * @param $timeout int[optional] delay time in seconds, default == 0
     */
    function meta_refresh($location, $timeout = 0) {
        $this->meta_refresh_location = $location;
        $this->meta_refresh_timeout = $timeout;
    }

    //
    // Page management functions
    //

    /**
     * Add an extra stylesheet(s)
     *
     * @param $stylesheet mixed string or sting array containing extra stylesheets
     */
    function add_stylesheet($stylesheet) {
        if (is_array($stylesheet)) {
            $this->stylesheets = $this->stylesheet + $stylesheet;
        }
        else {
            $this->stylesheets[] = $stylesheet;
        }
    }

    /**
     * Add a javascript file to this page
     *
     * @param $javascript_file mixed string or sting array containing extra JavaScript file(s)
     */
    function add_javascript_file($javascript_file) {
        if (is_array($javascript_file)) {
            $this->javascript_files = $this->javascript_files + $javascript_file;
        }
        else {
            $this->javascript_files[] = $javascript_file;
        }
    }

    /**
     * Add the given JavaScript to the head section of the page, to be run when the page is read
     *
     * @param $script string or string array 
     */
    function add_javascript_header_script($script) {
        if (is_array($script)) {
            $this->javascript_ready = $this->javascript_ready + $script;
        }
        else {
            $this->javascript_ready[] = $script;
        }
    }

    /**
     * Set the favicon for this page
     *
     * @param $favicon_filename string filename of the favicon
     */
    function add_favicon($favicon_filename) {
        $this->favicon = $favicon_filename;
    }

    //
    //
    // HTML header functions
    //

    ///
    // send_headers()
    //
    // Send the default headers for the HTML transaction.
    //
    // NOTE: these should be sent BEFORE anything else.
    //
    
    /**
     * Send the headers for the HTML transaction.
     * 
     * NOTE: these should be sent BEFORE anything else.
     *
     */
    function send_headers() {
        // Deliver the headers
        foreach ($this->headers as $key => $value) {
            header("{$key}: {$value}");
            print "{$key}: {$value}";
            print "\n";
        }
        /*
         * example from another project, verify useful for PhET
         * if so, move to appropriate location (not here)
        header("Expires: Mon, 1 Jan 1990 05:00:00 GMT");
        header("Cache-Control: no-store, no-cache, must-revalidate");
        header("Cache-Control: post-check=0, pre-check=0", false);
        header("Pragma: no-cache");
        */
    }

    function set_header($key, $value) {
        
    }

    //
    // HTML document section functions
    //

    /**
     * Output XHTML associated with the start of the document.
     *
     */
    function open_xhtml() {
        print <<<EOT
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">

EOT;
    }

    /**
     * Output HTML associated with the end of the document.
     *
     */
    function close_xhtml() {
        print <<<EOT
</html>

EOT;
    }


    //
    // HTML head section functions
    //

    /**
     * Output HTML associated with the head section.
     *
     */
    function open_xhtml_head() {
        if (!is_null($this->meta_refresh_location)) {
            $meta_refresh = "<meta http-equiv=\"Refresh\" content=\"{$this->meta_refresh_timeout};url={$this->meta_refresh_location}\" />";
        }
        else {
            $meta_refresh = "";
        }

        $formatted_title = format_string_for_html($this->full_title);

        print <<<EOT
  <head>
    <title>{$formatted_title}</title>

    <meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
    {$meta_refresh}

EOT;

        if (count($this->stylesheets) > 0) {
            $styles = array();
            foreach ($this->stylesheets as $stylesheet) {
                $styles[] = "@import url({$this->prefix}{$stylesheet});";
            }

            $rendered_styles = join("\n            ", $styles);

            print <<<EOT
    <style type="text/css">
        /*<![CDATA[*/
            {$rendered_styles}
        /*]]>*/
    </style>

EOT;
        }

        if (count($this->javascript_files) > 0) {
            $script_files = array();
            foreach ($this->javascript_files as $javascript_file) {
                $script_files[] = '<script type="text/javascript" src="'.$this->prefix.$javascript_file.'"></script>';
            }
            $rendered_script_files = join("\n    ", $script_files);
            print <<<EOT
    <!-- compliance patch for microsoft browsers -->
    <!--[if lt IE 7]><script src="{$this->prefix}js/ie7/ie7-standard-p.js" type="text/javascript"></script><![endif]-->
    {$rendered_script_files}

EOT;
        }

        if (count($this->javascript_ready) > 0) {
            $rendered_scripts = join("\n", $this->javascript_ready);
            print <<<EOT
    <script type="text/javascript">
        //<![CDATA[
            $(document).ready(
                function () {
{$rendered_scripts}
                }
            );
      //]]>
    </script>

EOT;
        }

        if ($this->favicon != '') {
            print <<<EOT
    <link rel="icon" href="{$this->favicon}" type="image/x-icon"/>
    <link rel="shortcut-icon" href="{$this->favicon}" type="image/x-icon"/>

EOT;
        }
    }

    /**
     * Output HTML associated with the end of the head section.
     *
     */
    function close_xhtml_head() {
        print "  </head>\n\n";
    }


    //
    // HTML body section functions
    //

    /**
     * Output HTML associated with start of the body section.
     *
     */
    function open_xhtml_body() {
        $referrer = "";
        if (!is_null($this->referrer)) {
            $formatted_referrer = format_string_for_html($this->referrer);
            $referrer = '<input type="hidden" name="referrer" value="'.$formatted_referrer.'"  class="always-enabled" />';
        }

        print <<<EOT
  <body id="top">
    <div id="skipNav">
        <a href="#content" accesskey="0">Skip to Main Content</a>
    </div>

    <div id="header">
        <div id="headerContainer">
            <div class="images">
                <div class="logo">
                    <a href="{$this->prefix}index.php"><img src="{$this->prefix}images/phet-logo.gif" alt="PhET Logo" title="Click here to go to the home page" /></a>
                </div>

                <div class="title">
                    <img src="{$this->prefix}images/logo-title.jpg" alt="Physics Education Technology - University of Colorado, Boulder" title="Physics Education Technology - University of Colorado, Boulder" />

                    <div id="quicksearch">
                        <form method="get" action="{$this->prefix}simulations/search.php">
                            <fieldset>
                                <span>Search</span>
                                <input type="text" size="15" name="search_for" title="Enter the text to search for" class="always-enabled" />
                                <input type="submit" value="Go" title="Click here to search the PhET website" class="always-enabled" />
                                {$referrer}
                            </fieldset>
                        </form>
                    </div>
                </div>
            </div>

            <div class="clear"></div>
        </div>
    </div>

    <div id="{$this->css_container_name}">

EOT;

        $this->render_navigation_bar();
    }

    /**
     * Output HTML associated with end of the body section
     *
     */
    function close_xhtml_body() {
        $utility_panel_html = $this->get_login_panel();

        print <<<EOT
                        </div>

                        <div id="footer">
                            <p>&copy; 2008 University of Colorado. <a href="http://phet.colorado.edu/about/licensing.php ">Some rights reserved.</a></p>
                        </div>
                    </div>
                </div>

                <div id="utility-panel">
                    $utility_panel_html
                </div>
            </body>

EOT;
    }


    //
    // Debugging functions
    //

    /**
     * Dump the var to the debugging output.
     *
     * @param $var mixed variable to dump
     */
    function debug_var_dump($var) {
        $result = "";
        if (is_array($var)) {
            foreach ($var as $key => $value) {
                $result = "$key => $value\n";
            }
        }
        else {
            $result = "text: $var";
        }

        $this->debug_add_text($result);
    }

    /**
     * Add text to the debug output.
     *
     * @param unknown_type $text
     */
    function debug_add_text($text) {
        $this->debug_content[] = $text;
    }

    /**
     * Output the debugging text to the appropriate section
     *
     */
    function debug_output_text() {
        if (count($this->debug_content) > 0) {
            print "<div class=\"debug_info\">\n";
            print "<h2 class=\"debug_title\">Debugging Info</h2>\n";
            print nl2br(htmlentities(join("\n", $this->debug_content)));
            print "</div>\n";
        }
    }


    //
    // Content functions
    //

    /**
     * Add text to the content.  (May not be needed for PhET)
     *
     * @param $text string HTML to add to the content
     */
    function add_content($text) {
        $this->content[] = $text;
    }

    //
    // Update Functions
    //

    /**
     * Do the logic in needs to before the page is rendered.
     * 
     * Base function does nothing, meant to be overridden
     *
     * @return bool TRUE if everything works OK
     */
    function update() {
        // Nothing to do, return success
        return TRUE;
    }


    //
    // Rendering functions
    //

    /**
     * Render the title of the page.
     *
     */
    function render_title() {
        if (!strcmp("", $this->render_title)) return;

        $formatted_text = format_string_for_html($this->render_title);
        print "    <h1>{$formatted_text}</h1>\n";
    }

    /**
     * Render the navigation bar.  Meant to be overridden in subclasses.
     *
     */
    function render_navigation_bar() {
        // No navigation bar in the base class, should be overridden
    }

    /**
     * Render the content of the page.  Will print the login / new account form if the login is required.
     *
     * @return bool FALSE if login required and the user isn't validated
     */
    function render_content() {
        $rendered_content = join("\n", $this->content);
        print <<<EOT
    <div class="main">
      {$rendered_content}
    </div>

EOT;

        return TRUE;
    }

    /**
     * Render a redirection message if the automatic redirection doesn't work.
     *
     */
    function render_redirect() {
        print <<<EOT
    <div class="main">
      <h2>This page is  being redirected.</h2>
      <p>If this does not happen automatically,<br />please select 
      <a href="{$this->meta_refresh_location}">this link</a>.</p>
    </div>

EOT;
    }

    /**
     * Render the page.
     *
     */
    function render() {
        $this->send_headers();

        $this->open_xhtml();

        $this->open_xhtml_head();
        $this->close_xhtml_head();

        $this->open_xhtml_body();

        $this->render_title();

        // FIXME: this should refactored, merely replacing with $this->meta_refresh_location may break things
        if (is_null($this->redirect_location)) {
            $this->render_content();
        }
        else {
            $this->render_redirect();
        }

        $this->debug_output_text();

        $this->close_xhtml_body();

        $this->close_xhtml();
    }


    //
    // Misc functions
    //

    /**
     * Get the panel that is displayed showing login status, or link to login page.  Meant to be overridden.
     *
     */
    function get_login_panel() {
        return "";
    }

}

?>
