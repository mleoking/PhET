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
import org.aswing.geom.*;

// should be instance of FlashCommon at _level0.common
class FlashCommon {
	
	public static var NULLVAL : String = "null";
	
	// the default background color for AsWing components (windows and text)
	public var backgroundColor : ASColor;
	
	// whether to dump debugging messages into the text field
	public var debugging : Boolean = true;
	
	// handles internationalization for common strings
	public var strings : CommonStrings;
	
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
			_root.createTextField("debugs", 104384, padding, padding, Stage.width - 2 * padding, Stage.height - 2 * padding);
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
		debug("Running " + getSimName() + " " + getFullVersionString() + "\n");
		
		// store the position of the common buttons for CommonButtons
		commonPosition = position;
		
		// load internationalization strings for common code
		strings = new CommonStrings();
		
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
	
	// returns whether the sim was run from the phet website
	public function fromPhetWebsite() : Boolean {
		return (new LocalConnection()).domain() == "phet.colorado.edu";
	}
	
	// returns whether the sim was run from a development site
	public function fromDevWebsite() : Boolean {
		var host : String = (new LocalConnection()).domain();
		return (host == "www.colorado.edu");
	}
	
	// returns whether the sim was run from a full installation
	public function fromFullInstallation() : Boolean {
		return (
			!fromPhetWebsite()
			&& !isPlaceholder(_level0.installationTimestamp)
			&& null_replace(_level0.installationTimestamp) != FlashCommon.NULLVAL
		);
	}
	
	// returns whether the sim was run from a standalone jar
	public function fromStandaloneJar() : Boolean {
		return (_level0.simDeployment == "standalone-jar");
	}
	
	// return whether a string is a placeholder
	public function isPlaceholder(str : String) : Boolean {
		return (str.substr(0, 2) == "@@" && str.substr(-2, 2) == "@@");
	}
	
	// TODO: remove after DEVELOPMENT??? or make harder to access debugging areas
	public function onKeyDown() {
		if(Key.getCode() == Key.PGUP || Key.getCode() == 121) {
			// page up OR F10
			_level0.debugs._visible = !_level0.debugs._visible;
		}
		if(Key.getCode() == 120) {
			// F9 was pressed
			updateHandler.simUpdatesAvailable(5, 10, 0, 0);
		}
		if(Key.getCode() == 36) {
			// Home was pressed
			updateHandler.installationUpdatesAvailable(1234567890, 0, 0);
		}
		if(Key.getCode() == 35) {
			// End was pressed
			//_level0.updateInstallationDetailsDialog.textArea.setHeight(300);
			/*
			debug("h: " + String(_level0.updateInstallationDetailsDialog.textArea.getTextField().textHeight) + "\n");
			var ta : JTextArea = _level0.updateInstallationDetailsDialog.textArea;
			ta.setHeight(ta.getTextField().textHeight);
			ta.updateUI();
			ta.revalidate();
			ta.getParent().revalidate();
			ta.paintImmediately();
			_level0.updateInstallationDetailsWindow.revalidate();
			ta.getParent().paintImmediately();
			debug("paintwidth: " + String(ta.getPaintBounds().width) + "\n");
			debug("paintheight: " + String(ta.getPaintBounds().height) + "\n");
			ta.paintImmediately();
			ta.invalidate();
			_level0.updateInstallationDetailsWindow.paintImmediately();
			_level0.updateInstallationDetailsWindow.invalidate();
			*/
			/*
			var ta : JTextArea = _level0.testTextArea;
			var tf : TextField = ta.getTextField();
			var d : Dimension;
			var b : Rectangle;
			
			debug("tf _width/_height: (" + String(tf._width) + ", " + String(tf._height) + ")\n");
			debug("tf textWidth/textHeight: (" + String(tf.textWidth) + ", " + String(tf.textHeight) + ")\n");
			debug("ta getWidth()/getHeight(): (" + String(ta.getWidth()) + ", " + String(ta.getHeight()) + ")\n");
			d = ta.getTextFieldAutoSizedSize();
			debug("ta getTextFieldAutoSizedSize: (" + String(d.width) + ", " + String(d.height) + ")\n");
			d = ta.countPreferredSize();
			debug("ta countPreferredSize: (" + String(d.width) + ", " + String(d.height) + ")\n");
			d = ta.getViewSize();
			debug("ta getViewSize: (" + String(d.width) + ", " + String(d.height) + ")\n");
			d = ta.getExtentSize();
			debug("ta getExtentSize: (" + String(d.width) + ", " + String(d.height) + ")\n");
			b = ta.getBounds();
			debug("ta getBounds: (" + String(b.width) + ", " + String(b.height) + ")\n");
			b = ta.getPaintBounds();
			debug("ta getPaintBounds: (" + String(b.width) + ", " + String(b.height) + ")\n");
			*/
		}
	}
	
