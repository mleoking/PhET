<?php

require_once 'PHPUnit/Framework.php';

require_once dirname(dirname(__FILE__)) . DIRECTORY_SEPARATOR . 'test_global.php';

// Do not need to include a file to test, it is all tested through the web interface
require_once('include/installer-utils.php');

class phetInfoTest extends PHPUnit_Framework_TestCase {

    const QUERY_URL = 'http://localhost/PhET/website/services/phet-info.php';

    const REQUESTED_PROJECT = 'balloons';
    //const REQUESTED_PROJECT = 'faraday';
    const REQUESTED_SIM = 'balloons';
    //const REQUESTED_SIM = 'magnet-and-compass';

    const NONEXISTENT_REQUESTED_PROJECT = 'nonexistent_project_faraday';
    const NONEXISTENT_REQUESTED_SIM = 'nonexistent_sim_magnet-and-compass';

    const NONE = 1100;
    const INVALID_MISSING_ATTR = 1101;
    const INVALID_BAD_ATTR = 1102;
    const INVALID_REQUEST_VERSION = 1103;
    const VALID=1104;

    const REQUEST_KEY = 'request';

    const INSTALLER_TIMESTAMP_AGE_IN_DAYS = 10;

    public function __construct() {
        // Convenience to access inside strings and heredocs
        $this->requested_project = self::REQUESTED_PROJECT;
        $this->requested_sim = self::REQUESTED_SIM;
        $this->nonexistent_requested_project = self::NONEXISTENT_REQUESTED_PROJECT;
        $this->nonexistent_requested_sim = self::NONEXISTENT_REQUESTED_SIM;

        $days_to_secs = 60 * 60 * 24;
        $this->installer_timestamp = time() -
            (self::INSTALLER_TIMESTAMP_AGE_IN_DAYS * $days_to_secs);
    }

    private function getPlainTextRequestParameters($xml) {
        // Setup submitting the POST elements
        return array(
            'method' => 'POST',
            'header' => 'Content-type: text/xml',
            'content' => $xml
            );
    }

    private function getRequestOverrides() {
        return array('PHET-DEFINE-OVERRIDE-DB_HOSTNAME' => 'localhost',
                     'PHET-DEFINE-OVERRIDE-DB_NAME' => 'phet_test',
                     'PHET-DEFINE-OVERRIDE-DB_USERNAME' => 'phet_test',
                     'PHET-DEFINE-OVERRIDE-DB_PASSWORD' => '',
                     'PHET-DEFINE-OVERRIDE-SIMS_ROOT' => SIMS_ROOT
            );
    }

    private function makeRequest($request_parameters, $verbose = false) {
        $query = $this->getRequestOverrides();
        if ($verbose) {
            $query['verbose'] = 1;
        }

        $query_string = http_build_query($query);

        $http_context = array('http' => $request_parameters);

        $stream = stream_context_create($http_context);

        // Make the request and return the data
        return file_get_contents(self::QUERY_URL.'?'.$query_string, 0, $stream);        
    }

    private function makePlainTextRequest($xml, $verbose = false) {
        return $this->makeRequest(
            $this->getPlainTextRequestParameters($xml),
            $verbose);
    }

    private function constructBadQuotesXML() {
        // Don't quote attributes, get an error
        return <<<EOT
<?xml version="1.0"?>
<phet_info>
            <sim_version project={$this->requested_project} sim={$this->requested_sim}/>
<phet_installer_update timestamp_seconds="{$this->installer_timestamp}"/>
</phet_info>
EOT;
    }

    private function constructBadDashesXML() {
        // Use dashed '-' attributes, get an error
        return <<<EOT
<?xml version="1.0"?>
<phet-info>
<sim-version project="{$this->requested_project}" sim="{$this->requested_sim}"/>
<phet-installer-update timestamp-seconds="{$this->installer_timestamp}"/>
</phet-info>
EOT;
    }

    private function constructBadRootElementNameXML() {
        // Give a bad root element name, get an error
        return <<<EOT
<?xml version="1.0"?>
<sim_start_cool_simulation_query>
<sim_version project="{$this->requested_project}" sim="{$this->requested_sim}"/>
<phet_installer_update timestamp_seconds="{$this->installer_timestamp}"/>
</sim_start_cool_simulation_query>
EOT;
    }

