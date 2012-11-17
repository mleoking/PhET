// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [], function () {

    // canvas and context
    var canvas = document.getElementById( 'faraday-canvas' ),
        context = canvas.getContext( '2d' );

    context.fillStyle = 'black';
    context.fillRect(0, 0, context.canvas.width, context.canvas.height);

    // text to render
    var myString = "Faraday was here.";

    // text style and color
    context.font = '40pt Arial';
    context.fillStyle = 'yellow';
    context.strokeStyle = 'red';

    // center in the canvas
    context.textAlign = 'center';
    context.textBaseline = 'middle';
    var centerX = ( canvas.width / 2 ),
        centerY = ( canvas.height / 2 );

    // fill and stroke the text
    context.fillText( myString, centerX, centerY );
    context.strokeText( myString, centerX, centerY );
} );
