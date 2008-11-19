
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
	
	public var manual : Boolean;
	
	// shorthand debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function UpdateHandler() {
		debug("UpdateHandler initializing\n");
		
		// set to true if the user is manually checking for updates
		// in this case, we should give them a response if they are
		// running the current version
		manual = false;
		
		// make this object accessible from _level0.updateHandler
		// should only be one copy of UpdateHandler (singleton-like)
		_level0.updateHandler = this;
		
		// make sure the user allows us to check for updates!
		if(_level0.preferences.checkForUpdates()) {
			checkUpdates();
		} else {
			debug("UpdateHandler: not checking for updates (preferences)\n");
		}
	}
	
	public function checkUpdates() : Void {
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
					if(_level0.updateHandler.manual) {
						_level0.common.updateHandler.updatesNotAvailable();
					}
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
		xml.load("http://phet.colorado.edu/jolson/deploy/sims/version.php?" + _level0.simName);
	}
	
	public function manualCheck() : Void {
		// TODO: add manual updates for this
		debug("UpdateHandler: checking manually for updates");
		manual = true;
		checkUpdates();
	}
	
	// called if a newer version is available online
	public function updatesAvailable() : Void {
		debug("UpdateHandler: Updates Available (dialog)!\n");
		
		// TODO: fill in with desired behavior.
	}
	
	public function updatesNotAvailable() : Void {
		debug("UpdateHandler: Updates Not Available (dialog)!\n");
		
		// TODO: fill in with desired behavior.
	}
}

