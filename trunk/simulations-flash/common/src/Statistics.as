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
	
	public var common : FlashCommon;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function Statistics() {
		//debug("Statistics initializing\n");
		
		messageError = false;
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// make this object accessible from _level0.statistics
		// should only be one copy of Statistics (singleton-like)
		_level0.statistics = this;
		common.statistics = this;
		
		// send the session start message.
		// if messages is disabled, the message will not be sent
		// CURRENTLY CALLED FROM ELSEWHERE sendSessionStart();
	}
	
	// get the exact string sent to the server. this will be used to show
	// users exactly what statistics data is sent.
	public function sessionStartMessage() : String {
		var str : String = "";
		
		// make data available to be read
		common.preferences.load();
		
		/////// message information
		str += "message_type = 'session' \n";
		str += "message_version = '0' \n";
		
		
		/////// simulation data
		str += "sim_type = 'flash' \n";
		
		// currently, project is the same as sim for Flash simulations
		str += "sim_project = '" + messageEscape(common.getSimProject()) + "' \n";
		str += "sim_name = '" + messageEscape(common.getSimName()) + "' \n";
		
		str += "sim_major_version = '" + messageEscape(common.getVersionMajor()) + "' \n";
		str += "sim_minor_version = '" + messageEscape(common.getVersionMinor()) + "' \n";
		str += "sim_dev_version = '" + messageEscape(common.getVersionDev()) + "' \n";
		str += "sim_svn_revision = '" + messageEscape(common.getVersionRevision()) + "' \n";
		str += "sim_version_timestamp = '" + messageEscape(common.getVersionTimestamp()) + "' \n";
		
		str += "sim_locale_language = '" + messageEscape(common.getLanguage()) + "' \n";
		str += "sim_locale_country = '" + messageEscape(common.getCountry()) + "' \n";
		
		str += "sim_sessions_since = '" + messageEscape(common.preferences.visitsSince()) + "' \n";
		str += "sim_total_sessions = '" + messageEscape(common.preferences.visitsEver()) + "' \n";
		
		str += "sim_deployment = '" + messageEscape(common.getDeployment()) + "' \n";
		str += "sim_distribution_tag = '" + messageEscape(common.getDistributionTag()) + "' \n";
		str += "sim_dev = '" + messageEscape(String(common.getDev())) + "' \n";
		
		
		/////// host data
		
		str += "host_flash_os = '" + messageEscape(System.capabilities.os) + "' \n";
		str += "host_flash_version = '" + messageEscape(System.capabilities.version) + "' \n";
		str += "host_locale_language = '" + messageEscape(System.capabilities.language) + "' \n";
		str += "host_flash_time_offset = '" + messageEscape(String((new Date()).getTimezoneOffset())) + "' \n";
		str += "host_flash_accessibility = '" + messageEscape(String(System.capabilities.hasAccessibility)) + "' \n";
		str += "host_flash_domain = '" + messageEscape((new LocalConnection()).domain()) + "' \n";
		
		
		/////// user data
		str += "user_preference_file_creation_time = '" + messageEscape(common.preferences.getUserTime()) + "' \n";
		str += "user_installation_timestamp = '" + messageEscape(common.getInstallationTimestamp()) + "' \n";
		str += "user_total_sessions = '" + messageEscape(common.preferences.getUserTotalSessions()) + "' \n";
		
		// unload data from shared object
		common.preferences.unload();
		
		return str;
	}
	
	// attempts to send a session start message
	public function sendSessionStart() : Void {
		// load the user's preferences so we can see whether messages is allowed and they
		// have accepted the privacy agreement
		common.preferences.load();
		
		if(!common.preferences.areStatisticsMessagesAllowed()) {
			debug("Statistics: cannot send session start message: statistics messages disabled\n");
			common.preferences.unload();
			return;
		}
		if(!common.preferences.isPrivacyOK()) {
			debug("Statistics: cannot send session start message: have not accepted agreement yet\n");
			common.preferences.unload();
			return;
		}
		
		// we no longer need preferences data, so we need to unload the data
		common.preferences.unload();
		
		debug("Statistics: sending session start message\n");
		
		// wrap the message in xml tags
		var str : String = "<?xml version=\"1.0\"?><phet_info><statistics_message " + sessionStartMessage() + " /></phet_info>";
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
		
		// TODO: remove after DEVELOPMENT
		_level0.statisticsReply = reply;
		
		// to traverse the XML, we don't want whitespace notes in it
		reply.ignoreWhite = true;
		
		// callback function when we receive the reply (or when it is known to have failed)
		reply.onLoad = function(success : Boolean) : Void {
			if(success) {
				_level0.debug("Statistics: message received:\n");
				_level0.debug(reply.toString()+"\n");
				
				// whether the message was successful
				var full_success : Boolean = false;
				
				var phetInfoResponse : XMLNode = reply.childNodes[0];
				
				if(phetInfoResponse.attributes.success == "true") {
					var responses : Array = phetInfoResponse.childNodes;
					for(var idx in responses) {
						var child : XMLNode = responses[idx];
						if(child.nodeName == "statistics_message_response") {
							if(child.attributes.success == "true") {
								_level0.debug("Statistics: statistics_message_response found\n");
								full_success = true;
							} else {
								_level0.debug("Statistics: statistics_message_response FAILURE\n");
							}
						}
					}
				} else {
					_level0.debug("Statistics: phet_info_response FAILURE\n");
				}
				
				
				if(full_success) {
					// statistics message successful
					_level0.debug("Statistics: Message Handshake Successful\n");
					_level0.common.preferences.resetSince();
				} else {
					// server could not record statistics message
					_level0.debug("WARNING: Statistics: Message Handshake Failure\n");
				}
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
	public function messageEscape(val) : String {
		var str : String;
		if(typeof(val) == "string") {
			str = val;
		} else if(typeof(val) == "number") {
			str = String(val);
		} else if(typeof(val) == "null") {
			return FlashCommon.NULLVAL;
		} else {
			debug("WARNING: Statistics.messageEscape invalid type: " + typeof(val) + " = " + String(val) + "\n");
			str = String(val);
		}
		if(str == null || str == undefined || common.isPlaceholder(str)) {
			return FlashCommon.NULLVAL;
		}
		return escape(str);
	}
}
