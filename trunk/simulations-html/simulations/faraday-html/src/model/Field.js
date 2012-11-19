// Copyright 2002-2012, University of Colorado

/**
 * Compass model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property' ], function( Logger, Property ) {

    function Field( visible, magnet ) {

        var logger = new Logger( "Field" ); // logger for this source file

        // initialize properties
        this.visible = new Property( visible );

        //DEBUG
        if ( true ) {
            this.visible.addObserver( function ( newValue ) {
                logger.debug( "visible=" + newValue );
            } );
        }
    }

    // Resets all properties
    Field.prototype.reset = function() {
        this.visible.reset();
    };

    return Field;
} );
