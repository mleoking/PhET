// Copyright 2002-2012, University of Colorado
require([
  'underscore',
  'easel',
  'model/particle2',
  'view/particleView'
], function ( _, Easel, Particle2, ParticleView ) {

    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    atomStage.addChild( ParticleView.createParticleView( new Particle2(0, 0, "red", 20, "proton")));

    atomStage.update();
    console.log("main init");

    //Get rid of text cursor when dragging on the canvas, see http://stackoverflow.com/questions/2659999/html5-canvas-hand-cursor-problems
    atomConstructionCanvas[0].onselectstart = function () { return false; }; // IE
    atomConstructionCanvas[0].onmousedown = function () { return false; }; // Mozilla

    // Set the frame rate.
    Easel.Ticker.setFPS( 60 );

    // Enable touch and mouse events.
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener( atomStage, true );

} );
