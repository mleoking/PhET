package edu.colorado.phet.flashcommon {
// Handles checking for a newer version of the simulation
// online, and what to do if that newer version exists.
//
// Author: Jonathan Olson

import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.net.URLLoader;
import flash.net.URLRequest;
import flash.system.Security;
import flash.xml.XMLDocument;

public class UpdateHandler {
    public static var SIM_REQUEST_VERSION: String = "1";
    public static var INSTALLER_REQUEST_VERSION: String = "1";

    // the latest version information detected from the server
    // TODO: rename these to match _level0 fields
    public var versionMajor: Number;
    public var versionMinor: Number;
    public var versionDev: Number;
    public var versionRevision: Number;
    public var simTimestamp: Number;
    public var simAskLaterDays: Number;

    public var installerRecommend: Boolean;
    public var installerTimestamp: Number;
    public var installerAskLaterDays: Number;

    // whether the "Check for updates now" button was clicked
    // (manual check for updates)
    public var manual: Boolean;

    public var common: FlashCommon;

    public var receivedSimResponse: Boolean;
    public var receivedInstallationResponse: Boolean;

    // shorthand debugging function
    public function debug( str: String ): void {
        FlashCommon.getInstance().debug( str );
    }

    // constructor
    public function UpdateHandler() {
        //debug("UpdateHandler initializing\n");

        // shortcut to FlashCommon, but now with type-checking!
        common = FlashCommon.getInstance();

        // set to true if the user is manually checking for updates
        // in this case, we should give them a response if they are
        // running the current version
        manual = false;

        receivedSimResponse = false;
        receivedInstallationResponse = false;

        // make this object accessible from _level0.updateHandler
        // should only be one copy of UpdateHandler (singleton-like)
        //		_level0.updateHandler = this;
        common.updateHandler = this;

        // make sure the user allows us to check for updates!
        if ( !common.preferences.areUpdatesAllowed() ) {
            debug( "UpdateHandler: not checking for updates (Preferences.areUpdatesAllowed() = false)\n" );
        }
        else {
            // check for both sim and installation
            sendStartupQuery( startupQueryString( true, true ) );
        }

    }

    public function startupQueryString( checkSim: Boolean, checkInstallation: Boolean ): String {
        // if user isn't querying anything, return undefined
        if ( !(checkSim || (checkInstallation && common.fromFullInstallation())) ) {
            return undefined;
        }

        var str: String = "<?xml version=\"1.0\"?>";
        str += "<phet_info>";

        if ( checkSim ) {
            str += "<sim_version request_version=\"" + SIM_REQUEST_VERSION + "\" project=\"" + common.getSimProject() + "\" sim=\"" + common.getSimName() + "\" />";
        }

        if ( checkInstallation && common.fromFullInstallation() ) {
            str += "<phet_installer_update request_version=\"" + INSTALLER_REQUEST_VERSION + "\" timestamp_seconds=\"" + String( common.getInstallationTimestamp() ) + "\" />";
        }

        str += "</phet_info>";

        debug( "UpdateHandler (2): Startup query:\n" + str + "\n" );

        return str;
    }

    public function sendStartupQuery( query: String ): void {

        if ( query === undefined || query == null ) {
            // must not be querying for anything, don't do anything
            return;
        }

        if ( !common.hasFlashVars() ) {
            debug( "UpdateHandler: could not find flash vars, will not check for updates\n" );
            return;
        }

        // make sure we can access phet.colorado.edu and all files under that domain
        // this is more of a sanity-check than anything else, this should be included
        // under FlashCommon.as
        Security.allowDomain( FlashCommon.getMainServer() );

        var request: URLRequest = new URLRequest( "http://" + FlashCommon.getMainServer() + "/services/phet-info" );
        request.contentType = "text/xml";
        request.method = "POST";
        request.data = new XML( query );
        var loader: URLLoader = new URLLoader();
        loader.addEventListener( Event.COMPLETE, function( evt: Event ): void {
            debug( "UpdateHandler (2): reply successfully received\n" );
            var xml: XMLDocument = new XMLDocument( loader.data );
            xml.ignoreWhite = true; // make sure that whitespace isn't treated as nodes! (DO NOT REMOVE THIS)
            debug( String( xml ) + "\n" );

            // TODO: remove after DEVELOPMENT
            receivedSimResponse = false;
            receivedInstallationResponse = false;

            if ( xml.childNodes[0].attributes.success != "true" ) {
                debug( "WARNING UpdateHandler: phet_info_response failure\n" );
                showUpdateError();
                return;
            }

            var children: Array = xml.childNodes[0].childNodes; // children of sim_startup_query_response

            for ( var idx in children ) {
                var child = children[idx];
                var atts: Object = child.attributes;
                if ( child.nodeName == "sim_version_response" ) {
                    debug( "UpdateHandler (2): received sim_version_response\n" );
                    // sanity checks
                    if ( atts["success"] != "true" ) {
                        debug( "WARNING UpdateHandler: sim_version_response failure\n" );

                        // do not continue further with this child
                        showUpdateError();
                        continue;
                    }
                    if ( atts["project"] != common.getSimProject() ) {
                        debug( "WARNING UpdateHandler (2): Project does not match\n" );
                    }
                    if ( atts["sim"] != common.getSimName() ) {
                        debug( "WARNING UpdateHandler (2): Name does not match\n" );
                    }

                    receivedSimResponse = true;

                    versionMajor = parseInt( atts["version_major"] );
                    versionMinor = parseInt( atts["version_minor"] );
                    versionDev = parseInt( atts["version_dev"] );
                    versionRevision = parseInt( atts["version_revision"] );
                    simTimestamp = parseInt( atts["version_timestamp"] );
                    simAskLaterDays = parseInt( atts["ask_me_later_duration_days"] );

                    debug( "   latest: " + common.zeroPadVersion( versionMajor, versionMinor, versionDev ) + " (" + String( versionRevision ) + ")\n" );

                }
                else {
                    if ( child.nodeName == "phet_installer_update_response" ) {
                        debug( "UpdateHandler (2): received phet_installer_update_response\n" );

                        if ( atts["success"] != "true" ) {
                            debug( "WARNING UpdateHandler: phet_installer_update_response failure\n" );

                            // do not continue further with this child
                            showUpdateError();
                            continue;
                        }

                        receivedInstallationResponse = true;

                        installerRecommend = (atts["recommend_update"] == "true");
                        installerTimestamp = parseInt( atts["timestamp_seconds"] );
                        installerAskLaterDays = parseInt( atts["ask_me_later_duration_days"] );
                    }
                    else {
                        debug( "WARNING UpdateHandler (2): unknown child: " + child.nodeName + "\n" );
                    }
                }
            }

            handleResponse();
        } );

        loader.addEventListener( IOErrorEvent.IO_ERROR, function( evt: Event ): void {
            debug( "WARNING: UpdateHandler (2): Failure to obtain latest version information\n" );
            showUpdateError();
        } );

        loader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, function( evt: Event ): void {
            debug( "WARNING: UpdateHandler (2): Failure to obtain latest version information\n" );
            showUpdateError();
        } );

        loader.load( request );

        // send the request, wait for the response to load
        //xml.load("http://localhost/jolson/deploy/fake-sim-startup-query.php?request=" + escape(query));
        //        var queryXML:XML = new XML( query );
        //        queryXML.addRequestHeader( "Content-type", "text/xml" );
        //        queryXML.sendAndLoad( "http://" + FlashCommon.getMainServer() + "/services/phet-info", xml );
    }

    public function manualCheckSim() {
        debug( "UpdateHandler: checking manually for sim" );
        manual = true;
        sendStartupQuery( startupQueryString( true, false ) );
    }

    public function manualCheckInstallation(): void {
        debug( "UpdateHandler: checking manually for installation" );
        manual = true;
        sendStartupQuery( startupQueryString( false, true ) );
    }

    public function handleResponse() {
        //debug("UpdateHandler: handleResponse()\n");

        var installShown = false;

        if ( receivedInstallationResponse && common.fromFullInstallation() ) {
            receivedInstallationResponse = false;

            if ( installerRecommend ) {
                if ( !manual && common.preferences.installationAskLaterElapsed() < 0 ) {
                    debug( "UpdateHandler: used selected ask later, installation time elapsed = " + String( common.preferences.installationAskLaterElapsed() ) + "\n" );
                }
                else {
                    installShown = true;
                    installationUpdatesAvailable( installerTimestamp, installerAskLaterDays );
                }
            }
            else {
                if ( manual ) {
                    showInstallationUpToDate();

                    // they can't query manually for both installer and sim
                    return;
                }

                // run this again to handle whether sim response was received
                handleResponse();
            }
        }

        if ( receivedSimResponse && !installShown ) {
            receivedSimResponse = false;

            if ( versionRevision == common.getVersionRevision() ) {
                // running the latest version
                debug( "UpdateHandler: running latest version\n" );

                // if the user clicked "Check for Updates Now", inform the user that no
                // update is available
                if ( manual ) {
                    showSimUpToDate();
                }
            }
            else {
                if ( versionRevision < common.getVersionRevision() ) {
                    debug( "WARNING UpdateHandler: running a more recent version than on the production website.\n" );

                    if ( manual ) {
                        showSimUpToDate();
                    }
                }
                else {
                    if ( isNaN( versionMajor ) || isNaN( versionMinor ) ) {
                        debug( "WARNING UpdateHandler: received NaN version information!\n" );

                        if ( manual ) {
                            showUpdateError();
                        }
                    }
                    else {
                        if ( !manual && versionRevision == common.preferences.getSkippedRevision() ) {
                            // user did not click "Check for Updates Now" AND the new version <= latest skipped version
                            debug( "UpdateHandler: used selected to skip this update\n" );
                        }
                        else {
                            if ( !manual && common.preferences.simAskLaterElapsed() < 0 ) {
                                debug( "UpdateHandler: used selected ask later, sim time elapsed = " + String( common.preferences.simAskLaterElapsed() ) + "\n" );
                            }
                            else {
                                if ( common.fromFullInstallation() && simTimestamp + 1800 > installerTimestamp ) {
                                    debug( "UpdateHandler: installer might not contain the most recent sim\n" );
                                }
                                simUpdatesAvailable( versionMajor, versionMinor, versionDev, versionRevision, simAskLaterDays );
                            }
                        }
                    }
                }
            }

        }


    }


    // called if a newer version is available online
    public function simUpdatesAvailable( versionMajor: Number, versionMinor: Number, versionDev: Number, versionRevision: Number, simAskLaterDays: Number ): void {
        debug( "UpdateHandler: Sim Updates Available (dialog)!\n" );

        CommonDialog.openUpdateSimDialog( versionMajor, versionMinor, versionDev, versionRevision, simAskLaterDays );
    }

    // called if a newer version is available online
    public function installationUpdatesAvailable( installerTimestamp: Number, installerAskLaterDays: Number ): void {
        debug( "UpdateHandler: Installation Updates Available (dialog)!\n" );

        CommonDialog.openUpdateInstallationDialog( installerTimestamp, installerAskLaterDays );
    }

    public function showUpdateError(): void {
        if ( !manual ) { return; }
        var str: String = "";
        str += CommonStrings.get( "VersionError1", "An error was encountered while trying to obtain version information." ) + "\n";
        str += CommonStrings.get( "VersionError2", "Please try again later, or visit <a href='{0}'>http://" + FlashCommon.getMainServer() + "</a>", ["asfunction:_level0.common.openExternalLink,http://" + FlashCommon.getMainServer() + ""] );
        CommonDialog.openMessageDialog( CommonStrings.get( "Error", "Error" ), str, false );
    }

    public function showSimUpToDate(): void {
        if ( !manual ) { return; }
        var str: String = "";
        str += CommonStrings.get( "MostCurrentVersion", "You have the most current version ({0}) of {1}.", [common.getVersionString(), common.getSimTitle()] );
        CommonDialog.openMessageDialog( CommonStrings.get( "UpToDate", "Up To Date" ), str, true );
    }

    public function showInstallationUpToDate(): void {
        if ( !manual ) { return; }
        var str: String = "";
        str += CommonStrings.get( "MostCurrentVersion", "You have the most current version ({0}) of {1}.", [FlashCommon.dateString( FlashCommon.dateOfSeconds( common.getInstallerCreationTimestamp() ) ), "PhET Offline Website Installer"] );
        CommonDialog.openMessageDialog( CommonStrings.get( "UpToDate", "Up To Date" ), str, true );
    }

}
}
