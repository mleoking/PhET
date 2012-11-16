// Copyright 2002-2012, University of Colorado

/**
 * Simple drag and drop on an Easel canvas.
 */
require( [
             'easel'
         ], function ( Easel ) {

    console.log("Here!");
    // Create the canvas where atoms will be constructed.
    var canvas = $( '#canvas' );
    var stage = new Easel.Stage( canvas[0] );

    // Create and add a circle.
    var particle = new Easel.Shape();
    particle.graphics.beginStroke( "black" ).beginFill( particle.color ).setStrokeStyle( 1 ).drawCircle( particle.xPos, particle.yPos, particle.radius ).endFill();
    stage.addChild( particle );
} );
