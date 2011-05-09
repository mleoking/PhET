<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class globalTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing undoMagicQuotes()
     *
     */

    public function testUndomagicquotes_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing debug_is_on()
     *
     */

    public function testDebugIsOn_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing microtime_float()
     *
     */

    public function testMicrotimeFloat_testCase() {
        $this->markTestIncomplete();
    }

}

?>
