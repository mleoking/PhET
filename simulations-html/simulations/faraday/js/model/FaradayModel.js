// Copyright 2002-2012, University of Colorado

/**
 * Model container.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'phetcommon/math/Dimension2D',
            'common/Point2D',
            'phetcommon/math/Range',
            'model/BarMagnet',
            'model/Compass',
            'model/FieldMeter'
        ],
        function ( Easel, Dimension2D, Point2D, Range, BarMagnet, Compass, FieldMeter ) {

            function FaradayModel() {
                // model elements
                this.barMagnet = new BarMagnet( new Point2D( 0, 0 ), new Dimension2D( 250, 50 ), 150, new Range( 0, 300 ), 0 );
                this.fieldMeter = new FieldMeter( new Point2D( -275, 100 ), true, this.barMagnet );
                this.compass = new Compass( new Point2D( -275, -100 ), true, this.barMagnet );
            }

            // Resets all model elements
            FaradayModel.prototype.reset = function () {
                this.barMagnet.reset();
                this.fieldMeter.reset();
                this.compass.reset();
            };

            // Animates the model, called by Easel.Ticker
            FaradayModel.prototype.tick = function() {
                this.compass.tick();
            };

            return FaradayModel;
        } );
