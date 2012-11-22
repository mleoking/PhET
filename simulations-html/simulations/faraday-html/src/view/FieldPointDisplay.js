// Copyright 2002-2012, University of Colorado

/**
 * Visualization of the B-field vector at a specific static location.
 * This is a specialized compass needle that knows its location.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'view/CompassNeedleDisplay'],
        function ( CompassNeedleDisplay ) {

            /**
             * @param {Dimension2D} size
             * @param {Number} orientation in radians
             * @param {Point2D} location
             * @constructor
             */
            function FieldPointDisplay( size, orientation, location ) {

                // constructor stealing
                CompassNeedleDisplay.call( this, size, orientation, 1 );

                this.location = location;
            }

            // prototype chaining
            FieldPointDisplay.prototype = new CompassNeedleDisplay();

            return FieldPointDisplay;
        } );
