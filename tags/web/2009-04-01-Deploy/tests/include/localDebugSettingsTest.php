<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class localDebugSettingsTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing setup_local_debug_settings()
     *
     */

    public function testSetupLocalDebugSettings_testCase() {
        $this->markTestIncomplete();
    }

}

?>
