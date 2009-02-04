// Statistics.as
//
// Handles statistics messages sent directly to
// the phet.colorado.edu server
//
// Author: Jonathan Olson

import flash.external.ExternalInterface;

class Statistics {
	
	// stores whether a message send has failed. if it has, don't send more!
	public var messageError : Boolean;
	
	// store the session id for all future statistics messages that need to be sent
	public var sessionId : String;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function Statistics() {
		debug("Statistics initializing\n");
		
		messageError = false;
		
		// make this object accessible from _level0.statistics
		// should only be one copy of Statistics (singleton-like)
		_level0.statistics = this;
		
		// send the session start message.
		// if messages is disabled, the message will not be sent
		// CURRENTLY CALLED FROM ELSEWHERE sendSessionStart();
	}
	
	// get the exact string sent to the server. this will be used to show
	// users exactly what statistics data is sent.
	public function sessionStartMessage() : String {
		var str : String = "";
		
		// make data available to be read
		_level0.preferences.load();
		
		/////// message information
		str += "message_type = 'session' \n";
		str += "message_version = '1' \n";
		
		
		/////// user data
		str += "user_preference_file_creation_time = '" + messageEscape(String(_level0.preferences.getUserTime())) + "' \n";
		str += "user_installation_timestamp = '" + messageEscape(String(_level0.installationTimestamp)) + "' \n";
		str += "user_total_sessions = '" + messageEscape(String(_level0.preferences.getUserTotalSessions())) + "' \n";
		
		
		
		/////// simulation data
		str += "sim_type = 'flash' \n";
		
		// currently, project is the same as sim for Flash simulations
		str += "sim_project = '" + messageEscape(_level0.simName) + "' \n";
		str += "sim_name = '" + messageEscape(_level0.simName) + "' \n";
		
		str += "sim_major_version = '" + messageEscape(_level0.versionMajor) + "' \n";
		str += "sim_minor_version = '" + messageEscape(_level0.versionMinor) + "' \n";
		str += "sim_dev_version = '" + messageEscape(_level0.dev) + "' \n";
		str += "sim_svn_revision = '" + messageEscape(_level0.revision) + "' \n";
		str += "sim_version_timestamp = '" + messageEscape(_level0.versionTimestamp) + "' \n";
		//str += "sim_version = '" + messageEscape(_level0.versionMajor + "." + _level0.versionMinor + "." + _level0.dev) + " (" + escape(_level0.revision) + ")' \n";
		
		str += "sim_locale_language = '" + messageEscape(_level0.languageCode) + "' \n";
		
		var countryCodeForm : String = "";
		if(_level0.countryCode == "none" ) {
			countryCodeForm = "null";
		} else {
			countryCodeForm = _level0.countryCode;
		}
		str += "sim_locale_country = '" + messageEscape(countryCodeForm) + "' \n";
		
		str += "sim_sessions_since = '" + messageEscape(_level0.preferences.visitsSince()) + "' \n";
		str += "sim_total_sessions = '" + messageEscape(_level0.preferences.visitsEver()) + "' \n";
		
		var deployment : String = "";
		if(!_level0.common.fromPhetWebsite() && !_level0.common.isPlaceholder(_level0.installationTimestamp) && _level0.installationTimestamp != "none") {
			// if not running from a website, AND installation timestamp is included, it must be a full installation!
			// thus we override
			deployment = "phet-installation";
		} else {
			deployment = _level0.simDeployment;
		}
		str += "sim_deployment = '" + messageEscape(deployment) + "' \n";
		str += "sim_distribution_tag = '" + messageEscape(_level0.simDistributionTag) + "' \n";
		str += "sim_dev = '" + messageEscape((_level0.dev > 0 ? "true" : "false")) + "' \n";
		
		
		/////// host data
		
		str += "host_flash_os = '" + messageEscape(System.capabilities.os) + "' \n";
		str += "host_flash_version = '" + messageEscape(System.capabilities.version) + "' \n";
		str += "host_locale_language = '" + messageEscape(System.capabilities.language) + "' \n";
		str += "host_flash_time_offset = '" + messageEscape(String((new Date()).getTimezoneOffset())) + "' \n";
		str += "host_flash_accessibility = '" + messageEscape(String(System.capabilities.hasAccessibility)) + "' \n";
		str += "host_flash_domain = '" + messageEscape((new LocalConnection()).domain()) + "' \n";
		
		// unload data from shared object
		_level0.preferences.unload();
		
		return str;
	}
	
	// attempts to send a session start message
	public function sendSessionStart() : Void {
		// load the user's preferences so we can see whether messages is allowed and they
		// have accepted the privacy agreement
		_level0.preferences.load();
		
		if(!_level0.preferences.areStatisticsMessagesAllowed()) {
			debug("Statistics: cannot send session start message: statistics messages disabled\n");
			_level0.preferences.unload();
			return;
		}
		if(!_level0.preferences.isPrivacyOK()) {
			debug("Statistics: cannot send session start message: have not accepted agreement yet\n");
			_level0.preferences.unload();
			return;
		}
		
		// we no longer need preferences data, so we need to unload the data
		_level0.preferences.unload();
		
		debug("Statistics: sending session start message\n");
		
		// wrap the message in xml tags
		var str : String = "<statistics " + sessionStartMessage() + "></statistics>";
		sendXML(new XML(str));
	}
	
	// this is used for all statistics messages to send the xml to the server
	public function sendXML(xml : XML) : Void {
		
		// if a message has previously failed, don't send more. the
		// network may be down
		if(messageError) { return; }
		
		// make sure it will be recognized as XML by the server
		xml.contentType = "text/xml";
		
		// allocate space for the reply message
		var reply : XML = new XML();
		
		// to traverse the XML, we don't want whitespace notes in it
		reply.ignoreWhite = true;
		
		// callback function when we receive the reply (or when it is known to have failed)
		reply.onLoad = function(success : Boolean) : Void {
			if(success) {
				_level0.debug("Statistics: message received:\n");
				_level0.debug(reply.toString()+"\n");
				_level0.preferences.resetSince();
			} else {
				_level0.debug("Statistics: message error!\n");
			}
		}
		
		// send it to the URL, will store result in reply
		// TODO: Change this to the permanent location!!!
		xml.sendAndLoad("http://phet.colorado.edu/statistics/submit_message.php", reply);
		// DEVELOPMENT: send statistics message to localhost
		//xml.sendAndLoad("http://localhost/statistics/submit_message.php", reply);
	}
	
	// sanitize information to be send to phet statistics: escape or turn into 'null' 
	public function messageEscape(str : String) : String {
		if(str == null || str == undefined || _level0.common.isPlaceholder(str)) {
			return "null";
		}
		return escape(str);
	}
}
