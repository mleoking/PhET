<?php

require_once('PHPUnit/Extensions/Database/TestCase.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

require_once('include/db-utils.php');

class UpdateUtilsTest extends PHPUnit_Extensions_Database_TestCase {
    
    // Name of the data file that populates the database before each
    // test is run
    const db_data_file = 'UpdateUtils.xml';

    // PDO object pointing to the test database
    protected static $pdo = NULL;

    public function __construct() {
        if (is_null(self::$pdo)) {
            self::$pdo = PHPUnit_Util_PDO::factory("mysql://phet_test@localhost/phet_test");
        }

        // Where to find the files
        $this->data_dir = 
            dirname(dirname(dirname(__FILE__))).DIRECTORY_SEPARATOR.
            "_files".DIRECTORY_SEPARATOR.
            'database'.DIRECTORY_SEPARATOR.
            'data'.DIRECTORY_SEPARATOR;
    }

    public function __destruct() {
        self::$pdo = NULL;
    }

    protected function getConnection() {
        return $this->createDefaultDBConnection(self::$pdo, 'phet_test');
    }

    protected function getDataSet() {
        return $this->createXMLDataSet($this->data_dir.self::db_data_file);
    }

    /**
     *
     * testing stuff
     *
     */

    public function setUp() {
        parent::setUp();
        $this->fixture = UpdateUtils::inst();
    }

    public function testGetSettings_DefaultExpected() {
        $expected_settings = array(
            'sim_ask_later_duration' => 33,
            'install_ask_later_duration' => 44,
            'install_recommend_update_age' => 55,
            'install_recommend_update_date' => '2001-02-03',
            );
        $settings = $this->fixture->getSettings();
        $this->assertEquals($expected_settings, $settings);
    }

    public function testSetSettings_badIntDataFails() {
        $new_data = array(
            'sim_ask_later_duration' => 'e354',  // bad entry
            'install_ask_later_duration' => 454,
            'install_recommend_update_age' => 554,
            'install_recommend_update_date' => '2002-12-22',
            );
        $success = $this->fixture->setSettings($new_data);
        $this->assertFalse($success);
        $settings = $this->fixture->getSettings();
        $this->assertNotEquals($new_data, $settings);
    }

    public function testSetSettings_badDateDataFails() {
        $new_data = array(
            'sim_ask_later_duration' => 354,
            'install_ask_later_duration' => 454,
            'install_recommend_update_age' => 554,
            'install_recommend_update_date' => '2002-32-22',  // bad date
            );
        $success = $this->fixture->setSettings($new_data);
        $this->assertFalse($success);
        $settings = $this->fixture->getSettings();
        $this->assertNotEquals($new_data, $settings);
    }

    public function testSetSettings_goodDataChangesExpected() {
        $new_data = array(
            'sim_ask_later_duration' => 354,
            'install_ask_later_duration' => 454,
            'install_recommend_update_age' => 554,
            'install_recommend_update_date' => '2002-12-22',
            );
        $success = $this->fixture->setSettings($new_data);
        $this->assertTrue($success);
        $settings = $this->fixture->getSettings();
        $this->assertEquals($new_data, $settings);
    }

    public function testSetSettings_unnecessaryKeysOk() {
        $new_data = array(
            'key_not_in_database1' => '3434a',
            'key_not_in_database2' => array(1,2,3,4),
            'sim_ask_later_duration' => 354,
            'install_ask_later_duration' => 454,
            'install_recommend_update_age' => 554,
            'install_recommend_update_date' => '2002-12-22',
            );
        $success = $this->fixture->setSettings($new_data);
        $this->assertTrue($success);
        $expected_data = array(
            'sim_ask_later_duration' => 354,
            'install_ask_later_duration' => 454,
            'install_recommend_update_age' => 554,
            'install_recommend_update_date' => '2002-12-22',
            );
        $settings = $this->fixture->getSettings();
        $this->assertEquals($expected_data, $settings);
    }
}

?>
