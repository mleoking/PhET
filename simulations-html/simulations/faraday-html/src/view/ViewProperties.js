// Copyright 2002-2012, University of Colorado

/**
 * Collection of properties that are specific to the view (have no model counterpart.)
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Property' ], function ( Property ) {

    function ViewProperties() {

        this.magnetTransparent = new Property( false );
        this.fieldVisible = new Property( true );
        this.compassVisible = new Property( true );
        this.fieldMeterVisible = new Property( false );
    }

    return ViewProperties;
} );
