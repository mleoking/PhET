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

    // Enable touch and mouse events.
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener( atomStage, true );

} );
