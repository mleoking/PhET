<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Include global.php and the autoloader will take care of the classes
require_once "include/global.php";

class SimFactoryTest extends PHPUnit_Framework_TestCase {
    protected $fixture;

    protected function setUp() {
        $this->fixture = SimFactory::inst();
    }

    /**
     *
     * Testing static inst()
     *
     */
    public function testInst_returnsProperClass() {
        $this->assertEquals('SimFactory', get_class($this->fixture));
    }

    /**
     *
     * Testing getById()
     *
     */

    public function testGetById_returnsJavaSimProperly() {
        $sim = $this->fixture->getById(81);
        $this->assertEquals('JavaSimulation', get_class($sim));
    }

    public function testGetById_returnsFlashSimProperly() {
        $sim = $this->fixture->getById(106);
        $this->assertEquals('FlashSimulation', get_class($sim));
    }

    public function testGetById_invalidSimThrowsException() {
        $this->setExpectedException('PhetSimException');
        $sim = $this->fixture->getById(1);
    }
}

?>
