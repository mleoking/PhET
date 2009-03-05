<?php

require_once 'PHPUnit/Framework.php';

require_once dirname(dirname(__FILE__)) . DIRECTORY_SEPARATOR . 'test_global.php';

// Do not need to include the acutal file to test, it is all tested through the web interface

// These files are needed
//require_once('include/locale-utils.php');
define("SITE_ROOT", "../");
    require_once('include/global.php');

class simJarRedirectTest extends PHPUnit_Framework_TestCase {

    const QUERY_URL = 'http://localhost/PhET/website/services/sim-jar-redirect.php';

    const GOOD_PROJECT = 'balloons';
    const GOOD_SIM = 'balloons';

    const GOOD_FOREIGN_LANGUAGE = 'cs';
    const GOOD_FOREIGN_LANGUAGE_COMBO = 'pt';
    const GOOD_FOREIGN_COUNTRY_COMBO = 'BR';
    const GOOD_FOREIGN_CONDENSED_LOCALE = 'bp';

    const BAD_PROJECT = 'BADballoons';
    const BAD_SIM = 'BADballoons';
    const BAD_FOREIGN_LANGUAGE = 'ab';
    const BAD_FOREIGN_COUNTRY = 'AX';

    const INVALID_FOREIGN_LANGUAGE = 'xx';
    const INVALID_FOREIGN_COUNTRY = 'YY';

    public function __construct() {
    }

    private function makeRequest($query_pairs, $verbose = true) {
        $query_int = array(
            'request_version=1',
            'OVERRIDE_SIMS_ROOT='.SIMS_ROOT,
            );
        foreach ($query_pairs as $key => $value) {
            $query_int[] = "{$key}={$value}";
        }

        $query = self::QUERY_URL.'?'.join('&', $query_int);
        return file_get_contents($query);
    }

    public function testPhetInfo_returnsErrorWithNoKeys() {
        $query = array(
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_returnsErrorWithIncompleteSimKeys() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_projectOnlyRequestReturnsErrorIfProjectNotFound() {
        $query = array(
            'project_only' => self::BAD_PROJECT,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_projectOnlyRequestReturnsExpectedForPostIomProjectAllJar() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            );
        $data = $this->makeRequest($query);
        $expected_data = file_get_contents(SIMS_ROOT.'balloons/balloons_all.jar');
        $this->assertEquals($expected_data, $data);
    }

    public function testPhetInfo_projectOnlyRequestReturnsErrorForProjectWithoutPostIomProjectallJar() {
        $query = array(
            'project' => 'color-vision',
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfProjectNonexsistant() {
        $query = array(
            'project' => self::BAD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfSimNonexsistant() {
        $query = array(
            'project' => self::BAD_PROJECT,
            'sim' => self::BAD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfProjectSimNonexsistant() {
        $query = array(
            'project' => self::BAD_PROJECT,
            'sim' => self::BAD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfLanguageInvalid() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::INVALID_FOREIGN_LANGUAGE,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfLanguageNonexsistant() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::BAD_FOREIGN_LANGUAGE,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfCountryInvalid() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE_COMBO,
            'country' => self::INVALID_FOREIGN_COUNTRY,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfCountryNonexsistant() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE_COMBO,
            'country' => self::BAD_FOREIGN_COUNTRY,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfLanguageCountryComboInvalid() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::INVALID_FOREIGN_LANGUAGE,
            'country' => self::INVALID_FOREIGN_COUNTRY,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfLanguageCountryComboNonexsistant() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::BAD_FOREIGN_LANGUAGE,
            'country' => self::BAD_FOREIGN_COUNTRY,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsErrorIfLongLocaleSpecifiedForLanugage() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => Locale::DEFAULT_LOCALE_LONG_FORM,
            );
        $data = $this->makeRequest($query);
        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsExpectedLongDefaultLocale() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => Locale::DEFAULT_LOCALE_LANGUAGE,
            'country' => Locale::DEFAULT_LOCALE_COUNTRY
            );
        $data = $this->makeRequest($query);
        $expected_data = file_get_contents(SIMS_ROOT.'balloons/balloons.jar');
        $this->assertEquals($expected_data, $data);
    }

    public function testPhetInfo_simRequestReturnsExpectedShortDefaultLocale() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => Locale::DEFAULT_LOCALE_LANGUAGE
            );
        $data = $this->makeRequest($query);
        $expected_data = file_get_contents(SIMS_ROOT.'balloons/balloons.jar');
        $this->assertEquals($expected_data, $data);
        //        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsExpectedValidForeignLanugage() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE
            );
        $data = $this->makeRequest($query);
        $expected_data = file_get_contents(SIMS_ROOT.'balloons/balloons_cs.jar');
        $this->assertEquals($expected_data, $data);
        //        $this->assertRegExp('/^Error:/', $data);
    }

    public function testPhetInfo_simRequestReturnsExpectedValidForeignLanugageCountryCombo() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::GOOD_FOREIGN_LANGUAGE_COMBO,
            'country' => self::GOOD_FOREIGN_COUNTRY_COMBO
            );
        $data = $this->makeRequest($query);
        $expected_data = file_get_contents(SIMS_ROOT.'balloons/balloons_bp.jar');
        $this->assertEquals($expected_data, $data);
    }

    public function testPhetInfo_simRequestReturnsExpectedValidCondensedForeignLocale() {
        $query = array(
            'project' => self::GOOD_PROJECT,
            'sim' => self::GOOD_SIM,
            'language' => self::GOOD_FOREIGN_CONDENSED_LOCALE,
            );
        $data = $this->makeRequest($query);
        $expected_data = file_get_contents(SIMS_ROOT.'balloons/balloons_bp.jar');
        $this->assertEquals($expected_data, $data);
        //        $this->assertRegExp('/^Error:/', $data);
    }

}

?>