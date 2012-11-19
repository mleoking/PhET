// Copyright 2002-2012, University of Colorado

/**
 * Display object for the E-field.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel' ], function( Easel ) {

    /**
     * @param {Field} field
     * @param {ModelViewTransform} mvt
     * @constructor
     */
    function FieldDisplay( field, mvt ) {

        // constructor stealing
        Easel.Text.call( this, "field", "bold 36px Arial", 'white' );

        // Register for synchronization with model.
        var thisDisplayObject = this;
        function updateVisibility( visible ) {
            thisDisplayObject.visible = visible;
        }
        field.visible.addObserver( updateVisibility );

        // sync now
        updateVisibility( field.visible.get() );
    }

    // prototype chaining
    FieldDisplay.prototype = new Easel.Text();

    return FieldDisplay;
} );
