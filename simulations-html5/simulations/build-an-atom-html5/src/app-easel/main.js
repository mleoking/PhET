// Copyright 2002-2012, University of Colorado
require( [
  'easel',
  'model/proton',
  'view/proton'
], function ( Easel, ProtonModel, ProtonView ) {

    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    var proton = new ProtonModel();
    var protonView = new ProtonView( proton );

    atomStage.addChild( protonView );
    atomStage.update();

    // Enable touch and mouse events.
    atomStage.enableMouseOver( 10 );
    atomStage.mouseMoveOutside = true;

    Easel.Ticker.addListener({
      tick:function () {
          atomStage.update();
      }
    });

} );