    private function getVersionRequest($test_request_type, $empty_tag) {
        $tag = 'sim_version';

        switch ($test_request_type) {
            case self::VALID:
                $guts = "request_version=\"1\" project=\"{$this->requested_project}\" sim=\"{$this->requested_sim}\"";
                break;
            case self::INVALID_MISSING_ATTR:
                // Asking for nonexistent info
                $guts = "request_version=\"1\"";
                break;
            case self::INVALID_BAD_ATTR:
                // Asking for nonexistent info
                $guts = "request_version=\"1\" project=\"{$this->nonexistent_requested_project}\" sim=\"{$this->nonexistent_requested_sim}\"";
                break;
            case self::INVALID_REQUEST_VERSION:
                $guts = "request_version=\"999\" project=\"{$this->requested_project}\" sim=\"{$this->requested_sim}\"";
                break;
            case self::NONE:
                return '';
            default:
                throw new RuntimeException("Invalid value passed to test_request_type in constructGoodEmptyTagXML");
        }

        if ($empty_tag) {
            return "<{$tag} $guts />";
        }
        else {
            return "<{$tag} $guts></{$tag}>";
        }
    }

    private function getInstallerRequest($test_request_type, $empty_tag) {
        $tag = 'phet_installer_update';

        switch ($test_request_type) {
            case self::VALID:
                $guts = 'request_version="1" timestamp_seconds="'.$this->installer_timestamp.'"';
                break;
            case self::INVALID_MISSING_ATTR:
                $guts = 'request_version="1"';
                break;
            case self::INVALID_BAD_ATTR:
                $guts = 'request_version="1" timestamp_seconds="JUST-RECENTLY"';
                break;
            case self::INVALID_REQUEST_VERSION:
                $guts = 'request_version="999" timestamp_seconds="'.$this->installer_timestamp.'"';
                break;
            case self::NONE:
                return '';
            default:
                throw new RuntimeException("Invalid value passed to test_request_type in constructGoodEmptyTagXML");
        }

        if ($empty_tag) {
            return "<{$tag} $guts />";
        }
        else {
            return "<{$tag} $guts></{$tag}>";
        }
    }

    private function constructGoodEmptyTagXML($version_request = self::NONE, $installer_request = self::NONE) {

        $version_xml = $this->getVersionRequest($version_request, true);
        $installer_xml = $this->getInstallerRequest($installer_request, true);

        return <<<EOT
<?xml version="1.0"?>
<phet_info>
{$version_xml}
{$installer_xml}
</phet_info>
EOT;
    }

    private function constructGoodFullTagXML($version_request = self::NONE, $installer_request = self::NONE) {
        $version_xml = $this->getVersionRequest($version_request, false);
        $installer_xml = $this->getInstallerRequest($installer_request, false);

        return <<<EOT
<?xml version="1.0"?>
<phet_info>
{$version_xml}
{$installer_xml}
</phet_info>
EOT;
    }

    private function getSuccessStatus($xml) {
        $this->assertTrue(isset($xml['success']), 'success attribute does not exist');
        return 'true' == ((string) $xml['success']);
    }

    private function hasError($xml, $path, $error_regex) {
        $success = false;
        foreach ($xml->xpath($path.'/error') as $error) {
            if (preg_match($error_regex, (string) $error)) {
                return true;
            }
        }

        return false;
    }

    public function setUp() {
        parent::setUp();
        $settings = array(
            'sim_ask_later_duration' => 1234,
            'install_ask_later_duration' => 11235,
            'install_recommend_update_age' => 1876,
            'install_recommend_update_date' => '1999-04-07',
             );
        $result = UpdateUtils::inst()->setSettings($settings);
        $this->assertTrue($result);
    }

