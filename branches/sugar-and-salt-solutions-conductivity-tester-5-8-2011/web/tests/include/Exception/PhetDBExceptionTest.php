<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Include global.php and the autoloader will take care of the classes
require_once "include/global.php";

class PhetDBExceptionTest extends PHPUnit_Framework_TestCase {
    protected $connection;

    protected function connectToDB() {
        // Assume success, not sure what to do in failure
        $this->connection = mysql_connect(DB_HOSTNAME,
                                          DB_USERNAME,
                                          DB_PASSWORD);
        
        return mysql_select_db(DB_NAME, $this->connection);
    }

    public function testPhetDBExceptionFullySpecifiedOk() {
        // Given/expected values
        $query = 'DB QUERY';
        $db_err = 'DB ERROR MESSAGE';
        $db_errno = 9;
        $message = 'ERROR MESSAGE';
        $code = 98738;

        // Get an exception
        $e = new PhetDBException($query, $db_err, $db_errno, $message, $code);

        // Tests
        $this->assertEquals($query, $e->getQuery());
        $this->assertEquals($db_err, $e->getDBErrorMessage());
        $this->assertEquals($db_errno, $e->getDBErrorCode());
        $this->assertEquals($message, $e->getMessage());
        $this->assertEquals($code, $e->getCode());
    }

    public function testPhetDBExceptionDefaultsGetBlankMysqlErrorsWithNoMysqlErrorCondition() {
        // Given/expected values
        $query = 'show tables';

        // Make a good call
        $this->connectToDB();
        $result = mysql_query($query);
        $this->assertNotEquals(false, $result);

        // Get an exception
        $e = new PhetDBException($query);

        // Test
        $this->assertEquals($query, $e->getQuery());
        $this->assertEquals('', $e->getMessage());
        $this->assertEquals(0, $e->getCode());
    }

    public function testPhetDBExceptionDefaultsGetMysqlErrorsWithBadQuery() {
        // Given/expected values
        $query = 'DB QUERY';
        
        // Make a bad call
        $this->connectToDB();
        $result = mysql_query($query);
        $this->assertEquals(false, $result);

        // Get an exception
        $e = new PhetDBException($query);

        // Tests
        $this->assertEquals($query, $e->getQuery());
        $this->assertEquals(mysql_error(), $e->getDBErrorMessage());
        $this->assertEquals(mysql_errno(), $e->getDBErrorCode());
    }

    public function testPhetDBExceptionToStringGivesInfo() {
        // Given/expected values
        $query = 'DB QUERY';
        $message = 'ERROR MESSAGE';
        $code = 98738;

        // Make a bad call
        $this->connectToDB();
        $result = mysql_query($query);
        $this->assertEquals(false, $result);

        // Get an exception
        $e = new PhetDBException($query, null, null, $message, $code);

        $str = $e->__toString();

        $this->assertFalse(false === stripos($str, $query));
        $this->assertFalse(false === stripos($str, mysql_error()));
        $this->assertFalse(false === stripos($str, mysql_errno()));
        $this->assertFalse(false === stripos($str, $message));
    }
}

?>
