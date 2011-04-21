// FlashCommon.as
//
// coordinates all common flash code, including the updates and statistics
//
// Author: Jonathan Olson

import org.aswing.ASColor;

import edu.colorado.phet.flashcommon.*;

// should be instance of FlashCommon at _level0.common
class edu.colorado.phet.flashcommon.FlashCommon {

	public static var NULLVAL : String = "null";

	// the default background color for AsWing components (windows and text)
	public var backgroundColor : ASColor;

	// whether to dump debugging messages into the text field
	public var debugging : Boolean = true;

	// handles internationalization for common strings
	public var strings : CommonStrings;

    public var simStrings: SimStrings;

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
	public var keyboardHandler : KeyboardHandler;

    public static var DISPLAY_CSS : String =
            "a:link{color:#0000FF;font-weight:bold;}" +
			"a:visited{color:#0000FF;font-weight:bold;}" +
			"a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
			"a:active{color:#0000FF;font-weight:bold;}";

    public static var LINK_STYLE_SHEET : TextField.StyleSheet;

	//public static var allowedSystemFonts : Array = ["_sans", "_serif", "_typewriter", "Times New Roman", "Arial"];

	//public static var defaultSystemFont : String = "_sans";

	// DEVELOPMENT
	public var inspector : Inspector;