    public function testPhetInfo_nonXMLRequestReturnsValidXML() {
        $not_xml = 'this is not XML';
        $data = $this->makePlainTextRequest($not_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertType('object', $xml);
        $this->assertEquals('SimpleXMLElement', get_class($xml));
    }

    public function testPhetInfo_nonXMLRequestReturnsValidXMLWithError() {
        $not_xml = 'this is not XML';
        $data = $this->makePlainTextRequest($not_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertFalse($this->getSuccessStatus($xml));
        $this->assertTrue(
            $this->hasError(
                $xml,
                '/phet_info_response',
                '/^badly formed XML$/')
            );
    }

    public function testPhetInfo_badQuoteXMLReturnsError() {
        $request_xml = $this->constructBadQuotesXML();
        $data = $this->makePlainTextRequest($request_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertFalse($this->getSuccessStatus($xml));
        $this->assertTrue(
            $this->hasError(
                $xml,
                '/phet_info_response',
                '/^badly formed XML$/')
            );
    }

    public function testPhetInfo_dashedXMLTagsReturnsError() {
        $request_xml = $this->constructBadDashesXML();
        $data = $this->makePlainTextRequest($request_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertFalse($this->getSuccessStatus($xml));
        $this->assertTrue(
            $this->hasError(
                $xml,
                '/phet_info_response',
                '/^underscores needed in XML tags$/')
            );
    }

    public function testPhetInfo_badRootElementNameReturnsError() {
        $request_xml = $this->constructBadRootElementNameXML();
        $data = $this->makePlainTextRequest($request_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertFalse($this->getSuccessStatus($xml));
        $this->assertTrue(
            $this->hasError(
                $xml,
                '/phet_info_response',
                '/^XML root element name invalid$/')
            );
    }

    public function testPhetInfo_noVersionRequestDoesNotGiveResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $this->assertFalse(isset($response_xml->sim_version_response));
    }

    public function testPhetInfo_incompleteVersionRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::INVALID_MISSING_ATTR);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->sim_version_response[0];
        $this->assertFalse($this->getSuccessStatus($info));
        $this->assertTrue(
            $this->hasError(
                $info,
                '/phet_info_response/sim_version_response',
                '/^required tags not specified: project, sim$/i')
            );
    }

    public function testPhetInfo_badVersionRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::INVALID_BAD_ATTR);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->sim_version_response[0];
        $this->assertFalse($this->getSuccessStatus($info));
        $this->assertTrue(
            $this->hasError(
                $info,
                '/phet_info_response/sim_version_response',
                '#^project and/or sim does not exist: "'.self::NONEXISTENT_REQUESTED_PROJECT.'", "'.self::NONEXISTENT_REQUESTED_SIM.'"$#i')
            );
    }

