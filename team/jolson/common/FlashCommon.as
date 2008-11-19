// FlashCommon.as
//
// coordinates all common flash code, including the updates and tracking
//
// Author: Jonathan Olson


// should be instance of FlashCommon at _level0.common
class FlashCommon {
	
	public var debugging : Boolean = true;
	
	// handles preferences the user selects, such as
	// enabling/disabling updates and tracking. also
	// stores how many times the particular sim has
	// been run
	public var preferences : Preferences;
	
	// handles checking for updates and handling what to do
	// if a newer version of the simulation is found
	public var updateHandler : UpdateHandler;
	
	// handles sending tracking messages to the server
	public var trackingHandler : TrackingHandler;
	
	/////////////////////////
	public var aboutButton : AboutButton;
	
	// initializes debug function at _level0.debug()
	public function initDebug() : Void {
		if(debugging) {
			_level0.debug = function(str : String) : Void {
				_level0.debugs.text += str;
			}
		} else {
			_level0.debug = function(str : String) : Void {
			}
		}
	}
	
	// shorthand to call _level0.debug()
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function FlashCommon() {
		initDebug();
		
		System.security.allowDomain("phet.colorado.edu");
		
		debug("Debugging information:\n");
		debug("FlashCommon initializing\n");
		
		// display version of this simulation
		debug("Running " + _level0.simName + " " + _level0.versionMajor);
		debug("." + _level0.versionMinor);
		debug(" dev:" + _level0.dev);
		debug(" rev:" + _level0.revision + "\n");
		
		// load preferences data
		preferences = new Preferences();
		
		// load update handler
		// must have preferences loaded first before loading updatehandler.
		updateHandler = new UpdateHandler();
		
		// load tracking handler
		// must have preferences loaded first before loading TrackingHandler.
		trackingHandler = new TrackingHandler();
		
		// load tracking button (defaults to the upper left)
		aboutButton = new AboutButton();
	}
}
