// Copyright 2002-2012, University of Colorado

/**
 * Model container.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'common/Dimension2D',
            'common/Logger',
            'common/ModelViewTransform2D',
            'common/Point2D',
            'common/Range',
            'model/BarMagnet',
            'model/Compass',
            'model/FieldMeter'
        ],
        function ( Dimension2D, Logger, ModelViewTransform2D, Point2D, Range, BarMagnet, Compass, FieldMeter ) {

            function FaradayModel( canvasWidth, canvasHeight ) {

                var logger = new Logger( "faraday-main" ); // logger for this source file

                // model-view transform
                var MVT_SCALE = 1; // 1 model unit == 1 view unit
                var MVT_OFFSET = new Point2D( 0.5 * canvasWidth / MVT_SCALE, 0.5 * canvasHeight / MVT_SCALE ); // origin in center of canvas
                this.mvt = new ModelViewTransform2D( MVT_SCALE, MVT_OFFSET );

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

            return FaradayModel;
        } );
