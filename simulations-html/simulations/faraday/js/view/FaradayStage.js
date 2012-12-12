// Copyright 2002-2012, University of Colorado

/**
 * Stage, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'phetcommon/math/Dimension2D',
            'common/Inheritance',
            'common/ModelViewTransform2D',
            'phetcommon/math/Point2D',
            'phetcommon/model/property/Property',
            'view/BarMagnetDisplay',
            'view/CompassDisplay',
            'view/FieldInsideDisplay',
            'view/FieldMeterDisplay',
            'view/FieldOutsideDisplay',
            'view/FrameRateDisplay',
            'i18n!../../nls/faraday-strings'
        ],
        function ( Easel,
                   Dimension2D, Inheritance, ModelViewTransform2D, Point2D, Property,
                   BarMagnetDisplay, CompassDisplay, FieldInsideDisplay, FieldMeterDisplay, FieldOutsideDisplay, FrameRateDisplay,
                   strings ) {

            function FaradayStage( canvas, model ) {

                Easel.Stage.call( this, canvas ); // constructor stealing

                this.enableMouseOver();

                // properties that are specific to the view (have no model counterpart)
                this.seeInside = new Property( false );
                this.showField = new Property( true );

                // model-view transform
                var MVT_SCALE = 1; // 1 model unit == 1 view unit
                var MVT_OFFSET = new Point2D( 0, 0 ); // origin relative to rootContainer
                var mvt = new ModelViewTransform2D( MVT_SCALE, MVT_OFFSET );

                // black background
                var background = new Easel.Shape();

                // needle size, used for field inside and outside the magnet
                var NEEDLE_SIZE = new Dimension2D( 25, 7 );

                // field outside the magnet
                var fieldOutside = new FieldOutsideDisplay( model.barMagnet, mvt, new Dimension2D( canvas.width, canvas.height ), NEEDLE_SIZE );
                fieldOutside.visible = this.showField.get();
                this.showField.addObserver( function ( visible ) {
                    fieldOutside.visible = visible;
                    if ( visible ) {
                        fieldOutside.updateField();
                    }
                } );

                // bar magnet
                var barMagnet = new BarMagnetDisplay( model.barMagnet, mvt );

                // field inside magnet
                var fieldInside = new FieldInsideDisplay( model.barMagnet, mvt, NEEDLE_SIZE );
                fieldInside.visible = this.seeInside.get();
                this.seeInside.addObserver( function ( visible ) {
                    fieldInside.visible = visible;
                } );

                // compass
                var compass = new CompassDisplay( model.compass, mvt, NEEDLE_SIZE );

                // field meter
                var meter = new FieldMeterDisplay( model.fieldMeter, mvt );

                // FPS display, upper left
                this.frameRateDisplay = new FrameRateDisplay();
                this.frameRateDisplay.x = 20;
                this.frameRateDisplay.y = 20;

                // rendering order
                this.addChild( background );
                var rootContainer = new Easel.Container();
                this.addChild( rootContainer );
                rootContainer.addChild( fieldOutside );
                rootContainer.addChild( barMagnet );
                rootContainer.addChild( fieldInside );
                rootContainer.addChild( compass );
                rootContainer.addChild( meter );
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
                            .beginFill( 'black' )
                            .rect( 0, 0, canvas.width, canvas.height );

                    // expand the grid to fill the canvas
                    fieldOutside.resize( new Dimension2D( canvas.width, canvas.height ) );

                    // move the root node to the center of the canvas, so the origin remains at the center
                    rootContainer.x = canvas.width / 2;
                    rootContainer.y = canvas.height / 2;

                    // force rendering update
                    that.tick();
                };
                $( window ).resize( handleResize );
                handleResize(); // initial size
            }

            Inheritance.inheritPrototype( FaradayStage, Easel.Stage );

            // Resets all view-specific properties
            FaradayStage.prototype.reset = function () {
                this.seeInside.reset();
                this.showField.reset();
            };

            return FaradayStage;
        } );
