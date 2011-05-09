<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class orderingUtilTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing order_get_all_orders()
     *
     */

    public function testOrderGetAllOrders_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing order_get_order_number_by_id()
     *
     */

    public function testOrderGetOrderNumberById_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing order_get_previous_order_number()
     *
     */

    public function testOrderGetPreviousOrderNumber_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing order_get_next_order_number()
     *
     */

    public function testOrderGetNextOrderNumber_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing order_get_sql_condition_postfix()
     *
     */

    public function testOrderGetSqlConditionPostfix_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing order_move_higher()
     *
     */

    public function testOrderMoveHigher_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing order_move_lower()
     *
     */

    public function testOrderMoveLower_testCase() {
        $this->markTestIncomplete();
    }

}

?>
