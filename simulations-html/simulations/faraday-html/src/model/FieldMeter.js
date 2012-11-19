// Copyright 2002-2012, University of Colorado

/**
 * Compass model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property' ], function( Logger, Property ) {

    function FieldMeter( location, visible ) {

        var logger = new Logger( "FieldMeter" ); // logger for this source file

        // initialize properties
        this.location = new Property( location );
        this.visible = new Property( visible );

        //DEBUG
        var DEBUG = true;
        if ( DEBUG ) {
            this.location.addObserver( function ( newValue ) {
                logger.debug( "location=" + newValue );
            } );
            this.visible.addObserver( function ( newValue ) {
                logger.debug( "visible=" + newValue );
            } );
        }
    }

    // Resets all properties
    FieldMeter.prototype.reset = function() {
        this.location.reset();
        this.visible.reset();
    };

    return FieldMeter;
} );
