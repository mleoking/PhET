// Copyright 2002-2012, University of Colorado

/**
 * Wrapper for logging. When we decide on a JS logging API, plug it in here.
 *
 * @author Chris Malley
 */
define( [], function() {

    function Logger() {}

    // Set this to false to disable logging application-wide.
    Logger.enabled = true;

    // All other methods call this one, which prints to the console.
    Logger.log = function ( prefix, message ) {
        if ( Logger.enabled ) {
            console.log( prefix + ": " + message );
        }
    };

    Logger.info = function ( message ) {
        Logger.log( "INFO", message );
    };

    Logger.warn = function ( message ) {
        Logger.log( "WARNING", message );
    };

    Logger.error = function ( message ) {
        Logger.log( "ERROR", message );
    };

    Logger.fatal = function ( message ) {
        Logger.log( "FATAL", message );
    };

    return Logger;
});
