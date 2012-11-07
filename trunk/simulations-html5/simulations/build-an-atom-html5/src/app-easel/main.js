// Copyright 2002-2012, University of Colorado
require([
  'underscore',
  'easel',
  'model/proton',
  'view/proton'
], function ( _, Easel, ProtonModel, ProtonView ) {

    var atomConstructionCanvas = $( '#atom-construction-canvas' );
    var atomStage = new Easel.Stage( atomConstructionCanvas[0] );

    var mvt = [ 100, 100, 4, 4];

    _.times(4, function(i){
      var proton = new ProtonModel( i * 10 * Math.random(), i * 10 * Math.random() );
      var protonView = new ProtonView( proton, mvt );

      atomStage.addChild( protonView );
    });



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
