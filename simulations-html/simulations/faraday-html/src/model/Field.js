// Copyright 2002-2012, University of Colorado

/**
 * Compass model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property' ], function( Logger, Property ) {

    function Field( visible ) {

        var logger = new Logger( "Field" ); // logger for this source file

        this.visible = new Property( visible );

        //DEBUG
        var DEBUG = true;
        if ( DEBUG ) {
            this.visible.addObserver( function ( newValue ) {
                logger.debug( "visible=" + newValue );
            } );
        }
    }

    Field.prototype.reset = function() {
        this.visible.reset();
    };

    return Field;
} );
