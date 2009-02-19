<?php

  // SPECIAL TEST EXTENSION:
  //
  // Because the unit tests are testing throught the web interface, it
  // can't set up the database to force certian conditions to be met.
  // So if the there is a key 'PHET-TEST-OVERRIDE' then force the
  // override specified.
  //
  // Key/Value pairs delimited with '::', and pairs are delimited with '::::'
  //
  // Examlpe:
  // PHET-TEST-DEFINE-OVERRIDE=DB_HOSTNAME::localhost~~~~DB_NAME::phet_test~~~~DB_USERNAME::phet_test~~~~DB_PASSWORD::~~~~SIMS_ROOT::/etc/etc/etc...

if (isset($_REQUEST['PHET-TEST-DEFINE-OVERRIDE'])) {
    $override = array();
    $params = split('~~~~', $_REQUEST['PHET-TEST-DEFINE-OVERRIDE']);
    foreach ($params as $param) {
        $pair = split('::', $param);
        $key = $pair[0];
        $value = $pair[1];
        $override[$key] = $value;
    }

    foreach ($override as $key => $value) {
        define($key, $value);
    }
}

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");
require_once('include/sys-utils.php');
require_once('include/sim-utils.php');


class PhetInfo {
    const ROOT_ELEMENT_NAME = 'phet_info';
    const QUERY_XML_KEY = 'request';
    const RESPONSE_XML_SHELL =
        '<?xml version="1.0"?><phet_info_response></phet_info_response>';
    const ERROR_RESPONSE_XML_SHELL =
        '<?xml version="1.0"?><phet_info_response></phet_info_response>';

    public function __construct($verbose) {
        $this->verbose = $verbose;
    }

    private function addErrorAttribute(&$tag, $error) {
        $tag->addAttribute('error', $error);
    }

    private function verifyRequiredTags($request_xml, $required_tags) {
        $missing_tags = array();
        foreach ($required_tags as $tag) {
            if (!isset($request_xml[$tag])) {
                $missing_tags[] = $tag;
            }
        }
        return $missing_tags;
    }

    private function sendErrorAndExit($error) {
        $xml = new SimpleXMLElement(self::ERROR_RESPONSE_XML_SHELL);
        $this->addErrorAttribute($xml, $error);

        if ($this->verbose) {
            print 'error:'.$error."\n";
        }
        else {
            send_file_to_browser('error.xml', $xml->asXML(), null, "attachment");
        }
        exit();
    }
    
    private function getValidXmlFromQuery($request) {
        // Get the XML, and parse it
        try {
            if ($this->verbose) var_dump($request);
            $xml_text = $request[self::QUERY_XML_KEY];
            $xml = new SimpleXMLElement($xml_text);
        }
        catch (ErrorException $e) {
            if ($e->getSeverity() == 8) {
                $this->sendErrorAndExit("'".self::QUERY_XML_KEY."' key not found");
            }
            else if ($e->getSeverity() == 2) {
                $this->sendErrorAndExit("badly formed XML");
            }
            else {
                $this->sendErrorAndExit("unexpetect internal error, xml=".urlencode($xml_text));
            }
        }

        if ($xml->getName() == 'phet-info') {
            $this->sendErrorAndExit('underscores needed in XML tags');
        }
        
        if ($xml->getName() != self::ROOT_ELEMENT_NAME) {
            $this->sendErrorAndExit('XML root element name invalid');
        }

        return $xml;
    }

    private function recommendInstallerUpdate($installer_timestamp, $tag) {
        $now = time();
        $days_to_secs = 60 * 60 * 24;

        $settings = UpdateUtils::inst()->getSettings();

        $max_age = $settings['install_recommend_update_age'] * $days_to_secs;        
        $installer_age = $now - $installer_timestamp;
        if ($installer_age > $max_age) {
            //$tag->addAttribute('recommend_update_reason', 'recommend on age');
            return true;
        }

        $regs = array();
        $result = preg_match('/([0-9]{4})-([0-9]{2})-([0-9]{2})/',
                             $settings['install_recommend_update_date'],
                             $date);
        $date_threshold = mktime(0, 0, 0, $date[2], $date[3], $date[1]);
        if ($installer_timestamp < $date_threshold) {
            //$tag->addAttribute('recommend_update_reason', "recommend on date {$settings['install_recommend_update_date']} {$installer_timestamp} {$date_threshold} - ".DB_USERNAME);

            return true;
        }

        //$tag->addAttribute('recommend_update_reason', "no recommend {$installer_age} {$max_age} {$installer_timestamp} {$date_threshold}".DB_USERNAME);
        return false;
    }

