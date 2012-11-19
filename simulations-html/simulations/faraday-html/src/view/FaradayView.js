// Copyright 2002-2012, University of Colorado

/**
 * View container, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'view/BarMagnetDisplay' ], function ( Easel, BarMagnetDisplay ) {

    function FaradayView( canvas, model, mvt ) {

        // Create the stage.
        this.stage = new Easel.Stage( canvas );

        // Fill the stage with a black background.
        var background = new Easel.Shape();
        background.graphics.beginFill( 'black' );
        background.graphics.rect( 0, 0, canvas.width, canvas.height );
        this.stage.addChild( background );

        // Render a bar magnet
        var barMagnetDisplay = new BarMagnetDisplay( model.barMagnet, mvt );
        this.stage.addChild( barMagnetDisplay );

        this.stage.enableMouseOver();
    }

    return FaradayView;
} );
