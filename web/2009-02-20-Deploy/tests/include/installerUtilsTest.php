<?php

require_once('PHPUnit/Extensions/Database/TestCase.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

// Get the file to test
require_once("include/installer-utils.php");

class installerUtilsTest extends PHPUnit_Extensions_Database_TestCase {

    // Name of the data file that populates the database before each
    // test is run
    const db_data_file = 'installerUtils.xml';

    // PDO object pointing to the test database
    protected static $pdo = NULL;

    public function __construct() {
        if (is_null(self::$pdo)) {
            self::$pdo = PHPUnit_Util_PDO::factory("mysql://phet_test@localhost/phet_test");
        }

        // Where to find the files
        $this->data_dir = 
            dirname(dirname(__FILE__)).DIRECTORY_SEPARATOR.
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
     * Testing installer_check_timestamp()
     *
     */

    public function testInstallerCheckTimestamp_TimestampWhenTableEmptyReturnsTrue() {
        $result = installer_check_timestamp(time());
        $this->assertTrue($result);
    }

    public function testInstallerCheckTimestamp_OlderTimestampReturnsFalse() {
        // Get a time
        $time = time();

        // Add a timestamp
        $result = installer_add_new_timestamp($time);
        $this->assertTrue($result);

        // Check against that a newer timestamp
        $result = installer_check_timestamp($time - 1);
        $this->assertFalse($result);
    }

    public function testInstallerCheckTimestamp_EqualTimestampReturnsTrue() {
        // Get a time
        $time = time();

        // Add a timestamp
        $result = installer_add_new_timestamp($time);
        $this->assertTrue($result);

        // Check against that a newer timestamp
        $result = installer_check_timestamp($time);
        $this->assertFalse($result);
    }

    public function testInstallerCheckTimestamp_NewerTimestampReturnsTrue() {
        // Get a time
        $time = time();

        // Add a timestamp
        $result = installer_add_new_timestamp($time);
        $this->assertTrue($result);

        // Check against that a newer timestamp
        $result = installer_check_timestamp($time + 1);
        $this->assertTrue($result);
    }

    public function testInstallerCheckTimestamp_NewerStringTimestampReturnsTrue() {
        // Get a time
        $time = time();

        // Add a timestamp
        $result = installer_add_new_timestamp($time);
        $this->assertTrue($result);

        // Check against that a newer timestamp
        $time1 = $time + 1;
        $result = installer_check_timestamp("$time1");
        $this->assertTrue($result);
    }

    /**
     *
     * Testing installer_add_new_timestamp()
     *
     */

    public function testInstallerAddNewTimestamp_IntTimestampAddsRow() {
        $result = installer_add_new_timestamp(time());
        $this->assertTrue($result);

        $result = self::$pdo->query('SELECT * FROM `installer_info`');
        $this->assertEquals($result->rowCount(), 1);
    }

    public function testInstallerAddNewTimestamp_StringTimestampAddsRow() {
        $time = time();
        $result = installer_add_new_timestamp("$time");
        $this->assertTrue($result);

        $result = self::$pdo->query('SELECT * FROM `installer_info`');
        $this->assertEquals($result->rowCount(), 1);
    }

    public function testInstallerAddNewTimestamp_AddsExpectedValue() {
        $time = time();
        $result = installer_add_new_timestamp($time);
        $this->assertTrue($result);

         // Get the latest row
         $result = self::$pdo->query('SELECT * FROM `installer_info` ORDER BY `installer_info_id` DESC LIMIT 1');
         $this->assertEquals($result->rowCount(), 1);
         $row = $result->fetch(PDO::FETCH_ASSOC);

         // Get the time we inserted
         $inserted_time = $row['installer_info_timestamp'];

         $this->assertEquals($inserted_time, "$time");
    }

    public function testInstallerAddNewTimestamp_OnlyRetainsProperNumberOfRows() {
        for ($i = 0; $i < 2 * INSTALLER_MAX_ROWS; ++$i) {
            $result = installer_add_new_timestamp(time());
            $this->assertTrue($result);
        }

        $result = self::$pdo->query('SELECT * FROM `installer_info`');
        $this->assertEquals(INSTALLER_MAX_ROWS, $result->rowCount());
    }

    /**
     *
     * Testing installer_get_latest_timestamp()
     *
     */

     public function testInstallerGetLatestTimestamp_emptyTableReturnsFalse() {
         $result = installer_get_latest_timestamp();
         $this->assertFalse($result);
     }

     public function testInstallerGetLatestTimestamp_nonemptyTableReturnsTimestamp() {
         installer_add_new_timestamp(time());
         $result = installer_get_latest_timestamp();
         $this->assertNotEquals(false, $result);
     }

     public function testInstallerGetLatestTimestamp_returnsString() {
         installer_add_new_timestamp(time());
         $result = installer_get_latest_timestamp();
         $this->assertType('string', $result);
     }

     public function testInstallerGetLatestTimestamp_returnsExpected() {
         $result = installer_add_new_timestamp(time());
         $this->assertTrue($result);

         // Get the latest row
         $result = self::$pdo->query('SELECT * FROM `installer_info` ORDER BY `installer_info_id` DESC LIMIT 1');
         $this->assertEquals($result->rowCount(), 1);
         $row = $result->fetch(PDO::FETCH_ASSOC);

         // Get the time we inserted
         $inserted_time = $row['installer_info_timestamp'];

         // Get the latest timestamp
         $result = installer_get_latest_timestamp();
         $this->assertEquals($inserted_time, $result);
     }

     public function testInstallerGetLatestTimestamp_afterMultipleAddsReturnsExpected() {
         $time = time();
         for ($i = 20; $i >=0; $i = $i -2) {
             $result = installer_add_new_timestamp($time - $i);
             $this->assertTrue($result);
         }

         // Get the latest row
         $result = self::$pdo->query('SELECT * FROM `installer_info` ORDER BY `installer_info_id` DESC LIMIT 1');
         $this->assertEquals($result->rowCount(), 1);
         $row = $result->fetch(PDO::FETCH_ASSOC);

         // Get the time we inserted
         $inserted_time = $row['installer_info_timestamp'];

         // Get the latest timestamp
         $result = installer_get_latest_timestamp();
         $this->assertEquals($inserted_time, $result);
     }

}

?>
