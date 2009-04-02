<?php

require_once('PHPUnit/Extensions/Database/TestCase.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

// Get the file to test
require_once("include/db-utils.php");
require_once("include/sim-utils.php");

class dbUtilsTest extends PHPUnit_Extensions_Database_TestCase {

    // Name of the data file that populates the database before each
    // test is run
    const db_data_file = 'dbUtils.xml';

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
     * Testing stripslashes_deep()
     *
     * Unused as of 1/9/09, skipping unit tests
     *
     **/

    /**
     *
     * Testing db_convert_condition_array_to_sql()
     *
     */

    public function testDbConvertConditionArrayToSql_EmptyConditionReturnsEmptyQuery() {
        $condition = array();
        $result = db_convert_condition_array_to_sql($condition);
        $this->assertEquals('', $result);
    }

    public function testDbConvertConditionArrayToSql_ValidConditionReturnsValidQuery() {
        $condition = array('foo1' => 'bar1', 'foo2' => 'bar2', 'foo3' => 'bar3');
        $expected_query = "`foo1`='bar1' AND `foo2`='bar2' AND `foo3`='bar3'";
        $result = db_convert_condition_array_to_sql($condition);
        $this->assertEquals($expected_query, $result);
    }

    public function testDbConvertConditionArrayToSql_ValidConditionReturnsValidQueryFuzzy() {
        $condition = array('foo1' => 'bar1', 'foo2' => 'bar2', 'foo3' => 'bar3');
        $expected_query = "`foo1` LIKE '%bar1%' AND `foo2` LIKE '%bar2%' AND `foo3` LIKE '%bar3%'";
        $result = db_convert_condition_array_to_sql($condition, true);
        $this->assertEquals($expected_query, $result);
    }

    public function testDbConvertConditionArrayToSql_ValidConditionWithEscapableCharsReturnsValidQuery() {
        $condition = array('fo"o1' => 'ba\r1', "fo'o2" => "ba\nr2", "fo\ro3" => "ba\x00r\x1a3");
        $expected_query = "`fo\\\"o1`='ba\\\\r1' AND `fo\\'o2`='ba\\nr2' AND `fo\\ro3`='ba\\0r\\Z3'";
        $result = db_convert_condition_array_to_sql($condition);
        $this->assertEquals($expected_query, $result);
    }

    public function testDbConvertConditionArrayToSql_ValidConditionWithEscapableCharsReturnsValidQueryFuzzy() {
        $condition = array('fo"o1' => 'ba\r1', "fo'o2" => "ba\nr2", "fo\ro3" => "ba\x00r\x1a3");
        $expected_query = "`fo\\\"o1` LIKE '%ba\\\\r1%' AND `fo\\'o2` LIKE '%ba\\nr2%' AND `fo\\ro3` LIKE '%ba\\0r\\Z3%'";
        $result = db_convert_condition_array_to_sql($condition, true);
        $this->assertEquals($expected_query, $result);
    }

    public function testDbConvertConditionArrayToSql_StringConditionFails() {
        $condition = 'hello';
        $result = db_convert_condition_array_to_sql($condition);
        $this->assertEquals('', $result);
    }

    public function testDbConvertConditionArrayToSql_NonkeyArrayCondition() {
        $condition = array('hello');
        $result = db_convert_condition_array_to_sql($condition);
        $this->assertEquals("`0`='hello'", $result);
    }

    /**
     *
     * Testing db_exec_query()
     *
     * Since the query return value is different for different
     * SQL statements, test success and failure of different
     * queries as suggested by the manual page:
     *     returning rulesets: SELECT, SHOW, DESCRIBE, EXPLAIN, etc
     *     returning bool: INSERT, UPDATE, DELETE, DROP, etc
     * The tests are not exhaustive for every SQL query, just
     * the ones listed above (not the etc).
     *
     * The first group are statements that return
     * information, the second group are statements tha modify the
     * database.  The first group we'll just check success and
     * failure, the second group we'll test success, failure,
     * and successful altercation of the database.
     *
     * Notes:
     * Not testing 'DROP'.  It is tricky to test with this framework
     * and not often used in PhET.
     *
     **/

    public function testDbExecQuery_BadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "BAD QUERY WONT WORK";
        db_exec_query($query);
    }

    public function testDbExecQuery_SelectGoodQueryReturnsResource() {
        $query = "SELECT * FROM `category`";
        $result = db_exec_query($query);
        $this->assertTrue(is_resource($result));
    }

    public function testDbExecQuery_SelectBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "SELECT * FROM `foo_category`";
        db_exec_query($query);
    }

    public function testDbExecQuery_ShowGoodQueryReturnsResource() {
        $query = "SHOW CREATE TABLE `category`";
        $result = db_exec_query($query);
        $this->assertTrue(is_resource($result));
    }

    public function testDbExecQuery_ShowBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "SHOW CREATE TABLE `FOO_category`";
        db_exec_query($query);
    }

    public function testDbExecQuery_DescribeGoodQueryReturnsResource() {
        $query = "DESCRIBE `category`";
        $result = db_exec_query($query);
        $this->assertTrue(is_resource($result));
    }

    public function testDbExecQuery_DescribeBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "DESCRIBE `FOO_category`";
        db_exec_query($query);
    }

    public function testDbExecQuery_ExplainGoodQueryReturnsResource() {
        $query = "EXPLAIN SELECT * FROM `category`";
        $result = db_exec_query($query);
        $this->assertTrue(is_resource($result));
    }

    public function testDbExecQuery_ExplainBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "EXPLAIN SELECT * FROM `foo_category`";
        db_exec_query($query);
    }

    public function testDbExecQuery_InsertGoodQueryReturnsTrue() {
        $query = "INSERT INTO `category` VALUES (387, 'Added Category', 1, 39, -1)";
        $result = db_exec_query($query);
        $this->assertTrue($result);
    }

    public function testDbExecQuery_InsertBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "INSERT INTO `foo_category` VALUES (387, 'Added Category', 1, 39, -1)";
        db_exec_query($query);
    }

    public function testDbExecQuery_InsertGoodQueryCorrectlyAltersData() {
        // Construct and run the query
        $query = "INSERT INTO `category` VALUES (387, 'Added Category', 1, 39, -1)";
        $result = db_exec_query($query);
        $this->assertTrue($result);

        // Get a copy of the original category table in the dataset,
        // add the row we expect to see
        $set = $this->createXMLDataSet($this->data_dir.self::db_data_file);
        $table = $set->getTable('category');
        $table->addRow(array('cat_id' => 387,
                             'cat_name' => 'Added Category',
                             'cat_is_visible' => 1,
                             'cat_order' => 39,
                             'cat_parent' => -1));

        // Get the current category table
        $db_set = $this->getConnection()->createDataSet()->getTable("category");

        // Make sure they match
        $this->assertTablesEqual($table, $db_set);
    }

    public function testDbExecQuery_UpdateGoodQueryReturnsTrue() {
        $query = "UPDATE `category` SET `cat_name` = 'Altered Category' WHERE `cat_id`=1";
        $result = db_exec_query($query);
        $this->assertTrue($result);
    }

    public function testDbExecQuery_UpdateBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "UPDATE `foo_category` SET `cat_name` = 'Altered Category' WHERE `cat_id`=1";
        db_exec_query($query);
    }

    public function testDbExecQuery_UpdateGoodQueryCorrectlyAltersData() {
        // Construct and run the query
        $query = "UPDATE `category` SET `cat_name` = 'Altered Category' WHERE `cat_id`=1";
        $result = db_exec_query($query);
        $this->assertTrue($result);

        // Get a copy of the original category table in the dataset,
        // alter the row we expect to see
        $set = $this->createXMLDataSet($this->data_dir.self::db_data_file);
        $table = $set->getTable('category');
        $table->setValue(0, 'cat_name', 'Altered Category');

        // Get the current category table
        $db_set = $this->getConnection()->createDataSet()->getTable("category");

        // Make sure they match
        $this->assertTablesEqual($db_set, $table);
    }

    public function testDbExecQuery_DeleteGoodQueryReturnsTrue() {
        $query = "DELETE FROM `category` WHERE `cat_id`=1";
        $result = db_exec_query($query);
        $this->assertTrue($result);
    }

    public function testDbExecQuery_DeleteBadQueryRaisesException() {
        $this->setExpectedException('PhetDBException');
        $query = "DELETE FROM `foo_category` WHERE `cat_id`=1";
        db_exec_query($query);
    }

    public function testDbExecQuery_DeleteGoodQueryCorrectlyAltersData() {
        // Construct and run the query
        $query = "DELETE FROM `category` WHERE `cat_id`=1";
        $result = db_exec_query($query);
        $this->assertTrue($result);

        // Get a copy of the original category table in the dataset,
        // alter the row we expect to see
        $set = $this->createXMLDataSet($this->data_dir.'dbUtilsTestAfterQueryDelete.xml');
        $table = $set->getTable('category');

        // Get the current category table
        $db_set = $this->getConnection()->createDataSet()->getTable("category");

        // Make sure they match
        $this->assertTablesEqual($db_set, $table);
    }

    /**
     *
     * Testing db_describe_table()
     *
     */

    public function testDbDescribeTable_GoodTableReturnsArray() {
        $table_description = db_describe_table('category');
        $this->assertType('array', $table_description);
    }

    public function testDbDescribeTable_GoodTableReturnsExpectedArray() {
        $table_description = db_describe_table('category');
        $expected_description = array(
            array('Field' => 'cat_id',
                  'Type' => 'int(11)',
                  'Null' => 'NO',
                  'Key' => 'PRI',
                  'Default' => null,
                  'Extra' => 'auto_increment'
                ),
            array('Field' => 'cat_name',
                  'Type' => 'text',
                  'Null' => 'NO',
                  'Key' => '',
                  'Default' => '',
                  'Extra' => ''
                ),
            array('Field' => 'cat_is_visible',
                  'Type' => 'tinyint(1)',
                  'Null' => 'NO',
                  'Key' => '',
                  'Default' => '1',
                  'Extra' => ''
                ),
            array('Field' => 'cat_order',
                  'Type' => 'int(11)',
                  'Null' => 'NO',
                  'Key' => '',
                  'Default' => '0',
                  'Extra' => ''
                ),
            array('Field' => 'cat_parent',
                  'Type' => 'int(11)',
                  'Null' => 'NO',
                  'Key' => '',
                  'Default' => '-1',
                  'Extra' => ''
                )
            );
        //        var_dump($table_description);
        $this->assertEquals($expected_description, $table_description);
    }

    public function testDbDescribeTable_TableWithWeirdCharactersOK() {
        $table_description = db_describe_table('cat\'"\'egory');
        $this->assertType('array', $table_description);
    }

    public function testDbDescribeTable_BadTableRaisesException() {
        $this->setExpectedException('PhetDBException');
        db_describe_table('foo_category');
    }

    /**
     *
     * Testing db_get_all_rows()
     *
     */

    public function testDbGetAllRows_ReturnsArrayWithCorrectNumberOfRows() {
        $result = db_get_all_rows('category');
        $this->assertEquals(20, count($result));
    }

    public function testDbGetAllRows_RaisesExceptionForBadTable() {
        $this->setExpectedException('PhetDBException');
        db_get_all_rows('foo_category');
    }

    public function testDbGetAllRows_ReturnsAssociativeArrayWithColumnHeaders() {
        // Get the first row from the query
        $result = db_get_all_rows('category');
        $result_row = $result[0];

        // Get the first row from the table
        $db_table = $this->getConnection()->createDataSet()->getTable("category");
        $db_row = $db_table->getRow(0);

        // Compare the keys of each row
        foreach ($db_row as $key => $value) {
            $this->assertTrue(array_key_exists($key, $result_row));
        }
    }

    public function testDbGetAllRows_WorksWithWeirdTableNames() {
        $result = db_get_all_rows('cat\'"\'egory');
        $this->assertEquals(20, count($result));
    }

    public function testDbGetAllRows_ReturnsEmptyArrayWhenNotEmpty() {
        // Do the test
        $result = db_get_all_rows('category');
        $this->assertType('array', $result);
    }

    public function testDbGetAllRows_ReturnsEmptyArrayWhenEmpty() {
        // Clear the database
        $result = mysql_query("DELETE FROM `category`");
        $this->assertTrue($result);

        $result = db_get_all_rows('category');
        $this->assertType('array', $result);
        $this->assertTrue(empty($result));
    }

    /**
     *
     * Testing db_get_row_by_condition()
     *
     */

    public function testDbGetRowByCondition_ReturnsOnlyFirstRow() {
        // Returns only the first row, even if there are several that
        // match the criteria
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_row_by_condition('category', $conditions);
        $expected_array = 
            array(
                'cat_id' => 1,
                'cat_name' => 'Featured Sims',
                'cat_is_visible' => 1,
                'cat_order' => 0,
                'cat_parent' => -1,
                );
        $this->assertEquals($expected_array, $result);
    }

    public function testDbGetRowByCondition_RaisesExceptionForBadTable() {
        $this->setExpectedException('PhetDBException');
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        db_get_row_by_condition('foo_category', $conditions);
    }

    public function testDbGetRowByCondition_ReturnsAssociativeArrayWithColumnHeaders() {
        // Get the first row from the query
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result_row = db_get_row_by_condition('category', $conditions);

        // Get the first row from the table
        $db_table = $this->getConnection()->createDataSet()->getTable("category");
        $db_row = $db_table->getRow(0);

        // Compare the keys of each row
        foreach ($db_row as $key => $value) {
            $this->assertTrue(array_key_exists($key, $result_row));
        }
    }

    public function testDbGetRowByCondition_WorksWithWeirdTableNames() {
        $conditions = array('cat_\'"\'_parent' => '-1', 'cat_\'"\'_is_visible' => '1');
        $result = db_get_row_by_condition('cat\'"\'egory', $conditions);
        $this->assertType('array', $result);
    }

    public function testDbGetRowByCondition_ReturnsArrayWhenNotEmpty() {
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_row_by_condition('category', $conditions);
        $this->assertType('array', $result);
    }

    public function testDbGetRowByCondition_ReturnsFalseWhenEmpty() {
        // Clear the database
        $result = mysql_query("DELETE FROM `category`");
        $this->assertTrue($result);

        // Do the test
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_row_by_condition('category', $conditions);
        $this->assertFalse($result);
    }

    public function testDbGetRowByCondition_ReturnsProperRowWithFuzzyMatch() {
        $conditions = array('cat_name' => 'iology');
        $result = db_get_row_by_condition('category', $conditions, true);
        $expected_array = 
            array(
                'cat_id' => 26,
                'cat_name' => 'Biology',
                'cat_is_visible' => 1,
                'cat_order' => 3,
                'cat_parent' => -1,
                );
        $this->assertEquals($expected_array, $result);
    }

    public function testDbGetRowByCondition_ExtraFieldAffectsQuery() {
        // Returns only the first row, even if there are several that
        // match the criteria, and alter it so it is ordered differently
        // that the defalt query
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $extra = 'ORDER BY `cat_name` DESC';
        $result = db_get_row_by_condition('category', $conditions, false, $extra);
        $expected_array = 
            array(
                'cat_id' => 21,
                'cat_name' => 'Physics',
                'cat_is_visible' => 1,
                'cat_order' => 2,
                'cat_parent' => -1,
                );
        $this->assertEquals($expected_array, $result);
    }

    /**
     *
     * Testing db_get_rows_by_condition()
     *
     * This should be very similar to the above but work with multiple rows
     *
     **/

    public function testDbGetRowsByCondition_ReturnsCorrectNumberOfRows() {
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_rows_by_condition('category', $conditions);
        $this->assertEquals(9, count($result));
    }

    public function testDbGetRowsByCondition_RaisesExceptionForBadTable() {
        $this->setExpectedException('PhetDBException');
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        db_get_rows_by_condition('foo_category', $conditions);
    }

    public function testDbGetRowsByCondition_ReturnsAssociativeArrayWithColumnHeaders() {
        // Get the first row from the query
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_rows_by_condition('category', $conditions);
        $result_row = $result[0];

        // Get the first row from the table
        $db_table = $this->getConnection()->createDataSet()->getTable("category");
        $db_row = $db_table->getRow(0);

        // Compare the keys of each row
        foreach ($db_row as $key => $value) {
            $this->assertTrue(array_key_exists($key, $result_row));
        }
    }

    public function testDbGetRowsByCondition_WorksWithWeirdTableNames() {
        $conditions = array('cat_\'"\'_parent' => '-1', 'cat_\'"\'_is_visible' => '1');
        $result = db_get_rows_by_condition('cat\'"\'egory', $conditions);
        $this->assertType('array', $result);
    }

    public function testDbGetRowsByCondition_ReturnsArrayWhenNotEmpty() {
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_rows_by_condition('category', $conditions);
        $this->assertType('array', $result);
    }

    public function testDbGetRowsByCondition_ReturnsEmptyArrayWhenDbHasNoRows() {
        // Clear the database
        $result = mysql_query("DELETE FROM `category`");
        $this->assertTrue($result);

        // Do the test
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_rows_by_condition('category', $conditions);
        $this->assertType('array', $result);
    }

    public function testDbGetRowsByCondition_ReturnsArrayWhenDbHasNoRows() {
        // Clear the database
        $result = mysql_query("DELETE FROM `category`");
        $this->assertTrue($result);

        // Do the test
        $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $result = db_get_rows_by_condition('category', $conditions);
        $this->assertType('array', $result);
        $this->assertTrue(empty($result));
    }

    public function testDbGetRowsByCondition_ReturnsProperRowWithFuzzyMatch() {
        $conditions = array('cat_name' => 'iology');
        $result = db_get_rows_by_condition('category', $conditions, true);
        $expected_array = array(
            array(
                'cat_id' => 26,
                'cat_name' => 'Biology',
                'cat_is_visible' => 1,
                'cat_order' => 3,
                'cat_parent' => -1,
                ));
        $this->assertEquals($expected_array, $result);
    }

    public function testDbGetRowsByCondition_DataReformatedByDefault() {
        // Returns only the first row, even if there are several that
        // match the criteria, and alter it so it is ordered differently
        // that the defalt query
        $conditions = array('cat_name' => '&');
        $result = db_get_rows_by_condition('category', $conditions, true);
        foreach ($result as $row) {
            $this->assertNotEquals(false, strpos($row['cat_name'], '&amp;'));
        }
    }

    public function testDbGetRowsByCondition_DataNotFormated() {
        // Returns only the first row, even if there are several that
        // match the criteria, and alter it so it is ordered differently
        // that the defalt query
        $conditions = array('cat_name' => '&');
        $result = db_get_rows_by_condition('category', $conditions, true, false);
        foreach ($result as $row) {
            $this->assertFalse(strpos($row['cat_name'], '&amp;'));
            $this->assertNotEquals(false, 0 != strpos($row['cat_name'], '&'));
        }
    }

    public function testDbGetRowsByCondition_ExtraFieldAffectsQuery() {
        // Returns only the first row, even if there are several that
        // match the criteria, and alter it so it is ordered differently
        // that the defalt query
          $conditions = array('cat_parent' => '-1', 'cat_is_visible' => '1');
        $extra = 'ORDER BY `cat_name` DESC';
        $result = db_get_rows_by_condition('category', $conditions, false, false, $extra);
        $previous_name = null;
        foreach ($result as $row) {
            if (is_null($previous_name)) {
                $previous_name = $row['cat_name'];
            }
            else {
                $this->assertLessThan($previous_name, $row['cat_name']);
            }
        }
    }

    /**
     *
     * Testing db_search_for()
     *
     */
    /* template:
     string(0) ""
     string(49) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%%' )"
     string(2) "  "
     string(75) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%%' ) AND ( `hello` LIKE '%%' )"
     string(4) "this"
     string(53) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%this%' )"
     string(13) "this and that"
     string(112) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%this%' ) AND ( `hello` LIKE '%and%' ) AND ( `hello` LIKE '%that%' )"
     string(15) "this,that,other"
     string(114) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%this%' ) AND ( `hello` LIKE '%that%' ) AND ( `hello` LIKE '%other%' )"
     string(19) "this , that , other"
     string(114) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%this%' ) AND ( `hello` LIKE '%that%' ) AND ( `hello` LIKE '%other%' )"
     string(17) "this ,that ,other"
     string(114) "SELECT * FROM `wacky` WHERE ( `hello` LIKE '%this%' ) AND ( `hello` LIKE '%that%' ) AND ( `hello` LIKE '%other%' )"
    */
    public function testDbSearchFor_EmptyArrayFieldsParameterThrowsException() {
        $this->setExpectedException('PhetException');
        db_search_for('', '', array());
    }

    public function testDbSearchFor_NonArrayFieldThrowsException() {
        $this->setExpectedException('PhetException');
        db_search_for('', '', 'henery');
    }

    public function testDbSearchFor_BadTableThrows() {
        $this->setExpectedException('PhetDBException');
        $table = 'fairyland';
        $search_for = 'bunnies, friends';
        $fields_to_search = array('column', 'row');
        db_search_for($table, $search_for, $fields_to_search);
    }

    public function testDbSearchFor_BadFieldsParameterThrows() {
        $this->setExpectedException('PhetDBException');
        $table = 'simulation';
        $search_for = 'bunnies, friends';
        $fields_to_search = array('column', 'row');
        db_search_for($table, $search_for, $fields_to_search);
    }

    public function testDbSearchFor_SingleSeachWordSingleFieldReturnsExpected() {
        $table = 'simulation';
        $search_for = 'mass';
        $fields_to_search = array('sim_name');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(106);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_SingleSeachWordMultiField1ReturnsExpected() {
        $table = 'simulation';
        $search_for = 'light';
        $fields_to_search = array('sim_name', 'sim_keywords');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(83);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_SingleSeachWordMultiField2ReturnsExpected() {
        $table = 'simulation';
        $search_for = 'balloon';
        $fields_to_search = array('sim_name', 'sim_keywords');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(81);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_MultiSeachWordWithSpacesReturnsExpected() {
        $table = 'simulation';
        $search_for = 'balloon static';
        $fields_to_search = array('sim_name');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(81);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_MultiSeachWordWithCommasReturnsExpected() {
        $table = 'simulation';
        $search_for = 'balloon,static';
        $fields_to_search = array('sim_name');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(81);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_MultiSeachWordWithCommasAndSpaces1ReturnsExpected() {
        $table = 'simulation';
        $search_for = 'balloon ,static';
        $fields_to_search = array('sim_name');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(81);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_MultiSeachWordWithCommasAndSpaces2ReturnsExpected() {
        $table = 'simulation';
        $search_for = 'balloon, static';
        $fields_to_search = array('sim_name');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(81);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    public function testDbSearchFor_MultiSeachWordWithCommasAndSpaces3ReturnsExpected() {
        $table = 'simulation';
        $search_for = 'balloon , static';
        $fields_to_search = array('sim_name');
        $result = db_search_for($table, $search_for, $fields_to_search);
        $expected_sim = sim_get_sim_by_id(81);
        $this->assertEquals(1, count($result));
        $this->assertEquals($expected_sim, $result[0]);
    }

    /**
     *
     * Testing db_form_alternation_where_clause()
     *
     */

    public function testDbFormAlternationWhereClause_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_get_row_by_id()
     *
     */

    public function testDbGetRowById_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_delete_row()
     *
     */

    public function testDbDeleteRow_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_insert_row()
     *
     */

    public function testDbInsertRow_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_get_blank_row()
     *
     */

    public function testDbGetBlankRow_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_simplify_sql_timestamp()
     *
     */

    public function testDbSimplifySqlTimestamp_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_update_table()
     *
     */

    public function testDbUpdateTable_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_backup_table()
     *
     */

    public function testDbBackupTable_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_restore_table()
     *
     */

    public function testDbRestoreTable_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_get_all_table_names()
     *
     */

    public function testDbGetAllTableNames_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_backup()
     *
     */

    public function testDbBackup_TestCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing db_restore()
     *
     */

    public function testDbRestore_TestCase() {
        $this->markTestIncomplete();
    }

}

?>
