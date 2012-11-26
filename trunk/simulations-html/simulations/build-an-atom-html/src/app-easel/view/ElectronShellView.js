// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel',
            'common/Point2D'

        ], function ( _, Easel, Point2D ) {

    var ElectronShellView = function ( atom, mvt ) {
        Easel.Container.prototype.initialize.call( this );
        this.initialize( atom, mvt );
    };

    var p = ElectronShellView.prototype;

    _.extend( p, Easel.Container.prototype );

    p.initialize = function ( atom, mvt ) {
        var innerRadius = mvt.modelToView( 80 );
        var innerRingShape = new Easel.Shape();
        innerRingShape.graphics
                .beginStroke( "blue" )
                .setStrokeStyle( 1 )
                .drawEllipse( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2 )
                .endStroke();
        this.addChild( innerRingShape );

        var outerRadius = mvt.modelToView( 160 );
        var outerRingShape = new Easel.Shape();
        outerRingShape.graphics
                .beginStroke( "blue" )
                .setStrokeStyle( 1 )
                .drawEllipse( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 )
                .endStroke();
        this.addChild( outerRingShape );

        var centerInViewSpace = mvt.modelToView( new Point2D( atom.xPos, atom.yPos ) );
        this.x = centerInViewSpace.x;
        this.y = centerInViewSpace.y;
    };

    return ElectronShellView;
} );
