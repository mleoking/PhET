
// UpdateHandler.as
//
// Handles checking for a newer version of the simulation
// online, and what to do if that newer version exists.
//
// Author: Jonathan Olson

class UpdateHandler {
	// the latest version information detected from the server
	// TODO: rename these to match _level0 fields
	public var versionMajor : Number;
	public var versionMinor : Number;
	public var versionDev : Number;
	public var versionRevision : Number;
	public var simTimestamp : Number;
	public var installerTimestamp : Number;
	
	// whether the "Check for updates now" button was clicked
	// (manual check for updates)
	public var manual : Boolean;
	
	public var common : FlashCommon;
	
	// shorthand debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function UpdateHandler() {
		debug("UpdateHandler initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// set to true if the user is manually checking for updates
		// in this case, we should give them a response if they are
		// running the current version
		manual = false;
		
		// make this object accessible from _level0.updateHandler
		// should only be one copy of UpdateHandler (singleton-like)
		_level0.updateHandler = this;
		common.updateHandler = this;
		
		// make sure the user allows us to check for updates!
		if(common.preferences.areUpdatesAllowed()) {
			checkUpdates();
		} else {
			debug("UpdateHandler: not checking for updates (Preferences.areUpdatesAllowed() = false)\n");
		}
	}
	
	public function checkUpdates() : Void {
		// make sure we can access phet.colorado.edu and all files under that domain
		// this is more of a sanity-check than anything else, this should be included
		// under FlashCommon.as
		System.security.allowDomain("phet.colorado.edu");
		
		// create XML that will be filled in with the response
		var xml : XML = new XML();
		
		// make sure that whitespace isn't treated as nodes! (DO NOT REMOVE THIS)
		xml.ignoreWhite = true;
		
		// function that is called when the XML is either loaded or fails somehow
		xml.onLoad = function(success : Boolean) {
			if(success) {
				_level0.debug("UpdateHandler: reply successfully received\n");
				_level0.debug(String(xml) + "\n");
				
				var hand : UpdateHandler = _level0.updateHandler;
				
				var simVersionInfo : XMLNode = xml.childNodes[0];
				var attributes : Object = simVersionInfo.attributes;
				
				hand.versionRevision = parseInt(attributes['revision']);
				hand.simTimestamp = parseInt(attributes['timestamp']);
				hand.installerTimestamp = parseInt(attributes['installer_timestamp']);
				hand.parseVersionInfo(attributes['version']);
				
				hand.debug("   latest: " + hand.common.zeroPadVersion(hand.versionMajor, hand.versionMinor, hand.versionDev) + " (" + String(hand.versionRevision) + ")\n");
				
				var latestSkipped : Array = _level0.preferences.getLatestSkippedUpdate();
				
				if(hand.versionRevision == _level0.common.getVersionRevision()) {
					// running the latest version
					_level0.debug("UpdateHandler: running latest version\n");
					
					// if the user clicked "Check for Updates Now", inform the user that no
					// update is available
					if(hand.manual) {
						hand.updatesNotAvailable();
					}
				} else if(hand.versionRevision < _level0.common.getVersionRevision()) {
					_level0.debug("WARNING UpdateHandler: running a more recent version than on the production website.\n");
				} else if(hand.versionMajor == undefined || hand.versionMinor == undefined) {
					_level0.debug("WARNING UpdateHandler: received undefined version information!\n");
				} else if(!(hand.manual) && (hand.versionMajor < latestSkipped[0] || (hand.versionMajor == latestSkipped[0] && hand.versionMinor <= latestSkipped[1]))) {
					// user did not click "Check for Updates Now" AND the new version <= latest skipped version
					_level0.debug("UpdateHandler: used selected to skip this update\n");
				} else if(!(hand.manual) && _level0.preferences.askLaterElapsed() < 1000 * 60 * 60 * 24) {
					_level0.debug("UpdateHandler: used selected ask later, time elapsed = " + String(_level0.preferences.askLaterElapsed()) + "\n");
				} else if(hand.common.fromFullInstallation() && hand.simTimestamp + 1800 > hand.installerTimestamp) {
					// installer was deployed before (or just around) the time the sim was deployed
					_level0.debug("UpdateHandler: installer might not contain the most recent sim\n");
				} else {
					hand.updatesAvailable(hand.versionMajor, hand.versionMinor, hand.versionDev);
				}
				
			} else {
				_level0.debug("WARNING: UpdateHandler: Failure to obtain latest version information\n");
			}
		}
		
		/////////////////////////////////////////////
		// TODO needs to be changed to the path of the version script for each simulation
		// most likely will be http://phet.colorado.edu/simulations/sim-version-info.php?project=BLAH&sim=BLAH
		//xml.load("http://phet.colorado.edu/jolson/deploy/sims/fake-sim-version-info.php?project=" + common.getSimProject() + "&sim=" + common.getSimName());
		xml.load("http://localhost/jolson/deploy/sims/fake-sim-version-info.php?project=" + _level0.simName + "&sim=" + _level0.simName);
	}
	
	public function manualCheck() : Void {
		debug("UpdateHandler: checking manually for updates");
		manual = true;
		checkUpdates();
	}
	
	// called if a newer version is available online
	public function updatesAvailable(versionMajor : Number, versionMinor : Number, versionDev : Number) : Void {
		debug("UpdateHandler: Updates Available (dialog)!\n");
		
		if(_level0.updateAvailableWindow) {
			// update window exists, just show it
			debug("Showing dialog again\n");
			_level0.updateAvailableWindow.show();
		} else {
			// update window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.updateAvailableDialog = new UpdateAvailableDialog(versionMajor, versionMinor, versionDev);
		}
	}
	
	// called if the user selected a manual check for updates and they are running the latest version
	public function updatesNotAvailable() : Void {
		debug("UpdateHandler: Updates Not Available (dialog)!\n");
		
		// only called if an update is manually checked for
		_level0.preferencesDialog.updatesButton.setText("No Updates Available");
		_level0.preferencesDialog.updatesButton.setEnabled(false);
		
	}
	
	public function parseVersionInfo(version : String) : Void {
		var splits : Array = version.split('.');
		if(splits.length != 3) {
			_level0.debug("WARNING: UpdateHandler: latest version information invalid\n");
		} else {
			versionMajor = parseInt(splits[0]);
			versionMinor = parseInt(splits[1]);
			versionDev = parseInt(splits[2]);
		}
	}
	
	
	
	
}