    private function processRequestInstaller($request_xml, &$response_xml) {
        $installer_tag = $response_xml->addChild('phet_installer_update_response', '');

        $missing_tags = $this->verifyRequiredTags($request_xml, array('timestamp_seconds'));

        if (!empty($missing_tags)) {
            $this->addErrorAttribute($installer_tag, 'required tags not specified: '.join(', ', $missing_tags));
            return;
        }

        if (!empty($missing_tags)) {
            $this->addErrorAttribute($installer_tag, 'required tags not specified: '.join(', ', $missing_tags));
            return;
        }

        if (0 == preg_match('/^[0-9]+$/', $request_xml['timestamp_seconds'])) {
            $this->addErrorAttribute($installer_tag, 'timestamp_seconds is invalid');
            return;
        }

        // Add the "ask me later" timeframe
        $installer_timestamp = (int) $request_xml['timestamp_seconds'];
        if ($this->recommendInstallerUpdate($installer_timestamp, $installer_tag)) {
            $installer_tag->addAttribute('recommend_update', 'true');
        }
        else {
            $installer_tag->addAttribute('recommend_update', 'false');
        }

        $settings = UpdateUtils::inst()->getSettings();
        $latest_timestamp = installer_get_latest_timestamp();
        $installer_tag->addAttribute('timestamp_seconds', $latest_timestamp);
        $installer_tag->addAttribute('ask_me_later_duration_days',
                                     $settings['install_ask_later_duration']);
    }

    private function processRequestSimVersion($request_xml, &$response_xml) {
        $version_tag = $response_xml->addChild('sim_version_response', '');

        $missing_tags = $this->verifyRequiredTags($request_xml, array('project', 'sim'));

        if (!empty($missing_tags)) {
            $this->addErrorAttribute($version_tag, 'required tags not specified: '.join(', ', $missing_tags));
            return;
        }

        $project = (string) $request_xml['project'];
        $sim = (string) $request_xml['sim'];

        $simulation = sim_get_sim_by_dirname_flavorname($project, $sim);
        if (!$simulation) {
            $this->addErrorAttribute($version_tag, "project and/or sim does not exist: \"{$project}\", \"{$sim}\"");
            return;
        }

        // Get the version info
        $version = sim_get_version($simulation, false);
        $version_string = '';
        if ((!empty($version['major'])) &&
            (!empty($version['minor'])) &&
            (!empty($version['dev']))) {
            $version_string = "{$version['major']}.{$version['minor']}.{$version['dev']}";
        }
        $version_tag->addAttribute('project', $project);
        $version_tag->addAttribute('sim', $sim);
        $version_tag->addAttribute('version', $version_string);
        $version_tag->addAttribute('revision', $version['revision']);
        $version_tag->addAttribute('timestamp_seconds', $version['timestamp']);

        // Add the "ask me later" timeframe
        $settings = UpdateUtils::inst()->getSettings();
        $version_tag->addAttribute('ask_me_later_duration_days', $settings['sim_ask_later_duration']);
    }

    private function processRequest($xml) {
        $response_xml = new SimpleXMLElement(self::RESPONSE_XML_SHELL);

        if (isset($xml->sim_version)) {
            $this->processRequestSimVersion($xml->sim_version, $response_xml);
        }

        if (isset($xml->phet_installer_update)) {
            $this->processRequestInstaller($xml->phet_installer_update, $response_xml);
        }

        return $response_xml;
    }

    public function go() {
        $xml = $this->getValidXmlFromQuery($_REQUEST);

        // Process the version_request if any
        $response_xml = $this->processRequest($xml);

        if ($this->verbose) {
            print "result:<br />\n";
            print nl2br(htmlentities($response_xml->asXML()));
        }
        else {
            send_file_to_browser('sim-startup-response.xml', $response_xml->asXML(), null, "attachment");
        }
    }
}

// Turn errors into exceptions
function exception_error_handler($errno, $errstr, $errfile, $errline) {
    throw new ErrorException($errstr, 0, $errno, $errfile, $errline);
}
set_error_handler("exception_error_handler");

$verbose = ((isset($_REQUEST['verbose'])) && $_REQUEST['verbose']);

$phet_info = new PhetInfo($verbose);
$phet_info->go();

?>