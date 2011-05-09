<?php

require_once('PHPUnit/Framework/TestSuite.php');
require_once('PHPUnit/Extensions/PhptTestSuite.php');

$root = dirname(__FILE__) . DIRECTORY_SEPARATOR;
require_once($root . 'SimFactoryTest.php');
require_once($root . 'JavaSimulationTest.php');
require_once($root . 'SimulationHTMLDecoratorTest.php');
$root = NULL;

class include_Simulation_AllTests {
    public static function suite() {
        $suite = new PHPUnit_Framework_TestSuite('include_Simulation');
    
        $suite->addTestSuite('SimFactoryTest');
        $suite->addTestSuite('JavaSimulationTest');
        $suite->addTestSuite('SimulationHTMLDecoratorTest');

        return $suite;
    }
}

?>