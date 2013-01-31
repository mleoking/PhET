// Copyright 2002-2013, University of Colorado

/**
 * Base class for solute particles.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
        'common/model/property/Property'
        ],
        function ( Property ) {

            /**
             * Constructor
             * @param {Color} color
             * @param {Number} size particles are square, this is the length of one side
             * @param {Point2D} location location of the particle in the beaker's coordinate frame
             * @param {Number} orientation in radians
             * @constructor
             */
            function SoluteParticle( color, size, location, orientation ) {
                this.color = color;
                this.size = size;
                this.locationProperty = new Property( location );
                this.orientation = orientation;
            }

            return SoluteParticle;
        } );