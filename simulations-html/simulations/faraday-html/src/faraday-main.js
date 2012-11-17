// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [ 'easel' ], function ( Easel ) {

    // Create the stage.
    var canvas = document.getElementById( 'faraday-canvas' );
    var stage = new Easel.Stage( canvas );

    // Fill the stage with a black background.
    var background = new Easel.Shape();
    background.graphics.beginFill( 'black' );
    background.graphics.rect( 0, 0, canvas.width, canvas.height );
    stage.addChild( background );

    // Render some text in the center of the stage.
    var text = new Easel.Text( "Faraday was here", "36px Arial", "red" );
    text.textAlign = 'center';
    text.textBaseline = 'middle';
    text.x = canvas.width / 2;
    text.y = canvas.height / 2;
    stage.addChild( text );

    stage.update();
} );
