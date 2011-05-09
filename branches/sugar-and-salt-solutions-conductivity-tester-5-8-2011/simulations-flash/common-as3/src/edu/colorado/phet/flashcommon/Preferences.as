package edu.colorado.phet.flashcommon {
// Handles loading of stored client-side preferences data,
// and allows other entities within the simulation to check
// the preferences.
//
// Author: Jonathan Olson

import flash.net.SharedObject;

public class Preferences {

    // current preferences version
    // this SHOULD NOT CHANGE after development, and is an
    // aid for development purposes.
    public static var CURRENT_PREF_VERSION: Number = 1.35;

    public static var FIELD_INSTALLATION_ASK_LATER: String = "installationAskLater";

    // reference to the shared object used to store preferences
    public var sharedObject: SharedObject;

    // "constant"s to refer to fields that change sim-to-sim but
    // are otherwise constant
    public var FIELD_SKIPPED_REVISION: String;
    public var FIELD_ASK_LATER: String;
    public var FIELD_VISITS_SINCE: String;
    public var FIELD_VISITS_EVER: String;

    public var common: FlashCommon;

    // shorthand for debugging function
    public function debug( str: String ): void {
        FlashCommon.getInstance().debug( str );
    }

    // constructor
    public function Preferences() {
        debug("Preferences initializing\n");

        // shortcut to FlashCommon, but now with type-checking!
        common = FlashCommon.getInstance();

        // make this object accessible from _level0.preferences
        // should only be one copy of Preferences (singleton-like)
        common.preferences = this;

        // load the shared object into sharedObject
        debug("Preferences constructor calling load()\n");
        load();
        debug("Finished: Preferences constructor calling load()\n");

        // set "constant" strings
        FIELD_SKIPPED_REVISION = common.getSimName() + "_skippedRevision";
        FIELD_ASK_LATER = common.getSimName() + "_askLater";
        FIELD_VISITS_SINCE = common.getSimName() + "_visitsSince";
        FIELD_VISITS_EVER = common.getSimName() + "_visitsEver";

        /////////////////////////////////////////
        // TEMPORARY FOR DEVELOPMENT PURPOSES
        /////////////////////////////////////////
        // if they don't have the newest field, reset their shared object!
        //if(!sharedObject.data.userId) {
        //debug("Preferences: DEVELOPMENT: resetting shared object, new information to be stored\n");
        //reset();
        //}
        /////////////////////////////////////////
        if ( sharedObject.data.exists && (!sharedObject.data.dataVersion || sharedObject.data.dataVersion != CURRENT_PREF_VERSION) ) {
            debug( "Preferences: DEVELOPMENT: resetting shared object, new information to be stored\n" );
            reset();
        }
        /////////////////////////////////////////
        // for resetting sharedobject data
        //		Key.addListener(this);
        /////////////////////////////////////////

        debug("Checking to see if shared object data exists\n");

        // if it is the first time simulations have been run from
        // their domain, we need to fill in default values.
        // we set data.exists, so in the future we can tell whether
        // SharedObject.getLocal() reads preferences or creates the
        // object.
        if ( !sharedObject.data.exists ) {
            debug( "Preferences do not exist! Creating, and filling with defaults.\n" );
            sharedObject.data.exists = true;
            sharedObject.data.allowStatistics = true;
            sharedObject.data.checkForUpdates = true;
            sharedObject.data.dataVersion = CURRENT_PREF_VERSION;
            sharedObject.data[FIELD_INSTALLATION_ASK_LATER] = 0;
            sharedObject.data.userPreferencesFileCreationTime = (new Date()).valueOf();
            sharedObject.data.userTotalSessions = 0;
            sharedObject.data.latestPrivacyAgreementVersion = 0;
        }
        else {
            debug( "Found preferences\n" );
        }

        // increment the number of times the current sim has been run
        if ( userAllowsStatistics() ) {
            incrementVisit();
        }

        // conditionally add update information if it doesn't exist
        initUpdateInfo();

        // save the shared object (preferences) to filesystem.
        save();

        // for debugging: print out each piece of data stored in the shared object
        for ( var i: String in sharedObject.data ) {
            debug( "    pref: " + i + " = " + String( sharedObject.data[i] ) + "\n" );
        }

        // if privacy is not up-to-snuff (and user is running from a non-phet-website
        // location), present the user with a dialog
        if ( !isPrivacyOK() && !common.fromPhetWebsite() && common.hasFlashVars() ) {
            //			_level0.privacyDialog = new PrivacyDialog();
            // create the background
            common.showBarrier();
            debug( "Adding backgroundMC" );
            new PrivacyDialog();
        }
        else {
            // do everything else once it has been verified
            common.postAgreement();
        }

        // unload the sharedObject from memory. this prevents an out-of-date version of
        // the preferences data to be saved when the sim is closed.
        flush();
    }

    // load the preferences data into sharedObject
    public function load(): void {
        //debug("Preferences: Loading shared object\n");
        try {
            //Have to use a different namespace than is used for AS2 because otherwise loading AS2 LSO in AS3 causes an unhandleable error, which causes the sim to be broken on startup, see #2762
            sharedObject = SharedObject.getLocal( "phetPrefsAS3", "/" );
        }
        catch( o: Object ) {
            debug( "Preferences.load caught exception: " + o );
        }
    }

    // unload the preferences data from sharedObject. this prevents Flash from saving
    // a possibly out-of-date version when the sim is closed
    public function flush(): void {
        //debug("Preferences: Unloading shared object\n");
        sharedObject.flush();
        //        delete sharedObject;
    }

    // returns whether the user has accepted the latest privacy agreement needed for this sim
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function isPrivacyOK(): Boolean {
        var ret: Boolean = common.getAgreementVersion() <= sharedObject.data.latestPrivacyAgreementVersion;
        if ( !ret ) {
            debug( "agreement version: " + common.getAgreementVersion() );
            debug( "latest agreed: " + sharedObject.data.latestPrivacyAgreementVersion );
        }
        return ret;
    }

    // saves the user's acceptance of the privacy agreement to preferences
    public function agreeToPrivacy(): void {
        load();
        sharedObject.data.latestPrivacyAgreementVersion = common.getAgreementVersion();
        save();
        flush();
    }

    // returns whether the user allows messages to be sent
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function userAllowsStatistics(): Boolean {
        return sharedObject.data.allowStatistics;
    }

    // allow other common code/simulation to check whether
    // statistics messages can be sent
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function areStatisticsMessagesAllowed(): Boolean {
        if ( common.fromPhetWebsite() ) {
            debug( "From PhET website: no statistics allowed\n" );
            return false;
        }
        return userAllowsStatistics();
    }

    // returns whether the user allows checking for updates
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function userAllowsUpdates(): Boolean {
        return sharedObject.data.checkForUpdates;
    }

    // allow other common code/simulation to check whether
    // checking for updates is allowed
    public function areUpdatesAllowed(): Boolean {
        if ( common.fromPhetWebsite() ) {
            debug( "From PhET website: no updates allowed (or needed)\n" );
            return false;
        }
        load();
        var ret: Boolean = userAllowsUpdates();
        flush();
        return ret;
    }

    public function getSkippedRevision(): Number {
        load();
        var ret: Number = sharedObject.data[FIELD_SKIPPED_REVISION];
        flush();
        return ret;
    }

    public function setSkippedRevision( revision: Number ): void {
        load();
        sharedObject.data[FIELD_SKIPPED_REVISION] = revision;
        save();
        flush();
    }

    // set ask me later time
    public function setSimAskLater( days: Number ): void {
        load();

        var dateMilliseconds: Number = (new Date()).valueOf() + days * 24 * 60 * 60 * 1000;
        sharedObject.data[FIELD_ASK_LATER] = dateMilliseconds;

        debug( "Preferences: sim ask later set to " + FlashCommon.dateString( FlashCommon.dateOfMilliseconds( dateMilliseconds ) ) + "\n" );

        save();
        flush();
    }

    public function setInstallationAskLater( days: Number ): void {
        load();

        var dateMilliseconds: Number = (new Date()).valueOf() + days * 24 * 60 * 60 * 1000;

        sharedObject.data[FIELD_INSTALLATION_ASK_LATER] = dateMilliseconds;

        debug( "Preferences: installation ask later set to " + FlashCommon.dateString( FlashCommon.dateOfMilliseconds( dateMilliseconds ) ) + "\n" );

        save();
        flush();
    }

    // allow other code to set the statistics and updates values
    public function setPrivacy( checkForUpdates: Boolean, allowStatistics: Boolean ): void {
        load();
        sharedObject.data.allowStatistics = allowStatistics;
        sharedObject.data.checkForUpdates = checkForUpdates;
        debug( "setting statistics to " + allowStatistics.toString() + "\n" );
        debug( "setting updates to " + checkForUpdates.toString() + "\n" );
        save();
        flush();
    }

    // resets (clears) any data stored on disk
    // (also resets the data in the local copy)
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function reset(): void {
        debug( "Preferences: resetting\n" );
        sharedObject.clear();
    }

    // saves the shared object (preferences data) to the
    // user's hard drive.
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function save(): void {
        debug( "Preferences: Saving shared object\n" );
        sharedObject.flush();
    }

    // creates or increments a preferences attribute specifying
    // how many times the current simulation has been run.
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function incrementVisit(): void {
        debug( "Preferences: Incrementing number of visits\n" );

        // increment total visits
        sharedObject.data.userTotalSessions = sharedObject.data.userTotalSessions + 1;

        // check whether property exists first. might be a new sim
        // or one the user hasn't seen yet.
        if ( sharedObject.data.hasOwnProperty( FIELD_VISITS_EVER ) ) {
            sharedObject.data[FIELD_VISITS_EVER] = sharedObject.data[FIELD_VISITS_EVER] + 1;
        }
        else {
            sharedObject.data[FIELD_VISITS_EVER] = 1;
        }

        if ( sharedObject.data.hasOwnProperty( FIELD_VISITS_SINCE ) ) {
            sharedObject.data[FIELD_VISITS_SINCE] = sharedObject.data[FIELD_VISITS_SINCE] + 1;
        }
        else {
            sharedObject.data[FIELD_VISITS_SINCE] = 1;
        }
    }

    // add sim-specific update information
    public function initUpdateInfo(): void {
        if ( !sharedObject.data.hasOwnProperty( FIELD_SKIPPED_REVISION ) ) {
            sharedObject.data[FIELD_SKIPPED_REVISION] = 0;
        }

        if ( !sharedObject.data.hasOwnProperty( FIELD_ASK_LATER ) ) {
            sharedObject.data[FIELD_ASK_LATER] = 0;
        }
    }

    // resets the number of #'s since sent
    public function resetSince(): void {
        debug( "Preferences: resetting visits since\n" );
        load();
        sharedObject.data[FIELD_VISITS_SINCE] = 0;
        save();
        flush();
    }

    // how many times the current simulation has ever been run (according to preferences)
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function visitsEver(): Number {
        return sharedObject.data[FIELD_VISITS_EVER];
    }

    // how many times the current simulation has been run since last message sent (according to preferences)
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function visitsSince(): Number {
        return sharedObject.data[FIELD_VISITS_SINCE];
    }

    // returns when the preferences file was created
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function getUserTime(): Number {
        return sharedObject.data.userPreferencesFileCreationTime;
    }

    // returns how many total times the user has run any simulation
    // NOTE: make sure preferences are loaded before calling, and unloaded sometime soon after
    public function getUserTotalSessions(): Number {
        return sharedObject.data.userTotalSessions;
    }

    // return number of milliseconds elapsed since ask-later was selected
    public function simAskLaterElapsed(): Number {
        load();
        var time: Number = sharedObject.data[FIELD_ASK_LATER];
        flush();
        return (new Date()).valueOf() - time;
    }

    public function installationAskLaterElapsed(): Number {
        load();
        var time: Number = sharedObject.data[FIELD_INSTALLATION_ASK_LATER];
        flush();
        return (new Date()).valueOf() - time;
    }

    /////////////////////////////////////////
    // for resetting preferences data
    //	public function onKeyDown() : void {
    //		if(!common.getDev()) {
    //			return;
    //		}
    //
    //		if(Key.getCode() == 119 && Key.isDown(Key.SHIFT)) {
    //			// F8 was pressed
    //			debug("Preferences: Manually resetting shared data\n");
    //			load();
    //			reset();
    //			unload();
    //		}
    //	}
}

}