package edu.colorado.phet.flashcommon {

import flash.display.DisplayObject;
import flash.display.DisplayObjectContainer;
import flash.display.Sprite;
import flash.events.KeyboardEvent;
import flash.events.MouseEvent;
import flash.events.TimerEvent;
import flash.geom.ColorTransform;
import flash.net.LocalConnection;
import flash.net.URLRequest;
import flash.net.navigateToURL;
import flash.system.Security;
import flash.text.Font;
import flash.text.StyleSheet;
import flash.text.TextField;
import flash.text.TextFormat;
import flash.ui.Keyboard;
import flash.utils.Timer;

import org.aswing.ASColor;
import org.aswing.AsWingManager;
import org.aswing.Insets;
import org.aswing.JFrame;
import org.aswing.JScrollPane;
import org.aswing.JTextArea;
import org.aswing.border.EmptyBorder;
import org.aswing.border.LineBorder;
import org.aswing.geom.IntDimension;

public class FlashCommon {

    protected static var instance: FlashCommon = null; // this will be written by subclasses

    public static var DEBUG_ENABLED: Boolean = false;

    public var commonButtons: CommonButtons;

    public var root: DisplayObjectContainer;

    public static var NULLVAL: String = "null";

    // blocks the user from interacting with the sim (but allows dialogs like the privacy dialog to be interacted with)
    private var barrierSprite: Sprite;

    // the default background color for AsWing components (windows and text)
    public var backgroundColor: ASColor;

    // whether to dump debugging messages into the text field
    public var debugging: Boolean = true;

    // handles preferences the user selects, such as
    // enabling/disabling updates and statistics messages. also
    // stores how many times the particular sim has
    // been run
    public var preferences: Preferences;

    // handles checking for updates and handling what to do
    // if a newer version of the simulation is found
    public var updateHandler: UpdateHandler;

    // handles sending statistics messages to the server
    public var statistics: Statistics;

    // whether everything is displayed with high contrast
    public var highContrast: Boolean = false;

    // invoked when high contrast is changed. this can be set by a sim wishing to have a better high contrast function
    public var highContrastFunction: Function = defaultHighContrastFunction;

    private var loadListeners: Array = new Array();

    /////////////////////////

    public static var DISPLAY_CSS: String =
            "a:link{color:#0000FF;font-weight:bold;}" +
            "a:visited{color:#0000FF;font-weight:bold;}" +
            "a:hover{color:#0000FF;text-decoration:underline;font-weight:bold;}" +
            "a:active{color:#0000FF;font-weight:bold;}";
    public static var CENTERED_CSS: String = "body{text-align:center}";

    // TODO: improvement of the stylesheets
    public static var LINK_STYLE_SHEET: StyleSheet;
    public static var CENTERED_LINK_STYLE_SHEET: StyleSheet;
    private var _aswingPadding: Boolean = true;

    //See these pages for a list of RTL Languages:
    //http://en.wikipedia.org/wiki/Right-to-left
    // and
    //http://www.i18nguy.com/temp/rtl.html
    // and this page for their language codes
    //http://en.wikipedia.org/wiki/ISO_639-1_language_matrix

    private const rtlLanguageList: Array = ["ar","iw","fa","ur","ji","jv","so","ks","ku","tk","ug","az","ms"];

    public static function getInstance(): FlashCommon {
        if ( instance == null ) {
            throw new Error( "premature request for FlashCommon" );
        }
        return instance;
    }

    /**
     * This is a singleton class. No easy way to enforce, so for now, don't call this.
     */
    public function FlashCommon() {
        instance = this;
    }

    public static function getArg( key: String ): String {
        return getInstance().getFlashArg( key );
    }

    public function getFlashArg( key: String ): String {
        // think of this as abstract
        throw new Error( "abstract:getFlashArg" );
    }

    public function debug( str: String ): void {
        trace( str ); // TODO: more like flashcommon-as2?
        debugToWindow( str );
    }

    public function initialize( root: Sprite ): void {
        this.root = root;

        barrierSprite = new Sprite();
        root.addChild( barrierSprite );

        // set up AsWing so it knows about the root (where it creates all of its Sprites)
        AsWingManager.initAsStandard( root, false, true );

        debugInit( root );

        if ( !hasFlashVars() ) {
            debug( "missing flashvars" );
        }

        if ( !hasFlashVars() && fromPhetWebsite() && getFlashArg( "backupSimName" ) ) {
            redirect( "http://" + getMainServer() + "/sims/" + getFlashArg( "backupSimName" ) + "/" + getFlashArg( "backupSimName" ) + "_en.html" );
        }

        if ( !hasFlashVars() ) {
            debug( "stopping FlashCommon initialization due to lack of FlashVars" );
            return;
        }

        // DEVELOPMENT: catch key events to this object
        //        Key.addListener( this );

        // set the default background color
        backgroundColor = ASColor.getASColor( 230, 230, 230 );

        LINK_STYLE_SHEET = new StyleSheet();
        LINK_STYLE_SHEET.parseCSS( DISPLAY_CSS );

        CENTERED_LINK_STYLE_SHEET = new StyleSheet();
        CENTERED_LINK_STYLE_SHEET.parseCSS( DISPLAY_CSS + CENTERED_CSS );

        //        _level0.highContrastFunction = defaultHighContrastFunction;
        //        _level0.highContrast = false;

        // TODO: Possibly extend this to run from other domains?
        Security.allowDomain( getMainServer() );

        debug( "Debugging information:\n" );
        debug( "FlashCommon initializing\n" );

        // display version of this simulation
        debug( "Running " + getSimName() + " " + getFullVersionString() + "\n" );

        // store the position of the common buttons for CommonButtons
        //        commonPosition = position;

        // initializes the TabHandler
        //tabHandler = new TabHandler();
        //        keyboardHandler = new KeyboardHandler();

        // load the statistics handler, but do not send the session-start message!!!
        debug("Loading statistics\n");
        statistics = new Statistics();
        debug("Finished loading statistics\n");

        // load preferences data
        debug("Loading preferences\n");
        preferences = new Preferences();
        debug("Finished loading preferences\n");

        // DEVELOPMENT: load the inspector
        if ( getDev() ) {
            //            inspector = new Inspector();
        }

        // TODO: move this to here
        if ( SimStrings.stringExists( "ksu.credits" ) ) {
            displayKSULogo();
        }
    }

    public function createKSULogo(): DisplayObject {
        throw new Error( "abstract crateKSULogo" );
    }

    protected function displayKSULogo(): void {
        var logoHolder = new Sprite();
        root.addChild( logoHolder );

        var logo: DisplayObject = createKSULogo();
        logoHolder.addChild( logo );
        logo.x = (getPlayAreaWidth() - logo.width) / 2;
        logo.y = (getPlayAreaHeight() - logo.height) / 2;

        var textHeight: Number = 28;

        // "Translated By" text
        var textField: TextField = new TextField();
        logoHolder.addChild( textField );
        textField.x = (getPlayAreaWidth() - logo.width) / 2;
        textField.y = (getPlayAreaHeight() - logo.height) / 2 - textHeight;
        textField.width = logo.width;
        textField.height = textHeight;
        textField.text = CommonStrings.get( "TranslatedBy", "Translated by" );
        var format: TextFormat = new TextFormat();
        format.size = 20;
        format.font = "Arial";
        textField.setTextFormat( format );
        textField.selectable = false;

        // background
        logoHolder.graphics.lineStyle( 1, 0x000000 );
        logoHolder.graphics.beginFill( 0xEEEEEE );
        var top: Number = getPlayAreaHeight() / 2 - logo.height / 2 - textHeight;
        var bottom: Number = getPlayAreaHeight() / 2 + logo.height / 2;
        var left: Number = getPlayAreaWidth() / 2 - logo.width / 2;
        var right: Number = getPlayAreaWidth() / 2 + logo.width / 2;
        var padding: Number = 15;
        logoHolder.graphics.moveTo( left - padding, top - padding );
        logoHolder.graphics.lineTo( right + padding, top - padding );
        logoHolder.graphics.lineTo( right + padding, bottom + padding );
        logoHolder.graphics.lineTo( left - padding, bottom + padding );
        logoHolder.graphics.endFill();

        logoHolder.addEventListener( MouseEvent.CLICK, function( e: MouseEvent ): void {
            logoHolder.visible = false;
        } );

        var timer: Timer = new Timer( 4000, 1 );
        timer.addEventListener( TimerEvent.TIMER_COMPLETE, function( e: TimerEvent ): void {
            logoHolder.visible = false;
        } );
        timer.start();
    }

    private function debugToWindow( str: String ): void {
        if ( debugText != null ) {
            debugText.appendText( str + "\n" );
            debugText.scrollToBottomLeft();
        }
    }

    private var debugText: JTextArea = null;

    private function debugInit( root: Sprite ): void {
        var debugFrame: JFrame = new JFrame( root, "debug" );
        debugText = new JTextArea();
        debugText.setText( "This is a debugging area\n" );
        var debugScroll: JScrollPane = new JScrollPane( debugText, JScrollPane.SCROLLBAR_AS_NEEDED, JScrollPane.SCROLLBAR_AS_NEEDED );
        debugScroll.setPreferredSize( new IntDimension( 400, 300 ) );
        debugScroll.setBorder( new EmptyBorder( new LineBorder( null, ASColor.GRAY, 1, 0 ), new Insets( 5, 5, 5, 5 ) ) );
        debugFrame.getContentPane().append( debugScroll );
        debugFrame.pack();
        if ( DEBUG_ENABLED ) {
            debugFrame.show();
        }

        if ( getDev() ) { // so we can enable buttons for debugging
            root.stage.addEventListener( KeyboardEvent.KEY_DOWN, function( e: KeyboardEvent ): void {
                if ( (e.keyCode == Keyboard.PAGE_UP || e.keyCode == Keyboard.F10) && e.shiftKey ) {
                    // page up OR F10
                    if ( debugFrame.isVisible() ) {
                        debugFrame.hide();
                    }
                    else {
                        debugFrame.show();
                    }
                }
                if ( e.keyCode == Keyboard.F9 && e.shiftKey ) {
                    // F9 was pressed
                    updateHandler.simUpdatesAvailable( 5, 10, 0, 0, 0 );
                }
                if ( e.keyCode == Keyboard.HOME && e.shiftKey ) {
                    // Home was pressed
                    updateHandler.installationUpdatesAvailable( 1234567890, 0 );
                }
                if ( e.keyCode == Keyboard.LEFT && e.shiftKey ) {
                    // left arrow
                    updateHandler.showUpdateError();
                }
                if ( e.keyCode == Keyboard.UP && e.shiftKey ) {
                    // up arrow
                    updateHandler.showSimUpToDate();
                }
                if ( e.keyCode == Keyboard.DOWN && e.shiftKey ) {
                    // down arrow
                    updateHandler.showInstallationUpToDate();
                }
                if ( e.keyCode == Keyboard.F8 && e.shiftKey ) {
                    // F8 was pressed
                    debug( "Preferences: Manually resetting shared data\n" );
                    preferences.load();
                    preferences.reset();
                    preferences.flush();
                }
            } );
        }
    }

    // this should be called from preferences when it is verified the
    // privacy agreement has been accepted
    public function postAgreement(): void {

        // load update handler
        // must have preferences loaded first before loading updatehandler.
        updateHandler = new UpdateHandler();

        // load statistics handler
        // must have preferences loaded first before loading Statistics.
        statistics.sendSessionStart();

        // load buttons with the position (defaults to upper left)
        commonButtons = new CommonButtons( root );

        for each ( var listener: Function in loadListeners ) {
            listener();
        }
    }

    // returns whether the sim was run from the phet website
    public function fromPhetWebsite(): Boolean {
        var domain: String = (new LocalConnection()).domain;
        var actually: Boolean = (domain == getMainServer() || domain == getMainServer() + "." || domain == "phet.colorado" || domain == "phet");

        return actually || fromDevWebsite();
        //return (new LocalConnection()).domain() == getMainServer();
    }

    // returns whether the sim was run from a development site
    public function fromDevWebsite(): Boolean {
        var host: String = (new LocalConnection()).domain;
        return (host == "www.colorado.edu");
    }

    // returns whether the sim was run from a full installation
    public function fromFullInstallation(): Boolean {
        if ( !hasFlashVars() ) {
            return false;
        }
        return (
               !fromPhetWebsite()
                       && !isPlaceholder( getFlashArg( "installationTimestamp" ) )
                       && null_replace( getFlashArg( "installationTimestamp" ) ) != FlashCommon.NULLVAL
               );
    }

    // returns whether the sim was run from a standalone jar
    public function fromStandaloneJar(): Boolean {
        return (getFlashArg( "simDeployment" ) == "standalone-jar");
    }

    // return whether a string is a placeholder
    public function isPlaceholder( str: String ): Boolean {
        if ( str == null ) { return false; }
        return (str.substr( 0, 2 ) == "@@" && str.substr( -2, 2 ) == "@@");
    }

    //    public function onKeyDown() {
    //        if ( !getDev() ) {
    //            return;
    //        }
    //        if ( (Key.getCode() == Key.PGUP || Key.getCode() == 121) && Key.isDown( Key.SHIFT ) ) {
    //            // page up OR F10
    //            _level0.debugs._visible = !_level0.debugs._visible;
    //        }
    //        if ( Key.getCode() == 120 && Key.isDown( Key.SHIFT ) ) {
    //            // F9 was pressed
    //            updateHandler.simUpdatesAvailable( 5, 10, 0, 0, 0 );
    //        }
    //        if ( Key.getCode() == 36 && Key.isDown( Key.SHIFT ) ) {
    //            // Home was pressed
    //            updateHandler.installationUpdatesAvailable( 1234567890, 0, 0 );
    //        }
    //        if ( Key.getCode() == 37 && Key.isDown( Key.SHIFT ) ) {
    //            // left arrow
    //            updateHandler.showUpdateError();
    //        }
    //        if ( Key.getCode() == 38 && Key.isDown( Key.SHIFT ) ) {
    //            // up arrow
    //            updateHandler.showSimUpToDate();
    //        }
    //        if ( Key.getCode() == 40 && Key.isDown( Key.SHIFT ) ) {
    //            // down arrow
    //            updateHandler.showInstallationUpToDate();
    //        }
    //    }

    // returns the version string with minor and dev fields padded with a zero if necessary
    public function zeroPadVersion( versionMajor: Number, versionMinor: Number, versionDev: Number = -1 ): String {
        var ret: String = "";
        ret += String( versionMajor ) + ".";
        ret += (versionMinor > 9 ? String( versionMinor ) : "0" + String( versionMinor ));
        if ( versionDev != -1 ) {
            ret += "." + (versionDev > 9 ? String( versionDev ) : "0" + String( versionDev ));
        }
        return ret;
    }

    // version like "1.00"
    public function getShortVersionString(): String {
        return zeroPadVersion( getVersionMajor(), getVersionMinor() );
    }

    // version like "1.00.00"
    public function getVersionString(): String {
        return zeroPadVersion( getVersionMajor(), getVersionMinor(), getVersionDev() );
    }

    // version like "1.00.00 (20000)"
    public function getFullVersionString(): String {
        return getVersionString() + " (" + String( getVersionRevision() ) + ")";
    }

    public static function getMainServer(): String {
        return "phet.colorado.edu";
    }

    // get the URL of the simulation on the website
    public function simWebsiteURL(): String {
        return "http://" + getMainServer() + "/services/sim-website-redirect.php?project=" + getSimProject() + "&sim=" + getSimName() + "&request_version=1";
    }

    // will return the full locale string: for example 'en', 'en_CA', 'es_MX' etc.
    public function getLocale(): String {
        var str: String = getLanguage();

        // if we have a country code, add _XX to the locale
        if ( getCountry() != NULLVAL ) {
            str += "_" + getCountry();
        }
        return str;
    }

    public function null_replace( val: String ): String {
        // TODO: possibly integrate checking for a placeholder
        if ( val == null ) {
            return NULLVAL;
        }
        return val;
    }


    // get functions for information passed through FlashVars


    public function getSimProject(): String {
        return getFlashArg( "projectName" );
    }

    public function getSimName(): String {
        return getFlashArg( "simName" );
    }

    public function getLanguage(): String {
        return getFlashArg( "languageCode" );
    }

    public function getCountry(): String {
        return null_replace( getFlashArg( "countryCode" ) );
    }

    public function getVersionMajor(): Number {
        return parseInt( getFlashArg( "versionMajor" ) );
    }

    public function getVersionMinor(): Number {
        return parseInt( getFlashArg( "versionMinor" ) );
    }

    public function getVersionDev(): Number {
        return parseInt( getFlashArg( "versionDev" ) );
    }

    public function getVersionRevision(): Number {
        return parseInt( getFlashArg( "versionRevision" ) );
    }

    public function getSimXML(): String {
        return getFlashArg( "internationalization" );
    }

    public function getCommonXML(): String {
        return getFlashArg( "commonStrings" );
    }

    public function getDeployment(): String {
        // all phet-installation, phet-development-website and phet-production-website will have
        // "phet-production-website" as the simDeployment FlashVar. We need to detect when this
        // is not the case (since the same HTML is deployed in all situations, except fields are
        // replaced in the Installation version.
        if ( fromFullInstallation() ) {
            return "phet-installation";
        }
        else {
            if ( fromDevWebsite() ) {
                return "phet-development-website";
            }
            else {
                return getFlashArg( "simDeployment" );
            }
        }
    }

    public function getDev(): Boolean {
        if ( getFlashArg( "simDev" ) == "false" || getFlashArg( "simDev" ) == "0" ) {
            return false;
        }
        return true;
    }

    public function getDistributionTag(): String {
        var distribTag: String = getFlashArg( "simDistributionTag" );
        if ( isPlaceholder( distribTag ) || null_replace( distribTag ) == NULLVAL ) {
            // if the distribution tag is null or a placeholder, it is not an actual distribution tag,
            // so we return null
            return null;
        }
        return distribTag;
    }

    public function getInstallationTimestampString(): String {
        var timestamp: String = getFlashArg( "installationTimestamp" );
        if ( null_replace( timestamp ) == NULLVAL || isPlaceholder( timestamp ) ) {
            return NULLVAL;
        }
        else {
            return timestamp;
        }
    }

    public function getInstallerCreationTimestamp(): Number {
        // WARNING: Do NOT CHANGE installerCreationTimestamp's name, it is used in the installation utility
        var timestamp: String = getFlashArg( "installerCreationTimestamp" );
        if ( null_replace( timestamp ) == NULLVAL || isPlaceholder( timestamp ) ) {
            return 0;
        }
        else {
            return parseInt( timestamp );
        }
    }

    public function getVersionTimestampString(): String {
        var timestamp: String = getFlashArg( "versionTimestamp" );
        if ( null_replace( timestamp ) == NULLVAL ) {
            return NULLVAL;
        }
        else {
            return timestamp;
        }
    }

    public function getBGColor(): Number {
        return parseInt( getFlashArg( "bgColor" ) );
    }

    public function getAgreementVersion(): Number {
        return parseInt( getFlashArg( "agreementVersion" ) );
    }

    public function getAgreementText(): String {
        // the agreement text must be stripped of newlines because Flash's HTML rendering incorrectly
        // considers newlines to be "<br>".
        //var strippedText = stripNewlines(_level0.agreementText);
        var strippedText: String = stripNewlines( SoftwareAgreement.agreementText );

        // this replacement will make clicks call _level0.common.openExternalLink(<url>) instead of
        // just opening the url
        var replacedHrefs: String = stringReplace( strippedText, "href=\"", "target=\"_blank\" href=\"" );

        //Image refs break in AS3, so omit them
        return replacedHrefs.replace( /<img.*<\/img>/g, "" );
    }

    public function getCreditsText(): String {
        // localize the credits test fields
        return StringUtils.format( getFlashArg( "creditsText" ), [
            CommonStrings.get( "SoftwareDevelopment", "Software Development" ),
            CommonStrings.get( "DesignTeam", "Design Team" ),
            CommonStrings.get( "LeadDesign", "Lead Design" ),
            CommonStrings.get( "Interviews", "Interviews" )
        ] );
    }

    public function getSimTitle(): String {
        var title: String = getFlashArg( "simTitle" );
        if ( title == null ) {
            // if sim title isn't specified, just use the sim name as a backup
            return getSimName();
        }
        else {
            return title;
        }
    }

    // return a date like Jan 12 2009
    public static function dateString( date: Date ): String {
        var year: String = new String( date.getFullYear() );
        var month: String = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"][date.getMonth()];
        return month + " " + String( date.getDate() ) + ", " + year;
    }

    public static function dateOfSeconds( seconds: Number ): Date {
        return new Date( seconds * 1000 );
    }

    public static function dateOfMilliseconds( milliseconds: Number ): Date {
        return new Date( milliseconds );
    }

    public function hasFlashVars(): Boolean {
        // check two flashvars variables that should always be included
        return (getFlashArg( "languageCode" ) !== null && getFlashArg( "simName" ) !== null);
    }

    public function prepareTranslatedTextField( field: TextField ): void {
        var format: TextFormat = field.getTextFormat();

        if ( getLanguage() == "en" ) {
            field.setTextFormat( format );
        }
        else {
            if ( field.embedFonts == false ) {
                // TODO: possibly change this to only use fonts in allowedSystemFonts
                field.setTextFormat( format );
            }
            else {
                field.embedFonts = false;
                field.setTextFormat( format );
            }
        }

        fixLocaleFont( field );
    }

    public function stripNewlines( str: String ): String {
        var ret: String = "";

        for ( var idx: Number = 0; idx < str.length; idx++ ) {
            if ( str.charCodeAt( idx ) == 10 || str.charCodeAt( idx ) == 13 ) {
                continue;
            }

            ret += str.charAt( idx );
        }

        return ret;
    }

    public function stringReplace( str: String, pattern: String, replacement: String ): String {
        var ret: String = "";
        var chopped: String = str;

        while ( chopped.indexOf( pattern ) != -1 ) {
            var startIdx: Number = chopped.indexOf( pattern );
            ret += chopped.slice( 0, startIdx );
            ret += replacement;
            chopped = chopped.substring( startIdx + pattern.length );
        }

        ret += chopped;

        return ret;
    }

    public static function getURL( url: String, target: String ): void {
        var request: URLRequest = new URLRequest( url );
        try {
            navigateToURL( request, target ); // second argument is target
        }
        catch ( e: Error ) {
            trace( "Error occurred!" );
        }

    }

    public function openExternalLink( str: String ): void {
        getURL( str, "_blank" );
    }

    public function redirect( str: String ): void {
        getURL( str, "_self" );
    }

    public function setHighContrast( contrast: Boolean ): void {
        highContrast = contrast;
        debug( "Contrast changing to: " + contrast + "\n" );
        highContrastFunction( contrast );
    }

    public function defaultHighContrastFunction( contrast: Boolean ): void {
        if ( contrast ) {
            var stretch: Number = 3.0;
            var newCenter: Number = 64;
            var offset: Number = newCenter - 128 * stretch;
            root.stage.transform.colorTransform = new ColorTransform( stretch, stretch, stretch, 1, offset, offset, offset, 1 );
        }
        else {
            root.stage.transform.colorTransform = new ColorTransform( 1, 1, 1, 1, 0, 0, 0, 0 );
        }
    }

    private function existingSystemFont( preferredFonts: Array ): String {
        throw new Error( "existingSystemFont is probably broken" );
        var fontList: Array = Font.enumerateFonts( true );
        for ( var pkey: * in preferredFonts ) {
            var pfont: String = preferredFonts[pkey];
            for ( var fkey: * in fontList ) {
                var ffont: String = fontList[fkey];
                if ( pfont == ffont ) {
                    debug( "Found good system font: " + pfont + "\n" );
                    return pfont;
                }
            }
            debug( "Could not find: " + pfont + "\n" );
        }
        return null;
    }

    private var cachedOverrideFont: String = null;

    public function getOverrideFont(): String {
        if ( getLocale() == "km" ) {
            if ( cachedOverrideFont == null ) {
                var khmerFonts: Array = ["Khmer OS", "MoolBoran", "Limon"];
                var font: String = existingSystemFont( khmerFonts );
                cachedOverrideFont = font;
            }
            return cachedOverrideFont;
        }
        else {
            return null;
        }
    }

    public function fixLocaleFont( field: TextField ): void {
        var font: String = getOverrideFont();
        if ( font ) {
            var format: TextFormat = field.getTextFormat();
            format.font = font;
            field.setTextFormat( format );
        }
    }

    public function addLoadListener( f: Function ): void {
        loadListeners.push( f );
    }

    // shows a barrier so that the user cannot interact directly with the sim
    public function showBarrier(): void {
        barrierSprite.graphics.beginFill( getBGColor(), 0.7 );
        // larger dimensions in case people resize afterwards
        barrierSprite.graphics.drawRect( -5000, -5000, 10000, 10000 );
        //        barrierSprite.graphics.drawRect( -500, -500, 100, 100 );
        barrierSprite.graphics.endFill();

        barrierSprite.useHandCursor = false;
        barrierSprite.addEventListener( MouseEvent.CLICK, function( evt: MouseEvent ): void {
        } );
    }

    // hides the barrier. see above
    public function hideBarrier(): void {
        barrierSprite.graphics.clear();
        root.removeChild( barrierSprite );
    }

    public function getPlayAreaWidth(): Number {
        throw new Error( "abstract method error" );
    }

    public function getPlayAreaHeight(): Number {
        throw new Error( "abstract method error" );
    }

    public function set aswingPadding( b: Boolean ): void {
        this._aswingPadding = b;
    }

    public function get aswingPadding(): Boolean {
        return _aswingPadding;
    }

    public function isRTL(): Boolean {
        return rtlLanguageList.indexOf( getLanguage() ) >= 0;
    }

    public function getRTLControlCode(): String {
        if ( FlashCommon.getInstance().isRTL() ) {
            return "\u200F";
        }
        else {
            return "";
        }
    }
}
}