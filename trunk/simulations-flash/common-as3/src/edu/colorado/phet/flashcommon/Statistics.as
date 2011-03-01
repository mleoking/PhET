package edu.colorado.phet.flashcommon {
import flash.events.Event;
import flash.events.IOErrorEvent;
import flash.events.SecurityErrorEvent;
import flash.net.LocalConnection;
import flash.net.URLLoader;
import flash.net.URLRequest;
import flash.system.Capabilities;
import flash.xml.XMLDocument;
import flash.xml.XMLNode;

// Handles statistics messages sent directly to
// the phet.colorado.edu server
//
// Author: Jonathan Olson

public class Statistics {

    // stores whether a message send has failed. if it has, don't send more!
    public var messageError: Boolean;

    // store the session id for all future statistics messages that need to be sent
    public var sessionId: String;

    public var common: FlashCommon;

    // shorthand for debugging function
    public function debug( str: String ): void {
        FlashCommon.getInstance().debug( str );
    }

    // constructor
    public function Statistics() {
        //debug("Statistics initializing\n");

        messageError = false;

        // shortcut to FlashCommon, but now with type-checking!
        debug("Statistics.init getting flash common instance\n");
        common = FlashCommon.getInstance();
        debug("Finished: Statistics.init getting flash common instance\n");

        // make this object accessible from _level0.statistics
        // should only be one copy of Statistics (singleton-like)
        //		_level0.statistics = this;
        common.statistics = this;

        // send the session start message.
        // if messages is disabled, the message will not be sent
        // CURRENTLY CALLED FROM ELSEWHERE sendSessionStart();
    }

    public function fieldTranslate( field: String ):String {
        var str: String = CommonStrings.get( "StatisticsField-" + field, field );
        if ( str != field ) {
            // it was translated
            return str;
        }
        debug( "WARNING Statistics: could not translate " + field + "\n" );
        var ob: Object = {
            message_type : "Message type",
            message_version : "Message version",
            sim_type : "Simulation type",
            sim_project : "Project name",
            sim_name : "Simulation name",
            sim_major_version : "Simulation version (major)",
            sim_minor_version : "Simulation version (minor)",
            sim_dev_version : "Simulation version (dev)",
            sim_revision : "Simulation version (revision)",
            sim_version_timestamp : "Simulation version (timestamp)",
            sim_distribution_tag : "Simulation version (distribution)",
            sim_locale_language : "Language",
            sim_locale_country : "Country",
            sim_deployment : "Deployment type",
            sim_dev : "Is this a developer version?",
            sim_total_sessions : "Total number of times this simulation has been run",
            sim_sessions_since : "Number of times this simulation has been run since last online",
            host_flash_os : "Operating system and name",
            host_flash_version : "Flash Player version",
            host_locale_language : "Host language",
            host_flash_time_offset : "Timezone",
            host_flash_accessibility : "Using an accessible device?",
            host_flash_domain : "Simulation domain",
            user_preference_file_creation_time : "Preferences file creation time",
            user_total_sessions : "Total number of times all simulations have been run",
            user_installation_timestamp : "PhET installation timestamp"
        };
        if ( ob[field] != undefined && ob[field] != null ) {
            return ob[field];
        }
        debug( "WARNING Statistics: could not find default for " + field + "\n" );
        return field;
    }

    public function fieldFormat( field: String, value: *, humanReadable: Boolean ): String {
        if ( humanReadable ) {
            return fieldTranslate( field ) + " " + unescape( messageEscape( value ) + "\n" );
        }
        else {
            return field + " = '" + messageEscape( value ) + "' \n";
        }
    }

    // get the exact string sent to the server. this will be used to show
    // users exactly what statistics data is sent.
    public function sessionStartMessage( humanReadable: Boolean ): String {
        var str: String = "";

        // make data available to be read
        common.preferences.load();

        /////// message information
        str += fieldFormat( "message_type", "session", humanReadable );
        str += fieldFormat( "message_version", "0", humanReadable );


        /////// simulation data
        str += fieldFormat( "sim_type", "flash", humanReadable );

        // currently, project is the same as sim for Flash simulations
        str += fieldFormat( "sim_project", common.getSimProject(), humanReadable );
        str += fieldFormat( "sim_name", common.getSimName(), humanReadable );

        str += fieldFormat( "sim_major_version", common.getVersionMajor(), humanReadable );
        str += fieldFormat( "sim_minor_version", common.getVersionMinor(), humanReadable );
        str += fieldFormat( "sim_dev_version", common.getVersionDev(), humanReadable );
        str += fieldFormat( "sim_revision", common.getVersionRevision(), humanReadable );
        str += fieldFormat( "sim_version_timestamp", common.getVersionTimestampString(), humanReadable );

        str += fieldFormat( "sim_locale_language", common.getLanguage(), humanReadable );
        str += fieldFormat( "sim_locale_country", common.getCountry(), humanReadable );

        str += fieldFormat( "sim_sessions_since", common.preferences.visitsSince(), humanReadable );
        str += fieldFormat( "sim_total_sessions", common.preferences.visitsEver(), humanReadable );

        str += fieldFormat( "sim_deployment", common.getDeployment(), humanReadable );
        str += fieldFormat( "sim_distribution_tag", common.getDistributionTag(), humanReadable );
        str += fieldFormat( "sim_dev", String( common.getDev() ), humanReadable );


        /////// host data

        str += fieldFormat( "host_flash_os", Capabilities.os, humanReadable );
        str += fieldFormat( "host_flash_version", Capabilities.version, humanReadable );
        str += fieldFormat( "host_locale_language", Capabilities.language, humanReadable );
        str += fieldFormat( "host_flash_time_offset", String( (new Date()).getTimezoneOffset() ), humanReadable );
        str += fieldFormat( "host_flash_accessibility", String( Capabilities.hasAccessibility ), humanReadable );
        str += fieldFormat( "host_flash_domain", (new LocalConnection()).domain, humanReadable );


        /////// user data
        str += fieldFormat( "user_preference_file_creation_time", common.preferences.getUserTime(), humanReadable );
        str += fieldFormat( "user_installation_timestamp", common.getInstallationTimestampString(), humanReadable );
        str += fieldFormat( "user_total_sessions", common.preferences.getUserTotalSessions(), humanReadable );

        // unload data from shared object
        common.preferences.flush();

        return str;
    }

    // attempts to send a session start message
    public function sendSessionStart(): void {
        // load the user's preferences so we can see whether messages is allowed and they
        // have accepted the privacy agreement
        common.preferences.load();

        if ( !common.preferences.areStatisticsMessagesAllowed() ) {
            debug( "Statistics: cannot send session start message: statistics messages disabled\n" );
            common.preferences.flush();
            return;
        }
        if ( !common.hasFlashVars() ) {
            debug( "Statistics: flash vars were not detected, will not send message\n" );
            common.preferences.flush();
            return;
        }
        if ( !common.preferences.isPrivacyOK() ) {
            debug( "Statistics: cannot send session start message: have not accepted agreement yet\n" );
            common.preferences.flush();
            return;
        }

        // we no longer need preferences data, so we need to unload the data
        common.preferences.flush();

        debug( "Statistics: sending session start message\n" );

        // wrap the message in xml tags
        var str: String = "<?xml version=\"1.0\"?><submit_message><statistics_message " + sessionStartMessage( false ) + " /></submit_message>";
        var queryXML: XML = new XML( str );
        //        queryXML.addRequestHeader( "Content-type", "text/xml" );
        sendXML( queryXML );
    }

    // this is used for all statistics messages to send the xml to the server
    public function sendXML( query: XML ): void {

        // if a message has previously failed, don't send more. the
        // network may be down
        if ( messageError ) { return; }

        var request: URLRequest = new URLRequest( "http://" + FlashCommon.getMainServer() + "/statistics/submit_message.php" );
        request.contentType = "text/xml"; // make sure it will be recognized as XML by the server
        request.method = "POST";
        request.data = new XML( query );
        var loader: URLLoader = new URLLoader();
        loader.addEventListener( Event.COMPLETE, function( evt: Event ): void {
            debug( "Statistics (2): reply successfully received\n" );
            var response: XMLDocument = new XMLDocument( loader.data );
            response.ignoreWhite = true; // make sure that whitespace isn't treated as nodes! (DO NOT REMOVE THIS)
            debug( String( response ) + "\n" );

            var submitMessageResponse: XMLNode = response.childNodes[0];
            debug( "sMR: " );
            debug( submitMessageResponse.toString() );
            debug( "---" );

            // whether the message was successful
            var full_success: Boolean = false;

            if ( submitMessageResponse.attributes.success == "true" ) {
                var responses: Array = submitMessageResponse.childNodes;
                for each ( var child: XMLNode in responses ) {
                    debug( "child: " );
                    debug( child.toString() );
                    debug( "---" );
                    if ( child.nodeName == "statistics_message_response" && child.attributes.success == "true" ) {
                        debug( "Statistics: statistics_message_response found\n" );
                        full_success = true;
                    }
                    else {
                        debug( "Statistics: statistics_message_response FAILURE\n" );
                    }
                }
            }
            else {
                debug( "Statistics: phet_info_response FAILURE\n" );
            }


            if ( full_success ) {
                // statistics message successful
                debug( "Statistics: Message Handshake Successful\n" );
                common.preferences.resetSince();
            }
            else {
                // server could not record statistics message
                debug( "WARNING: Statistics: Message Handshake Failure\n" );
            }
        } );

        loader.addEventListener( IOErrorEvent.IO_ERROR, function( evt: Event ): void {
            debug( "Statistics: message error! (io)\n" );
            debug( String( evt ) );
        } );

        loader.addEventListener( SecurityErrorEvent.SECURITY_ERROR, function( evt: Event ): void {
            debug( "Statistics: message error! (security)\n" );
            debug( String( evt ) );
        } );

        loader.load( request );

    }

    // sanitize information to be send to phet statistics: escape or turn into 'null'
    public function messageEscape( value: * ): String {
        var str: String;
        if ( value == null ) {
            return FlashCommon.NULLVAL;
        }
        if ( typeof(value) == "string" ) {
            str = value;
        }
        else {
            if ( typeof(value) == "number" ) {
                str = String( value );
            }
            else {
                if ( typeof(value) == "null" ) {
                    return FlashCommon.NULLVAL;
                }
                else {
                    debug( "WARNING: Statistics.messageEscape invalid type: " + typeof(value) + " = " + String( value ) + "\n" );
                    str = String( value );
                }
            }
        }
        if ( str == null || common.isPlaceholder( str ) ) {
            return FlashCommon.NULLVAL;
        }
        return escape( str );
    }
}
}