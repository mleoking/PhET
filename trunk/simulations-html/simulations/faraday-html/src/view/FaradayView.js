// Copyright 2002-2012, University of Colorado

/**
 * View container, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Property', 'view/BarMagnetDisplay' ], function ( Easel, Property, BarMagnetDisplay ) {

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

        // TODO field
        this.field = new Easel.Text( "field", "bold 36px Arial", 'white' );
        this.field.x = 50;
        this.field.y = 50;
        this.field.visible = this.fieldVisible.get();
        this.stage.addChild( this.field );

        // bar magnet
        this.barMagnet = new BarMagnetDisplay( model.barMagnet, mvt );
        this.stage.addChild( this.barMagnet );

        // TODO compass
        this.compass = new Easel.Text( "compass", "bold 36px Arial", 'white' );
        this.compass.x = 50;
        this.compass.y = 100;
        this.compass.visible = this.compassVisible.get();
        this.stage.addChild( this.compass );

        // TODO field meter
        this.meter = new Easel.Text( "meter", "bold 36px Arial", 'white' );
        this.meter.x = 50;
        this.meter.y = 150;
        this.meter.visible = this.fieldMeterVisible.get();
        this.stage.addChild( this.meter );

        this.stage.enableMouseOver();
    }

    return FaradayView;
} );
