<?php

require_once('PHPUnit/Framework/TestSuite.php');

$root = dirname(__FILE__) . DIRECTORY_SEPARATOR;
require_once($root . 'include' . DIRECTORY_SEPARATOR . 'AllTests.php');
require_once($root . 'simulations' . DIRECTORY_SEPARATOR . 'AllTests.php');
require_once ($root . 'services' . DIRECTORY_SEPARATOR . 'AllTests.php');
unset($root);

class AllTests {
    public static function suite() {
        $suite = new PHPUnit_Framework_TestSuite('PHPUnit_Extensions');
    
        $suite->addTestSuite(include_AllTests::suite());
        $suite->addTestSuite(simulation_AllTests::suite());
        $suite->addTestSuite(services_AllTests::suite());
    
        return $suite;
    }
}

?>