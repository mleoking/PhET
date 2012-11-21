// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'easel'
], function ( _, Easel ) {

    var ElectronShellView = function ( centerX, centerY ) {
        Easel.Container.prototype.initialize.call(this);
        this.initialize( centerX, centerY );
    };

    var p = ElectronShellView.prototype;

    _.extend(p, Easel.Container.prototype);

    p.initialize = function ( centerX, centerY ) {
        var innerRadius = 80;
        var innerRingShape = new Easel.Shape();
        innerRingShape.graphics
                .beginStroke( "blue" )
                .setStrokeStyle( 1 )
                .drawEllipse( -innerRadius, -innerRadius, innerRadius * 2, innerRadius * 2 )
                .endStroke();
        this.addChild( innerRingShape );

        var outerRadius = 160;
        var outerRingShape = new Easel.Shape();
        outerRingShape.graphics
                .beginStroke( "blue" )
                .setStrokeStyle( 1 )
                .drawEllipse( -outerRadius, -outerRadius, outerRadius * 2, outerRadius * 2 )
                .endStroke();
        this.addChild( outerRingShape );

        this.x = centerX;
        this.y = centerY;
    };

    return ElectronShellView;
});
