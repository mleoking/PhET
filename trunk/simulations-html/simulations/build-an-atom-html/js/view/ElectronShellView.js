// Copyright 2002-2012, University of Colorado
define( [
            'underscore',
            'easel',
            'common/Point2D'

        ], function ( _, Easel, Point2D ) {

    var ElectronShellView = function ( atom, innerRadius, outerRadius, mvt ) {
        this.initialize( atom, innerRadius, outerRadius, mvt );
    };

    var p = ElectronShellView.prototype;

    _.extend( p, Easel.Container.prototype );

    p.initialize = function ( atom, innerRadius, outerRadius, mvt ) {
        Easel.Container.prototype.initialize.call( this );
        var innerRadiusInView = mvt.modelToView( innerRadius );
        var innerRingShape = new Easel.Shape();
        innerRingShape.graphics
                .beginStroke( "blue" )
                .setStrokeStyle( 1 )
                .drawEllipse( -innerRadiusInView, -innerRadiusInView, innerRadiusInView * 2, innerRadiusInView * 2 )
                .endStroke();
        this.addChild( innerRingShape );

        var outerRadiusInView = mvt.modelToView( outerRadius );
        var outerRingShape = new Easel.Shape();
        outerRingShape.graphics
                .beginStroke( "blue" )
                .setStrokeStyle( 1 )
                .drawEllipse( -outerRadiusInView, -outerRadiusInView, outerRadiusInView * 2, outerRadiusInView * 2 )
                .endStroke();
        this.addChild( outerRingShape );

        var centerInViewSpace = mvt.modelToView( new Point2D( atom.xPos, atom.yPos ) );
        this.x = centerInViewSpace.x;
        this.y = centerInViewSpace.y;
    };

    return ElectronShellView;
} );
