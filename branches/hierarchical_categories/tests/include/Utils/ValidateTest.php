<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Get the file to test
require_once("include/utils/Validate.class.php");

class ValidateTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing locale_remap_combined_language_code()
     *
     */

    public function setUp() {
        parent::setUp();
        $this->fixture = Validate::inst();
    }

    public function testValidInt_badValues() {
        $bad_values = array(
            '  7',  // preceeding whitespace
            '7  ',  // trailing whitespace
            '  7  ',  // preceeding and trailing whitespace
            '3.5',  // decimal number
            'hello',  // character string
            );
        
        foreach ($bad_values as $value) {
            $result = $this->fixture->validInt($value);
            $this->assertFalse($result);
        }
    }

    public function testValidInt_goodValues() {
        $good_values = array(
            3,  // type integer
            '7',  // simple number
            '243',  // longer number
            );
        
        foreach ($good_values as $value) {
            $result = $this->fixture->validInt($value);
            $this->assertTrue($result);
        }
    }

    public function testValidDate_badValues() {
        $bad_values = array(
            '  2008-04-04',  // preceeding whitespace
            '2008-04-04    ',  // trailing whitespace
            '  2008-04-04    ',  // preceeding and trailing whitespace
            '3.5',  // decimal number
            'hello',  // character string
            '2008-22-04',  // obvisouly wrong month
            '2008-02-57',  // obvisouly wrong day
            );
        
        foreach ($bad_values as $value) {
            $result = $this->fixture->validDate($value);
            $this->assertFalse($result);
        }
    }

    public function testValidDate_goodValues() {
        $good_values = array(
            '2008-03-04',  // full date
            '2008-3-4',  // date without preceeding zeros
            );
        
        foreach ($good_values as $value) {
            $result = $this->fixture->validDate($value);
            $this->assertTrue($result);
        }
    }
}

?>
