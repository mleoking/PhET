// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [ 'easel',
           'common/Logger',
           'common/ModelViewTransform',
           'model/FaradayModel',
           'view/FaradayView'
         ],
         function ( Easel, Logger, ModelViewTransform, FaradayModel, FaradayView ) {

    var logger = new Logger( "faraday-main" ); // logger for this source file

    var canvas = document.getElementById( 'faraday-canvas' );

    // Model ----------------------------------------------------------

    var MVT_SCALE = 1; // 1 model unit == 1 view unit
    var MVT_OFFSET = new Easel.Point( 0.5 * canvas.width / MVT_SCALE, 0.5 * canvas.height / MVT_SCALE ); // origin in center of canvas
    var mvt = new ModelViewTransform( MVT_SCALE, MVT_OFFSET );

    var model = new FaradayModel();

    // View ----------------------------------------------------------

    var view = new FaradayView( canvas, model, mvt );

    // Controls ----------------------------------------------------------

    //TODO similar for other check boxes
    var showCompassCheckBox = document.getElementById( "showCompassCheckBox" );
    showCompassCheckBox.onclick = function() {
        //TODO change compass visibility
        logger.info( "showCompassCheckBox=" + showCompassCheckBox.checked );
    };

    var flipPolarityButton = document.getElementById( "flipPolarityButton" );
    flipPolarityButton.onclick = function() {
        model.barMagnet.orientation.set( model.barMagnet.orientation.get() + Math.PI );
    };

    var resetAllButton = document.getElementById( "resetAllButton" );
    resetAllButton.onclick = function () {
        model.reset();
    };

    // Animation loop ----------------------------------------------------------

    Easel.Ticker.addListener( view.stage );
    Easel.Ticker.setFPS( 60 );
} );
