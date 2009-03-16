<?php

//
// Clear the specified web page caches:
//

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");

define("CACHE_KEY", "cache");
$COMMANDS = array(
    "sims" => "return cache_clear_simulations();",
    "simulations" => "return cache_clear_simulations();",
    "admin" => "return cache_clear_admin();",
    "teacher_ideas" => "return cache_clear_teacher_ideas();",
    "all" => "return cache_clear_all();"
    );

class ClearCachePage extends SitePage {

    function update() {
        global $COMMANDS;

        $result = parent::update();
        if (!$result) {
            return $result;
        }

        $this->valid_cache = false;
        if (isset($_GET[CACHE_KEY])) {
            if (array_key_exists($_GET[CACHE_KEY], $COMMANDS)) {
                $this->valid_cache = true;
                $this->clear_result = eval($COMMANDS[$_GET[CACHE_KEY]]);
            }
        }
    }

    function render_content() {
        global $COMMANDS;

        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (!$this->valid_cache) {
            print "<h2>Invalid</h2>\n";
            print "<p>Cache not specified or is invalid.  Valid cache names are:\n";
            print "<ul>\n";
            foreach (array_keys($COMMANDS) as $valid_key_name) {
                print "<li>{$valid_key_name}</li>\n";
            }
            print "</ul>\n";
            print "</p>\n";
        }
        else if (!$this->clear_result) {
            print <<<EOT
            <h2>ERROR</h2>
            <p>An internal error occured, cannot clear cache called {$_GET[CACHE_KEY]}</p>
            <p>If you suspect the cache is not clearing properly, please contact the website maintainer</p>

EOT;
        }
        else {
            print <<<EOT
            <h2>Success</h2>
            <p>The {$_GET[CACHE_KEY]} cache has been cleared.</p>

EOT;
        }
    }

}

$page = new ClearCachePage("Clear Cache", NAV_ADMIN, null, AUTHLEVEL_NONE, false);
$page->update();
$page->render();

?>