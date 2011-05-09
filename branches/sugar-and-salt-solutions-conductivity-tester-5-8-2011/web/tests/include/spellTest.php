<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class spellTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing spell_is_valid_word()
     *
     */

    public function testSpellIsValidWord_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing test_spell_check()
     *
     */

    public function testTestSpellCheck_testCase() {
        $this->markTestIncomplete();
    }

}

?>
