require( [
             'easel'
         ], function ( Easel ) {

    console.log( "Starting simple easel drag and drop test." );

    // Create the stage.
    var canvas = $( '#test-canvas' );
    var stage = new Easel.Stage( canvas[0] );

    // Create and add a circle.
    var particle = new Easel.Shape();
    particle.graphics.beginStroke( "black" ).beginFill( "green" ).setStrokeStyle( 2 ).drawCircle( 100, 100, 30 ).endFill();
    console.log( particle );
    stage.addChild( particle );

    // Create and hook up the mouse event handlers.
    function showPointer( mouseEvent ) {
        document.body.style.cursor = "pointer";
    }
    particle.onMouseOver = showPointer;

    function showDefault( mouseEvent ) {
        document.body.style.cursor = "default";
    }
    particle.onMouseOut = showDefault;

    function pressHandler( e ) {
        console.log( "Pressed." )
        //Make dragging relative to touch point
        var relativePressPoint = null;
        e.onMouseMove = function ( event ) {
            var transformed = event.target.parent.globalToLocal( event.stageX, event.stageY );
            if ( relativePressPoint === null ) {
                relativePressPoint = {x:e.target.x - transformed.x, y:e.target.y - transformed.y};
            }
            else {
                e.target.x = transformed.x + relativePressPoint.x;
                e.target.y = transformed.y + relativePressPoint.y;
            }
        };
    }
    particle.onPress = pressHandler;

    // Enable mouse events.
    stage.enableMouseOver( 10 );
    stage.mouseMoveOutside = true;

    // Set the frame rate at which the stage is updated.
    Easel.Ticker.setFPS( 60 );
    Easel.Ticker.addListener( stage, true );

} );
