define( [
            'easel',
            'view/DragHandler',
            'image!images/barMagnet.png'
        ],
        function ( Easel, DragHandler, barMagnetImage ) {

            function BarMagnetDisplay( barMagnet, mvt ) {

                Easel.Bitmap.call( this, barMagnetImage );

                // Compute scale factors to match model.
                this.scaleX = mvt.modelToView( barMagnet.size.width ) / this.image.width;
                this.scaleY = mvt.modelToView( barMagnet.size.height ) / this.image.height;

                // Move registration point to the center.
                this.regX = this.image.width / 2;
                this.regY = this.image.height / 2;

                // @param {Point2D} point
                DragHandler.register( this, function ( point ) {
                    barMagnet.location.set( mvt.viewToModel( point ) );
                } );

                // Register for synchronization with model.
                var that = this;
                barMagnet.location.addObserver( function updateLocation( /* Point2D */ location ) {
                    var point = mvt.modelToView( location );
                    that.x = point.x;
                    that.y = point.y;
                } );
            }

            BarMagnetDisplay.prototype = new Easel.Bitmap();

            return BarMagnetDisplay;
        } );
