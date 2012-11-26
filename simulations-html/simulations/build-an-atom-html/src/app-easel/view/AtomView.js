// Copyright 2002-2012, University of Colorado
define( [
            'easel',
            'common/Point2D'
        ], function ( Easel, Point2D ) {

    /**
     * @param xPos
     * @param yPos
     * @param mvt
     * @constructor
     */
    var AtomView = function ( atom, mvt ) {
        this.initialize( atom, mvt );
    };

    var p = AtomView.prototype = new Easel.Container(); // inherit from Container

    p.Container_initialize = p.initialize;

    p.initialize = function ( atom, mvt ) {
        this.Container_initialize();
        var x = new Easel.Shape();
        var sizeInPixels = mvt.modelToView( 20 );
        var center = mvt.modelToView( new Point2D( atom.xPos, atom.yPos ) );
        x.graphics.beginStroke( "orange" )
                .setStrokeStyle( 5 )
                .moveTo( center.x - sizeInPixels / 2, center.y - sizeInPixels / 2 )
                .lineTo( center.x + sizeInPixels / 2, center.y + sizeInPixels / 2 )
                .moveTo( center.x - sizeInPixels / 2, center.y + sizeInPixels / 2 )
                .lineTo( center.x + sizeInPixels / 2, center.y - sizeInPixels / 2 )
                .endStroke();
        this.addChild( x );
    };

    return AtomView;
} );
