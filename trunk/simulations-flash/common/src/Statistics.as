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
	
	public function fieldTranslate(field : String) {
		var str = common.strings.get("StatisticsField-" + field, field);
		if(str != field) {
			// it was translated
			return str;
		}
		debug("WARNING Statistics: could not translate " + field + "\n");
		var ob : Object = {
				message_type : "Message type",
				message_version : "Message version",
				sim_type : "Simulation type",
				sim_project : "Project name",
				sim_name : "Simulation name",
				sim_major_version : "Simulation version (major)",
				sim_minor_version : "Simulation version (minor)",
				sim_dev_version : "Simulation version (dev)",
				sim_svn_revision : "Simulation version (revision)",
				sim_version_timestamp : "Simulation version (timestamp)",
				sim_distribution_tag : "Simulation version (distribution)",
				sim_locale_language : "Language",
				sim_locale_country : "Country",
				sim_deployment : "Deployment type",
				sim_dev : "Is this a developer version?",
				sim_total_sessions : "Total number of times this simulation has been run",
				sim_sessions_since : "Number of times this simulation has been run since last online",
				host_flash_os : "Operating system and name",
				host_flash_version : "Flash Player version",
				host_locale_language : "Host language",
				host_flash_time_offset : "Timezone",
				host_flash_accessibility : "Using an accessible device?",
				host_flash_domain : "Simulation domain",
				user_preference_file_creation_time : "Preferences file creation time",
				user_total_sessions : "Total number of times all simulations have been run",
				user_installation_timestamp : "PhET installation timestamp"
		}
		if(ob[field] != undefined) {
			return ob[field];
		}
		debug("WARNING Statistics: could not find default for " + field + "\n");
		return field;
	}
	
	public function fieldFormat(field : String, val, humanReadable : Boolean) {
		if(humanReadable) {
			return fieldTranslate(field) + ": " + unescape(messageEscape(val) + "\n");
		} else {
			return field + " = '" + messageEscape(val) + "' \n";
		}
	}
	
	// get the exact string sent to the server. this will be used to show
	// users exactly what statistics data is sent.
	public function sessionStartMessage(humanReadable : Boolean) : String {
		var str : String = "";
		
		// make data available to be read
		common.preferences.load();
		
		/////// message information
		str += fieldFormat("message_type", "session", humanReadable);
		str += fieldFormat("message_version", "0", humanReadable);
		
		
		/////// simulation data
		str += fieldFormat("sim_type", "flash", humanReadable);
		
		// currently, project is the same as sim for Flash simulations
		str += fieldFormat("sim_project", common.getSimProject(), humanReadable);
		str += fieldFormat("sim_name", common.getSimName(), humanReadable);
		
		str += fieldFormat("sim_major_version", common.getVersionMajor(), humanReadable);
		str += fieldFormat("sim_minor_version", common.getVersionMinor(), humanReadable);
		str += fieldFormat("sim_dev_version", common.getVersionDev(), humanReadable);
		str += fieldFormat("sim_svn_revision", common.getVersionRevision(), humanReadable);
		str += fieldFormat("sim_version_timestamp", common.getVersionTimestamp(), humanReadable);
		
		str += fieldFormat("sim_locale_language", common.getLanguage(), humanReadable);
		str += fieldFormat("sim_locale_country", common.getCountry(), humanReadable);
		
		str += fieldFormat("sim_sessions_since", common.preferences.visitsSince(), humanReadable);
		str += fieldFormat("sim_total_sessions", common.preferences.visitsEver(), humanReadable);
		
		str += fieldFormat("sim_deployment", common.getDeployment(), humanReadable);
		str += fieldFormat("sim_distribution_tag", common.getDistributionTag(), humanReadable);
		str += fieldFormat("sim_dev", String(common.getDev()), humanReadable);
		
		
		/////// host data
		
		str += fieldFormat("host_flash_os", System.capabilities.os, humanReadable);
		str += fieldFormat("host_flash_version", System.capabilities.version, humanReadable);
		str += fieldFormat("host_locale_language", System.capabilities.language, humanReadable);
		str += fieldFormat("host_flash_time_offset", String((new Date()).getTimezoneOffset()), humanReadable);
		str += fieldFormat("host_flash_accessibility", String(System.capabilities.hasAccessibility), humanReadable);
		str += fieldFormat("host_flash_domain", (new LocalConnection()).domain(), humanReadable);
		
		
		/////// user data
		str += fieldFormat("user_preference_file_creation_time", common.preferences.getUserTime(), humanReadable);
		str += fieldFormat("user_installation_timestamp", common.getInstallationTimestamp(), humanReadable);
		str += fieldFormat("user_total_sessions", common.preferences.getUserTotalSessions(), humanReadable);
		
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
		if(!common.hasFlashVars()) {
			debug("Statistics: flash vars were not detected, will not send message\n");
			common.preferences.unload();
			return;
		}
		
		// we no longer need preferences data, so we need to unload the data
		common.preferences.unload();
		
		debug("Statistics: sending session start message\n");
		
		// wrap the message in xml tags
		var str : String = "<?xml version=\"1.0\"?><submit_message><statistics_message " + sessionStartMessage(false) + " /></submit_message>";
		var queryXML = new XML(str);
		queryXML.addRequestHeader("Content-type", "text/xml");
		sendXML(queryXML);
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
