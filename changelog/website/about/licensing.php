<?php

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

class LicensingPage extends SitePage {

    function render_title() {
        // The Licensing file is HTML and has its own title,
        // so don't display the title as normal.
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        // The website licensing is a seperate file and is included
        // into the licensing page, rather than having everything in
        // one place.  Updating the licensing should update the
        // respective license file.
        //
        // The License_website_v7.htm file was modified from the
        // original given in the ticket to get it to render properly
        // with the PhET website.  When updating, similar processing
        // may be necessary.
        print file_get_contents('./License_website_v7.htm');
    }
}

$page = new LicensingPage("Licensing", NavBar::NAV_ABOUT_PHET, null);
$page->update();
$page->render();

?>
