// Copyright 2002-2012, University of Colorado

/**
 * Compass model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property' ], function( Logger, Property ) {

    function Compass( location ) {

        var logger = new Logger( "Compass" ); // logger for this source file

        this.location = new Property( location );

        //DEBUG
        var DEBUG = true;
        if ( DEBUG ) {
            this.location.addObserver( function ( newValue ) {
                logger.debug( "location=" + newValue );
            } );
        }
    }

    Compass.prototype.reset = function() {
        this.location.reset();
    };

    return Compass;
} );
