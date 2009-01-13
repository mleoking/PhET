// TrackingHandler.as
//
// Handles tracking messages sent directly to
// the phet.colorado.edu server
//
// Author: Jonathan Olson

import flash.external.ExternalInterface;

class TrackingHandler {
	
	// stores whether a message send has failed. if it has, don't send more!
	public var messageError : Boolean;
	
	// store the session id for all future tracking messages that need to be sent
	public var sessionId : String;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function TrackingHandler() {
		debug("TrackingHandler initializing\n");
		
		messageError = false;
		
		// make this object accessible from _level0.trackingHandler
		// should only be one copy of TrackingHandler (singleton-like)
		_level0.trackingHandler = this;
		
		// generate a new session id, which should be unique
		sessionId = String(Math.floor(Math.random() * 1000000)) + String((new Date()).valueOf());
		
		// send the session start message.
		// if tracking is disabled, the message will not be sent
		sendSessionStart();
	}
	
	// get the exact string sent to the server. this will be used to show
	// users exactly what tracking data is sent.
	public function sessionStartMessage() : String {
		//var str : String = "<tracking ";
		var str : String = "";
		
		/////// message information
		str += "message_type = 'session' \n";
		str += "message_version = '1' \n";
		
		
		/////// user data
		str += "user_preference_file_creation_time = '" + escape(String(_level0.preferences.getUserTime())) + "' \n";
		str += "user_total_sessions = '" + escape(String(_level0.preferences.getUserTotalSessions())) + "' \n";
		
		
		
		/////// simulation data
		str += "sim_type = 'flash' \n";
		
		// currently, project is the same as sim for Flash simulations
		str += "sim_project = '" + escape(_level0.simName) + "' \n";
		str += "sim_name = '" + escape(_level0.simName) + "' \n";
		
		str += "sim_major_version = '" + escape(_level0.versionMajor) + "' \n";
		str += "sim_minor_version = '" + escape(_level0.versionMinor) + "' \n";
		str += "sim_dev_version = '" + escape(_level0.dev) + "' \n";
		str += "sim_svn_revision = '" + escape(_level0.revision) + "' \n";
		//str += "sim_version = '" + escape(_level0.versionMajor + "." + _level0.versionMinor + "." + _level0.dev) + " (" + escape(_level0.revision) + ")' \n";
		
		str += "sim_locale_language = '" + escape(_level0.languageCode) + "' \n";
		str += "sim_locale_country = '" + escape(_level0.countryCode) + "' \n";
		//str += "sim_locale = '" + escape(_level0.common.localeString()) + "' \n";
		
		str += "sim_sessions_since = '" + escape(_level0.preferences.visitsSince()) + "' \n";
		str += "sim_sessions_ever = '" + escape(_level0.preferences.visitsEver()) + "' \n";
		str += "sim_usage_type = '" + escape(_level0.simUsageType) + "' \n";
		str += "sim_distribution_tag = '" + escape(_level0.simDistributionTag) + "' \n";
		str += "sim_dev = '" + escape((_level0.dev > 0 ? "true" : "false")) + "' \n";
		
		
		/////// host data
		
		str += "host_os = '" + escape(System.capabilities.os) + "' \n";
		str += "host_flash = '" + escape(System.capabilities.version) + "' \n";
		str += "host_language = '" + escape(System.capabilities.language) + "' \n";
		str += "host_time_offset = '" + escape(String((new Date()).getTimezoneOffset())) + "' \n";
		str += "host_flash_accessibility = '" + escape(String(System.capabilities.hasAccessibility)) + "' \n";
		str += "host_flash_domain = '" + escape((new LocalConnection()).domain()) + "' \n";
		
		//str += "></tracking>";
		return str;
	}
	
	// return a string timestamp
	public function timestampString() : String {
		return escape(String((new Date()).valueOf()));
	}
	
	public function sendSessionStart() : Void {
		if(!_level0.preferences.allowTracking()) {
			debug("TrackingHandler: cannot send session start message: tracking disabled\n");
			return;
		}
		debug("TrackingHandler: sending session start message\n");
		var str : String = "<tracking " + sessionStartMessage() + "></tracking>";
		sendXML(new XML(str));
	}
	
	// this is used for all tracking messages to send the xml to the server
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
				_level0.debug("TrackingHandler: message received:\n");
				_level0.debug(reply.toString()+"\n");
			} else {
				_level0.debug("TrackingHandler: message error!\n");
			}
		}
		
		// send it to the URL, will store result in reply
		// TODO: Change this to the permanent location!!!
		xml.sendAndLoad("http://phet.colorado.edu/jolson/deploy/tracking/tracker.php", reply);
	}
}
