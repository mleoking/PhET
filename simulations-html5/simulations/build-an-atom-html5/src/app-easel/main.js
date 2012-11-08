// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'model/particle2',
             'view/particleView',
             'view/atom-view',
             'view/bucket-view'
         ], function ( _, Easel, Particle2, ParticleView, AtomView, BucketView ) {

    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    var root = new Easel.Container();
    atomStage.addChild( root );
    root.addChild( new BucketView( 100, 500 ) );
    root.addChild( new BucketView( 250, 500 ) );
    root.addChild( new BucketView( 400, 500 ) );
    root.addChild( ParticleView.createParticleView( new Particle2( 0, 0, "red", 20, "proton" ) ) );
    root.addChild( ParticleView.createParticleView( new Particle2( 50, 50, "gray", 20, "neutron" ) ) );
    var atomView = new AtomView();
    atomView.x = 100;
    atomView.y = 100;
    root.addChild( atomView );

    atomStage.update();
    console.log( "main init" );

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    atomConstructionCanvas[0].onselectstart = function () { return false; }; // IE
    atomConstructionCanvas[0].onmousedown = function () { return false; }; // Mozilla

    // Set the frame rate.
    Easel.Ticker.setFPS( 60 );

    //resize the canvas when the window is resized
    //Copied from energy skate park easel prototype
    var onResize = function () {
        var winW = $( window ).width(),
                winH = $( window ).height(),
                scale = Math.min( winW / 1024, winH / 768 ),
                canvasW = scale * 1024,
                canvasH = scale * 768;
        atomConstructionCanvas.attr( 'width', canvasW );
        atomConstructionCanvas.attr( 'height', canvasH );
        var left = (winW - canvasW) / 2;
        var top = (winH - canvasH) / 2;
        atomConstructionCanvas.offset( {left:left, top:top} );
        root.scaleX = root.scaleY = scale;
        atomStage.update();
    };
    $( window ).resize( onResize );
    onResize(); // initial position


    // Enable and configure touch and mouse events.
    Easel.Touch.enable( atomStage, false, false );
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener( atomStage, true );

} );
