// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [
             'easel',
             'i18n!../nls/faraday-strings',
             'common/CanvasQuirks',
             'phetcommon/util/Logger',
             'common/ModelViewTransform2D',
             'phetcommon/view/PrototypeDialog',
             'model/FaradayModel',
             'view/OptionsPanel',
             'view/FaradayStage'
         ],
         function ( Easel, Strings, CanvasQuirks, Logger, ModelViewTransform2D, PrototypeDialog, FaradayModel, OptionsPanel, FaradayStage ) {

             Logger.enabled = true;

             // Title --------------------------------------------------------------------

             $( 'title' ).html( Strings.title );

             // Canvas --------------------------------------------------------------------

             var canvas = document.getElementById( 'faraday-canvas' );
             CanvasQuirks.fixTextCursor( canvas );

             // MVC --------------------------------------------------------------------

             var model = new FaradayModel();
             var stage = new FaradayStage( canvas, model );
             OptionsPanel.init( model, stage );

             // Animation loop ----------------------------------------------------------

             Easel.Ticker.addListener( model );
             Easel.Ticker.addListener( stage );
             Easel.Ticker.addListener( stage.frameRateDisplay );
             Easel.Ticker.setFPS( 60 );
             Easel.Touch.enable( stage, false, false );

             PrototypeDialog.init( Strings.title );
         } );
