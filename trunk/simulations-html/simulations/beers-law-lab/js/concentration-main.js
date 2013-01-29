// Copyright 2013, University of Colorado

/**
 * Main entry point for the "Concentration" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [
             'easel',
             'phetcommon/util/Logger',
             'phetcommon/view/ModelViewTransform2D',
             'common/view/CanvasQuirks',
             'concentration/model/ConcentrationModel',
             'concentration/view/ConcentrationStage',
             'i18n!../nls/beers-law-lab-strings'
         ],
         function ( Easel, Logger, ModelViewTransform2D, CanvasQuirks, ConcentrationModel, ConcentrationStage, Strings ) {

             Logger.enabled = true;

             // Title --------------------------------------------------------------------

             $( 'title' ).html( Strings.title );

             // Model --------------------------------------------------------------------

             var model = new ConcentrationModel();

             // View --------------------------------------------------------------------

             var canvas = document.getElementById( 'canvas' );
             CanvasQuirks.fixTextCursor( canvas );
             var stage = new ConcentrationStage( canvas, model );

             // Animation loop ----------------------------------------------------------

             Easel.Ticker.addListener( model );
             Easel.Ticker.addListener( stage );
             Easel.Ticker.addListener( stage.frameRateDisplay );
             Easel.Ticker.setFPS( 60 );
             Easel.Touch.enable( stage, false, false );
         } );
