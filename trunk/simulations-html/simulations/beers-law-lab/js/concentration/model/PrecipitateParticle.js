// Copyright 2002-2013, University of Colorado

/**
 * One particle that makes up the precipitate that forms on the bottom of the beaker.
 * Precipitate particles are static (they don't move). They have no associated animation,
 * and they magically appear on the bottom of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'common/model/Inheritance',
            'concentration/model/SoluteParticle'
        ],
        function ( Inheritance, SoluteParticle ) {

            /**
             * Constructor
             * @param {Solute} solute
             * @param {Point2D} location location in the beaker's coordinate frame
             * @param {Number} orientation in radians
             * @constructor
             */
            function PrecipitateParticle( solute, location, orientation ) {
                SoluteParticle.call( solute.particleColor, solute.particleSize, location, orientation );
            }

            Inheritance.inheritPrototype( PrecipitateParticle, SoluteParticle );

            return PrecipitateParticle;
        } );
