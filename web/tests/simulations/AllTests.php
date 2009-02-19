<?php

  //error_reporting(E_ALL | E_STRICT);

require_once 'PHPUnit/Framework/TestSuite.php';
require_once 'PHPUnit/Extensions/PhptTestSuite.php';


class simulation_AllTests {
  public static function suite() {
    $suite = new PHPUnit_Framework_TestSuite('simulation');
    
    return $suite;
  }
}

?>
