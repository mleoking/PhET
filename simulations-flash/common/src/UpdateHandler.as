
// UpdateHandler.as
//
// Handles checking for a newer version of the simulation
// online, and what to do if that newer version exists.
//
// Author: Jonathan Olson

import org.aswing.ASColor;

class UpdateHandler {
	public static var SIM_REQUEST_VERSION : String = "1";
	public static var INSTALLER_REQUEST_VERSION : String = "1";
	
	// the latest version information detected from the server
	// TODO: rename these to match _level0 fields
	public var versionMajor : Number;
	public var versionMinor : Number;
	public var versionDev : Number;
	public var versionRevision : Number;
	public var simTimestamp : Number;
	public var simAskLaterDays : Number;
	
	public var installerRecommend : Boolean;
	public var installerTimestamp : Number;
	public var installerAskLaterDays : Number;
	
	// whether the "Check for updates now" button was clicked
	// (manual check for updates)
	public var manual : Boolean;
	
	public var common : FlashCommon;
	
	public var receivedSimResponse : Boolean;
	public var receivedInstallationResponse : Boolean;
	
	// shorthand debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function UpdateHandler() {
		//debug("UpdateHandler initializing\n");
		
		// shortcut to FlashCommon, but now with type-checking!
		common = _level0.common;
		
		// set to true if the user is manually checking for updates
		// in this case, we should give them a response if they are
		// running the current version
		manual = false;
		
		receivedSimResponse = false;
		receivedInstallationResponse = false;
		
		// make this object accessible from _level0.updateHandler
		// should only be one copy of UpdateHandler (singleton-like)
		_level0.updateHandler = this;
		common.updateHandler = this;
		
