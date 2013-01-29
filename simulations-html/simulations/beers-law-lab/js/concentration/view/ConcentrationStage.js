// Copyright 2013, University of Colorado

/**
 * Stage for the "Concentration" module, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'phetcommon/view/ModelViewTransform2D',
            'phetcommon/math/Point2D',
            'common/model/Inheritance',
            'common/view/FrameRateDisplay',
            'concentration/view/BeakerNode',
            'i18n!../../../nls/beers-law-lab-strings'
        ],
        function ( Easel, ModelViewTransform2D, Point2D, Inheritance, FrameRateDisplay, BeakerNode, Strings ) {

            function ConcentrationStage( canvas, model ) {

                Easel.Stage.call( this, canvas ); // constructor stealing

                this.enableMouseOver();

                // model-view transform
                var MVT_SCALE = 1; // 1 model unit == 1 view unit
                var MVT_OFFSET = new Point2D( 0, 0 ); // origin relative to rootContainer
                var mvt = new ModelViewTransform2D( MVT_SCALE, MVT_OFFSET );

                // background that fills the stage
                var background = new Easel.Shape();

                // frame rate display
                this.frameRateDisplay = new FrameRateDisplay( 'black' );

                //TODO create other view components
                var beakerNode = new BeakerNode( model.beaker, mvt );

                // rendering order
                this.addChild( background );
                var rootContainer = new Easel.Container();
                this.addChild( rootContainer );
                //TODO add view components to rootContainer
                rootContainer.addChild( beakerNode );
                rootContainer.addChild( this.frameRateDisplay );

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

                    // move the root node to the bottom center of the canvas, so the origin remains at the bottom center
                    rootContainer.x = canvas.width / 2;
                    rootContainer.y = canvas.height - 50;

                    // frame rate display at upper left
                    that.frameRateDisplay.x = -( canvas.width / 2 ) + 10;
                    that.frameRateDisplay.y = -( canvas.height / 2 ) + 10;

                    // force rendering update
                    that.tick();
                };
                $( window ).resize( handleResize );
                handleResize(); // initial size
            }

            Inheritance.inheritPrototype( ConcentrationStage, Easel.Stage );

            // Resets all view-specific properties
            ConcentrationStage.prototype.reset = function () {
                //TODO
            };

            return ConcentrationStage;
        } );
