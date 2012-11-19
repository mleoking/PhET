// Copyright 2002-2012, University of Colorado

/**
 * Display object for the field meter.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ], function( Easel ) {

    /**
     * @param {FieldMeter} fieldMeter
     * @param {ModelViewTransform} mvt
     * @constructor
     */
    function FieldMeterDisplay( fieldMeter, mvt ) {
        // constructor stealing
        Easel.Text.call( this, "meter", "bold 36px Arial", 'white' );
    }

    // prototype chaining
    FieldMeterDisplay.prototype = new Easel.Text();

    return FieldMeterDisplay;
} );
