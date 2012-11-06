// Copyright 2002-2012, University of Colorado
require( [
             'easel'
         ], function ( Easel ) {

    // Draw a square on screen.
    var canvas = $( '#canvas' );
    var stage = new Easel.Stage( canvas[0] );
    var shape = new Easel.Shape();
    shape.graphics.beginFill( 'rgba(255,0,0,1)' ).drawCircle( 200, 200, 150 );
    stage.addChild( shape );
    stage.update();

    // Enable touch and mouse events.
    stage.enableMouseOver( 10 );
    stage.mouseMoveOutside = true;

    // Handler for mouse events on the shape.
    shape.onPress = function ( evt ) {
        var offset = {
            x:shape.x - evt.stageX,
            y:shape.y - evt.stageY
        };
        evt.onMouseMove = function ( ev ) {
            shape.x = ev.stageX + offset.x;
            shape.y = ev.stageY + offset.y;
            console.log( "Dragged!!!" );
        };
        console.log( "Pressed!!!" );
    };

    Easel.Ticker.addListener( {
                                  tick:function () {
                                      stage.update();
                                  }
                              } );

} );
