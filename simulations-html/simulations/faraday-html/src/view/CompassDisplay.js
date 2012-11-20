// Copyright 2002-2012, University of Colorado

/**
 * Display object for the compass.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel',
          'common/MathUtil',
          'view/CompassNeedleDisplay',
          'view/DragHandler'
        ],
        function( Easel, MathUtil, CompassNeedleDisplay, DragHandler ) {

    /**
     * @param {Compass} compass
     * @param {ModelViewTransform} mvt
     * @constructor
     */
    function CompassDisplay( compass, mvt ) {

        // constructor stealing
        Easel.Container.call( this );

        var needle = new CompassNeedleDisplay( 0, 1 );
        this.addChild( needle );

        // Dragging.
        DragHandler.register( this, function( point ) {
            compass.location.set( mvt.viewToModel( point ) );
        });

        // Register for synchronization with model.
        var thisDisplayObject = this;

        // @param {Point} location
        function updateLocation( location ) {
            var point = mvt.modelToView( location );
            thisDisplayObject.x = point.x;
            thisDisplayObject.y = point.y;
            thisDisplayObject.rotation = MathUtil.toDegrees( compass.orientation.get() );
        }
        compass.location.addObserver( updateLocation );

        // @param {Boolean} visible
        function updateVisibility( visible ) {
            thisDisplayObject.visible = visible;
        }
        compass.visible.addObserver( updateVisibility );

        // sync now
        updateLocation( compass.location.get() );
        updateVisibility( compass.visible.get() );
    }

    // prototype chaining
    CompassDisplay.prototype = new Easel.Container();

    return CompassDisplay;
} );