		// make sure the user allows us to check for updates!
		if(!common.preferences.areUpdatesAllowed()) {
			debug("UpdateHandler: not checking for updates (Preferences.areUpdatesAllowed() = false)\n");
		} else {
			// check for both sim and installation
			sendStartupQuery(startupQueryString(true, true));
		}
		
	}
	
	public function startupQueryString(checkSim : Boolean, checkInstallation : Boolean) : String {
		// if user isn't querying anything, return undefined
		if(!(checkSim || (checkInstallation && common.fromFullInstallation()))) {
			return undefined;
		}
		
		var str = "<?xml version=\"1.0\"?>";
		str += "<phet_info>";
		
		if(checkSim) {
			str += "<sim_version request_version=\"" + SIM_REQUEST_VERSION + "\" project=\"" + common.getSimProject() + "\" sim=\"" + common.getSimName() + "\" />";
		}
		
		if(checkInstallation && common.fromFullInstallation()) {
			str += "<phet_installer_update request_version=\"" + INSTALLER_REQUEST_VERSION + "\" timestamp_seconds=\"" + String(common.getInstallationTimestamp()) + "\" />";
		}
		
		str += "</phet_info>";
		
		_level0.debug("UpdateHandler (2): Startup query:\n" + str + "\n");
		
		return str;
	}
	
	public function sendStartupQuery(query : String) : Void {
		
		if(query === undefined) {
			// must not be querying for anything, don't do anything
			return;
		}
		
		if(!common.hasFlashVars()) {
			debug("UpdateHandler: could not find flash vars, will not check for updates\n");
			return;
		}
		
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
				_level0.debug("UpdateHandler (2): reply successfully received\n");
				_level0.debug(String(xml) + "\n");
				
				// TODO: remove after DEVELOPMENT
				_level0.debugXML = xml;
				
				hand.receivedSimResponse = false;
				hand.receivedInstallationResponse = false;
				
				if(xml.childNodes[0].attributes.success != "true") {
					_level0.debug("WARNING UpdateHandler: phet_info_response failure\n");
					return;
				}
				
				var children : Array = xml.childNodes[0].childNodes; // children of sim_startup_query_response
				
				var hand : UpdateHandler = _level0.updateHandler;
				
				
				
				for(var idx in children) {
					var child = children[idx];
					var atts : Object = child.attributes;
					if(child.nodeName == "sim_version_response") {
						_level0.debug("UpdateHandler (2): received sim_version_response\n");
						// sanity checks
						if(atts["success"] != "true") {
							_level0.debug("WARNING UpdateHandler: sim_version_response failure\n");
							
							// do not continue further with this child
							continue;
						}
						if(atts["project"] != hand.common.getSimProject()) {
							_level0.debug("WARNING UpdateHandler (2): Project does not match\n");
						}
						if(atts["sim"] != hand.common.getSimName()) {
							_level0.debug("WARNING UpdateHandler (2): Name does not match\n");
						}
						
						hand.receivedSimResponse = true;
						
						hand.versionMajor = parseInt(atts["version_major"]);
						hand.versionMinor = parseInt(atts["version_minor"]);
						hand.versionDev = parseInt(atts["version_dev"]);
						hand.versionRevision = parseInt(atts["version_revision"]);
						hand.simTimestamp = parseInt(atts["version_timestamp"]);
						hand.simAskLaterDays = parseInt(atts["ask_me_later_duration_days"]);
						
						hand.debug("   latest: " + hand.common.zeroPadVersion(hand.versionMajor, hand.versionMinor, hand.versionDev) + " (" + String(hand.versionRevision) + ")\n");
						
					} else if(child.nodeName == "phet_installer_update_response") {
						_level0.debug("UpdateHandler (2): received phet_installer_update_response\n");
						
						if(atts["success"] != "true") {
							_level0.debug("WARNING UpdateHandler: phet_installer_update_response failure\n");
							
							// do not continue further with this child
							continue;
						}
						
						hand.receivedInstallationResponse = true;
						
						hand.installerRecommend = (atts["recommend_update"] == "true");
						hand.installerTimestamp = parseInt(atts["timestamp_seconds"]);
						hand.installerAskLaterDays = parseInt(atts["ask_me_later_duration_days"]);
					} else {
						_level0.debug("WARNING UpdateHandler (2): unknown child: " + child.nodeName + "\n");
						_level0.debugLastUnknownChild = child;
					}
				}
				
				hand.handleResponse();
				
			} else {
				_level0.debug("WARNING: UpdateHandler (2): Failure to obtain latest version information\n");
			}
		}
		
		// send the request, wait for the response to load
		//xml.load("http://localhost/jolson/deploy/fake-sim-startup-query.php?request=" + escape(query));
		var queryXML = new XML(query);
		queryXML.addRequestHeader("Content-type", "text/xml");
		queryXML.sendAndLoad("http://phet.colorado.edu/services/phet-info", xml);
	}
	
	public function manualCheckSim() : Void {
		debug("UpdateHandler: checking manually for sim");
		manual = true;
		sendStartupQuery(startupQueryString(true, false));
	}
	
	public function manualCheckInstallation() : Void {
		debug("UpdateHandler: checking manually for installation");
		manual = true;
		sendStartupQuery(startupQueryString(false, true));
	}
	
	public function handleResponse() : Void {
		//debug("UpdateHandler: handleResponse()\n");
		
		var installShown = false;
		
		if(receivedInstallationResponse && common.fromFullInstallation()) {
			receivedInstallationResponse = false;
			
			if(installerRecommend) {
				if(!manual && common.preferences.installationAskLaterElapsed() < 0) {
					_level0.debug("UpdateHandler: used selected ask later, installation time elapsed = " + String(common.preferences.installationAskLaterElapsed()) + "\n");
				} else {
					installShown = true;
					installationUpdatesAvailable(installerTimestamp, installerAskLaterDays);
				}
			} else {
				if(manual) {
					_level0.preferencesDialog.updatesInstallationButton.setText(common.strings.get("NoUpdatesAvailable", "No Updates Available"));
					_level0.preferencesDialog.updatesInstallationButton.setEnabled(false);
					_level0.preferencesDialog.updatesInstallationButton.setForeground(ASColor.RED);
				}
				
				// run this again to handle whether sim response was received
				handleResponse();
			}
		}
		
		if(receivedSimResponse && !installShown) {
			receivedSimResponse = false;
			
			var latestSkipped : Array = common.preferences.getLatestSkippedUpdate();
			
			if(versionRevision == common.getVersionRevision()) {
				// running the latest version
				_level0.debug("UpdateHandler: running latest version\n");
				
				// if the user clicked "Check for Updates Now", inform the user that no
				// update is available
				if(manual) {
					_level0.preferencesDialog.updatesSimButton.setText(common.strings.get("NoUpdatesAvailable", "No Updates Available"));
					_level0.preferencesDialog.updatesSimButton.setEnabled(false);
					_level0.preferencesDialog.updatesSimButton.setForeground(ASColor.RED);
				}
			} else if(versionRevision < common.getVersionRevision()) {
				_level0.debug("WARNING UpdateHandler: running a more recent version than on the production website.\n");
			} else if(versionMajor == undefined || versionMinor == undefined) {
				_level0.debug("WARNING UpdateHandler: received undefined version information!\n");
			} else if(!manual && (versionMajor < latestSkipped[0] || (versionMajor == latestSkipped[0] && versionMinor <= latestSkipped[1]))) {
				// user did not click "Check for Updates Now" AND the new version <= latest skipped version
				_level0.debug("UpdateHandler: used selected to skip this update\n");
			} else if(!manual && common.preferences.simAskLaterElapsed() < 0) {
				_level0.debug("UpdateHandler: used selected ask later, sim time elapsed = " + String(common.preferences.simAskLaterElapsed()) + "\n");
			} else {
				if(common.fromFullInstallation() && simTimestamp + 1800 > installerTimestamp) {
					_level0.debug("UpdateHandler: installer might not contain the most recent sim\n");
				}
				simUpdatesAvailable(versionMajor, versionMinor, versionDev, simAskLaterDays);
			}
			
		}
		
		
	}
	
	
	// called if a newer version is available online
	public function simUpdatesAvailable(versionMajor : Number, versionMinor : Number, versionDev : Number, simAskLaterDays : Number) : Void {
		debug("UpdateHandler: Sim Updates Available (dialog)!\n");
		
		if(_level0.updateSimWindow) {
			// update window exists, just show it
			debug("Showing dialog again\n");
			_level0.updateSimWindow.show();
		} else {
			// update window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.updateSimDialog = new UpdateSimDialog(versionMajor, versionMinor, versionDev, simAskLaterDays);
		}
	}
	
	// called if a newer version is available online
	public function installationUpdatesAvailable(installerTimestamp : Number, installerAskLaterDays : Number) : Void {
		debug("UpdateHandler: Installation Updates Available (dialog)!\n");
		
		if(_level0.updateInstallationWindow) {
			// update window exists, just show it
			debug("Showing dialog again\n");
			_level0.updateInstallationWindow.show();
		} else {
			// update window doesn't exist, we must create it
			debug("Creating Dialog\n");
			_level0.updateInstallationDialog = new UpdateInstallationDialog(installerTimestamp, installerAskLaterDays);
		}
	}
	
	
}

