// Copyright 2002-2012, University of Colorado

/**
 * Visualization of the B-field vector at a specific static location.
 * This is a specialized compass needle that knows its location.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'common/Inheritance',
            'view/CompassNeedleDisplay'
        ],
        function ( Inheritance, CompassNeedleDisplay ) {

            /**
             * @param {Dimension2D} size
             * @param {Point2D} location
             * @constructor
             */
            function FieldPointDisplay( size, location ) {
                CompassNeedleDisplay.call( this, size ); // constructor stealing
                this.location = location;
            }

            // prototype chaining
            Inheritance.inheritPrototype( FieldPointDisplay, CompassNeedleDisplay );

            return FieldPointDisplay;
        } );
