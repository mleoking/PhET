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
           'view/ControlPanel',
           'view/FaradayStage'
         ],
         function ( Easel, Logger, ModelViewTransform, FaradayModel, ControlPanel, FaradayStage ) {

    Logger.enabled = true;

    // MVC --------------------------------------------------------------------

    var canvas = document.getElementById( 'faraday-canvas' );
    var model = new FaradayModel( canvas.width, canvas.height );
    var view = new FaradayStage( canvas, model );
    var controls = new ControlPanel( model, view );

    // Animation loop ----------------------------------------------------------

    Easel.Ticker.addListener( view.stage );
    Easel.Ticker.setFPS( 60 );
} );
