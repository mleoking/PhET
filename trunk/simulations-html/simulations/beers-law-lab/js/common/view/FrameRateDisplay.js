// Copyright 2013, University of Colorado

/**
 * Displays the Easel frame rate.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/model/Inheritance'
        ],
        function( Easel, Inheritance ) {

            function FrameRateDisplay( foreground ) {
                 Easel.Text.call( this, "?", "bold 20px Arial", foreground ); // constructor stealing
            }

            Inheritance.inheritPrototype( FrameRateDisplay, Easel.Text );

            FrameRateDisplay.prototype.tick = function() {
                this.text = Easel.Ticker.getMeasuredFPS().toFixed(0) + " fps";
            };

            return FrameRateDisplay;
        }
);
