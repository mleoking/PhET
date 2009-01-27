﻿
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
		if(_level0.preferences.areUpdatesAllowed()) {
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
				// XML was returned and successfully parsed (valid XML, but we need to still
				// extract information from it
				_level0.debug("UpdateHandler: successfully obtained version information\n");
				
				versionMajor = xml.firstChild.childNodes[0].attributes.value;
				versionMinor = xml.firstChild.childNodes[1].attributes.value;
				dev = xml.firstChild.childNodes[2].attributes.value;
				revision = xml.firstChild.childNodes[3].attributes.value;
				
				// use _level0.debug since we cannot access the shorthand version from the callback
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
				} else if(versionMajor != undefined && versionMinor != undefined) {
					_level0.common.updateHandler.updatesAvailable(versionMajor, versionMinor, dev);
				}
			} else {
				_level0.debug("UpdateHandler: network failure, cannot read version information\n");
			}
		}
		
		/////////////////////////////////////////////
		// TODO needs to be changed to the path of the version script for each simulation
		// most likely will be http://phet.colorado.edu/simulations/sim-version-info.php?project=BLAH&sim=BLAH
		xml.load("http://phet.colorado.edu/jolson/deploy/sims/version.php?" + _level0.simName);
		
		
		var testXML : XML = new XML();
		testXML.ignoreWhite = true;
		testXML.onLoad = function(success : Boolean) {
			if(success) {
				_level0.debug("UpdateHandler: TEST: success of message\n");
				_level0.debug(String(testXML) + "\n");
			} else {
				_level0.debug("UpdateHandler: TEST: failure of message\n");
			}
		}
		
		testXML.load("http://phet.colorado.edu/simulations/sim-version-info.php?project=" + _level0.simName + "&sim=" + _level0.simName);
	}
	
	public function manualCheck() : Void {
		debug("UpdateHandler: checking manually for updates");
		manual = true;
		checkUpdates();
	}
	
	// called if a newer version is available online
	public function updatesAvailable(versionMajor : String, versionMinor : String, dev : String) : Void {
		debug("UpdateHandler: Updates Available (dialog)!\n");
		
		if(_level0.updateAvailableWindow) {
			// update window exists, just show it
			debug("Showing dialog again\n");
			_level0.updateAvailableWindow.show();
		} else {
			// update window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.updateAvailableDialog = new UpdateAvailableDialog(versionMajor, versionMinor, dev);
		}
	}
	
	public function updatesNotAvailable() : Void {
		debug("UpdateHandler: Updates Not Available (dialog)!\n");
		
		// TODO: fill in with desired behavior.
		// only called if an update is manually checked for
		_level0.preferencesDialog.updatesButton.setText("No Updates Available");
		_level0.preferencesDialog.updatesButton.setEnabled(false);
	}
}

