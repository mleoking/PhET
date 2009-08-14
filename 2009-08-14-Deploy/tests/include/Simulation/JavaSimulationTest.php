<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Include global.php and the autoloader will take care of the classes
require_once "include/global.php";

class JavaSimulationTest extends PHPUnit_Framework_TestCase {
    const SIM_ID = 81;
    protected $fixture;

    protected function setUp() {
        $this->fixture = SimFactory::inst()->getById(self::SIM_ID);
    }

    /**
     *
     * Testing getId()
     *
     */

    public function testGetId_returnsExpected() {
        $this->assertEquals(self::SIM_ID, $this->fixture->getId());
    }

    /**
     *
     * Testing getName()
     *
     */

    public function testGetName_returnsExpected() {
        $this->assertEquals('Balloons and Static Electricity', $this->fixture->getName());
    }

    /**
     *
     * Testing getScreenshotUrl()
     *
     */

    public function testGetScreenshotUrl_returnsExpected() {
        $this->assertEquals(SIMS_ROOT.'balloons/balloons-screenshot.png', $this->fixture->getScreenshotUrl());
    }

    /**
     *
     * Testing getLaunchUrl()
     *
     */

    public function testGetLaunchUrl_default() {
        $expected = SIMS_ROOT.'balloons/balloons_en.jnlp';
        $launch_url = $this->fixture->getLaunchUrl(Locale::DEFAULT_LOCALE);
        $this->assertEquals($expected, $launch_url);
    }
}

?>