// Copyright 2002-2012, University of Colorado

/**
 * Display object for the compass.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ], function( Easel ) {

    /**
     * @param {FaradayModel} model
     * @constructor
     */
    function CompassDisplay( model ) {
        // constructor stealing
        Easel.Text.call( this, "compass", "bold 36px Arial", 'white' );
    }

    // prototype chaining
    CompassDisplay.prototype = new Easel.Text();

    return CompassDisplay;
} );
