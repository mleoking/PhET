<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class zipTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing unix2DosTime()
     *
     */

    public function testUnix2dostime_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing add_file()
     *
     */

    public function testAddFile_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing build_zipped_file()
     *
     */

    public function testBuildZippedFile_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing write_to_file()
     *
     */

    public function testWriteToFile_testCase() {
        $this->markTestIncomplete();
    }

}

?>
