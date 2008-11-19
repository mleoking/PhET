// Preferences.as
//
// Handles loading of stored client-side preferences data,
// and allows other entities within the simulation to check
// the preferences.
//
// Author: Jonathan Olson


class Preferences {
	
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
		
		// if it is the first time simulations have been run from
		// their domain, we need to fill in default values.
		// we set data.exists, so in the future we can tell whether
		// SharedObject.getLocal() reads preferences or creates the
		// object.
		if(!sharedObject.data.exists) {
			debug("Preferences do not exist! Creating, and filling with defaults.\n");
			sharedObject.data.exists = true;
			sharedObject.data.allowTracking = true;
			sharedObject.data.checkForUpdates = true;
			sharedObject.data.userId = String((new Date()).valueOf()) + String(Math.floor(Math.random() * 10000));
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
		
		//reset();
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
		save();
	}
	
	// return the user's ID that is saved in the preferences:
	public function userId() : String {
		return sharedObject.data.userId;
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
		
		// key will look like "pendulum-lab_visits"
		var key : String = _level0.simName + "_visits";
		
		// check whether property exists first. might be a new sim
		// or one the user hasn't seen yet.
		if(sharedObject.data.hasOwnProperty(key)) {
			sharedObject.data[key] = sharedObject.data[key] + 1;
		} else {
			sharedObject.data[key] = 1;
		}
	}
}

