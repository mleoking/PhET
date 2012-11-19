// Copyright 2002-2012, University of Colorado

/**
 * Display object for the compass.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'view/DragHandler' ], function( Easel, DragHandler ) {

    /**
     * @param {Compass} compass
     * @param {ModelViewTransform} mvt
     * @constructor
     */
    function CompassDisplay( compass, mvt ) {

        // constructor stealing
        Easel.Text.call( this, "compass", "bold 36px Arial", 'white' );

        //XXX center
        this.textAlign = 'center';
        this.textBaseline = 'middle';

        // Dragging.
        DragHandler.register( this, function( point ) {
            compass.location.set( mvt.viewToModel( point ) );
        });

        // Register for synchronization with model.
        var thisDisplayObject = this;
        function updateLocation( location ) {
            var point = mvt.modelToView( location );
            thisDisplayObject.x = point.x;
            thisDisplayObject.y = point.y;
        }
        compass.location.addObserver( updateLocation );

        // sync now
        updateLocation( compass.location.get() );
    }

    // prototype chaining
    CompassDisplay.prototype = new Easel.Text();

    return CompassDisplay;
} );
