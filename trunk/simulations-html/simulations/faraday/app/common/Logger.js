// Copyright 2002-2012, University of Colorado

/**
 * Wrapper for logging. When we decide on a JS logging API, plug it in here.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [],
        function () {

            /**
             * @class Logger
             * @constructor
             * @param sourceName name of the source file in which the logger instance was created.
             */
            function Logger( sourceName ) {
                this.sourceName = sourceName;
            }

            // Enable or disable logging application-wide.
            Logger.enabled = false;

            // All other methods call this one, which prints to the console.
            Logger.log = function ( prefix, sourceName, message ) {
                if ( Logger.enabled ) {
                    console.log( prefix + " : " + sourceName + " : " + message );
                }
            };

            Logger.prototype.debug = function ( message ) {
                Logger.log( "DEBUG", this.sourceName, message );
            };

            Logger.prototype.info = function ( message ) {
                Logger.log( "INFO", this.sourceName, message );
            };

            Logger.prototype.warn = function ( message ) {
                Logger.log( "WARNING", this.sourceName, message );
            };

            Logger.prototype.error = function ( message ) {
                Logger.log( "ERROR", this.sourceName, message );
            };

            Logger.prototype.fatal = function ( message ) {
                Logger.log( "FATAL", this.sourceName, message );
            };

            return Logger;
        } );
