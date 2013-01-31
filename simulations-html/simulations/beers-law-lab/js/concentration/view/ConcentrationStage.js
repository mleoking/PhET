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
            'common/view/FrameRateNode',
            'concentration/view/BeakerNode',
            'concentration/view/ShakerNode',
            'i18n!../../../nls/beers-law-lab-strings'
        ],
        function ( Easel, ModelViewTransform2D, Point2D, Inheritance, FrameRateNode, BeakerNode, ShakerNode, Strings ) {

            function ConcentrationStage( canvas, model ) {

                Easel.Stage.call( this, canvas ); // constructor stealing

                this.enableMouseOver();

                // model-view transform
                var MVT_SCALE = 1; // 1 model unit == 1 view unit
                var MVT_OFFSET = new Point2D( 0, 0 ); // origin relative to rootContainer
                var mvt = new ModelViewTransform2D( MVT_SCALE, MVT_OFFSET );

                // background that fills the stage
                var background = new Easel.Shape();

                // frame rate display, upper left
                var frameRateNode = new FrameRateNode( 'black' );
                frameRateNode.x = 20;
                frameRateNode.y = 20;

                //TODO create other view components
                var beakerNode = new BeakerNode( model.beaker, mvt );
                var shakerNode = new ShakerNode( model.shaker, mvt );

                // rendering order
                this.addChild( background );
                var rootContainer = new Easel.Container();
                this.addChild( rootContainer );
                //TODO add view components to rootContainer
                rootContainer.addChild( beakerNode );
                rootContainer.addChild( shakerNode );
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
