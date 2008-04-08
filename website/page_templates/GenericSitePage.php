<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class GenericSitePage extends SitePage {
    function __construct($content_printer, $page_title, $nav_selected_page, $referrer, $required_authentication_level) {
        $this->content_printer = $content_printer;
        parent::__construct($page_title, $nav_selected_page, $referrer, $required_authentication_level);
    }

    function render_title() {
        // Don't render the title by default
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        call_user_func($this->content_printer);
        return true;
    }
}

?>