	// returns the version string with minor and dev fields padded with a zero if necessary
	public function zeroPadVersion(versionMajor : Number, versionMinor : Number, versionDev : Number) : String {
		var ret : String = "";
		ret += String(versionMajor) + ".";
		ret += (versionMinor > 9 ? String(versionMinor) : "0" + String(versionMinor));
		if(versionDev != null && versionDev != undefined) {
			ret += "." + (versionDev > 9 ? String(versionDev) : "0" + String(versionDev));
		}
		return ret;
	}
	
	// version like "1.00"
	public function getShortVersionString() : String {
		return zeroPadVersion(getVersionMajor(), getVersionMinor());
	}
	
	// version like "1.00.00"
	public function getVersionString() : String {
		return zeroPadVersion(getVersionMajor(), getVersionMinor(), getVersionDev());
	}
	
	// version like "1.00.00 (20000)"
	public function getFullVersionString() : String {
		return getVersionString() + " (" + String(getVersionRevision()) + ")";
	}
	
	// get the URL of the simulation on the website
	public function simWebsiteURL() : String {
		var str : String = "http://phet.colorado.edu/simulations/sims.php?sim=";
		for(var i : Number = 0; i < _level0.simName.length; i++) {
			if(_level0.simName.charAt(i) == "-") {
				str += "_";
			} else {
				str += _level0.simName.charAt(i);
			}
		}
		return str;
	}
	
	public function getLocale() : String {
		var str : String = getLanguage();
		
		// if we have a country code, add _XX to the locale
		if(getCountry() != NULLVAL) {
			str += "_" + getCountry();
		}
		return str;
	}
	
	public function null_replace(val : String) : String {
		// TODO: possibly integrate checking for a placeholder
		if(val === undefined || val === null) {
			return NULLVAL;
		}
		return val;
	}
	
	
	
	
	
	// get functions for information passed through FlashVars
	
	
	public function getSimProject() : String {
		return _level0.simName;
	}
	public function getSimName() : String {
		return _level0.simName;
	}
	public function getLanguage() : String {
		return _level0.languageCode;
	}
	public function getCountry() : String {
		return null_replace(_level0.countryCode);
	}
	public function getVersionMajor() : Number {
		return parseInt(_level0.versionMajor);
	}
	public function getVersionMinor() : Number {
		return parseInt(_level0.versionMinor);
	}
	public function getVersionDev() : Number {
		return parseInt(_level0.versionDev);
	}
	public function getVersionRevision() : Number {
		return parseInt(_level0.versionRevision);
	}
	public function getSimXML() : String {
		return _level0.internationalization;
	}
	public function getCommonXML() : String {
		return _level0.commonStrings;
	}
	public function getDeployment() : String {
		if(fromFullInstallation()) {
			return "phet-installation";
		} else if (fromDevWebsite()) {
			return "phet-development-website";
		} else {
			return _level0.simDeployment;
		}
	}
	public function getDev() : Boolean {
		if(_level0.simDev == "false" || _level0.simDev == "0") {
			return false;
		}
		return true;
	}
	public function getDistributionTag() : String {
		if(isPlaceholder(_level0.simDistributionTag) || null_replace(_level0.simDistributionTag) == NULLVAL) {
			return null;
		}
		return _level0.simDistributionTag;
	}
	public function getInstallationTimestamp() : Number {
		if(null_replace(_level0.installationTimestamp) == NULLVAL || isPlaceholder(_level0.installationTimestamp)) {
			return null;
		} else {
			return parseInt(_level0.installationTimestamp);
		}
	}
	public function getInstallerCreationTimestamp() : Number {
		if(null_replace(_level0.installerCreationTimestamp) == NULLVAL || isPlaceholder(_level0.installerCreationTimestamp)) {
			return null;
		} else {
			return parseInt(_level0.installerCreationTimestamp);
		}
	}
	public function getVersionTimestamp() : Number {
		if(null_replace(_level0.versionTimestamp) == NULLVAL) {
			return null;
		} else {
			return parseInt(_level0.versionTimestamp);
		}
	}
	public function getBGColor() : Number {
		return parseInt(_level0.bgcolor);
	}
	public function getAgreementVersion() : Number {
		return parseInt(_level0.agreementVersion);
	}
	public function getAgreementText() : String {
		return _level0.agreementText;
	}
	public function getCreditsText() : String {
		return strings.format(_level0.creditsText, [
													strings.get("SoftwareDevelopment", "Software Development"),
													strings.get("DesignTeam", "Design Team"),
													strings.get("LeadDesign", "Lead Design"),
													strings.get("Interviews", "Interviews")
													]);
	}
	
	
	public static function dateString(date : Date) : String {
		var year : String = new String(date.getYear() + 1900);
		var month : String = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"][date.getMonth()];
		return month + " " + String(date.getDate()) + ", " + year;
	}
	
	public static function dateOfSeconds(seconds : Number) : Date {
		return new Date(seconds * 1000);
	}
	
	public static function dateOfMilliseconds(milliseconds : Number) : Date {
		return new Date(milliseconds);
	}
}
