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

             // canvas background
             var background = new Easel.Shape();
             stage.addChild( background );

             // root display object
             var rootContainer = new Easel.Container();
             stage.addChild( rootContainer );

             var barMagnetDisplay = new BarMagnetDisplay( barMagnet, mvt );
             rootContainer.addChild( barMagnetDisplay );

             // Resize ----------------------------------------------------------

             var handleResize = function () {

                 // get the window width
                 var width = $( window ).width();
                 var height = $( window ).height();
                 console.log( "window width=" + width + " height=" + height );

                 // make the canvas fill the window
                 $( "#canvas" ).attr( 'width', width );
                 $( "#canvas" ).attr( 'height', height );

                 // expand the background to fill the canvas
                 background.graphics
                         .beginFill( 'black' )
                         .rect( 0, 0, canvas.width, canvas.height );

                 // move the root node to the center of the canvas, so the origin remains at the center
                 rootContainer.x = canvas.width / 2;
                 rootContainer.y = canvas.height / 2;
             };
             $( window ).resize( handleResize );
             handleResize();

             // Animation loop ----------------------------------------------------------

             Easel.Ticker.addListener( stage );
             Easel.Ticker.setFPS( 60 );
             Easel.Touch.enable( stage, false, false );
         } );
