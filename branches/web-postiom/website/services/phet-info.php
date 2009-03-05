<?php

  // SPECIAL TEST EXTENSION:
  //
  // Because the unit tests are testing through the web interface, it
  // can't set up the database to force certian conditions to be met.
  // So if the there is a key prefix on the query string key then
  // force the override specified.

define('OVERRIDE_PREFIX', 'PHET-DEFINE-OVERRIDE-');
foreach ($_GET as $key => $value) {
    if (0 === strpos($key, OVERRIDE_PREFIX)) {
        define(substr($key, strlen(OVERRIDE_PREFIX)), $value);
    }
}

// In each web accessable script SITE_ROOT must be defined FIRST
if (!defined("SITE_ROOT")) define("SITE_ROOT", "../");

// See global.php for an explaination of the next line
require_once(dirname(dirname(__FILE__))."/include/global.php");
require_once('include/sim-utils.php');

class PhetInfo {
    const ROOT_ELEMENT_NAME = 'phet_info';
    const REQUEST_VERSION_ATTR = 'request_version';
    const RESPONSE_XML_SHELL =
        '<?xml version="1.0"?><phet_info_response></phet_info_response>';
    const ERROR_RESPONSE_XML_SHELL =
        '<?xml version="1.0"?><phet_info_response></phet_info_response>';

    public function __construct($verbose) {
        $this->verbose = $verbose;
    }

    private function addErrorElement($tag, $error) {
        $this->setFail($tag);
        $tag->addChild('error', $error);
    }

    private function verifyRequiredAttributes($request_xml, $required_tags) {
        $missing_tags = array();
        foreach ($required_tags as $tag) {
            if (!isset($request_xml[$tag])) {
                $missing_tags[] = $tag;
            }
        }
        return $missing_tags;
    }

    private function setSuccess($xml) {
        if (isset($xml['success'])) {
            $xml['success'] = 'true';
        }
        else {
            $xml->addAttribute('success', 'true');
        }
    }

    private function setFail($xml) {
        if (isset($xml['success'])) {
            $xml['success'] = 'false';
        }
        else {
            $xml->addAttribute('success', 'false');
        }
    }

    private function sendErrorAndExit($error) {
        $xml = new SimpleXMLElement(self::ERROR_RESPONSE_XML_SHELL);
        $this->setFail($xml, false);
        $this->addErrorElement($xml, $error);

        if ($this->verbose) {
            print 'error:'.$error."\n";
        }
        else {
            send_file_to_browser('error.xml', $xml->asXML(), null, "attachment");
        }
        exit();
    }

