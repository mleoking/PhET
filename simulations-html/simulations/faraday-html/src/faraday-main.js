// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [ 'easel',
           'model/BarMagnet',
           'view/BarMagnetDisplay'
         ],
         function ( Easel, BarMagnet, BarMagnetDisplay ) {

    // Model ----------------------------------------------------------

    var barMagnet = new BarMagnet( new Easel.Point( 200, 200 ), 10 );
    barMagnet.location.addObserver( function() {
        console.log( "barMagnet.location=" + barMagnet.location.get().toString() );
    });

    // View ----------------------------------------------------------

    // Create the stage.
    var canvas = document.getElementById( 'faraday-canvas' );
    var stage = new Easel.Stage( canvas );

    // Fill the stage with a black background.
    var background = new Easel.Shape();
    background.graphics.beginFill( 'black' );
    background.graphics.rect( 0, 0, canvas.width, canvas.height );
    stage.addChild( background );

    // Render a bar magnet
    var barMagnetDisplay = new BarMagnetDisplay( barMagnet );
    stage.addChild( barMagnetDisplay );

    stage.update();
    stage.enableMouseOver();

    // Start the animation loop.
    Easel.Ticker.addListener( stage );
    Easel.Ticker.setFPS( 60 );
} );
