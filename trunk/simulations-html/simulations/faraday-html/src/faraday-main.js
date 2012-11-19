// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [ 'easel',
           'common/Dimension',
           'common/Logger',
           'common/ModelViewTransform',
           'model/BarMagnet',
           'view/BarMagnetDisplay'
         ],
         function ( Easel, Dimension, Logger, ModelViewTransform, BarMagnet, BarMagnetDisplay ) {

    var logger = new Logger( "faraday-main" ); // logger for this source file

    var canvas = document.getElementById( 'faraday-canvas' );

    // Model ----------------------------------------------------------

    var MVT_SCALE = 1; // 1 model unit == 1 view unit
    var MVT_OFFSET = new Easel.Point( 0.5 * canvas.width / MVT_SCALE, 0.5 * canvas.height / MVT_SCALE ); // origin in center of canvas
    var mvt = new ModelViewTransform( MVT_SCALE, MVT_OFFSET );

    var barMagnet = new BarMagnet( new Easel.Point( 0, 0 ), new Dimension( 250, 50 ), 10, 0 );
    barMagnet.location.addObserver( function() {
        logger.info( "barMagnet.location=" + barMagnet.location.get().toString() );
    } );
    barMagnet.strength.addObserver( function () {
        logger.info( "barMagnet.strength=" + barMagnet.strength.get() );
    } );
    barMagnet.orientation.addObserver( function () {
        logger.info( "barMagnet.orientation=" + barMagnet.orientation.get() );
    } );

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

    stage.enableMouseOver();

    // Animation loop ----------------------------------------------------------

    Easel.Ticker.addListener( stage );
    Easel.Ticker.setFPS( 60 );
} );
