// Copyright 2002-2012, University of Colorado

/**
 * View container, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/Property',
          'view/BarMagnetDisplay',
          'view/CompassDisplay',
          'view/FieldDisplay',
          'view/FieldMeterDisplay'
        ],
        function ( Easel, Property, BarMagnetDisplay, CompassDisplay, FieldDisplay, FieldMeterDisplay ) {

    function FaradayView( canvas, model, mvt ) {

        // properties that are specific to the view (have no model counterpart.)
        this.magnetTransparent = new Property( false );
        this.fieldVisible = new Property( true );
        this.compassVisible = new Property( true );
        this.fieldMeterVisible = new Property( false );

        // Create the stage.
        this.stage = new Easel.Stage( canvas );

        // Fill the stage with a black background.
        var background = new Easel.Shape();
        background.graphics.beginFill( 'black' );
        background.graphics.rect( 0, 0, canvas.width, canvas.height );
        this.stage.addChild( background );

        // field
        this.field = new FieldDisplay( model );
        this.field.x = 50;
        this.field.y = 50;
        this.field.visible = this.fieldVisible.get();
        this.stage.addChild( this.field );

        // bar magnet
        this.barMagnet = new BarMagnetDisplay( model.barMagnet, mvt );
        this.stage.addChild( this.barMagnet );

        // compass
        this.compass = new CompassDisplay( model );
        this.compass.x = 50;
        this.compass.y = 100;
        this.compass.visible = this.compassVisible.get();
        this.stage.addChild( this.compass );

        // field meter
        this.meter = new FieldMeterDisplay( model );
        this.meter.x = 50;
        this.meter.y = 150;
        this.meter.visible = this.fieldMeterVisible.get();
        this.stage.addChild( this.meter );

        this.stage.enableMouseOver();
    }

    return FaradayView;
} );
