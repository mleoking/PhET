
// UpdateHandler.as
//
// Handles checking for a newer version of the simulation
// online, and what to do if that newer version exists.
//
// Author: Jonathan Olson

class UpdateHandler {
	public var versionMajor : String;
	public var versionMinor : String;
	public var dev : String;
	public var revision : String;
	
	// shorthand debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function UpdateHandler() {
		debug("UpdateHandler initializing\n");
		
		// make this object accessible from _level0.updateHandler
		// should only be one copy of UpdateHandler (singleton-like)
		_level0.updateHandler = this;
		
		// make sure the user allows us to check for updates!
		if(_level0.preferences.checkForUpdates()) {
			// make sure we can access phet.colorado.edu and all files under that domain
			System.security.allowDomain("phet.colorado.edu");
			
			// create XML that will be filled in with the response
			var xml : XML = new XML();
			
			// make sure that whitespace isn't treated as nodes!
			xml.ignoreWhite = true;
			
			// function that is called when the XML is either loaded or fails somehow
			xml.onLoad = function(success : Boolean) {
				if(success) {
					_level0.debug("UpdateHandler: successfully obtained version information\n");
					
					versionMajor = xml.firstChild.childNodes[0].attributes.value;
					versionMinor = xml.firstChild.childNodes[1].attributes.value;
					dev = xml.firstChild.childNodes[2].attributes.value;
					revision = xml.firstChild.childNodes[3].attributes.value;
					
					_level0.debug("    latest version: " + versionMajor);
					_level0.debug("." + versionMinor);
					_level0.debug(" dev:" + dev);
					_level0.debug(" rev:" + revision + "\n");
					
					// check major and minor version numbers for equality
					if(versionMajor == _level0.versionMajor && versionMinor == _level0.versionMinor) {
						_level0.debug("UpdateHandler: running latest version\n");
					} else {
						_level0.common.updateHandler.updatesAvailable();
					}
				} else {
					_level0.debug("UpdateHandler: network failure, cannot read version information\n");
				}
			}
			
			/////////////////////////////////////////////
			// CHANGE ME
			// needs to be changed to the path of the version script for each simulation
			xml.load("http://phet.colorado.edu/tracking/flash-tests/version.php");
		} else {
			debug("UpdateHandler: not checking for updates (preferences)\n");
		}
	}
	
	// called if a newer version is available online
	public function updatesAvailable() {
		debug("UpdateHandler: Updates Available\n");
		
		// TODO: fill in with desired behavior.
	}
}

