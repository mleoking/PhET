// Copyright 2002-2012, University of Colorado

/**
 * Stage, sets up the scenegraph.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/Dimension2D',
            'common/Inheritance',
            'common/Property',
            'view/BarMagnetDisplay',
            'view/CompassDisplay',
            'view/FieldInsideDisplay',
            'view/FieldMeterDisplay',
            'view/FieldOutsideDisplay'
        ],
        function ( Easel, Dimension2D, Inheritance, Property, BarMagnetDisplay, CompassDisplay, FieldInsideDisplay, FieldMeterDisplay, FieldOutsideDisplay ) {

            function FaradayStage( canvas, model ) {

                Easel.Stage.call( this, canvas ); // constructor stealing

                this.enableMouseOver();

                // properties that are specific to the view (have no model counterpart)
                this.seeInside = new Property( false );
                this.showField = new Property( true );

                // black background
                var background = new Easel.Shape();
                background.graphics.beginFill( 'black' );
                background.graphics.rect( 0, 0, canvas.width, canvas.height );

                // needle size, used for field inside and outside the magnet
                var NEEDLE_SIZE = new Dimension2D( 25, 7 );

                // field outside the magnet
                var field = new FieldOutsideDisplay( model.barMagnet, model.mvt, new Dimension2D( canvas.width, canvas.height ), NEEDLE_SIZE );
                field.visible = this.showField.get();
                this.showField.addObserver( function( visible ) {
                    field.visible = visible;
                } );

                // bar magnet
                var barMagnet = new BarMagnetDisplay( model.barMagnet, model.mvt );

                // field inside magnet
                var fieldInside = new FieldInsideDisplay( model.barMagnet, model.mvt, NEEDLE_SIZE );
                fieldInside.visible = this.seeInside.get();
                this.seeInside.addObserver( function ( visible ) {
                    fieldInside.visible = visible;
                } );

                // compass
                var compass = new CompassDisplay( model.compass, model.mvt, NEEDLE_SIZE );

                // field meter
                var meter = new FieldMeterDisplay( model.fieldMeter, model.mvt );

                // rendering order
                this.addChild( background );
                this.addChild( field );
                this.addChild( barMagnet );
                this.addChild( fieldInside );
                this.addChild( compass );
                this.addChild( meter );
            }

            Inheritance.inheritPrototype( FaradayStage, Easel.Stage );

            // Resets all view-specific properties
            FaradayStage.prototype.reset = function () {
                this.seeInside.reset();
                this.showField.reset();
            };

            return FaradayStage;
        } );
