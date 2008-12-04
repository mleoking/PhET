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
		
		// set up a function that can be called in JavaScript as beforeClose().
		// it might be sent before the simulation is closed.
		ExternalInterface.addCallback("beforeClose", this, sendSessionEnd);
		
		// send the session start message.
		// if tracking is disabled, the message will not be sent
		sendSessionStart();
	}
	
	// get the exact string sent to the server. this will be used to show
	// users exactly what tracking data is sent.
	public function sessionStartMessage() : String {
		//var str : String = "<tracking ";
		var str : String = "";
		str += "type = 'session-started' \n";
		
		// user id that is stored in preferences
		str += "user-id = '" + escape(_level0.preferences.userId()) + "' \n";
		
		// our unique session id
		str += "session-id = '" + escape(sessionId) + "' \n";
		
		str += "flash-version = '" + escape(System.capabilities.version) + "' \n";
		
		// currently, project is the same as sim for Flash simulations
		str += "project = '" + escape(_level0.simName) + "' \n";
		str += "sim = '" + escape(_level0.simName) + "' \n";
		str += "sim-type = '" + "flash" + "' \n";
		str += "sim-version = '" + escape(_level0.versionMajor + "." + _level0.versionMinor + "." + _level0.dev) + "' \n";
		str += "sim-revision = '" + escape(_level0.revision) + "' \n";
		str += "sim-locale = '" + escape(_level0.countryCode) + "' \n";
		str += "dev = '" + escape((_level0.dev > 0 ? "true" : "false")) + "' \n";
		str += "os = '" + escape(System.capabilities.os) + "' \n";
		str += "locale-default = '" + escape(System.capabilities.language) + "' \n";
		
		str += "flash-audio = '" + escape(String(System.capabilities.hasAudio)) + "' \n";
		str += "flash-accessibility = '" + escape(String(System.capabilities.hasAccessibility)) + "' \n";
		str += "flash-manufacturer = '" + escape(System.capabilities.manufacturer) + "' \n";
		str += "flash-playertype = '" + escape(System.capabilities.playerType) + "' \n";
		str += "screen-x = '" + escape(String(System.capabilities.screenResolutionX)) + "' \n";
		str += "screen-y = '" + escape(String(System.capabilities.screenResolutionY)) + "' \n";
		str += "user-timezone-offset = '" + escape(String((new Date()).getTimezoneOffset())) + "' \n";
		str += "flash-domain = '" + escape((new LocalConnection()).domain()) + "' \n";
		str += "timestamp = '" + escape(String((new Date()).valueOf())) + "' \n";
		
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
	
	public function sendSessionEnd() : Void {
		if(messageError) { return; }
		if(!_level0.preferences.allowTracking()) {
			debug("TrackingHandler: cannot send session end message: tracking disabled\n");
			return;
		}
		debug("TrackingHandler: sending session end message\n");
		var str : String = "<tracking ";
		str += "type = 'session-ended' ";
		str += "session-id = '" + escape(sessionId) + "' ";
		str += "timestamp = '" + timestampString() + "' ";
		
		str += "></tracking>";
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
