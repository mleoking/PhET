// Copyright 2013, University of Colorado

/**
 * A movable model element.
 * Semantics of units are determined by the client.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [ 'phetcommon/model/property/Property' ], function( Property ) {

    /**
     * Constructor
     * @param {Point2D} location
     * @param {Rectangle} dragBounds
     * @constructor
     */
    function Movable( location, dragBounds ) {
        this.location = new Property( location );
        this.dragBounds = dragBounds;
    }

    Movable.prototype.reset = function() {
        this.location.reset();
    }

    return Movable;
});
