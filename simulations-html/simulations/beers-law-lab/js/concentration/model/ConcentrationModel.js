// Copyright 2013, University of Colorado

/**
 * Model container for the "Concentration" module.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
        'phetcommon/math/Dimension2D',
        'phetcommon/math/Point2D',
        'concentration/model/Beaker'
        ],
        function ( Dimension2D, Point2D, Beaker ) {

            function ConcentrationModel() {
                // model elements
                //TODO
                this.beaker = new Beaker( new Point2D( 400, 550 ), new Dimension2D( 600, 300 ), 1 );
            }

            // Resets all model elements
            ConcentrationModel.prototype.reset = function () {
                //TODO
            };

            // Animates the model, called by Easel.Ticker
            ConcentrationModel.prototype.tick = function() {
                //TODO
            };

            return ConcentrationModel;
        } );
