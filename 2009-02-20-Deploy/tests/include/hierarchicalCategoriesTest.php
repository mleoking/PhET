<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class hierarchicalCategoriesTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing __construct()
     *
     */

    public function testConstruct_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing hier_add()
     *
     */

    public function testHierAdd_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing init_hier_cats()
     *
     */

    public function testInitHierCats_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing generate_sql_orders()
     *
     */

    public function testGenerateSqlOrders_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing commit_orders()
     *
     */

    public function testCommitOrders_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_hier_cats()
     *
     */

    public function testGetHierCats_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_possible_parent_names()
     *
     */

    public function testGetPossibleParentNames_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing walk()
     *
     */

    public function testWalk_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing do_new_parent()
     *
     */

    public function testDoNewParent_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing find_stuff_containing_id0()
     *
     */

    public function testFindStuffContainingId0_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing move_up()
     *
     */

    public function testMoveUp_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing move_down()
     *
     */

    public function testMoveDown_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing go_one_up()
     *
     */

    public function testGoOneUp_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing go_one_down()
     *
     */

    public function testGoOneDown_testCase() {
        $this->markTestIncomplete();
    }

}

?>
