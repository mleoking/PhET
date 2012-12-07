define( ['easel', 'underscore', 'model/skater-model', 'view/easel-root'], function ( Easel, _, SkaterModel, EaselRoot ) {
    function EnergySkateParkCanvas( $canvas, Strings, analytics ) {

        $canvas[0].onselectstart = function () { return false; }; // IE
        $canvas[0].onmousedown = function () { return false; }; // Mozilla
        console.log( $canvas );

        var skaterModel = new SkaterModel();
        var groundHeight = 116;
        var groundY = 768 - groundHeight;

        var root = EaselRoot( skaterModel, groundHeight, groundY, analytics );

        var stage = new Easel.Stage( $canvas[0] );
        stage.mouseMoveOutside = true;
        stage.addChild( root );

//        debugger
        var width = $canvas.width();
        var height = $canvas.height();
        var scale = Math.min( width / 1024, height / 768 );
        var canvasW = scale * 1024;
        var canvasH = scale * 768;

        //Allow the canvas to fill the screen, but still center the content within the window.
        root.scaleX = root.scaleY = scale / 4;
        root.x = 0;
        root.y = 0;

        //Paint once after initialization
        stage.update();


        var moduleActive = true;
        var paused = false;
        Easel.Ticker.setFPS( 60 );
        Easel.Ticker.addListener( function () {
            if ( true ) {
                stage.tick();
            }
//            if ( moduleActive ) {
//                if ( !paused ) {
//                    var subdivisions = 1;
//                    for ( var i = 0; i < subdivisions; i++ ) {
//                        Physics.updatePhysics( skaterModel, groundHeight, root.splineLayer, self.dt.get() / subdivisions );
//                    }
//
//                    updateFrameRate();
//                    root.tick();
//                }
//                stage.tick();
//            }
        } );

        //Enable touch and prevent default
        Easel.Touch.enable( stage, false, false );

        //Necessary to enable MouseOver events
        stage.enableMouseOver();

    }

    return EnergySkateParkCanvas;
} );
