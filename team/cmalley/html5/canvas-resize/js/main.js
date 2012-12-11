// Copyright 2002-2012, University of Colorado

/**
 * Main entry point for the "Faraday's Electromagnetic Lab" sim.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
require( [
             'easel',
             'math/Dimension2D',
             'math/Point2D',
             'model/BarMagnet',
             'view/BarMagnetDisplay',
             'view/ModelViewTransform2D'
         ],
         function ( Easel, Dimension2D, Point2D, BarMagnet, BarMagnetDisplay, ModelVewTransform2D ) {

             // Model ------------------------------------------------------------------

             var barMagnet = new BarMagnet( new Point2D( 0, 0 ), new Dimension2D( 250, 50 ));

             // View -------------------------------------------------------------------

             var mvt = new ModelVewTransform2D( 1, new Point2D( 0, 0 ) );

             var canvas = document.getElementById( 'canvas' );
             var stage = new Easel.Stage( canvas );
             stage.enableMouseOver();

             // black background
             var background = new Easel.Shape();
             background.graphics
                     .beginFill( 'black' )
                     .rect( 0, 0, canvas.width, canvas.height );
             stage.addChild( background );

             var barMagnetDisplay = new BarMagnetDisplay( barMagnet, mvt );
             stage.addChild( barMagnetDisplay );

             var handleResize = function () {
                 var width = $( window ).width();
                 var height = $( window ).height();
                 console.log( "window width=" + width + " height=" + height );
             };
             $( window ).resize( handleResize );
             handleResize();

             // Animation loop ----------------------------------------------------------

             Easel.Ticker.addListener( stage );
             Easel.Ticker.setFPS( 60 );
             Easel.Touch.enable( stage, false, false );
         } );
