<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class localeCodesCountryTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing locale_get_country_map()
     *
     */

    public function testLocaleGetCountryMap_testCase() {
        $this->markTestIncomplete();
    }

}

?>
