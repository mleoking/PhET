// Copyright 2013, University of Colorado

/**
 * Model container for the "Concentration" module.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'phetcommon/math/Dimension2D',
            'phetcommon/math/Point2D',
            'phetcommon/model/property/Property',
            'common/model/Rectangle',
            'concentration/model/Beaker',
            'concentration/model/Shaker',
            'concentration/model/DrinkMix'
        ],
        function ( Dimension2D, Point2D, Property, Rectangle, Beaker, Shaker, DrinkMix ) {

            function ConcentrationModel() {

                // constants
                var SHAKER_MAX_DISPENSING_RATE = 0.2; // mol/sec

                // model elements
                this.soluteProperty = new Property( new DrinkMix() );
                this.beaker = new Beaker( new Point2D( 400, 550 ), new Dimension2D( 600, 300 ), 1 );
                this.shaker = new Shaker( new Point2D( 340, 170 ), new Rectangle( 225, 50, 400, 160 ), 0.75 * Math.PI, this.soluteProperty, SHAKER_MAX_DISPENSING_RATE );
            }

            // Resets all model elements
            ConcentrationModel.prototype.reset = function () {
                this.shaker.reset();
            };

            // Animates the model, called by Easel.Ticker
            ConcentrationModel.prototype.tick = function() {
                this.shaker.tick();
            };

            return ConcentrationModel;
        } );
