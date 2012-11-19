// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property', 'common/Vector' ], function ( Logger, Property, Vector ) {

    /**
     * @class BarMagnet
     * @constructor
     * @param {Point} location
     * @param {Dimension} size
     * @param {Number} strength in Gauss
     * @param {Number} orientation in radians
     */
    function BarMagnet( location, size, strength, orientation ) {

        var logger = new Logger( "BarMagnet" ); // logger for this source file

        // initialize properties
        this.location = new Property( location );
        this.size = size;
        this.strength = new Property( strength );
        this.orientation = new Property( orientation );

        // Debugging output
        var DEBUG = true;
        if ( DEBUG ) {
            this.location.addObserver( function ( newValue ) {
                logger.debug( "location=" + newValue );
            } );
            this.strength.addObserver( function ( newValue ) {
                logger.debug( "strength=" + newValue );
            } );
            this.orientation.addObserver( function ( newValue ) {
                logger.debug( "orientation=" + newValue );
            } );
        }
    }

    // Resets all properties
    BarMagnet.prototype.reset = function() {
        this.location.reset();
        this.strength.reset();
        this.orientation.reset();
    };

    /*
     * Gets the E-field vector at a point.
     * Note that this is not physically accurate.
     * It was not feasible to implement a numerical model directly, as it relies on double integrals.
     * See BarMagnet.java in simulations-java/faraday for details.
     *
     * @param {Point} point
     * @return {Vector}
     */
    BarMagnet.prototype.getFieldVector = function( point ) {
         return new Vector( 10, 10 ); //TODO
    };

    return BarMagnet;
} );

