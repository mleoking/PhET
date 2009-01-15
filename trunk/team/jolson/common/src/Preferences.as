// Preferences.as
//
// Handles loading of stored client-side preferences data,
// and allows other entities within the simulation to check
// the preferences.
//
// Author: Jonathan Olson


class Preferences {
	
	// current preferences version
	// this SHOULD NOT CHANGE after development, and is an
	// aid for development purposes.
	public static var CURRENT_PREF_VERSION : Number = 1.0;
	
	// current privacy agreement version
	// this should be changed when a new agreement would need to be
	// accepted by people who have accepted an old agreement
	public static var CURRENT_PRIVACY_VERSION : Number = 1.0;
	
	// reference to the shared object used to store preferences
	public var sharedObject : SharedObject;
	
	// shorthand for debugging function
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function Preferences() {
		debug("Preferences initializing\n");
		
		// make this object accessible from _level0.preferences
		// should only be one copy of Preferences (singleton-like)
		_level0.preferences = this;
		
		// load shared object
		sharedObject = SharedObject.getLocal("phetPrefs", "/");
		
		/////////////////////////////////////////
		// TEMPORARY FOR DEVELOPMENT PURPOSES
		/////////////////////////////////////////
		// if they don't have the newest field, reset their shared object!
		//if(!sharedObject.data.userId) {
			//debug("Preferences: DEVELOPMENT: resetting shared object, new information to be stored\n");
			//reset();
		//}
		/////////////////////////////////////////
		if(!sharedObject.data.dataVersion || sharedObject.data.dataVersion != CURRENT_PREF_VERSION) {
			debug("Preferences: DEVELOPMENT: resetting shared object, new information to be stored\n");
			reset();
		}
		/////////////////////////////////////////
		// for resetting sharedobject data
		Key.addListener(this);
		/////////////////////////////////////////
		
		// if it is the first time simulations have been run from
		// their domain, we need to fill in default values.
		// we set data.exists, so in the future we can tell whether
		// SharedObject.getLocal() reads preferences or creates the
		// object.
		if(!sharedObject.data.exists) {
			// TODO: We must now prompt the user to accept the privacy conditions.
			debug("Preferences do not exist! Creating, and filling with defaults.\n");
			sharedObject.data.exists = true;
			sharedObject.data.allowTracking = true;
			sharedObject.data.checkForUpdates = true;
			sharedObject.data.dataVersion = CURRENT_PREF_VERSION;
			sharedObject.data.userPreferencesFileCreationTime = (new Date()).valueOf();
			sharedObject.data.userTotalSessions = 0;
			sharedObject.data.latestPrivacyAgreementVersion = 0;
		} else {
			debug("Found preferences\n");
		}
		
		// increment the number of times the current sim has been run
		incrementVisit();
		
		// save the shared object (preferences) to filesystem.
		save();
		
		// for debugging: print out each piece of data stored in the shared object
		for(var i : String in sharedObject.data) {
			debug("    pref: " + i + " = " + String(sharedObject.data[i]) + "\n");
		}
		
		// if privacy is not up-to-snuff, present the user with a dialog
		if(!isPrivacyOK()) {
			_level0.privacyDialog = new PrivacyDialog();
		}
		
		//reset();
	}
	
	public function isPrivacyOK() : Boolean {
		return CURRENT_PRIVACY_VERSION <= sharedObject.data.latestPrivacyAgreementVersion;
	}
	
	public function agreeToPrivacy() : Void {
		sharedObject.data.latestPrivacyAgreementVersion = CURRENT_PRIVACY_VERSION;
		save();
	}
	
	// allow other common code/simulation to check whether
	// the user allows tracking
	public function allowTracking() : Boolean {
		return sharedObject.data.allowTracking;
	}
	
	// allow other common code/simulation to check whether
	// the user allows checking for updates
	public function checkForUpdates() : Boolean {
		return sharedObject.data.checkForUpdates;
	}
	
	// allow other code to set the tracking and updates values
	public function setTracking(updates : Boolean, tracking : Boolean) : Void {
		sharedObject.data.allowTracking = tracking;
		sharedObject.data.checkForUpdates = updates;
		debug("setting tracking to " + tracking.toString() + "\n");
		debug("setting updates to " + updates.toString() + "\n");
		save();
	}
	
	// resets (clears) any data stored on disk
	// (also resets the data in the local copy)
	public function reset() : Void {
		debug("preferences: resetting\n");
		sharedObject.clear();
	}
	
	// saves the shared object (preferences data) to the
	// user's hard drive.
	public function save() : Void {
		debug("preferences: saving\n");
		sharedObject.flush();
	}
	
	// creates or increments a preferences attribute specifying
	// how many times the current simulation has been run.
	public function incrementVisit() : Void {
		debug("preferences: Incrementing number of visits\n");
		
		// increment total visits
		sharedObject.data.userTotalSessions = sharedObject.data.userTotalSessions + 1;
		
		// keys for sim-specific counts. will look like
		// "pendulum-lab_visitsEver" and "pendulum-lab_visitsSince"
		var keyEver : String = _level0.simName + "_visitsEver";
		var keySince : String = _level0.simName + "_visitsSince";
		
		// check whether property exists first. might be a new sim
		// or one the user hasn't seen yet.
		if(sharedObject.data.hasOwnProperty(keyEver)) {
			sharedObject.data[keyEver] = sharedObject.data[keyEver] + 1;
		} else {
			sharedObject.data[keyEver] = 1;
		}
		
		if(sharedObject.data.hasOwnProperty(keySince)) {
			sharedObject.data[keySince] = sharedObject.data[keySince] + 1;
		} else {
			sharedObject.data[keySince] = 1;
		}
	}
	
	// resets the number of #'s since sent
	public function resetSince() : Void {
		sharedObject.data[_level0.simName + "_visitsSince"] = 0;
	}
	
	// how many times the current simulation has ever been run (according to preferences)
	public function visitsEver() : Number {
		return sharedObject.data[_level0.simName + "_visitsEver"];
	}
	
	// how many times the current simulation has been run since last message sent (according to preferences)
	public function visitsSince() : Number {
		return sharedObject.data[_level0.simName + "_visitsSince"];
	}
	
	public function getUserTime() : Number {
		return sharedObject.data.userPreferencesFileCreationTime;
	}
	public function getUserTotalSessions() : Number {
		return sharedObject.data.userTotalSessions;
	}
	
	/////////////////////////////////////////
	// for resetting preferences data
	public function onKeyDown() : Void {
		if(Key.getCode() == 119) {
			// F8 was pressed
			debug("Resetting shared data\n");
			reset();
		}
	}
}

