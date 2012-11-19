// Copyright 2002-2012, University of Colorado

/**
 * Display object for the field meter.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/MathUtil',
          'view/DragHandler',
          'image!resources/images/fieldMeter.png'
        ],
        function( Easel, MathUtil, DragHandler, fieldMeterImage ) {

    /**
     * @param {FieldMeter} fieldMeter
     * @param {ModelViewTransform} mvt
     * @constructor
     */
    function FieldMeterDisplay( fieldMeter, mvt ) {

        // constructor stealing
        Easel.Bitmap.call( this, fieldMeterImage );

        // Move registration point to the center of probe crosshairs.
        this.regX = this.image.width / 2;
        this.regY = 28; // manually measured in image file

        // Dragging.
        DragHandler.register( this, function ( point ) {
            fieldMeter.location.set( mvt.viewToModel( point ) );
        } );

        // Register for synchronization with model.
        {
            var thisDisplayObject = this;

            // location
            function updateLocation( location ) {
                var point = mvt.modelToView( location );
                thisDisplayObject.x = point.x;
                thisDisplayObject.y = point.y;
            }
            fieldMeter.location.addObserver( updateLocation );

            // visible
            function updateVisibility( visible ) {
                thisDisplayObject.visible = visible;
            }
            fieldMeter.visible.addObserver( updateVisibility );

            // value
            function updateValues( value ) {
                console.log( "x=" + value.getX() + " y=" + value.getY() + " r=" + value.getMagnitude() + " a=" + MathUtil.toDegrees( value.getAngle() ) ); //XXX
            }
            fieldMeter.value.addObserver( updateValues );
        }

        // sync now
        updateLocation( fieldMeter.location.get() );
        updateVisibility( fieldMeter.visible.get() );
        updateValues( fieldMeter.value.get() );
    }

    // prototype chaining
    FieldMeterDisplay.prototype = new Easel.Bitmap();

    return FieldMeterDisplay;
} );
