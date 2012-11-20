// Copyright 2002-2012, University of Colorado

/**
 * Stage, sets up the scenegraph.
 * Uses composition of Easel.Stage, inheritance proved to be problematic.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/Property',
          'view/BarMagnetDisplay',
          'view/CompassDisplay',
          'view/CompassNeedleDisplay',
          'view/FieldDisplay',
          'view/FieldMeterDisplay'
        ],
        function ( Easel, Property, BarMagnetDisplay, CompassDisplay, CompassNeedleDisplay, FieldDisplay, FieldMeterDisplay ) {

    function FaradayStage( canvas, model ) {

        // view-specific properties (have no model counterpart.)
        this.magnetTransparent = new Property( false );
        this.magnetTransparent.addObserver( function() {
             //TODO change transparency of barMagnet display object
        });

        // stage
        this.stage = new Easel.Stage( canvas );
        this.stage.enableMouseOver();

        // black background
        var background = new Easel.Shape();
        background.graphics.beginFill( 'black' );
        background.graphics.rect( 0, 0, canvas.width, canvas.height );

        // field
        this.field = new FieldDisplay( model.field, model.mvt );

        // bar magnet
        this.barMagnet = new BarMagnetDisplay( model.barMagnet, model.mvt );

        // compass
        this.compass = new CompassDisplay( model.compass, model.mvt );

        // field meter
        this.meter = new FieldMeterDisplay( model.fieldMeter, model.mvt );

        // rendering order
        this.stage.addChild( background );
        this.stage.addChild( this.field );
        this.stage.addChild( this.barMagnet );
        this.stage.addChild( this.compass );
        this.stage.addChild( this.meter );

        //XXX test compass needle
        var compassNeedle = new CompassNeedleDisplay( 0, 1 );
        var origin = model.mvt.modelToView( new Easel.Point(0,0) );
        compassNeedle.x = origin.x;
        compassNeedle.y = origin.y;
        this.stage.addChild( compassNeedle );
    }

    // Resets all view-specific properties
    FaradayStage.prototype.reset = function() {
       this.magnetTransparent.reset();
    };

    return FaradayStage;
} );
