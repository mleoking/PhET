<?php

require_once('PHPUnit/Framework/TestSuite.php');
require_once('PHPUnit/Extensions/PhptTestSuite.php');

$root = dirname(__FILE__) . DIRECTORY_SEPARATOR;
require_once($root . 'PhetDBExceptionTest.php');
$root = NULL;

class include_Exception_AllTests {
    public static function suite() {
        $suite = new PHPUnit_Framework_TestSuite('include_Exception');
        
        $suite->addTestSuite('PhetDBExceptionTest');

        return $suite;
    }
}

?>