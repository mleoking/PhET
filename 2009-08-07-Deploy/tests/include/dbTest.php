<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class dbTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing connect_to_db()
     *
     */

    public function testConnectToDb_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing showerror()
     *
     */

    public function testShowerror_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing missingfield()
     *
     */

    public function testMissingfield_testCase() {
        $this->markTestIncomplete();
    }

}

?>
