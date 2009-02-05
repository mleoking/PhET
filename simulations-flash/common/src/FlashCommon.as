// FlashCommon.as
//
// coordinates all common flash code, including the updates and statistics
//
// Author: Jonathan Olson

import org.aswing.EmptyLayout;
import org.aswing.JButton;
import org.aswing.JWindow;
import org.aswing.JFrame;
import org.aswing.*;

// should be instance of FlashCommon at _level0.common
class FlashCommon {
	
	// the default background color for AsWing components (windows and text)
	public var backgroundColor : ASColor;
	
	// whether to dump debugging messages into the text field
	public var debugging : Boolean = true;
	
	// handles internationalization for common strings
	public var commonStrings : CommonStrings;
	
	// handles preferences the user selects, such as
	// enabling/disabling updates and statistics messages. also
	// stores how many times the particular sim has
	// been run
	public var preferences : Preferences;
	
	// handles checking for updates and handling what to do
	// if a newer version of the simulation is found
	public var updateHandler : UpdateHandler;
	
	// handles sending statistics messages to the server
	public var statistics : Statistics;
	
	/////////////////////////
	public var commonButtons : CommonButtons;
	
	public var commonPosition : String;
	
	// handles keyboard accessibility (tab traversal)
	public var tabHandler : TabHandler;
	
	// DEVELOPMENT
	public var inspector : Inspector;
	
	// initializes debug function at _level0.debug()
	public function initDebug() : Void {
		if(debugging) {
			var padding : Number = 50;
			_root.createTextField("debugs", _root.getNextHighestDepth(), padding, padding, Stage.width - 2 * padding, Stage.height - 2 * padding);
			_root.debugs._visible = false;
			_root.debugs.background = true;
			_root.debugs.wordWrap = true;
			_root.debugs.multiline = true;
			_root.debugs.border = true;
			_level0.debug = function(str : String) : Void {
				_root.debugs.text += str;
				_root.debugs.scroll += 100;
				_root.debugs.html = true;
			}
		} else {
			_level0.debug = function(str : String) : Void {
			}
			_root.debugs._visible = false;
		}
	}
	
	// shorthand to call _level0.debug()
	public function debug(str : String) : Void {
		_level0.debug(str);
	}
	
	// constructor
	public function FlashCommon(position : String) {
		initDebug();
		
		// make it accessible from everywhere
		_level0.common = this;
		
		// DEVELOPMENT: catch key events to this object
		Key.addListener(this);
		
		// set the default background color
		backgroundColor = ASColor.getASColor(230, 230, 230);
		
		// TODO: Possibly extend this to run from other domains?
		System.security.allowDomain("phet.colorado.edu");
		
		debug("Debugging information:\n");
		debug("FlashCommon initializing\n");
		
		// display version of this simulation
		debug("Running " + _level0.simName + " " + _level0.versionMajor);
		debug("." + _level0.versionMinor);
		debug(" dev:" + _level0.versionDev);
		debug(" rev:" + _level0.versionRevision + "\n");
		
		// store the position of the common buttons for CommonButtons
		commonPosition = position;
		
		// load internationalization strings for common code
		commonStrings = new CommonStrings();
		
		// initializes the TabHandler
		tabHandler = new TabHandler();
		
		// load the statistics handler, but do not send the session-start message!!!
		statistics = new Statistics();
		
		// load preferences data
		preferences = new Preferences();
		
		// DEVELOPMENT: load the inspector
		inspector = new Inspector();
		
	}
	
	// this should be called from preferences when it is verified the
	// privacy agreement has been accepted
	public function postAgreement() : Void {
		
		// load update handler
		// must have preferences loaded first before loading updatehandler.
		updateHandler = new UpdateHandler();
		
		// load statistics handler
		// must have preferences loaded first before loading Statistics.
		statistics.sendSessionStart();
		
		// load buttons with the position (defaults to upper left)
		commonButtons = new CommonButtons(commonPosition);
	}
	
	// return a string representing the sim's locale (NOT the user's default)
	public function localeString() : String {
		var str : String = _level0.languageCode;
		
		// if we have a country code, add _XX to the locale
		if(_level0.countryCode != "none") {
			str += "_" + _level0.countryCode;
		}
		return str;
	}
	
	// returns whether the sim was run from the phet website
	public function fromPhetWebsite() : Boolean {
		return ((new LocalConnection()).domain() == "phet.colorado.edu");
	}
	
	// returns whether the sim was run from a full installation
	public function fromFullInstallation() : Boolean {
		return (_level0.simDeployment == "phet-installation");
	}
	
	// returns whether the sim was run from a standalone jar
	public function fromStandaloneJar() : Boolean {
		return (_level0.simDeployment == "standalone-jar");
	}
	
	// return whether a string is a placeholder
	public function isPlaceholder(str : String) : Boolean {
		return (str.substr(0, 2) == "@@" && str.substr(-2, 2) == "@@");
	}
	
	// DEVELOPMENT
	public function onKeyDown() {
		if(Key.getCode() == Key.PGUP) {
			_level0.debugs._visible = !_level0.debugs._visible;
		}
		if(Key.getCode() == 120) {
			// F9 was pressed
			_level0.updateHandler.updatesAvailable("5", "10", "00");
		}
	}
}
