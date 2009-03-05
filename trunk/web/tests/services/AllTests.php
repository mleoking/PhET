<?php

  //error_reporting(E_ALL | E_STRICT);

require_once 'PHPUnit/Framework/TestSuite.php';
require_once 'PHPUnit/Extensions/PhptTestSuite.php';

$root = dirname(__FILE__) . DIRECTORY_SEPARATOR;
require_once($root . 'phetInfoTest.php');
require_once($root . 'simJarRedirectTest.php');
unset($root);

class services_AllTests {
  public static function suite() {
    $suite = new PHPUnit_Framework_TestSuite('simulation');
    
  $suite->addTestSuite('simJarRedirectTest');
  $suite->addTestSuite('phetInfoTest');
    
    return $suite;
  }
}

?>
