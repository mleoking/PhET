<?php

require_once('PHPUnit/Framework/TestSuite.php');
require_once('PHPUnit/Extensions/PhptTestSuite.php');

// Convenience variable
$root = dirname(__FILE__) . DIRECTORY_SEPARATOR;

// Classes
require_once($root . 'Utils' . DIRECTORY_SEPARATOR . 'AllTests.php');
require_once($root . 'Exception' . DIRECTORY_SEPARATOR . 'AllTests.php');
//require_once($root . 'Simulation' . DIRECTORY_SEPARATOR . 'AllTests.php');

// Util files
require_once($root . 'authenticationTest.php');
require_once($root . 'cacheUtilsTest.php');
require_once($root . 'contribUtilsTest.php');
require_once($root . 'dbTest.php');
require_once($root . 'dbUtilsTest.php');
require_once($root . 'globalTest.php');
require_once($root . 'hierarchicalCategoriesTest.php');
require_once($root . 'installerUtilsTest.php');
require_once($root . 'localDebugSettingsTest.php');
require_once($root . 'localeCodesCountryTest.php');
require_once($root . 'localeCodesLanguageTest.php');
require_once($root . 'logUtilsTest.php');
require_once($root . 'nominateUtilsTest.php');
require_once($root . 'orderingUtilTest.php');
require_once($root . 'researchUtilsTest.php');
//require_once($root . 'setupAutoloadTest.php');
require_once($root . 'simUtilsTest.php');
require_once($root . 'spellTest.php');
require_once($root . 'sysUtilsTest.php');
require_once($root . 'webUtilsTest.php');
require_once($root . 'zipTest.php');

// Convenience variable no longer needed
$root = NULL;


class include_AllTests {
  public static function suite() {
    $suite = new PHPUnit_Framework_TestSuite('include');
    
    // Class tests
    $suite->addTestSuite(include_utils_AllTests::suite());
    $suite->addTestSuite(include_Exception_AllTests::suite());
    /*
    $suite->addTestSuite(include_Simulation_AllTests::suite());
    $suite->addTestSuite('dbutilsTest');
    $suite->addTestSuite('simUtilsTest');
    $suite->addTestSuite('localeUtilsTest');
    $suite->addTestSuite('installerUtilsTest');
    */

    // Funtction-bundle file tests
    $suite->addTestSuite('authenticationTest');
    $suite->addTestSuite('cacheUtilsTest');
    $suite->addTestSuite('contribUtilsTest');
    $suite->addTestSuite('dbTest');
    $suite->addTestSuite('dbUtilsTest');
    $suite->addTestSuite('globalTest');
    $suite->addTestSuite('hierarchicalCategoriesTest');
    $suite->addTestSuite('installerUtilsTest');
    $suite->addTestSuite('localDebugSettingsTest');
    $suite->addTestSuite('localeCodesCountryTest');
    $suite->addTestSuite('localeCodesLanguageTest');
    $suite->addTestSuite('logUtilsTest');
    $suite->addTestSuite('nominateUtilsTest');
    $suite->addTestSuite('orderingUtilTest');
    $suite->addTestSuite('researchUtilsTest');
    $suite->addTestSuite('simUtilsTest');
    $suite->addTestSuite('spellTest');
    $suite->addTestSuite('sysUtilsTest');
    $suite->addTestSuite('webUtilsTest');
    $suite->addTestSuite('zipTest');

    return $suite;
  }
}

?>