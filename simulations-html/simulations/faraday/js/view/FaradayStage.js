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
            'common/Property',
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
                var MVT_OFFSET = new Point2D( 0.5 * canvas.width / MVT_SCALE, 0.5 * canvas.height / MVT_SCALE ); // origin in center of canvas
                var mvt = new ModelViewTransform2D( MVT_SCALE, MVT_OFFSET );

                // black background
                var background = new Easel.Shape();
                background.graphics
                        .beginFill( 'black' )
                        .rect( 0, 0, canvas.width, canvas.height );

                // needle size, used for field inside and outside the magnet
                var NEEDLE_SIZE = new Dimension2D( 25, 7 );

                // field outside the magnet
                var field = new FieldOutsideDisplay( model.barMagnet, mvt, new Dimension2D( canvas.width, canvas.height ), NEEDLE_SIZE );
                field.visible = this.showField.get();
                this.showField.addObserver( function ( visible ) {
                    field.visible = visible;
                    if ( visible ) {
                        field.updateField();
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
                this.addChild( field );
                this.addChild( barMagnet );
                this.addChild( fieldInside );
                this.addChild( compass );
                this.addChild( meter );
                this.addChild( this.frameRateDisplay );
            }

            Inheritance.inheritPrototype( FaradayStage, Easel.Stage );

            // Resets all view-specific properties
            FaradayStage.prototype.reset = function () {
                this.seeInside.reset();
                this.showField.reset();
            };

            return FaradayStage;
        } );
