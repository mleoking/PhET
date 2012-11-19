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

        // bar magnet
        this.barMagnet = new BarMagnet( new Easel.Point( 0, 0 ), new Dimension( 250, 50 ), 10, 0 );

        // Debug output for the bar magnet.
        var thisModel = this;
        this.barMagnet.location.addObserver( function () {
            logger.info( "barMagnet.location=" + thisModel.barMagnet.location.get().toString() );
        } );
        this.barMagnet.strength.addObserver( function () {
            logger.info( "barMagnet.strength=" + thisModel.barMagnet.strength.get() );
        } );
        this.barMagnet.orientation.addObserver( function () {
            logger.info( "barMagnet.orientation=" + thisModel.barMagnet.orientation.get() );
        } );

        // field
        this.field = new Field();

        // field meter
        this.fieldMeter = new FieldMeter( new Easel.Point( 0, 0 ) );

        // compass
        this.compass = new Compass( new Easel.Point( 0, 0 ) );
    }

    FaradayModel.prototype.reset = function() {
        this.barMagnet.reset();
    };

    return FaradayModel;
} );
