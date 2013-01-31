// Copyright 2002-2013, University of Colorado

/**
 * A particle that comes out of the shaker.
 * The particle falls towards the surface of the solution, may bounce off the wall
 * of the beaker, and disappears when it hits the surface of the solution (or bottom of the beaker,
 * if the beaker is empty.)
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'phetcommon/math/Point2D',
            'phetcommon/math/Vector2D',
            'common/model/Inheritance',
            'concentration/model/SoluteParticle'
        ],
        function ( Point2D, Vector2D, Inheritance, SoluteParticle ) {

            /**
             * Constructor
             * @param {Solute} solute
             * @param {Point2D} location in the beaker's coordinate frame
             * @param {Number} orientation in radians
             * @param {Vector2D} initialVelocity
             * @param {Vector2D} acceleration
             * @constructor
             */
            function ShakerParticle( solute, location, orientation, initialVelocity, acceleration ) {

                SoluteParticle.call( this, solute.particleColor, solute.particleSize, location, orientation );

                this.solute = solute;
                this.velocity = initialVelocity;
                this.acceleration = acceleration;
            }

            Inheritance.inheritPrototype( ShakerParticle, SoluteParticle );

            /**
             *  Propagates the particle to a new location.
             *  @param {Number} deltaSeconds
             *  @param {Beaker} beaker
             */
            ShakerParticle.prototype.tick = function ( deltaSeconds, beaker ) {

                this.velocity = this.velocity.plus( this.acceleration.times( deltaSeconds ) );
                var newLocation = this.locationProperty.get().plus( this.velocity.times( deltaSeconds ) );

                /*
                 * Did the particle hit the left wall of the beaker? If so, change direction.
                 * Note that this is a very simplified model, and only deals with the left wall of the beaker,
                 * which is the only wall that the particles can hit in practice.
                 */
                var minX = beaker.getMinX() + this.solute.particleSize;
                if ( newLocation.x <= minX ) {
                    newLocation = new Point2D( minX, newLocation.y );
                    this.velocity = new Vector2D( Math.abs( this.velocity.x ), this.velocity.y );
                }

                this.locationProperty.set( newLocation );
            }

            return ShakerParticle;
        } );
