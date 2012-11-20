// Copyright 2002-2012, University of Colorado

/**
 * A compass needle.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Dimension' ], function( Easel, Dimension ) {

    /**
     * @param {Number} orientation in degrees
     * @param {Number} alpha 0 (transparent) to 1 (opaque)
     * @constructor
     */
    function CompassNeedleDisplay( orientation, alpha ) {

        // constructor stealing
        Easel.Container.call( this );

        var SIZE = new Dimension( 40, 20 );

        var northShape = new Easel.Shape();
        northShape.graphics.beginFill( Easel.Graphics.getRGB( 255, 0, 0, alpha ) ); // red
        northShape.graphics.moveTo( 0, -SIZE.height / 2 )
                           .lineTo( SIZE.width / 2, 0 )
                           .lineTo( 0, SIZE.height / 2 )
                           .closePath();

        var southShape = new Easel.Shape();
        southShape.graphics.beginFill( Easel.Graphics.getRGB( 255, 255, 255, alpha ) ); // white
        southShape.graphics.moveTo( 0, -SIZE.height / 2 )
                           .lineTo( -SIZE.width / 2, 0 )
                           .lineTo( 0, SIZE.height / 2 )
                           .closePath();

        this.addChild( northShape );
        this.addChild( southShape );

        this.rotation = orientation;
    }

    // prototype chaining
    CompassNeedleDisplay.prototype = new Easel.Container();

    return CompassNeedleDisplay;
});