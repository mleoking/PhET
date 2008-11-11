// TrackingHandler.as
//
// Handles tracking messages sent directly to
// the phet.colorado.edu server
//
// Author: Jonathan Olson

import flash.external.ExternalInterface;

class TrackingHandler {
	
	public var sessionId : String;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function TrackingHandler() {
		debug("TrackingHandler initializing\n");
		
		// make this object accessible from _level0.trackingHandler
		// should only be one copy of TrackingHandler (singleton-like)
		_level0.trackingHandler = this;
		
		//if(_level0.preferences.allowTracking()) {
		//}
		sessionId = String(Math.floor(Math.random() * 1000000)) + String((new Date()).valueOf());
		
		ExternalInterface.addCallback("beforeClose", this, sendSessionEnd);
		sendSessionStart();
	}
	
	public function sendSessionStart() : Void {
		if(!_level0.preferences.allowTracking()) {
			debug("TrackingHandler: cannot send session start message: tracking disabled\n");
		}
		debug("TrackingHandler: sending session start message\n");
		var str : String = "<tracking ";
		str += "type = 'session-started' ";
		str += "user-id = '" + escape(_level0.preferences.userId()) + "' ";
		str += "session-id = '" + escape(sessionId) + "' ";
		str += "flash-version = '" + escape(System.capabilities.version) + "' ";
		str += "project = '" + escape(_level0.simName) + "' ";
		str += "sim = '" + escape(_level0.simName) + "' ";
		str += "sim-type = '" + "flash" + "' ";
		str += "sim-version = '" + escape(_level0.versionMajor + "." + _level0.versionMinor + "." + _level0.dev) + "' ";
		str += "sim-revision = '" + escape(_level0.revision) + "' ";
		str += "sim-locale = '" + escape(_level0.countryCode) + "' ";
		str += "dev = '" + escape((_level0.dev > 0 ? "true" : "false")) + "' ";
		str += "os = '" + escape(System.capabilities.os) + "' ";
		str += "locale-default = '" + escape(System.capabilities.language) + "' ";
		
		str += "flash-audio = '" + escape(String(System.capabilities.hasAudio)) + "' ";
		str += "flash-accessibility = '" + escape(String(System.capabilities.hasAccessibility)) + "' ";
		str += "flash-manufacturer = '" + escape(System.capabilities.manufacturer) + "' ";
		str += "flash-playertype = '" + escape(System.capabilities.playerType) + "' ";
		str += "screen-x = '" + escape(String(System.capabilities.screenResolutionX)) + "' ";
		str += "screen-y = '" + escape(String(System.capabilities.screenResolutionY)) + "' ";
		str += "user-timezone-offset = '" + escape(String((new Date()).getTimezoneOffset())) + "' ";
		str += "flash-domain = '" + escape((new LocalConnection()).domain()) + "' ";
		str += "timestamp = '" + escape(String((new Date()).valueOf())) + "' ";
		
		str += "></tracking>";
		sendXML(new XML(str));
	}
	
	public function sendSessionEnd() : Void {
		if(!_level0.preferences.allowTracking()) {
			debug("TrackingHandler: cannot send session end message: tracking disabled\n");
		}
		debug("TrackingHandler: sending session end message\n");
		var str : String = "<tracking ";
		str += "type = 'session-ended' ";
		str += "session-id = '" + escape(sessionId) + "' ";
		str += "timestamp = '" + escape(String((new Date()).valueOf())) + "' ";
		
		str += "></tracking>";
		sendXML(new XML(str));
	}
	
	public function sendXML(xml : XML) : Void {
		xml.contentType = "text/xml";
		var reply : XML = new XML();
		reply.ignoreWhite = true;
		reply.onLoad = function(success : Boolean) : Void {
			if(success) {
				_level0.debug("TrackingHandler: message received:\n");
				_level0.debug(reply.toString()+"\n");
			} else {
				_level0.debug("TrackingHandler: message error!\n");
			}
		}
		xml.sendAndLoad("http://phet.colorado.edu/jolson/deploy/tracking/tracker.php", reply);
	}
}
