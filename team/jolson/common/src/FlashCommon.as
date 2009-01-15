// FlashCommon.as
//
// coordinates all common flash code, including the updates and tracking
//
// Author: Jonathan Olson

import org.aswing.EmptyLayout;
import org.aswing.JButton;
import org.aswing.JWindow;
import org.aswing.JFrame;
import org.aswing.*;

// should be instance of FlashCommon at _level0.common
class FlashCommon {
	
	public var backgroundColor : ASColor;
	
	public var debugging : Boolean = true;
	
	// handles internationalization for common strings
	public var commonStrings : CommonStrings;
	
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
	public var commonButtons : CommonButtons;
	
	// handles keyboard accessibility (tab traversal)
	public var tabHandler : TabHandler;
	
	// initializes debug function at _level0.debug()
	public function initDebug() : Void {
		if(debugging) {
			_level0.debug = function(str : String) : Void {
				_level0.debugs.text += str;
				_level0.debugs.scroll += 100;
			}
		} else {
			_level0.debug = function(str : String) : Void {
			}
			_level0.debugs._visible = false;
		}
	}
	
	// shorthand to call _level0.debug()
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function FlashCommon() {
		initDebug();
		
		// make it accessible from everywhere
		_level0.common = this;
		
		// set the default background color
		backgroundColor = ASColor.getASColor(230, 230, 230);
		
		// TODO: Possibly extend this to run from other domains?
		System.security.allowDomain("phet.colorado.edu");
		
		debug("Debugging information:\n");
		debug("FlashCommon initializing\n");
		
		// display version of this simulation
		debug("Running " + _level0.simName + " " + _level0.versionMajor);
		debug("." + _level0.versionMinor);
		debug(" dev:" + _level0.dev);
		debug(" rev:" + _level0.revision + "\n");
		
		// load internationalization strings for common code
		commonStrings = new CommonStrings();
		
		// load preferences data
		preferences = new Preferences();
		
		// load update handler
		// must have preferences loaded first before loading updatehandler.
		updateHandler = new UpdateHandler();
		
		// load tracking handler
		// must have preferences loaded first before loading TrackingHandler.
		trackingHandler = new TrackingHandler();
		
		// load buttons (defaults to the upper left)
		commonButtons = new CommonButtons();
		
		// initializes the TabHandler
		tabHandler = new TabHandler();
	}
	
	public function localeString() : String {
		var str : String = _level0.languageCode;
		if(_level0.countryCode != "none") {
			str += "_" + _level0.countryCode;
		}
		return str;
	}
}
