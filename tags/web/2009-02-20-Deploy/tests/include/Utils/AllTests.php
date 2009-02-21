<?php

  //error_reporting(E_ALL | E_STRICT);

require_once 'PHPUnit/Framework/TestSuite.php';
require_once 'PHPUnit/Extensions/PhptTestSuite.php';

require_once dirname(__FILE__) . DIRECTORY_SEPARATOR . 'UpdateUtilsTest.php';
require_once dirname(__FILE__) . DIRECTORY_SEPARATOR . 'ValidateTest.php';

class include_utils_AllTests {
  public static function suite() {
    $suite = new PHPUnit_Framework_TestSuite('include_utils');
    
    $suite->addTestSuite('UpdateUtilsTest');
    $suite->addTestSuite('ValidateTest');
    
    return $suite;
  }
}

?>