    public function testPhetInfo_badVersionRequestVersionGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::INVALID_REQUEST_VERSION);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->sim_version_response[0];
        $this->assertFalse($this->getSuccessStatus($info));
        $this->assertTrue(
            $this->hasError(
                $info,
                '/phet_info_response/sim_version_response',
                '#^Invalid request version$#i')
            );
    }

    public function testPhetInfo_versionRequestGivesResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $this->assertTrue(isset($response_xml->sim_version_response));
    }

    public function testPhetInfo_versionRequestGivesValidResponse() {
        $request_xml = $this->constructGoodFullTagXML(self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->sim_version_response[0];
        $this->assertTrue($this->getSuccessStatus($info));
        $this->assertEquals(self::REQUESTED_PROJECT, (string) $info['project']);
        $this->assertEquals(self::REQUESTED_SIM, (string) $info['sim']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['version_major']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['version_minor']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['version_dev']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['version_revision']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['version_timestamp']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['ask_me_later_duration_days']);
    }

    public function testPhetInfo_versionRequestGivesExpected() {
        $days_to_secs = 60 * 60 * 24;
        $recommend_date = date('Y-m-d', (time() - (5 * $days_to_secs)));
        $settings = array(
            'sim_ask_later_duration' => 12342,
            'install_ask_later_duration' => 11235,
            'install_recommend_update_age' => 25,
            'install_recommend_update_date' => $recommend_date,
            );
        UpdateUtils::inst()->setSettings($settings);

        $request_xml = $this->constructGoodFullTagXML(self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->sim_version_response[0];
        $this->assertTrue($this->getSuccessStatus($info));
        $this->assertEquals(self::REQUESTED_PROJECT, (string) $info['project']);
        $this->assertEquals(self::REQUESTED_SIM, (string) $info['sim']);
        $this->assertRegExp('/^1$/', (string) $info['version_major']);
        $this->assertRegExp('/^08$/', (string) $info['version_minor']);
        $this->assertRegExp('/^00$/', (string) $info['version_dev']);
        $this->assertRegExp('/^28720$/', (string) $info['version_revision']);
        $this->assertRegExp('/^1235020762$/', (string) $info['version_timestamp']);
        $this->assertRegExp('/^12342$/', (string) $info['ask_me_later_duration_days']);
    }

    public function testPhetInfo_noInstallerRequestDoesNotGiveResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::NONE);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $this->assertFalse(isset($response_xml->phet_installer_update_response));
    }

    public function testPhetInfo_incompleteInstallerRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::INVALID_MISSING_ATTR);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertFalse($this->getSuccessStatus($info));
        $this->assertTrue(
            $this->hasError(
                $info,
                '/phet_info_response/phet_installer_update_response',
                '/^required tags not specified: timestamp_seconds$/i')
            );
    }

    public function testPhetInfo_badInstallerRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::INVALID_BAD_ATTR);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertFalse($this->getSuccessStatus($info));
        $this->assertTrue(
            $this->hasError(
                $info,
                '/phet_info_response/phet_installer_update_response',
                '/^timestamp_seconds is invalid$/i')
            );
    }

    public function testPhetInfo_badInstallerRequestVersionGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::INVALID_REQUEST_VERSION);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertFalse($this->getSuccessStatus($info));
        $this->assertTrue(
            $this->hasError(
                $info,
                '/phet_info_response/phet_installer_update_response',
                '/^Invalid request version/i')
            );
    }

    public function testPhetInfo_installerRequestGivesResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $this->assertTrue(isset($response_xml->phet_installer_update_response));
    }

    public function testPhetInfo_installerRequestGivesValidResponse() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertTrue($this->getSuccessStatus($info));
        $this->assertRegExp('/^(true|false)$/i', (string) $info['recommend_update']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^[0-9]+/', (string) $info['ask_me_later_duration_days']);
    }

    public function testPhetInfo_installerDoesNotRecommendUpgrade() {
        // Date of newest installer
        $newest_installer = 123450;
        installer_add_new_timestamp($newest_installer);
        $days_to_secs = 60 * 60 * 24;
        $recommend_date = date('Y-m-d', (time() - (25 * $days_to_secs)));
        $settings = array(
            'sim_ask_later_duration' => 1234,
            'install_ask_later_duration' => 11235,
            'install_recommend_update_age' => 25,
            'install_recommend_update_date' => $recommend_date,
            );

        UpdateUtils::inst()->setSettings($settings);
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertTrue($this->getSuccessStatus($info));

        // First just check a valid response
        $this->assertRegExp('/^(true|false)$/i', (string) $info['recommend_update']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^[0-9]+/', (string) $info['ask_me_later_duration_days']);

        // Now check specifically
        $this->assertRegExp('/^false$/i', (string) $info['recommend_update']);
        $this->assertRegExp("/^{$newest_installer}$/", (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^11235$/', (string) $info['ask_me_later_duration_days']);
    }

    public function testPhetInfo_recommesUpgradeByAge() {
        // Date of newest installer
        $newest_installer = 123450;
        installer_add_new_timestamp($newest_installer);
        $days_to_secs = 60 * 60 * 24;
        $recommend_date = date('Y-m-d', (time() - (25 * $days_to_secs)));
        $settings = array(
            'sim_ask_later_duration' => 1234,
            'install_ask_later_duration' => 11235,
            'install_recommend_update_age' => 5,
            'install_recommend_update_date' => $recommend_date,
            );

        UpdateUtils::inst()->setSettings($settings);
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertTrue($this->getSuccessStatus($info));

        // First just check a valid response
        $this->assertRegExp('/^(true|false)$/i', (string) $info['recommend_update']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^[0-9]+/', (string) $info['ask_me_later_duration_days']);

        // Now check specifically
        $this->assertRegExp('/^true$/i', (string) $info['recommend_update']);
        $this->assertRegExp("/^{$newest_installer}$/", (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^11235$/', (string) $info['ask_me_later_duration_days']);
    }

    public function testPhetInfo_recommendsUpgradeByDate() {
        // Date of newest installer
        $newest_installer = 123450;
        installer_add_new_timestamp($newest_installer);
        $days_to_secs = 60 * 60 * 24;
        $recommend_date = date('Y-m-d', (time() - (5 * $days_to_secs)));
        $settings = array(
            'sim_ask_later_duration' => 1234,
            'install_ask_later_duration' => 11235,
            'install_recommend_update_age' => 25,
            'install_recommend_update_date' => $recommend_date,
            );

        UpdateUtils::inst()->setSettings($settings);
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makePlainTextRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue($this->getSuccessStatus($response_xml));
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertTrue($this->getSuccessStatus($info));

        // First just check a valid response
        $this->assertRegExp('/^(true|false)$/i', (string) $info['recommend_update']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^[0-9]+/', (string) $info['ask_me_later_duration_days']);

        // Now check specifically
        $this->assertRegExp('/^true/i', (string) $info['recommend_update']);
        $this->assertRegExp("/^{$newest_installer}$/", (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^11235$/', (string) $info['ask_me_later_duration_days']);
    }

}

?>