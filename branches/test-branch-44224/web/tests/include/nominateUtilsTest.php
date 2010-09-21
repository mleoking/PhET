<?php

require_once('PHPUnit/Framework.php');

// SITE_ROOT - Relative path to website's top directory from the tested file
if (!defined('SITE_ROOT')) define('SITE_ROOT', '../');

// Get the test globals to set everything up
require_once(dirname(dirname(__FILE__)).'/test_global.php');

class nominateUtilsTest extends PHPUnit_Framework_TestCase {
    /**
     *
     * Testing is_contribution_nominated_by()
     *
     */

    public function testIsContributionNominatedBy_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_all_nominations_for_contribution()
     *
     */

    public function testGetAllNominationsForContribution_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_nomination_count_for_contribution()
     *
     */

    public function testGetNominationCountForContribution_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing nominate_contribution()
     *
     */

    public function testNominateContribution_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_nomination_statistics()
     *
     */

    public function testGetNominationStatistics_testCase() {
        $this->markTestIncomplete();
    }

    /**
     *
     * Testing get_nomination_descriptions()
     *
     */

    public function testGetNominationDescriptions_testCase() {
        $this->markTestIncomplete();
    }

}

?>
