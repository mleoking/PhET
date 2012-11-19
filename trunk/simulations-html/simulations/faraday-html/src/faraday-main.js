// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [ 'easel',
           'common/ModelViewTransform',
           'model/BarMagnet',
           'view/BarMagnetDisplay'
         ],
         function ( Easel, ModelViewTransform, BarMagnet, BarMagnetDisplay ) {

    var canvas = document.getElementById( 'faraday-canvas' );

    // Model ----------------------------------------------------------

    var MVT_SCALE = 2; // 1 model unit == 2 view units
    var MVT_OFFSET = new Easel.Point( 0.5 * canvas.width / MVT_SCALE, 0.5 * canvas.height / MVT_SCALE ); // origin in center of canvas
    var mvt = new ModelViewTransform( MVT_SCALE, MVT_OFFSET );

    var barMagnet = new BarMagnet( new Easel.Point( 0, 0 ), 10 );
    barMagnet.location.addObserver( function() {
        console.log( "barMagnet.location=" + barMagnet.location.get().toString() );
    });

    // View ----------------------------------------------------------

    // Create the stage.
    var stage = new Easel.Stage( canvas );

    // Fill the stage with a black background.
    var background = new Easel.Shape();
    background.graphics.beginFill( 'black' );
    background.graphics.rect( 0, 0, canvas.width, canvas.height );
    stage.addChild( background );

    // Render a bar magnet
    var barMagnetDisplay = new BarMagnetDisplay( barMagnet, mvt );
    stage.addChild( barMagnetDisplay );

    stage.update();
    stage.enableMouseOver();

    // Start the animation loop.
    Easel.Ticker.addListener( stage );
    Easel.Ticker.setFPS( 60 );
} );
