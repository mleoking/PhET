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
            'common/view/FrameRateDisplay',
            'i18n!../../../nls/beers-law-lab-strings'
        ],
        function ( Easel, ModelViewTransform2D, Point2D, Inheritance, FrameRateDisplay, Strings ) {

            function BeersLawStage( canvas, model ) {

                Easel.Stage.call( this, canvas ); // constructor stealing

                this.enableMouseOver();

                // model-view transform
                var MVT_SCALE = 1; // 1 model unit == 1 view unit
                var MVT_OFFSET = new Point2D( 0, 0 ); // origin relative to rootContainer
                var mvt = new ModelViewTransform2D( MVT_SCALE, MVT_OFFSET );

                // background that fills the stage
                var background = new Easel.Shape();

                // frame rate display, upper left
                this.frameRateDisplay = new FrameRateDisplay( 'black' );
                //TODO are these new 2 lines necessary?
                this.frameRateDisplay.x = 20;
                this.frameRateDisplay.y = 20;

                // rendering order
                this.addChild( background );
                var rootContainer = new Easel.Container();
                this.addChild( rootContainer );
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

                    // move the root node to the center of the canvas, so the origin remains at the center
                    rootContainer.x = canvas.width / 2;
                    rootContainer.y = canvas.height / 2;

                    // frame rate display at upper left
                    that.frameRateDisplay.x = -( canvas.width / 2 ) + 10;
                    that.frameRateDisplay.y = -( canvas.height / 2 ) + 10;

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