	// initializes debug function at _level0.debug()
	public function initDebug() : Void {
		if(debugging) {
			var padding : Number = 50;
			_root.createTextField("debugs", 104384, padding, padding, Stage.width - 2 * padding, Stage.height - 2 * padding);
			var format : TextFormat = _root.debugs.getTextFormat();
			format.font = "Arial";
			_root.debugs.setTextFormat( format );
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

		if( !hasFlashVars() && fromPhetWebsite() && _level0.backupSimName ) {
			redirect( "http://" + getMainServer() + "/sims/" + _level0.backupSimName + "/" + _level0.backupSimName + "_en.html" );
		}

		// DEVELOPMENT: catch key events to this object
		Key.addListener(this);

		// set the default background color
		backgroundColor = ASColor.getASColor(230, 230, 230);

        LINK_STYLE_SHEET = new TextField.StyleSheet();
        LINK_STYLE_SHEET.parseCSS( DISPLAY_CSS );

        _level0.highContrastFunction = defaultHighContrastFunction;
        _level0.highContrast = false;

		// TODO: Possibly extend this to run from other domains?
		System.security.allowDomain( getMainServer() );

		debug("Debugging information:\n");
		debug("FlashCommon initializing\n");

		// display version of this simulation
		debug("Running " + getSimName() + " " + getFullVersionString() + "\n");

		// store the position of the common buttons for CommonButtons
		commonPosition = position;

		// load internationalization strings for common code
		strings = new CommonStrings();

        // load internationalization strings for simulation strings (used here for translation/ksu credits)
        simStrings = new SimStrings();

		// initializes the TabHandler
		//tabHandler = new TabHandler();
		keyboardHandler = new KeyboardHandler();

		// load the statistics handler, but do not send the session-start message!!!
		statistics = new Statistics();

		// load preferences data
		preferences = new Preferences();

		// DEVELOPMENT: load the inspector
		if(getDev()) {
            debug("Development version.\n");
			inspector = new Inspector();
		}

        if( simStrings.stringExists( "ksu.credits" ) ) {
            debug("This is a KSU translation. Showing the dialog.\n");
            displayKSULogo();
        } else {
            debug("Not a KSU translation\n");
        }
	}

    public function displayKSULogo():Void {
        // create the MovieClip that will hold the logo and text
        _level0.createEmptyMovieClip( "logoHolder", _level0.getNextHighestDepth() + 10 );
        var logoHolder = _level0.logoHolder;

        // add the logo onto the logo holder and center it
        logoHolder.attachMovie( "LogoECSME", "logo0", logoHolder.getNextHighestDepth() + 10 );
        var logo = logoHolder.logo0;
        logo._x = Stage.width / 2;
        logo._y = Stage.height / 2;
        var textHeight = 28;

        // add "Translated by" as text above
        logoHolder.createTextField( "translatedBy", logoHolder.getNextHighestDepth(), Stage.width / 2 - logo._width / 2, Stage.height / 2 - logo._height / 2 - textHeight, logo._width, textHeight );
        var textField = logoHolder.translatedBy;
        textField.text = strings.get( "TranslatedBy", "Translated by" );
        var format:TextFormat = new TextFormat();
        format.size = 20;
        format.font = "Arial";
        textField.selectable = false;
        textField.setTextFormat( format );

        // fill in a background behind the logo and text
        logoHolder.beginFill( 0xEEEEEE );
        logoHolder.lineStyle( 1, 0x000000 );
        var top = Stage.height / 2 - logo._height / 2 - textHeight;
        var bottom = Stage.height / 2 + logo._height / 2;
        var left = Stage.width / 2 - logo._width / 2;
        var right = Stage.width / 2 + logo._width / 2;
        var padding = 15;
        logoHolder.moveTo( left - padding, top - padding );
        logoHolder.lineTo( right + padding, top - padding );
        logoHolder.lineTo( right + padding, bottom + padding );
        logoHolder.lineTo( left - padding, bottom + padding );
        logoHolder.endFill();

        logoHolder.onRelease = function():Void {
            logoHolder._visible = false;
        }

        setInterval( function():Void {
            logoHolder._visible = false;
        }, 4000 );
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
		var domain : String = (new LocalConnection()).domain();
		var actually : Boolean = (domain == getMainServer() || domain == getMainServer() + "." || domain == "phet.colorado" || domain == "phet");

		return actually || fromDevWebsite();
		//return (new LocalConnection()).domain() == getMainServer();
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

	public function onKeyDown() {
		if(!getDev()) {
			return;
		}
		if((Key.getCode() == Key.PGUP || Key.getCode() == 121) && Key.isDown(Key.SHIFT)) {
			// page up OR F10
			_level0.debugs._visible = !_level0.debugs._visible;
		}
		if(Key.getCode() == 120 && Key.isDown(Key.SHIFT)) {
			// F9 was pressed
			updateHandler.simUpdatesAvailable(5, 10, 0, 0, 0);
		}
		if(Key.getCode() == 36 && Key.isDown(Key.SHIFT)) {
			// Home was pressed
			updateHandler.installationUpdatesAvailable(1234567890, 0, 0);
		}
		if(Key.getCode() == 37 && Key.isDown(Key.SHIFT)) {
			// left arrow
			updateHandler.showUpdateError();
		}
		if(Key.getCode() == 38 && Key.isDown(Key.SHIFT)) {
			// up arrow
			updateHandler.showSimUpToDate();
		}
		if(Key.getCode() == 40 && Key.isDown(Key.SHIFT)) {
			// down arrow
			updateHandler.showInstallationUpToDate();
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

    public static function getMainServer() : String {
        return "phet.colorado.edu";
    }

	// get the URL of the simulation on the website
	public function simWebsiteURL() : String {
		return "http://" + getMainServer() + "/services/sim-website-redirect.php?project=" + getSimProject() + "&sim=" + getSimName() + "&request_version=1";
	}

	// will return the full locale string: for example 'en', 'en_CA', 'es_MX' etc.
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
		// all phet-installation, phet-development-website and phet-production-website will have
		// "phet-production-website" as the simDeployment FlashVar. We need to detect when this
		// is not the case (since the same HTML is deployed in all situations, except fields are
		// replaced in the Installation version.
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
			// if the distribution tag is null or a placeholder, it is not an actual distribution tag,
			// so we return null
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
        // WARNING: Do NOT CHANGE installerCreationTimestamp's name, it is used in the installation utility
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
		// the agreement text must be stripped of newlines because Flash's HTML rendering incorrectly
		// considers newlines to be "<br>".
		//var strippedText = stripNewlines(_level0.agreementText);
		var strippedText = stripNewlines(SoftwareAgreement.agreementText);

		// this replacement will make clicks call _level0.common.openExternalLink(<url>) instead of
		// just opening the url
		return stringReplace(strippedText, "href=\"", "href=\"asfunction:_level0.common.openExternalLink,");
	}
	public function getCreditsText() : String {
		// localize the credits test fields
		return strings.format(_level0.creditsText, [
			strings.get("SoftwareDevelopment", "Software Development"),
			strings.get("DesignTeam", "Design Team"),
			strings.get("LeadDesign", "Lead Design"),
			strings.get("Interviews", "Interviews")
		]);
	}

	public function getSimTitle() : String {
		if(_level0.simTitle == undefined) {
			// if sim title isn't specified, just use the sim name as a backup
			return getSimName();
		} else {
			return _level0.simTitle;
		}
	}

	// return a date like Jan 12 2009
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

	public function hasFlashVars() : Boolean {
		// check two flashvars variables that should always be included
		return (_level0.languageCode !== undefined && _level0.simName !== undefined);
	}

	public function prepareTranslatedTextField(field : TextField) {
		var format : TextFormat = field.getTextFormat();

		if(getLanguage() == "en") {
			field.setTextFormat(format);
		} else if(field.embedFonts == false) {
			// TODO: possibly change this to only use fonts in allowedSystemFonts
			field.setTextFormat(format);
		} else {
			field.embedFonts = false;
			field.setTextFormat(format);
		}

		fixLocaleFont( field );
	}

	public function stripNewlines(str : String) {
		var ret : String = "";

		for(var idx = 0; idx < str.length; idx++) {
			if(str.charCodeAt(idx) == 10 || str.charCodeAt(idx) == 13) {
				continue;
			}

			ret += str.charAt(idx);
		}

		return ret;
	}

	public function stringReplace(str : String, pattern : String, replacement : String) {
		var ret : String = "";
		var chopped : String = str;

		while(chopped.indexOf(pattern) != -1) {
			var startIdx : Number = chopped.indexOf(pattern);
			ret += chopped.slice(0, startIdx);
			ret += replacement;
			chopped = chopped.substring(startIdx + pattern.length);
		}

		ret += chopped;

		return ret;
	}

	public function openExternalLink(str : String) {
		getURL(str, "_blank");
	}

	public function redirect( str : String ) {
		getURL( str, "_self" );
	}

    public function defaultHighContrastFunction( contrast : Boolean ) {
        _level0.highContrast = contrast;
        _level0.debug( "Contrast changing to: " + contrast + "\n" );
        if( contrast ) {
            var stretch : Number = 3.0;
            var newCenter : Number = 64;
            var offset = newCenter - 128 * stretch;
            _level0.transform.colorTransform = new flash.geom.ColorTransform( stretch, stretch, stretch, 1, offset, offset, offset, 1 );
        } else {
            _level0.transform.colorTransform = new flash.geom.ColorTransform( 1, 1, 1, 1, 0, 0, 0, 0 );
        }
    }

	private function existingSystemFont( preferredFonts : Array ) : String {
		var fontList : Array = TextField.getFontList();
		for( var pkey in preferredFonts ) {
			var pfont : String = preferredFonts[pkey];
			for( var fkey in fontList ) {
				var ffont : String = fontList[fkey];
				if( pfont == ffont ) {
					_level0.debug( "Found good system font: " + pfont + "\n" );
					return pfont;
				}
			}
			_level0.debug( "Could not find: " + pfont + "\n" );
		}
		return undefined;
	}

	private var cachedOverrideFont : String = null;
	public function getOverrideFont() : String {
		if( getLocale() == "km" ) {
			if( cachedOverrideFont == null ) {
				var khmerFonts : Array = ["Khmer OS", "MoolBoran", "Limon"];
				var font : String = existingSystemFont( khmerFonts );
				cachedOverrideFont = font;
			}
			return cachedOverrideFont;
		} else {
			return undefined;
		}
	}

	public function fixLocaleFont( field : TextField ) {
		var font : String = getOverrideFont();
		if( font ) {
			var format : TextFormat = field.getTextFormat();
			format.font = font;
			field.setTextFormat( format );
		}
	}
}
