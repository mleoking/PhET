// Copyright 2013, University of Colorado

/**
 * Stage for the "Beer's Law" module, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'phetcommon/view/ModelViewTransform2D',
            'phetcommon/math/Point2D',
            'common/model/Inheritance',
            'common/view/FrameRateNode',
            'i18n!../../../nls/beers-law-lab-strings'
        ],
        function ( Easel, ModelViewTransform2D, Point2D, Inheritance, FrameRateNode, Strings ) {

            function BeersLawStage( canvas, model ) {

                Easel.Stage.call( this, canvas ); // constructor stealing

                this.enableMouseOver();

                // model-view transform (1cm == 125 pixels)
                var mvt = new ModelViewTransform2D( 125, new Point2D( 0, 0 ) );

                // background that fills the stage
                var background = new Easel.Shape();

                // frame rate display, upper left
                var frameRateNode = new FrameRateNode( 'black' );
                frameRateNode.x = 20;
                frameRateNode.y = 20;

                //TODO create other view components

                // rendering order
                this.addChild( background );
                var rootContainer = new Easel.Container();
                this.addChild( rootContainer );
                //TODO add view components to rootContainer
                rootContainer.addChild( frameRateNode );

                // resize handler
                var that = this;
                var handleResize = function () {

                    // get the window width
                    var width = $( window ).width();
                    var height = $( window ).height();

                    // make the canvas fill the window
                    canvas.width = width;
                    canvas.height = height;

                    // expand the background to fill the canvas
                    background.graphics
                            .beginFill( 'white' )
                            .rect( 0, 0, canvas.width, canvas.height );

                    // move the root node to the center of the canvas, so the origin remains at the center
                    rootContainer.x = canvas.width / 2;
                    rootContainer.y = canvas.height / 2;

                    // force rendering update
                    that.tick();
                };
                $( window ).resize( handleResize );
                handleResize(); // initial size
            }

            Inheritance.inheritPrototype( BeersLawStage, Easel.Stage );

            // Resets all view-specific properties
            BeersLawStage.prototype.reset = function () {
                //TODO
            };

            return BeersLawStage;
        } );
