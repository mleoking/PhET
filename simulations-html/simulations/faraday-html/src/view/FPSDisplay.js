// Copyright 2002-2012, University of Colorado

/**
 * Displays the Easel frame rate.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Inheritance' ],
        function( Easel, Inheritance ) {

            function FPSDisplay() {
                 Easel.Text.call( this, "?", "bold 20px Arial", 'white' ); // constructor stealing
            }

            Inheritance.inheritPrototype( FPSDisplay, Easel.Text );

            FPSDisplay.prototype.tick = function() {
                this.text = Easel.Ticker.getMeasuredFPS().toFixed(0) + " fps";
            };

            return FPSDisplay;
        }
);
