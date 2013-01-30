// Copyright 2002-2013, University of Colorado

/**
 * Model of a shaker, contains solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'phetcommon/math/Point2D',
            'phetcommon/model/property/Property',
            'common/model/Inheritance',
            'common/model/Movable'
        ],
        function ( Point2D, Property, Inheritance, Movable ) {

            /**
             * Constructor
             * @param {Point2D} location
             * @param {Number} orientation in radians
             * @param {Rectangle} dragBounds
             * @param {Property<Solute>} soluteProperty
             * @param {Number} maxDispensingRate
             * @constructor
             */
            function Shaker( location, dragBounds, orientation, soluteProperty, maxDispensingRate ) {

                Movable.call( this, location, dragBounds ); // constructor stealing

                this.orientation = orientation;
                this.soluteProperty = soluteProperty;
                this.maxDispensingRate = maxDispensingRate;

                this.visibleProperty = new Property( true );
                this.emptyProperty = new Property( false );
                this.dispensingRateProperty = new Property( 0 );
                this.previousLocation = location;

                // set the dispensing rate to zero when the shaker becomes empty or invisible
                var that = this;
                var observer = function() {
                    if ( that.emptyProperty.get() || !that.visibleProperty.get() ) {
                        that.dispensingRateProperty.set( 0 );
                    }
                };
                this.emptyProperty.addObserver( observer );
                this.visibleProperty.addObserver( observer );
            }

            Inheritance.inheritPrototype( Shaker, Movable );

            // Resets the shaker to its initial state.
            Shaker.prototype.resetSuper = Movable.prototype.reset; //TODO awful hack for calling super.reset
            Shaker.prototype.reset = function () {
                this.resetSuper();
                this.dispensingRateProperty.reset();
                this.previousLocation = this.locationProperty.get(); // to prevent shaker from dispensing solute when its location is reset
            }

            /*
             * This is called whenever the simulation clock ticks.
             * Sets the dispensing rate if the shaker is moving.
             */
            Shaker.prototype.tick = function () {
                if ( this.visibleProperty.get() && !this.emptyProperty.get() ) {
                    if ( this.previousLocation.equals( this.locationProperty.get() ) ) {
                        this.dispensingRateProperty.set( 0 ); // shaker is not moving, don't dispense anything
                    }
                    else {
                        this.dispensingRateProperty.set( this.maxDispensingRate ); // this seems to work fine
                    }
                }
                this.previousLocation = this.locationProperty.get();
            }

            return Shaker;
        } );