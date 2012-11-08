// Copyright 2002-2012, University of Colorado
require( [
             'underscore',
             'easel',
             'model/particle2',
             'view/particleView',
             'view/atom-view'
         ], function ( _, Easel, Particle2, ParticleView, AtomView ) {

    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    atomStage.addChild( ParticleView.createParticleView( new Particle2( 0, 0, "red", 20, "proton" ) ) );
    atomStage.addChild( ParticleView.createParticleView( new Particle2( 50, 50, "gray", 20, "neutron" ) ) );
    var atomView = new AtomView();
    atomView.x = 100;
    atomView.y = 100;
    atomStage.addChild( atomView );

    atomStage.update();
    console.log( "main init" );

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    atomConstructionCanvas[0].onselectstart = function () { return false; }; // IE
    atomConstructionCanvas[0].onmousedown = function () { return false; }; // Mozilla

    // Set the frame rate.
    Easel.Ticker.setFPS( 60 );

    // Enable and configure touch and mouse events.
    Easel.Touch.enable( atomStage, false, false );
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener( atomStage, true );

} );
