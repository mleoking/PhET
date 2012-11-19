// Copyright 2002-2012, University of Colorado

/**
 * Model container.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/Dimension',
          'common/Logger',
          'common/ModelViewTransform',
          'model/BarMagnet',
          'model/Compass',
          'model/Field',
          'model/FieldMeter'
        ],
        function ( Easel, Dimension, Logger, ModelViewTransform, BarMagnet, Compass, Field, FieldMeter ) {

    function FaradayModel( canvasWidth, canvasHeight ) {

        var logger = new Logger( "faraday-main" ); // logger for this source file

        // model-view transform
        var MVT_SCALE = 1; // 1 model unit == 1 view unit
        var MVT_OFFSET = new Easel.Point( 0.5 * canvasWidth / MVT_SCALE, 0.5 * canvasHeight / MVT_SCALE ); // origin in center of canvas
        this.mvt = new ModelViewTransform( MVT_SCALE, MVT_OFFSET );

        // model elements
        this.barMagnet = new BarMagnet( new Easel.Point( 0, 0 ), new Dimension( 250, 50 ), 150, 0 );
        this.field = new Field( true, this.barMagnet );
        this.fieldMeter = new FieldMeter( new Easel.Point( -275, 100 ), true, this.barMagnet );
        this.compass = new Compass( new Easel.Point( -275, -100 ), true, this.barMagnet );
    }

    // Resets all model elements
    FaradayModel.prototype.reset = function() {
        this.barMagnet.reset();
        this.field.reset();
        this.fieldMeter.reset();
        this.compass.reset();
    };

    return FaradayModel;
} );
