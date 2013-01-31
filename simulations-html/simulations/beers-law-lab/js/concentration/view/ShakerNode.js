// Copyright 2002-2013, University of Colorado

/**
 * Shaker that contains a solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'easel',
            'phetcommon/math/MathUtil',
            'common/model/Inheritance',
            'common/view/DebugOriginNode',
            'common/view/MovableDragHandler',
            'image!images/shaker.png'
        ],
        function ( Easel, MathUtil, Inheritance, DebugOriginNode, MovableDragHandler, shakerImage ) {

            /**
             * Constructor
             * @param {Shaker} shaker
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function ShakerNode( shaker, mvt ) {

                Easel.Container.call( this );

                // constants
                var DEBUG_ORIGIN = true;

                // shaker image
                var imageNode = new Easel.Bitmap( shakerImage );
                imageNode.scaleX = 0.75;
                imageNode.scaleY = 0.75;

                // label
                var labelNode = new Easel.Text( "?", "bold 22px Arial", "black" );
                labelNode.textAlign = 'center';
                labelNode.textBaseline = 'middle';

                // common parent, to simplify rotation and label alignment.
                var parentNode = new Easel.Container();
                this.addChild( parentNode );
                parentNode.addChild( imageNode );
                parentNode.addChild( labelNode );
                parentNode.rotation = MathUtil.toDegrees( shaker.orientation - Math.PI );

                // Manually adjust these values until the origin is in the middle hole of the shaker.
                parentNode.x = -45;
                parentNode.y = -30;

                // origin
                if ( DEBUG_ORIGIN ) {
                    this.addChild( new DebugOriginNode( 'red' ) );
                }

                var that = this;

                // sync location with model
                shaker.locationProperty.addObserver( function updateLocation( location ) {
                    that.x = mvt.modelToView( location.x );
                    that.y = mvt.modelToView( location.y );
                } );

                // sync visibility with model
                shaker.visibleProperty.addObserver( function updateVisibility( visible ) {
                    that.visible = visible;
                } );

                // sync solute with model
                shaker.soluteProperty.addObserver( function updateSolute( solute ) {
                    labelNode.text = solute.formula;
                    labelNode.x = 20 + imageNode.scaleX * imageNode.image.width / 2;
                    labelNode.y = imageNode.scaleY * imageNode.image.height / 2;
                } );

                // drag handler
                //TODO mvt.modelToView(shaker.dragBounds)
                MovableDragHandler.register( this, shaker.dragBounds, function ( point ) {
                    shaker.locationProperty.set( mvt.viewToModel( point ) );
                } );
            }

            Inheritance.inheritPrototype( ShakerNode, Easel.Container );

            return ShakerNode;
        } );
