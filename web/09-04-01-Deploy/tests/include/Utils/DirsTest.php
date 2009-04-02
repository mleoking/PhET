<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(dirname(__FILE__))).'/test_global.php');

// Include global.php and the autoloader will take care of the classes
require_once "include/global.php";

    error_reporting(E_ALL);
    ini_set("display_errors", 1);

class DirsTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing locale_remap_combined_language_code()
     *
     */

    public function testRoots_immutableReturnsExpected() {
        $this->fixture = Dirs::inst(true);
        $pairs = array(
            SITE_ROOT => 'siteRoot',
            PORTAL_ROOT => 'portalRoot',
            CACHE_ROOT.CACHE_DIRNAME.DIRECTORY_SEPARATOR => 'cacheDir',
            SIMS_ROOT => 'simsRoot',
            PHET_DIST_ROOT => 'phetDistRoot',
            PHET_DIST_ROOT.'newsletters'.DIRECTORY_SEPARATOR => 'newslettersRoot'
            );
        foreach ($pairs as $expected => $func) {

            $this->assertEquals($expected, $this->fixture->$func());
        }
        $this->fixture->clear();
    }

    public function testRoots_immutableThrowsExceptionWhenSet() {
        $this->fixture = Dirs::inst(true);
        $pairs = array(
            SITE_ROOT => 'setSiteRoot',
            PORTAL_ROOT => 'setPortalRoot',
            // CACHE_ROOT not setable
            SIMS_ROOT => 'setSimsRoot',
            PHET_DIST_ROOT => 'setPhetDistRoot'
            );
        $exceptions_caught = 0;
        foreach ($pairs as $expected => $func) {
            try {
                $this->fixture->$func('DIFFERENT DIR');
            }
            catch (PhetException $e){
                ++$exceptions_caught;
            }
        }
        $this->assertEquals(count($pairs), $exceptions_caught);
        $this->fixture->clear();
    }

    public function testRoots_volitileReturnsExpected() {
        $this->fixture = Dirs::inst(false);
        $pairs = array(
            SITE_ROOT => 'siteRoot',
            PORTAL_ROOT => 'portalRoot',
            // CACHE_ROOT not settable
            SIMS_ROOT => 'simsRoot',
            PHET_DIST_ROOT => 'phetDistRoot'
            );
        foreach ($pairs as $expected => $func) {
            $this->assertEquals($expected, $this->fixture->$func());
        }
        $this->fixture->clear();
    }

    public function testRoots_volitileAllowsChanges() {
        $dir = 'DIFFERENT DIR';
        $this->fixture = Dirs::inst(false);
        $pairs = array(
            SITE_ROOT => 'setSiteRoot',
            PORTAL_ROOT => 'setPortalRoot',
            // CACHE_ROOT not settable
            SIMS_ROOT => 'setSimsRoot',
            PHET_DIST_ROOT => 'setPhetDistRoot'
            );
        foreach ($pairs as $expected => $func) {
            $this->fixture->$func($dir);
            $this->assertEquals($dir, $this->fixture->siteRoot());
        }
        $this->fixture->clear();
    }
}

?>
