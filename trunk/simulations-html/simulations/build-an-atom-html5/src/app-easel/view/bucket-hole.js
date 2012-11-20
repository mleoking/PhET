// Copyright 2002-2012, University of Colorado
define([
  'underscore',
  'easel'
], function ( _, Easel ) {

    var BucketHole = function ( x, y ) {
        Easel.Container.prototype.initialize.call(this);
        this.initialize( x, y );
    };

    var p = BucketHole.prototype;

    _.extend(p, Easel.Container.prototype);

    p.initialize = function ( x, y ) {
        var shape = new Easel.Shape();
        shape.graphics.beginStroke( "black" ).beginFill( "black" ).setStrokeStyle( 2 ).drawEllipse( 0, 0, 150, 30 ).endStroke().endFill();
        this.addChild( shape );

        this.x = x;
        this.y = y;
    };

    return BucketHole;
});
