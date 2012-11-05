// Copyright 2002-2012, University of Colorado
require( [
             'easel'
         ], function ( Easel ) {

    // Draw a square on screen.
    var canvas = $( '#canvas' );
    var stage = new Easel.Stage( canvas[0] );
    var shape = new Easel.Shape();
    shape.graphics.beginFill( 'rgba(255,0,0,1)' ).drawRoundRect( 0, 0, 120, 120, 10 );
    stage.addChild( shape );
    stage.update();

} );
