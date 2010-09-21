<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Include global.php and the autoloader will take care of the classes
require_once "include/global.php";

class SimulationHTMLDecoratorTest extends PHPUnit_Framework_TestCase {
    const SIM_ID = 106;
    protected $fixture;

    protected function setUp() {
        $this->sim = SimFactory::inst()->getById(self::SIM_ID);
        $this->fixture = new SimulationHTMLDecorator($this->sim);
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
        $this->assertEquals('Masses &amp; Springs', $this->fixture->getName());
    }

    /**
     *
     * Testing getScreenshotUrl()
     *
     */

    public function testGetScreenshotUrl_returnsExpected() {
        // TODO: this is not correct, it should return an HTML formatted URL
        // Note: We never have this case in our code, but we should always do it right
        $this->assertEquals($this->sim->getScreenshotUrl(),
                            $this->fixture->getScreenshotUrl());
    }

    public function testGetLaunchUrl() {
    }

    public function testGuidanceRecommended_sameAsSimulation() {
        $this->assertEquals($this->sim->guidanceRecommended(),
                            $this->fixture->guidanceRecommended());
    }

    public function testGetGuidanceRecommendedAnchor_returnsExpected() {
        $expected = '<a href="../about/legend.php"><img src="../images/sims/ratings/crutch25x25.png" alt="Not standalone" width="37" title="Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity."/></a>'; 
        $this->assertEquals($expected, $this->fixture->getGuidanceAnchor());
    }

    public function testGetGuidanceRecommendedImageTag_returnsExpected() {
        $expected = '<img src="../images/sims/ratings/crutch25x25.png" alt="Not standalone" width="37" title="Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity."/>'; 
        $this->assertEquals($expected, $this->fixture->getGuidanceImageTag());
    }

    public function testGetGuidanceRecommendedImageUrl_returnsExpected() {
        $expected = SITE_ROOT.'images/sims/ratings/crutch25x25.png';
        $this->assertEquals($expected, $this->fixture->getGuidanceImageUrl());
    }

    public function testGetGuidanceRecommendedDescription_returnsExpected() {
        $expected = 'Guidance Recommended: This simulation is very effective when used in conjunction with a lecture, homework or other teacher designed activity.'; 
        $this->assertEquals($expected, $this->fixture->getGuidanceDescription());
    }
}

?>