    private function getValidXmlFromQuery() {
        // Get the XML, and parse it
        try {
            $xml_text = file_get_contents("php://input");
            $xml = new SimpleXMLElement($xml_text);
        }
        catch (ErrorException $e) {
            if ($e->getSeverity() == 2) {
                $this->sendErrorAndExit("badly formed XML");
            }
            else {
                $this->sendErrorAndExit("unexpected internal error, xml=".urlencode($xml_text));
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

    private function recommendInstallerUpdate($installer_timestamp) {
        $now = time();
        $days_to_secs = 60 * 60 * 24;

        $settings = UpdateUtils::inst()->getSettings();

        $max_age = $settings['install_recommend_update_age'] * $days_to_secs;
        $installer_age = $now - $installer_timestamp;
        if ($installer_age > $max_age) {
            return true;
        }

        $regs = array();
        $result = preg_match('/([0-9]{4})-([0-9]{2})-([0-9]{2})/',
                             $settings['install_recommend_update_date'],
                             $date);
        $date_threshold = mktime(0, 0, 0, $date[2], $date[3], $date[1]);
        if ($installer_timestamp < $date_threshold) {
            return true;
        }

        return false;
    }

    private function processRequestInstaller($request_xml, $response_xml) {
        $instal_element = $response_xml->addChild('phet_installer_update_response', '');

        // Assume success, change if not
        $this->setSuccess($instal_element);

        // Only support version 1 right now, refactor if we go to v2
        if (!isset($request_xml[self::REQUEST_VERSION_ATTR]) ||
            ($request_xml[self::REQUEST_VERSION_ATTR] != 1)) {
            $this->addErrorElement($instal_element, 'Invalid request version');
            return;
        }

        $missing_tags = $this->verifyRequiredAttributes($request_xml, array('timestamp_seconds'));

        if (!empty($missing_tags)) {
            $this->addErrorElement($instal_element, 'required tags not specified: '.join(', ', $missing_tags));
            return;
        }

        if (!empty($missing_tags)) {
            $this->addErrorElement($instal_element, 'required tags not specified: '.join(', ', $missing_tags));
            return;
        }

        if (0 == preg_match('/^[0-9]+$/', $request_xml['timestamp_seconds'])) {
            $this->addErrorElement($instal_element, 'timestamp_seconds is invalid');
            return;
        }

        // Add the "ask me later" timeframe
        $installer_timestamp = (int) $request_xml['timestamp_seconds'];
        if ($this->recommendInstallerUpdate($installer_timestamp)) {
            $instal_element->addAttribute('recommend_update', 'true');
        }
        else {
            $instal_element->addAttribute('recommend_update', 'false');
        }

        $settings = UpdateUtils::inst()->getSettings();
        $latest_timestamp = installer_get_latest_timestamp();
        $instal_element->addAttribute('timestamp_seconds', $latest_timestamp);
        $instal_element->addAttribute('ask_me_later_duration_days',
                                      $settings['install_ask_later_duration']);
    }

    private function processRequestSimVersion($request_xml, $response_xml) {
        $version_element = $response_xml->addChild('sim_version_response', '');

        // Assume success, change if not
        $this->setSuccess($version_element);

        // Only support version 1 right now, refactor if we go to v2
        if (!isset($request_xml[self::REQUEST_VERSION_ATTR]) ||
            ($request_xml[self::REQUEST_VERSION_ATTR] != 1)) {
            $this->addErrorElement($version_element, 'Invalid request version');
            return;
        }

        // Check for missing tags
        $missing_tags = $this->verifyRequiredAttributes($request_xml, array('project', 'sim'));
        if (!empty($missing_tags)) {
            $this->addErrorElement($version_element, 'required tags not specified: '.join(', ', $missing_tags));
            return;
        }

        $project = (string) $request_xml['project'];
        $sim = (string) $request_xml['sim'];

        $simulation = sim_get_sim_by_dirname_flavorname($project, $sim);
        if (!$simulation) {
            $this->addErrorElement($version_element, "project and/or sim does not exist: \"{$project}\", \"{$sim}\"");
            return;
        }

        // Get the version info
        $version = sim_get_version($simulation, false);
        $missing_attributes = array();
        foreach ($version as $key => $value) {
            if (empty($value)) {
                $missing_attributes[] = "version_{$key}";
            }
        }

        if (!empty($missing_attributes)) {
            $error = "can't find value for attributes: ".join(', ', $missing_attributes);
            $this->addErrorElement($version_element, $error);
            return;
        }

        $version_element->addAttribute('project', $project);
        $version_element->addAttribute('sim', $sim);
        $version_element->addAttribute('version_major', $version['major']);
        $version_element->addAttribute('version_minor', $version['minor']);
        $version_element->addAttribute('version_dev', $version['dev']);
        $version_element->addAttribute('version_revision', $version['revision']);
        $version_element->addAttribute('version_timestamp', $version['timestamp']);

        // Add the "ask me later" timeframe
        $settings = UpdateUtils::inst()->getSettings();
        $version_element->addAttribute('ask_me_later_duration_days', $settings['sim_ask_later_duration']);
    }

    private function processRequest($xml) {
        $response_xml = new SimpleXMLElement(self::RESPONSE_XML_SHELL);

        // The error checking has already happened for the root element
        $this->setSuccess($response_xml);

        if (isset($xml->sim_version)) {
            $this->processRequestSimVersion($xml->sim_version, $response_xml);
        }

        if (isset($xml->phet_installer_update)) {
            $this->processRequestInstaller($xml->phet_installer_update, $response_xml);
        }

        return $response_xml;
    }

    public function go() {
        $xml = $this->getValidXmlFromQuery();

        // Process the version_request if any
        $response_xml = $this->processRequest($xml);

        if ($this->verbose) {
            print "result:<br />\n";
            print nl2br(htmlentities($response_xml->asXML()));
        }
        else {
            send_file_to_browser('phet-info-response.xml', $response_xml->asXML(), null, "attachment");
        }
    }
}

// Turn errors into exceptions
function exception_error_handler($errno, $errstr, $errfile, $errline) {
    throw new ErrorException($errstr, 0, $errno, $errfile, $errline);
}
set_error_handler("exception_error_handler");

$verbose = ((isset($_GET['verbose'])) && $_GET['verbose']);

$phet_info = new PhetInfo($verbose);
$phet_info->go();

?>