<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class sysUtilsTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing file_put_contents()
     *
     */

    public function testFilePutContents_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_headers()
     *
     */

    public function testGetHeaders_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing mkdir_recursive()
     *
     */

    public function testMkdirRecursive_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing mkdirs()
     *
     */

    public function testMkdirs_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing urlsize()
     *
     */

    public function testUrlsize_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing url_or_file_size()
     *
     */

    public function testUrlOrFileSize_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing auto_detect_mime_type()
     *
     */

    public function testAutoDetectMimeType_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing expire_page_immediately()
     *
     */

    public function testExpirePageImmediately_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing send_file_to_browser()
     *
     */

    public function testSendFileToBrowser_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_file_extension()
     *
     */

    public function testGetFileExtension_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing remove_file_extension()
     *
     */

    public function testRemoveFileExtension_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing flock_get_contents()
     *
     */

    public function testFlockGetContents_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing flock_put_contents()
     *
     */

    public function testFlockPutContents_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing create_temp_dir()
     *
     */

    public function testCreateTempDir_testCase() {
        $this->markTestIncomplete();
    }

}

?>
