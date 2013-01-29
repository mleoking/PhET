// Copyright 2013, University of Colorado

define( [], function() {

    /**
     * Constructor
     * @param {Point2D} location bottom center
     * @param {Dimension2D} size
     * @param {Number} volume in liters (L)
     * @constructor
     */
    function Beaker( location, size, volume ) {
        this.location = location;
        this.size = size;
        this.volume = volume;
    }

    // Gets the x coordinate of the left wall.
    Beaker.prototype.getMinX = function () {
        return this.location.x - ( this.size.width / 2 );
    }

    return Beaker;
} );