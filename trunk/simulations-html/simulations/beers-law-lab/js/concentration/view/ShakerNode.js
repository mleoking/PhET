// Copyright 2002-2013, University of Colorado

/**
 * Shaker that contains a solute in solid form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'easel',
            'common/model/Inheritance',
            'common/view/DebugOriginNode',
            'image!images/shaker.png'
        ],
        function ( Easel, Inheritance, DebugOriginNode, shakerImage ) {

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
                this.addChild( imageNode );

                // origin
                if ( DEBUG_ORIGIN ) {
                    this.addChild( new DebugOriginNode( 'red' ) );
                }

                //TODO add dynamic label
                //TODO rotate
                //TODO add drag handler that changes shaker location

                var that = this;

                // sync location with model
                shaker.locationProperty.addObserver( function updateLocation( location ) {
                    that.x = mvt.modelToView( location.x );
                    that.y = mvt.modelToView( location.y );
                } );

                // sync visibility with model
                shaker.visibleProperty.addObserver( function updateVisibility( visible ) {
                    that.visible = visible;
                } )
            }

            Inheritance.inheritPrototype( ShakerNode, Easel.Container );

            return ShakerNode;
        } );
