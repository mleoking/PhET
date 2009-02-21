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
    const VALID=1103;

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

    private function makeRequest($xml, $request_key = self::REQUEST_KEY, $verbose = false) {
        $query = array();
        $query[$request_key] = $xml;


        // Special override to the script to make it use the test database
        $query['PHET-TEST-DEFINE-OVERRIDE'] = 'DB_HOSTNAME::localhost~~~~DB_NAME::phet_test~~~~DB_USERNAME::phet_test~~~~DB_PASSWORD::~~~~SIMS_ROOT::'.SIMS_ROOT;

        if ($verbose) {
            $query['verbose'] = 1;
        }

        $request_content = http_build_query($query);

        $request_parameters = array(
            'method' => 'POST',
            'header' => 'Content-type: application/x-www-form-urlencoded',
            'content' => $request_content,
            );

        $http_context = array('http' => $request_parameters);

        $stream = stream_context_create($http_context);

        // Make the request and return the data
        return file_get_contents(self::QUERY_URL, 0, $stream);
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

    private function getVersionRequest($version_request, $empty_tag) {
        $tag = 'sim_version';

        switch ($version_request) {
            case self::VALID:
                $guts = "project=\"{$this->requested_project}\" sim=\"{$this->requested_sim}\"";
                break;
            case self::INVALID_MISSING_ATTR:
                // Asking for nonexistent info
                $guts = "";
                break;
            case self::INVALID_BAD_ATTR:
                // Asking for nonexistent info
                $guts = "project=\"{$this->nonexistent_requested_project}\" sim=\"{$this->nonexistent_requested_sim}\"";
                break;
            case self::NONE:
                return '';
            default:
                throw new RuntimeException("Invalid value passed to version_request in constructGoodEmptyTagXML");
        }
    
        if ($empty_tag) {
            return "<{$tag} $guts />";
        }
        else {
            return "<{$tag} $guts></{$tag}>";
        }
    }

    private function getInstallerRequest($installer_request, $empty_tag) {
        $tag = 'phet_installer_update';
        switch ($installer_request) {
            case self::VALID:
                $guts = 'timestamp_seconds="'.$this->installer_timestamp.'"';
                break;
            case self::INVALID_MISSING_ATTR:
                $guts = '';
                break;
            case self::INVALID_BAD_ATTR:
                $guts = 'timestamp_seconds="JUST-RECENTLY"';
                break;
            case self::NONE:
                return '';
            default:
                throw new RuntimeException("Invalid value passed to version_request in constructGoodEmptyTagXML");
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
        $data = $this->makeRequest($not_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertType('object', $xml);
        $this->assertEquals('SimpleXMLElement', get_class($xml));
    }

    public function testPhetInfo_nonXMLRequestReturnsValidXMLWithError() {
        $not_xml = 'this is not XML';
        $data = $this->makeRequest($not_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertRegExp('/^badly formed XML$/', (string) $xml['error']);
    }

    public function testPhetInfo_noRequestKeyReturnsError() {
        $request_xml = $this->constructBadQuotesXML();
        $data = $this->makeRequest($request_xml, 'bad_request_key');
        $xml = new SimpleXMLElement($data);
        $this->assertRegExp("/^'request' key not found$/", (string) $xml['error']);
    }

    public function testPhetInfo_badQuoteXMLReturnsError() {
        $request_xml = $this->constructBadQuotesXML();
        $data = $this->makeRequest($request_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertRegExp('/^badly formed XML$/', (string) $xml['error']);
    }

    public function testPhetInfo_dashedXMLTagsReturnsError() {
        $request_xml = $this->constructBadDashesXML();
        $data = $this->makeRequest($request_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertRegExp('/^underscores needed in XML tags$/', (string) $xml['error']);
    }

    public function testPhetInfo_badRootElementNameReturnsError() {
        $request_xml = $this->constructBadRootElementNameXML();
        $data = $this->makeRequest($request_xml);
        $xml = new SimpleXMLElement($data);
        $this->assertRegExp('/^XML root element name invalid$/', (string) $xml['error']);
    }

    public function testPhetInfo_noVersionRequestDoesNotGiveResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertFalse(isset($response_xml->sim_version_response));
    }

    public function testPhetInfo_incompleteVersionRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::INVALID_MISSING_ATTR);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->sim_version_response[0];
        $this->assertRegExp('/^required tags not specified: project, sim$/i', (string) $info['error']);
    }

    public function testPhetInfo_badVersionRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::INVALID_BAD_ATTR);
        $data = $this->makeRequest($request_xml, self::REQUEST_KEY);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->sim_version_response[0];
        $this->assertRegExp('#^project and/or sim does not exist: "'.self::NONEXISTENT_REQUESTED_PROJECT.'", "'.self::NONEXISTENT_REQUESTED_SIM.'"$#i', (string) $info['error']);
    }

    public function testPhetInfo_versionRequestGivesResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::VALID);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue(isset($response_xml->sim_version_response));
    }

    public function testPhetInfo_versionRequestGivesValidResponse() {
        $request_xml = $this->constructGoodFullTagXML(self::VALID);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->sim_version_response[0];
        $this->assertEquals(self::REQUESTED_PROJECT, (string) $info['project']);
        $this->assertEquals(self::REQUESTED_SIM, (string) $info['sim']);
        $this->assertRegExp('/^[0-9]+\.[0-9]+\.[0-9]+$/', (string) $info['version']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['revision']);
        $this->assertRegExp('/^[0-9]+$/', (string) $info['timestamp_seconds']);
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
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->sim_version_response[0];
        $this->assertEquals(self::REQUESTED_PROJECT, (string) $info['project']);
        $this->assertEquals(self::REQUESTED_SIM, (string) $info['sim']);
        $this->assertRegExp('/^1\.08\.00$/', (string) $info['version']);
        $this->assertRegExp('/^28720$/', (string) $info['revision']);
        $this->assertRegExp('/^1235020762$/', (string) $info['timestamp_seconds']);
        $this->assertRegExp('/^12342$/', (string) $info['ask_me_later_duration_days']);
    }

    public function testPhetInfo_noInstallerRequestDoesNotGiveResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::NONE);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertFalse(isset($response_xml->phet_installer_update_response));
    }

    public function testPhetInfo_incompleteInstallerRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::INVALID_MISSING_ATTR);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertRegExp('/^required tags not specified: timestamp_seconds$/i', (string) $info['error']);
    }

    public function testPhetInfo_badInstallerRequestGivesResponseWithError() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::INVALID_BAD_ATTR);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->phet_installer_update_response[0];
        $this->assertRegExp('/^timestamp_seconds is invalid$/i', (string) $info['error']);
    }

    public function testPhetInfo_installerRequestGivesResponseTag() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $this->assertTrue(isset($response_xml->phet_installer_update_response));
    }

    public function testPhetInfo_installerRequestGivesValidResponse() {
        $request_xml = $this->constructGoodFullTagXML(self::NONE, self::VALID);
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->phet_installer_update_response[0];
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
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->phet_installer_update_response[0];

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
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->phet_installer_update_response[0];

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
        $data = $this->makeRequest($request_xml);
        $response_xml = new SimpleXMLElement($data);
        $info = $response_xml->phet_installer_update_response[0];

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